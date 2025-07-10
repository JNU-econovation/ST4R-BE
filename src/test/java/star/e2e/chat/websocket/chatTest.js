let stompClient = null;

function setUIState(isConnected) {
    $('#connectBtn').prop('disabled', isConnected);
    $('#disconnectBtn').prop('disabled', !isConnected);
    $('#sendBtn').prop('disabled', !isConnected);
    $('#subscribeBtn').prop('disabled', !isConnected);
    $('#serverUrl, #jwtToken').prop('disabled', isConnected);
}

function connect() {
    const serverUrl = $('#serverUrl').val();
    const jwtToken = $('#jwtToken').val();
    const previewUrl = $('#previewDestination').val();

    if (!serverUrl || !jwtToken) {
        alert('Server URL and JWT Token are required.');
        return;
    }

    const socket = new SockJS(serverUrl);
    stompClient = Stomp.over(socket);

    const headers = { 'Authorization': jwtToken.startsWith('Bearer ') ? jwtToken : `Bearer ${jwtToken}` };

    stompClient.connect(headers, function (frame) {
        setUIState(true);
        logSystemMessage('Connected to server.');

        if (previewUrl) {
            stompClient.subscribe(previewUrl, function (message) {
                logChatPreview(JSON.parse(message.body));
            });
            logSystemMessage(`Auto-subscribed to chat preview: ${previewUrl}`);
        }

    }, function (error) {
        logSystemMessage(`Connection error: ${error}`, 'error');
        setUIState(false);
    });
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect(() => {
            setUIState(false);
            logSystemMessage('Disconnected.');
        });
    }
}

function subscribeToTopic() {
    const destination = $('#subscribeDestination').val();
    if (!destination) {
        alert('Subscribe destination is required.');
        return;
    }
    stompClient.subscribe(destination, function (message) {
        logChatMessage(JSON.parse(message.body), destination);
    });
    logSystemMessage(`Subscribed to: ${destination}`);
}

function sendMessage() {
    const destination = $('#publishDestination').val();
    const messageContent = $('#messageContent').val();

    if (!destination || !messageContent) {
        alert('Publish destination and message are required.');
        return;
    }

    if (stompClient) {
        const chatMessage = { message: messageContent };
        stompClient.send(destination, {}, JSON.stringify(chatMessage));
        logMessage(messageContent, 'You (Sent)', 'msg-send', destination);
        $('#messageContent').val('');
    }
}

function logChatMessage(msg, destination) {
    const content = `
        <strong>Team ID:</strong> ${msg.teamId}<br>
        <strong>Chat ID:</strong> ${msg.chatId}<br>
        <strong>Member ID:</strong> ${msg.memberId}<br>
        <strong>Email:</strong> ${msg.email}<br>
        <strong>Message:</strong> ${msg.message}<br>
        <strong>Chatted At:</strong> ${new Date(msg.chattedAt).toLocaleString()}
    `;
    logMessage(content, msg.email, 'msg-recv', destination);
}

function logChatPreview(preview) {
    const logDiv = $('#preview-log');
    const previewHtml = `
        <div class="msg msg-sys">
            <strong>Team ID: ${preview.teamId}</strong><br>
            Target Member ID: ${preview.targetMemberId}<br>
            Unread Count: ${preview.unreadCount}<br>
            Recent Message: "${preview.recentMessage}"<br>
        </div>`;
    logDiv.prepend(previewHtml);
}

function logSystemMessage(message, type = 'system') {
    logMessage(message, type.toUpperCase(), 'msg-sys');
}

function logMessage(content, sender, msgClass, destination = '') {
    const time = new Date().toLocaleTimeString();
    const destText = destination ? `<strong>To:</strong> ${destination}` : '';
    $('#messages').append(
        `<div class="msg ${msgClass}">
            <span class="time">${time}</span>
            <div><span class="sender">${sender}: </span>${content}</div>
            <div style="font-size: 0.8em; color: #555; margin-top: 4px;">${destText}</div>
        </div>`
    );
    $('#messages').scrollTop($('#messages')[0].scrollHeight);
}

$(function () {
    setUIState(false);
    $('#messageContent').on('keypress', function (e) {
        if (e.key === 'Enter' && !e.shiftKey) {
            e.preventDefault();
            $('#sendBtn').click();
        }
    });
    $('#subscribeBtn').on('click', subscribeToTopic);
});
