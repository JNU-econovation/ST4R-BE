// WebSocket 클라이언트와 채팅 상태를 관리하는 전역 변수
let stompClient = null;
let chatState = {
    serverBaseUrl: null, // API 호출을 위한 서버 기본 주소
    teamId: null,
    currentMemberId: null,
    jwtToken: null,
    subscription: null, // 활성화된 구독을 저장하기 위한 변수
    teamMembers: [], // { id: number, name: string }
    messages: [], // { chatId, sender: {id, nickname}, message, chattedAt, ... }
    readStatus: {}, // { memberId: "iso_timestamp" }
};

// UI 상태를 설정하는 함수
function setConnectionState(isConnected) {
    $('#connectBtn').prop('disabled', isConnected);
    $('#disconnectBtn').prop('disabled', !isConnected);
    $('#serverUrl, #jwtToken').prop('disabled', isConnected);
    $('#enterRoomBtn').prop('disabled', !isConnected);

    if (isConnected) {
        $('#status').removeClass('disconnected').addClass('connected').text('Connected');
    } else {
        $('#status').removeClass('connected').addClass('disconnected').text('Disconnected');
        $('#chat-window').hide();
    }
}

// WebSocket 서버에 연결
function connect() {
    const serverUrl = $('#serverUrl').val();
    const jwtToken = $('#jwtToken').val();
    if (!serverUrl || !jwtToken) {
        alert('Server URL and JWT Token are required.');
        return;
    }
    chatState.serverBaseUrl = serverUrl.substring(0, serverUrl.indexOf('/websocket/connect'));
    chatState.jwtToken = jwtToken.startsWith('Bearer ') ? jwtToken : `Bearer ${jwtToken}`;

    const socket = new SockJS(serverUrl);
    stompClient = Stomp.over(socket);

    stompClient.connect({ 'Authorization': chatState.jwtToken },
        () => {
            setConnectionState(true);
            logSystemMessage('Connected. Enter Team ID and your Member ID.');
            stompClient.subscribe('/member/queue/previews', (message) => logChatPreview(JSON.parse(message.body)));
        },
        (error) => {
            logSystemMessage(`Connection error: ${error}`);
            setConnectionState(false);
        }
    );
}

// WebSocket 연결 해제
function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect(() => {
            setConnectionState(false);
            logSystemMessage('Disconnected.');
            resetChatState(true);
        });
    }
}

// 채팅방 입장 처리
async function enterChatRoom() {
    if (chatState.subscription) {
        chatState.subscription.unsubscribe();
        logSystemMessage(`Unsubscribed from previous topic.`);
    }

    const teamId = $('#teamId').val();
    const memberId = $('#memberId').val();
    if (!teamId || !memberId) {
        alert('Team ID and your Member ID are required.');
        return;
    }

    resetChatState(false);
    chatState.teamId = teamId;
    chatState.currentMemberId = parseInt(memberId, 10);

    try {
        // 1. 팀 멤버 및 초기 읽음 상태 가져오기
        const apiUrl = `${chatState.serverBaseUrl}/groups/${teamId}/chats/readCounts`;
        const response = await fetch(apiUrl, { headers: { 'Authorization': chatState.jwtToken } });
        if (!response.ok) throw new Error(`Failed to fetch initial data: ${response.statusText}`);
        
        const initialReadCounts = await response.json();
        chatState.teamMembers = initialReadCounts.map(item => ({ id: item.memberId, name: `Member ${item.memberId}` }));
        initialReadCounts.forEach(item => {
            chatState.readStatus[item.memberId] = item.readTime;
        });

        $('#chat-window').show();
        $('#messages').empty();
        logSystemMessage(`Entered chat for Team ID: ${teamId}. Fetching history...`);

        // 2. 이전 대화 기록 불러오기
        await fetchAndRenderHistory();

        // 3. 실시간 채팅 구독
        subscribeToChatTopic();

        // 4. 모든 메시지를 읽음으로 처리
        markAsRead();
        logSystemMessage('Marked all messages as read.');

    } catch (error) {
        logSystemMessage(`Error entering chat room: ${error.message}`);
    }
}

