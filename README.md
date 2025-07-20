# <h1>ST4R - 별자리 커뮤니티 플랫폼 BackEnd</h1>

### 🚧 현재 이 README는 Gemini CLI가 제작한 것이며, 올바르지 않은 정보가 다량 있다는 것을 인지해주셨으면 감사하겠습니다. 추후에 리팩토링 하겠습니다. 🚧


<p align="center">
    별과 관련된 이야기를 나누고, 함께할 모임을 찾는 커뮤니티 플랫폼의 백엔드 API 서버입니다.
</p>

<p align="center">
    <a href="#-기능-소개"><strong>기능 소개</strong></a> ·
    <a href="#-기술-스택"><strong>기술 스택</strong></a> ·
    <a href="#-api-명세"><strong>API 명세</strong></a> ·
    <a href="#-데이터베이스-구조"><strong>DB 구조</strong></a> ·
    <a href="#-시작하기"><strong>시작하기</strong></a>
</p>

---

## 🌟 기능 소개

ST4R는 별을 사랑하는 사람들을 위한 커뮤니티 공간입니다. 별자리에 대한 지식이나 감상을 나누고, 자유롭게 소통하며, 함께 관심사를 공유할 모임을 만들 수 있습니다.

- **사용자 인증**: 카카오 소셜 로그인을 통한 간편하고 안전한 인증 시스템을 제공합니다. JWT는 응답 본문을 통해 전달됩니다.
- **게시판 (Board)**: 별자리에 대한 이야기, 관측 후기, 질문 등 다양한 주제로 자유롭게 글을 작성, 조회, 수정, 삭제할 수 있는 공간입니다.
- **모임 (Group)**: 함께 별을 관측하거나, 특정 주제에 대해 이야기 나눌 소모임을 만들고, 멤버를 모집하며, 활동 계획을 관리할 수 있습니다.
- **실시간 채팅**: 모임 멤버 간의 원활한 소통과 계획 조율을 위해 WebSocket 기반의 실시간 채팅 기능을 제공합니다.
- **검색 및 필터링**: 위치, 카테고리, 키워드 등 다양한 조건으로 원하는 게시글이나 모임을 검색할 수 있습니다.
- **마이페이지**: 내가 작성한 글, '좋아요'한 글/모임, 소속된 모임 목록 등 개인화된 정보를 확인할 수 있습니다.
- **'좋아요' 기능**: 다른 사람의 멋진 게시글이나 흥미로운 모임에 '좋아요'를 표시하고, 마이페이지에서 모아볼 수 있습니다.

---

## 🛠️ 기술 스택

### Backend
- **Java 21**
- **Spring Boot 3.4.5**
- **Spring Security**: JWT 기반 인증 및 인가
- **Spring Data JPA**: 데이터베이스 연동 및 관리
- **QueryDSL**: 동적 쿼리 및 타입-세이프 쿼리 작성
- **Spring WebSocket**: 실시간 채팅 기능 구현
- **Spring Retry**: Optimistic Lock 충돌 시 재시도 로직 구현

### Database
- **MySQL**: 주 데이터베이스
- **Redis**: 실시간 채팅에서 사용자의 메시지 읽음 상태를 처리하기 위해 사용
- **H2 Database**: 테스트 환경용 인메모리 데이터베이스

### Infrastructure
- **AWS S3**: 게시글에 첨부된 이미지 등 정적 파일 저장

### Build & Test
- **Gradle**: 의존성 관리 및 빌드 자동화
- **JUnit 5, Rest-Assured**: 단위 테스트 및 E2E 테스트

### Libraries
- **JWT (Java JWT)**: JSON Web Token 생성 및 검증
- **Lombok**: 보일러플레이트 코드 자동 생성
- **Jackson**: JSON 데이터 처리

---

## 📜 API 명세

- `(Auth)`: 인증된 사용자만 접근 가능 (Authorization 헤더에 Bearer 토큰 필요)
- `(Optional Auth)`: 인증/비인증 사용자 모두 접근 가능 (인증 시 개인화된 데이터 제공)

### 인증 (Authentication)
| Method | URL | 설명                    | 인증 |
| --- | --- |-----------------------| --- |
| `GET` | `/auth/kakao/login` | 카카오 로그인 페이지로 리다이렉트    | - |
| `GET` | `/auth/kakao/callback` | 카카오 로그인 콜백 처리, JWT 발급 | - |
| `POST` | `/auth/logout` | 로그아웃                  | `(Auth)` |
| `PATCH` | `/completeRegistration` | 추가 정보 입력 (회원가입 완료)    | `(Auth)` |

