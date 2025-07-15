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

### 2. 설정 파일 구성
`src/main/resources/` 경로에 아래 설명에 따라 **4개의 `.properties` 파일**을 생성하고, 자신의 환경에 맞게 내용을 채워야 합니다.


#### `application-prod.properties`
- Spring 애플리케이션의 프로덕션 환경 설정을 담당합니다.
```properties
# 데이터베이스 연결 정보
spring.datasource.url=jdbc:mysql://your_host:your_port/star_db?useUnicode=true&characterEncoding=UTF-8
spring.datasource.username=your_db_username
spring.datasource.password=your_db_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

#redis
spring.data.redis.host=your_redis_host
spring.data.redis.port=your_redis_port
spring.data.redis.password=your_redis_password

# JPA 및 하이버네이트 설정 (프로덕션에서는 보통 validate 또는 none 사용)
spring.jpa.hibernate.ddl-auto=validate
```

#### `application-secret.properties`
- 데이터베이스 접속 정보, JWT 비밀 키 등 민감한 정보를 관리합니다.
```properties


kakao.rest-api-key=카카오 api 키

# JWT 토큰 생성에 사용될 비밀 키 (충분히 길고 복잡한 문자열 사용)
jwt.secret.key=your_super_secret_jwt_key_that_is_long_and_secure

# AES 암호화에 사용될 키 (카카오 액세스 토큰 암호화 등)
aes.secret.key=your_super_secret_aes_key_16_24_32_bytes
```

#### `application-aws.properties`
- AWS S3 연동에 필요한 자격 증명 및 리전 정보를 설정합니다.
```properties
# AWS Access Key
spring.cloud.aws.credentials.access-key=your_aws_access_key

# AWS Secret Key
spring.cloud.aws.credentials.secret-key=your_aws_secret_key

# S3 버킷 이름
spring.cloud.aws.s3.bucket=your_s3_bucket_name

# AWS 리전
spring.cloud.aws.region.static=your_region

spring.servlet.multipart.max-file-size=

spring.servlet.multipart.max-request-size=
```

#### `application-kakao-auth-prod.properties`
- 카카오 소셜 로그인 Redirect Uri를 설정합니다.
```properties

# 카카오 로그인 후 리다이렉트될 URI
kakao.redirect-uri=http://your-domain.com/auth/kakao/callback
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