// 이전 대화 기록을 불러와 렌더링하는 함수
async function fetchAndRenderHistory() {
    try {
        const historyUrl = `${chatState.serverBaseUrl}/groups/${chatState.teamId}/chats?size=10&sort=chattedAt`;
        const response = await fetch(historyUrl, { headers: { 'Authorization': chatState.jwtToken } });
        if (!response.ok) throw new Error(`Failed to fetch chat history: ${response.statusText}`);

        const historySlice = await response.json();
        const historyMessages = historySlice.content;

        if (historyMessages.length > 0) {
            chatState.messages.unshift(...historyMessages);
            renderHistory(historyMessages);
            logSystemMessage('Chat history loaded.');
        } else {
            logSystemMessage('No previous chat history.');
        }
        // 메시지 렌더링 후 스크롤을 맨 아래로 이동
        $('#messages').scrollTop($('#messages')[0].scrollHeight);

    } catch (error) {
        logSystemMessage(`Error fetching history: ${error.message}`);
    }
}

// 여러 메시지(대화 기록)를 한 번에 렌더링
function renderHistory(messages) {
    const messagesHtml = messages.map(msg => getMessageHtml(msg)).join('');
    $('#messages').append(messagesHtml);
}

// 단일 메시지를 화면에 렌더링
function renderSingleMessage(msg) {
    const messageHtml = getMessageHtml(msg);
    $('#messages').append(messageHtml);
    $('#messages').scrollTop($('#messages')[0].scrollHeight);
}

// 메시지 객체를 HTML 문자열로 변환
function getMessageHtml(msg) {
    const isMine = msg.sender.id === chatState.currentMemberId;
    const timestamp = new Date(msg.chattedAt).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
    const unreadCount = calculateUnreadCount(msg.chattedAt);

    return `
        <div class="chat-message ${isMine ? 'mine' : 'theirs'}" data-message-id="${msg.chatId}" data-timestamp="${msg.chattedAt}">
            ${!isMine ? `<div class="sender">${msg.sender.nickname}</div>` : ''}
            <div class="content">${msg.message}</div>
            <div class="message-meta">
                <div class="unread-count">${unreadCount > 0 ? unreadCount : ''}</div>
                <div class="timestamp">${timestamp}</div>
            </div>
        </div>
    `;
}

// 채팅 토픽 구독
function subscribeToChatTopic() {
    const destination = `/subscribe/${chatState.teamId}`;
    // *** 해결책: 새로운 구독을 chatState에 저장 ***
    chatState.subscription = stompClient.subscribe(destination, (message) => {
        const broadcast = JSON.parse(message.body);
        handleBroadcast(broadcast);
    });
    logSystemMessage(`Subscribed to: ${destination}`);
}

// 서버로부터 받은 메시지 처리
function handleBroadcast(broadcast) {
    switch (broadcast.messageType) {
        case 'general':
            handleNewMessage(broadcast.message);
            break;
        case 'updateReadTime':
            handleReadTimeUpdate(broadcast.message);
            break;
    }
}

// 새 메시지 도착 시 처리
function handleNewMessage(msg) {
    if (!chatState.teamMembers.find(m => m.id === msg.sender.id)) {
        chatState.teamMembers.push({ id: msg.sender.id, name: msg.sender.nickname });
    }

    chatState.messages.push(msg);
    renderSingleMessage(msg);
    updateAllUnreadCounts();
    markAsRead();
}

// '읽음' 상태 업데이트 메시지 처리
function handleReadTimeUpdate(update) {
    chatState.readStatus[update.updateMemberId] = update.updateReadTime;
    updateAllUnreadCounts();
}

// '/markAsRead' 메시지를 서버로 전송
function markAsRead() {
    if (!stompClient || !chatState.teamId) return;
    const destination = `/markAsRead/${chatState.teamId}`;
    stompClient.send(destination, {}, '');
}