### 게시판 (Boards)
| Method | URL | 설명 | 인증 |
| --- | --- | --- | --- |
| `GET` | `/home` | 게시글 목록 조회 (검색/필터링) | `(Optional Auth)` |
| `POST` | `/home/boards` | 게시글 생성 | `(Auth)` |
| `GET` | `/home/boards/{boardId}` | 게시글 상세 조회 | `(Optional Auth)` |
| `PUT` | `/home/boards/{boardId}` | 게시글 수정 | `(Auth)` |
| `DELETE` | `/home/boards/{boardId}` | 게시글 삭제 | `(Auth)` |

### 모임 (Groups)
| Method | URL | 설명 | 인증 |
| --- | --- | --- | --- |
| `POST` | `/groups` | 모임 생성 | `(Auth)` |
| `GET` | `/groups` | 모임 목록 조회 (검색/필터링) | `(Optional Auth)` |
| `GET` | `/groups/my` | 내가 속한 모임 목록 조회 | `(Auth)` |
| `GET` | `/groups/{teamId}` | 모임 상세 정보 조회 | `(Optional Auth)` |
| `PUT` | `/groups/{teamId}` | 모임 정보 수정 | `(Auth)` |
| `DELETE` | `/groups/{teamId}` | 모임 삭제 | `(Auth)` |

### 실시간 채팅 (Chat)
| Method | URL | 설명 | 인증 |
| --- | --- | --- | --- |
| `GET` | `/groups/{teamId}/chat/messages` | 이전 채팅 메시지 조회 | `(Auth)` |
| `SUB` | `/sub/chat/room/{teamId}` | 채팅방 구독 (메시지 수신) | `(Auth)` |
| `PUB` | `/pub/chat/message/{teamId}` | 채팅 메시지 발행 (메시지 전송) | `(Auth)` |

---

## 🗄️ 데이터베이스 구조

- **Member**: 사용자 정보.
- **Board**: 커뮤니티 게시글. `Member`와 N:1 관계.
- **Team**: 사용자들이 생성하는 소모임. `Member` (모임장)와 N:1 관계.
- **Comment**: 게시글에 대한 댓글.
- **TeamMember**: 모임과 `Member`의 N:M 관계를 나타내는 중간 테이블.
- **Category**: 게시글의 카테고리 (e.g., '별자리 이야기', '자유 수다', '장비 질문').

---

## 🚀 시작하기

### 1. 프로젝트 클론
```bash
git clone https://github.com/your-repository/ST4R-BE.git
cd ST4R-BE
```

### 2. env 파일 구성

프로젝트 루트 디렉토리에 .env 파일을 생성합니다.

```properties
# 이후 .env 파일을 다음과 같이 작성합니다.


# MySQL
SPRING_MYSQL_URL=jdbc:MySQL_호스트://mysql:3306/star?serverTimezone=UTC&allowLoadLocalInfile=true
SPRING_MYSQL_DRIVER_CLASS_NAME=com.mysql.cj.jdbc.Driver
MYSQL_ROOT_PASSWORD=your_mysql_root_password
MYSQL_DATABASE=your_database
MYSQL_USER=your_username
MYSQL_PASSWORD=your_mysql_password

# Redis
REDIS_PASSWORD=your_redis_password
SPRING_REDIS_HOST=your_redis_host
SPRING_REDIS_PORT=your_redis_port
SPRING_REDIS_PASSWORD=your_redis_password

SPRING_JPA_DDL_AUTO=update

# S3
S3_MAX_FILE_SIZE=ex) 20MB
S3_MAX_REQUEST_SIZE=ex) 50MB
S3_ACCESS_KEY=YOUR_ACCESS_KEY
S3_SECRET_KEY=YOUR_SECRET_KEY
S3_REGION=YOUR_REGION
S3_BUCKET_NAME=YOUR_BUCKET_NAME

KAKAO_BE_CALLBACK_URL=http://YOUR_BACKEND_DEPLOY_URL:YOUR_PORT/oauth/kakao/callback

# Encrypt
JWT_SECRET_KEY=YOUR_JWT_SECRET_KEY
AES_SECRET_KEY=YOUR_AES_SECRET_KEY

# API Key
KAKAO_REST_API_KEY=YOUR_KAKAO_KEY
WEATHER_REST_API_KEY=YOUR_OPEN_WEATHER_API_KEY
GEOCODING_REST_API_KEY=YOUR https://www.vworld.kr/dev/v4dv_geocoderguide2_s001.do API_KEY

```


### 3. 빌드 및 실행
프로젝트 루트 디렉터리에서 아래 명령어를 실행합니다.
```bash
# 프로젝트 빌드
./gradlew build

# 애플리케이션 실행
java -jar build/libs/star-0.0.1-SNAPSHOT.jar
```
애플리케이션은 `application-prod.properties`에 설정된 포트(기본 `8080`)에서 실행됩니다.