// 메시지 전송
function sendMessage() {
    const messageContent = $('#messageContent').val();
    if (!messageContent.trim() || !stompClient) return;

    const destination = `/broadcast/${chatState.teamId}`;
    stompClient.send(destination, {}, JSON.stringify({ message: messageContent }));
    $('#messageContent').val('');
}

// 특정 메시지의 안 읽은 사람 수 계산
function calculateUnreadCount(messageTimestamp) {
    const messageTime = new Date(messageTimestamp).getTime();
    let unreadCount = 0;
    chatState.teamMembers.forEach(member => {
        const memberReadTime = chatState.readStatus[member.id] ? new Date(chatState.readStatus[member.id]).getTime() : 0;
        if (messageTime > memberReadTime) {
            unreadCount++;
        }
    });
    return unreadCount;
}

// 화면의 모든 메시지에 대해 안 읽은 사람 수를 다시 계산하고 업데이트
function updateAllUnreadCounts() {
    $('.chat-message').each(function() {
        const timestamp = $(this).data('timestamp');
        if (timestamp) {
            const unreadCount = calculateUnreadCount(timestamp);
            const unreadEl = $(this).find('.unread-count');
            unreadEl.text(unreadCount > 0 ? unreadCount : '');
        }
    });
}

// 단일 메시지를 화면에 렌더링
function renderSingleMessage(msg) {
    const messageHtml = getMessageHtml(msg);
    $('#messages').append(messageHtml);
    $('#messages').scrollTop($('#messages')[0].scrollHeight);
}

// 메시지 객체를 HTML 문자열로 변환
function getMessageHtml(msg) {
    const isMine = msg.sender.id === chatState.currentMemberId;
    const timestamp = new Date(msg.chattedAt).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
    const unreadCount = calculateUnreadCount(msg.chattedAt);

    return `
        <div class="chat-message ${isMine ? 'mine' : 'theirs'}" data-message-id="${msg.chatId}" data-timestamp="${msg.chattedAt}">
            ${!isMine ? `<div class="sender">${msg.sender.nickname}</div>` : ''}
            <div class="content">${msg.message}</div>
            <div class="message-meta">
                <div class="unread-count">${unreadCount > 0 ? unreadCount : ''}</div>
                <div class="timestamp">${timestamp}</div>
            </div>
        </div>
    `;
}

// 채팅 미리보기 로그
function logChatPreview(preview) {
    const logDiv = $('#preview-log');
    const previewHtml = `
        <div class="msg msg-sys">
            <strong>Team ID: ${preview.teamId}</strong><br>
            Unread: ${preview.unreadCount}, Recent: "${preview.recentMessage || 'N/A'}"
        </div>`;
    logDiv.prepend(previewHtml);
}

// 시스템 메시지 로그
function logSystemMessage(message) {
    $('#messages').append(`<div class="msg msg-sys" style="text-align: center;">${message}</div>`);
    $('#messages').scrollTop($('#messages')[0].scrollHeight);
}

// 채팅 상태 초기화
function resetChatState(fullReset = true) {
    chatState.teamId = null;
    chatState.currentMemberId = null;
    chatState.subscription = null; // 구독 상태도 초기화
    if (fullReset) {
        chatState.serverBaseUrl = null;
        chatState.jwtToken = null;
    }
    chatState.teamMembers = [];
    chatState.messages = [];
    chatState.readStatus = {};

    $('#messages').empty();
    if (fullReset) $('#chat-window').hide();
}

// 문서 로드 시 이벤트 핸들러 바인딩
$(function () {
    setConnectionState(false);
    $('#connectBtn').on('click', connect);
    $('#disconnectBtn').on('click', disconnect);
    $('#enterRoomBtn').on('click', enterChatRoom);
    $('#sendBtn').on('click', sendMessage); // sendBtn에 대한 이벤트 핸들러 추가
    $('#messageContent').on('keypress', function (e) {
        if (e.key === 'Enter' && !e.shiftKey) {
            e.preventDefault();
            sendMessage();
        }
    });
});