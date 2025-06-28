<h1 align="center">$\bf{\large{\color{#6580DD} DKU-VeTT \ Backend \ Server }}$</h1>

## 개발 환경
### Language 
![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![Python](https://img.shields.io/badge/python-3670A0?style=for-the-badge&logo=python&logoColor=ffdd54)
### Framework & Runtime
![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)
![FastAPI](https://img.shields.io/badge/FastAPI-005571?style=for-the-badge&logo=fastapi)
### Database
![MariaDB](https://img.shields.io/badge/MariaDB-003545?style=for-the-badge&logo=mariadb&logoColor=white)
![MongoDB](https://img.shields.io/badge/MongoDB-%234ea94b.svg?style=for-the-badge&logo=mongodb&logoColor=white)
![Redis](https://img.shields.io/badge/redis-%23DD0031.svg?style=for-the-badge&logo=redis&logoColor=white)
### Infra & Messaging
![Apache Kafka](https://img.shields.io/badge/Apache%20Kafka-000?style=for-the-badge&logo=apachekafka)
### LLM & AI Framework
![Langchain](https://img.shields.io/badge/langchain-1C3C3C?style=for-the-badge&logo=langchain&logoColor=white)
![PyTorch](https://img.shields.io/badge/PyTorch-%23EE4C2C.svg?style=for-the-badge&logo=PyTorch&logoColor=white)
### DevOps & CI/CD
![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![GitHub Actions](https://img.shields.io/badge/GitHub_Actions-2088FF?style=for-the-badge&logo=githubactions&logoColor=white)

<hr>

## Key Dependencies and Features

### 1. Microservice Architecture (MSA)
- 서비스 기능별로 모듈화된 독립 실행 유닛으로 구성
- 인증/회원, 채팅, 장소, AI 진단/채팅, 외부 API 관리 등의 도메인을 독립적으로 분리
- 각 서비스는 REST API, Kafka 메시지, gRPC 등으로 상호 통신

### 2. Transactional Outbox Pattern
- 데이터베이스 변경과 Kafka 메시지 발행을 하나의 트랜잭션 내에서 안정적으로 처리
- Outbox 테이블에 이벤트를 저장하고, Kafka 발행 실패에 대비한 재처리 로직 적용
- 서비스 간 데이터 정합성 유지 및 비동기 통신의 안정성 보장

### 3. Kafka 기반 비동기 메시지 브로커
- 회원 삭제/수정 등의 이벤트를 Kafka 메시지로 발행
- 이벤트 구독자는 Kafka를 통해 메시지를 받아 gRPC 기반 동기화 수행
- 서비스 간 직접 호출 없이 느슨한 결합 실현

### 4. WebSocket 기반 실시간 채팅
- 그룹 채팅 기능은 WebSocket을 활용하여 실시간 메시지 송수신 구현
- pub/sub 구조로 특정 채팅방 내 사용자 간 빠르고 효율적인 커뮤니케이션 지원
- 읽음 처리, 즐겨찾기, 채팅방 검색 등 다양한 부가 기능 탑재

### 5. AI Hub 기반 진단 및 대화형 모델 연동
- 사용자 입력 데이터를 기반으로 AI 진단 모델(Python 기반) 실행
- RAG 기법을 활용하여 사용자 맥락에 맞는 AI 응답 생성
- LLM과 연동된 AI 채팅방 기능을 통해 상담형 대화 UX 제공

<hr>

## 아키텍처
### 소프트웨어 아키텍처
![image](https://github.com/user-attachments/assets/81d167e3-78dc-4721-9af1-b5d097d0cbf4)

본 시스템은 사용자의 사진 진단 및 AI 기반 챗봇 기능을 제공하는 MSA반의 서비스 구조로 설계되어 있습니다. <br>
각 마이크로서비스는 역할에 따라 분리되어 있으며, 독립적인 데이터베이스와 서버 인스턴스를 통해 <br>
도메인별 책임과 데이터 독립성을 보장합니다. 서버 간 통신은 고성능의 gRPC 프로토콜을 기반으로 구성되어, <br>
빠르고 안정적인 서비스 호출을 목표로 합니다. 전체 시스템은 Eureka 기반의 서비스 디스커버리와 <br>
API 게이트웨이를 통해 유기적으로 연결되며, 확장성과 장애 격리에 유리한 구조를 갖추고 있습니다. <br>


| 서비스명               | 설명                                                     |
| ------------------ | ------------------------------------------------------ |
| **API 게이트웨이 서비스**  | 클라이언트와 모든 내부 서비스 간의 요청을 라우팅 및 인증 처리                    |
| **Eureka 서비스**     | 각 서비스의 등록과 디스커버리를 담당하는 중심 허브                           |
| **장소 서비스**         | 장소 관련 기능을 제공하며 `vett_place` 데이터베이스와 연동                 |
| **채팅 서비스**         | 사용자의 일반 대화 처리, `vett_chat` 데이터베이스와 연동                  |
| **인증 및 회원 관리 서비스** | 사용자 인증 및 권한 검증과 회원의 정보를 관리<br>`vett_member` 데이터베이스와 연동 |
| **AI 채팅 서비스**      | AI LLM 기반 대화 처리, `vett_llm` 데이터베이스와 연동                 |
| **AI 진단 서비스**      | 사용자의 사진 기반 진단 처리, `vett_diagnosis` 데이터베이스와 연동          |
| **외부 API 관리 서비스**  | 외부 시스템과의 인터페이스를 담당하는 게이트웨이 역할                          |
| **관리자 서버**         | 관리자 인터페이스 기능 제어                                        |

<br>

### 시스템 아키텍처
각 서비스 기능은 독립적인 서비스로 구성되어 있으며, 통신은 주로 REST API 및 Kafka, gRPC를 통해 이루어집니다. <br>
또한 서비스 전반은 Docker를 통해 컨테이너화되어 관리됩니다. <br>
![캡스톤 디자인 시스템 아키텍처 (1)](https://github.com/user-attachments/assets/e4f64607-ba67-4384-a3e6-2dcc526380a3)

<br>

### Transactional Outbox Pattern
MSA 환경에서 가장 중요한 요소는 서버 간 데이터 동기화입니다. 이를 위해 Transactional Outbox Pattern을 적용하여 <br>
데이터베이스 일관성과 이벤트 발행의 신뢰성을 동시에 보장할 수 있습니다. <br>

#### 패턴 개요
- Outbox 테이블에 데이터 변경과 관련된 이벤트 정보를 저장
- Kafka로 메시지를 안전하게 발행하며 트랜잭션의 원자성 유지
- 장애 발생 시에도 Outbox 테이블의 상태를 통해 후속 처리 가능

#### Outbox 예시
| 필드               | 값                                                     |
| ---------------- | ----------------------------------------------------- |
| `id`             | de06bc17-abe1-4a41-83b8-838a2cf437e8                  |
| `aggregate_type` | User                                                  |
| `event_type`     | UserDeleted                                           |
| `payload`        | `{ "id": "H-87D...", "name": "오규찬", "email": "..." }` |
| `timestamp`      | 2025-04-23 18:30:25.123                               |
| `status`         | READY\_TO\_PUBLISH                                    |

Outbox 상태는 아래와 같은 Enum으로 관리됩니다. <br>

```
public enum OutboxStatus {
  READY_TO_PUBLISH,
  PUBLISHED,
  MESSAGE_CONSUME,
  FAILED,
  PERMANENTLY_FAILED
}
```
#### 패턴 흐름도
![image](https://github.com/user-attachments/assets/0f4adeaf-9fb3-4453-ac69-4fb395289da5)

#### 1단계: 회원 정보 삭제 요청
- 인증 및 회원 관리 서비스에서 회원 탈퇴 요청이 들어옵니다.
- 해당 요청은 트랜잭션 내에서 처리됩니다.


#### 2단계: 트랜잭션 내부 이벤트 처리 (Outbox 기록)
| 시점              | 설명                                                                                                                               |
| --------------- | -------------------------------------------------------------------------------------------------------------------------------- |
| `Before_commit` | 트랜잭션 커밋 직전에 **Spring 이벤트 리스너**가 동작하여 Outbox 테이블에 <br> 이벤트(예: `UserDeleted`)를 **비동기적으로 저장**합니다.                                        |
| 저장 내용           | Outbox 테이블에는 `aggregate_type`, `event_type`, `payload`, `timestamp`, `status` 등이 <br> 기록됩니다. `status`는 초기값 `READY_TO_PUBLISH`로 설정됩니다. |


#### 3단계: 트랜잭션 내부 이벤트 처리 (Outbox 기록)
| 시점             | 설명                                                          |
| -------------- | ----------------------------------------------------------- |
| `After_commit` | 트랜잭션이 **성공적으로 커밋된 이후**, Spring의 이벤트 리스너가 Kafka로 메시지를 발행합니다. |
| 메시지 내용         | Outbox 테이블에 저장된 `payload` 데이터가 Kafka 메시지로 전송됩니다.            |
| 상태 변경          | 발행 성공 시, Outbox 테이블의 `status`가 `PUBLISHED`로 변경됩니다.          |


#### 4단계: Kafka 시스템 메시지 전달
- Kafka 시스템은 메시지를 수신하고, 이를 구독 중인 여러 서비스로 전달합니다.


#### 5단계: Kafka 시스템 메시지 전달
| 대상                     | 설명                                                                       |
| ---------------------- | ------------------------------------------------------------------------ |
| **내부 Kafka 리스너**       | 인증 서비스 내 Kafka 리스너가 발행 성공을 확인하고, <br> Outbox 상태를 `MESSAGE_CONSUME` 등으로 갱신합니다. |
| **외부 서비스** | Kafka 메시지를 수신한 뒤, 메시지의 회원 ID를 통해 <br> gRPC로 회원 정보를 조회하거나 삭제 작업을 수행합니다.        |
| **gRPC 서비스 호출**        | Kafka 메시지 내 포함된 ID를 사용해 인증 서비스의 gRPC API로 최신 정보를 요청합니다.                  |
| **데이터 동기화**            | 각 외부 서비스는 gRPC 응답을 기반으로 동기화 테이블을 업데이트 합니다.             |

<br>

#### Kafka 이벤트 발행 – Transactional Outbox Pattern 적용
아래 코드는 Spring의 @TransactionalEventListener를 활용하여 <br>
트랜잭션 커밋 전/후로 Outbox 이벤트를 안전하게 처리하고 Kafka에 발행하는 방식입니다. <br>

<br>

##### BEFORE_COMMIT
```Java
@TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
public void handleOutboxEvent(OutboxEvent event) {
    memberOutboxService.saveNewOutboxProcess(event);
}
```
- 트랜잭션 커밋 직전에 동작
- Outbox 테이블에 이벤트(event)를 저장하여 데이터와 이벤트 발행 내역을 함께 기록
- 데이터베이스 트랜잭션과 Outbox 동기화 보장

<br>

##### AFTER_COMMIT
```Java
@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
@Transactional(propagation = Propagation.REQUIRES_NEW)
@Retryable(
    retryFor = { KafkaSendException.class },
    maxAttempts = 3,
    backoff = @Backoff(delay = 2000),
    recover = "recover"
)
public void handleKafkaEvent(OutboxEvent event) {
    ...
}
```
- 트랜잭션 커밋 후에 실행되며, Kafka로 메시지를 발행
- @Transactional(REQUIRES_NEW)을 통해 별도 트랜잭션으로 메시지 전송
- @Retryable을 사용해 Kafka 발송 실패 시 최대 3회 재시도, 2초 간격
- 발행 성공 시 OutboxStatus.PUBLISHED로 상태 변경
- 실패 시 OutboxStatus.FAILED로 설정하고 KafkaSendException 발생

<br>

##### 실패 이벤트 처리
```Java
@Scheduled(fixedDelay = 60000 * 3) // 3 minute
  public void retryFailedMessages() {
    List<Outbox> failedEvents = memberOutboxService.getFailedOutboxEvents();
    for (Outbox outbox : failedEvents) {
        String topic = memberOutboxService.getMemberKafkaTopic(outbox.getEventType());
        String payload = outbox.getPayload();
        try {
            kafkaTemplate.send(topic,payload);
            log.info("Successfully retried Kafka message, eventId: {}", outbox.getId());
        } catch (Exception e) {
            log.error("Failed to retry Kafka message, eventId: {}", outbox.getId(), e);
        }
    }
}
```
- @Scheduled 애노테이션을 이용해 3분마다 주기적 실행
- OutboxStatus.FAILED 상태인 이벤트를 조회
- Kafka 전송을 재시도하고 성공 시 로그 기록
- 실패 시에도 로깅을 통해 추후 분석 가능

<hr>

## Component & API URI Collection
### Member Component
인증 및 권한 관리, 회원정보 관리를 담당하는 컴포넌트 <br>
![image](https://github.com/user-attachments/assets/281fb738-64b3-40c2-a834-98414108e8fc)

### Member Component API
| URI                                    | Method | 설명                |
| -------------------------------------- | ------ | ----------------- |
| `/auth/identity/sign-up`               | POST   | 사용자 회원가입          |
| `/auth/identity/sign-in`               | POST   | 사용자 로그인           |
| `/auth/identity/reissue`               | POST   | 사용자 접근 토큰 갱신      |
| `/auth/identity/social`                | POST   | 사용자 소셜 로그인        |
| `/auth/identity/is-duplicate/{userId}` | GET    | 사용자 아이디 중복체크      |
| `/auth/identity/find-id`               | POST   | 사용자 아이디 찾기        |
| `/auth/identity/verify-code/{userId}`  | POST   | 이메일 인증을 위한 코드 전송  |
| `/auth/identity/is-verify`             | POST   | 사용자의 인증 코드 유효성 검사 |
| `/auth/identity/password`              | PATCH  | 사용자 비밀번호 변경       |
| `/auth/api/user/{id}`                  | GET    | 사용자 정보 반환         |
| `/auth/api/user/logout`                | POST   | 사용자 로그아웃          |
| `/auth/api/user/{id}`                  | DELETE | 사용자 회원 탈퇴         |
| `/auth/api/user/{id}`                  | PATCH  | 사용자 회원정보 변경       |

<br>

### Chat Component
사용자의 채팅방, 채팅 이력, 채팅방 고정 등을 관리하는 컴포넌트 <br>
![image](https://github.com/user-attachments/assets/f4501859-3207-4e8d-8e4d-1dda88909773)

### Chat Component API
| URI                                             | Method    | 설명                   |
| ----------------------------------------------- | --------- | -------------------- |
| `/chat/room`                                    | POST      | 새로운 그룹 채팅방 생성        |
| `/chat/rooms`                                   | GET       | 모든 그룹 채팅방 반환         |
| `/chat/rooms/{memberId}`                        | GET       | 특정 사용자가 참여중인 채팅방 반환  |
| `/chat/room/{roomId}/{memberId}`                | POST      | 특정 사용자가 그룹 채팅방에 참여   |
| `/chat/room/{roomId}/{memberId}`                | DELETE    | 특정 사용자가 그룹 채팅방 참여 해지 |
| `/chat/room/is-participate/{roomId}/{memberId}` | GET       | 특정 사용자의 채팅방 참여 여부 확인 |
| `/chat/rooms/keyword/{keyword}`                 | GET       | 키워드로 채팅방 검색          |
| `/chat/pin/{roomId}/{memberId}`                 | GET       | 특정 채팅방 즐겨찾기 여부 확인    |
| `/chat/pin/{roomId}/{memberId}`                 | POST      | 채팅방 즐겨찾기 등록          |
| `/chat/pin/{pinId}`                             | DELETE    | 즐겨찾기 삭제              |
| `/chat/message/{roomId}`                        | GET       | 특정 채팅방의 메시지 조회       |
| `/chat/unread-clear/{roomId}/{memberId}`        | POST      | 읽지 않은 메시지 수 초기화      |
| `/chat/unread-count/{roomId}/{memberId}`        | GET       | 읽지 않은 메시지 수 반환       |
| `/chat/message`                                 | WebSocket | pub/sub 방식 메시지 송수신   |

<br>

### LLM Chat Component
사용자의 AI 채팅 이력을 관리하는 컴포넌트 <br>
![image](https://github.com/user-attachments/assets/4fa72840-be37-4fb9-9913-d9b319da02d0)

### LLM Chat Component API
| URI                                         | Method | 설명                  |
| ------------------------------------------- | ------ | ------------------- |
| `/py/llm/chat`                              | POST   | RAG 기반 LLM 모델 채팅 반환 |
| `/py/llm/diagnosis`                         | POST   | 진단 결과에 대한 LLM 응답 반환 |
| `/llm/chat-section/{memberId}/{title}`      | POST   | 사용자 AI 채팅방 개설       |
| `/llm/chat-section/{chatSectionId}`         | DELETE | AI 채팅방 삭제           |
| `/llm/chat-section/{chatSectionId}/{title}` | PATCH  | AI 채팅방 제목 변경        |
| `/llm/chat-sections/{memberId}`             | GET    | 사용자 AI 채팅방 리스트 반환   |
| `/llm/chat/{chatSectionId}`                 | GET    | AI 채팅방의 채팅 정보 조회    |
| `/llm/chat/{chatSectionId}`                 | POST   | AI 채팅방의 채팅 정보 저장    |

<br>

### Place Component
반려동물 동반 가능한 장소 데이터를 관리하는 컴포넌트 <br>
![image](https://github.com/user-attachments/assets/5bc2947e-5569-4716-9886-a4d0a77fc4b0)

### Place Component API
| URI                           | Method | 설명                       |
| ----------------------------- | ------ | ------------------------ |
| `/place/all`                  | GET    | 모든 장소 데이터 반환             |
| `/place/category`             | GET    | 카테고리 별 장소 데이터 반환         |
| `/place/categories`           | GET    | 전체 카테고리 종류 반환            |
| `/place/dist/{category}`      | POST   | 거리순으로 장소 정렬 후 반환         |
| `/place/open/{category}`      | GET    | 현재 운영중인 장소 리스트 (카테고리 필터) |
| `/place/open/dist/{category}` | POST   | 운영중인 장소 거리순 반환 (카테고리 필터) |
| `/place/search/{keyword}`     | GET    | 키워드로 장소 검색               |
| `/place/filter`               | POST   | 필터를 통한 장소 검색             |

<br>

### Diagnosis Component 
반려동물 사진을 통해 진단 서비스를 처리하는 컴포넌트 <br>
![image](https://github.com/user-attachments/assets/22c51131-9349-4bbd-8f92-7627dda8069c)

### Diagnosis Component API ( 개발 중 )
| URI                | Method | 설명            |
| ------------------ | ------ | ------------- |
| `/py/predict/skin` | POST   | 반려동물 피부 질환 진단 |
| `/py/predict/eye`  | POST   | 반려동물 안구 질환 진단 |

<br>

### Integration Component
전체 서비스 컴포넌트를 관리하고 클라이언트 요청 라우팅 및 외부 API 호출을 관리하는 컴포넌트 <br>
![image](https://github.com/user-attachments/assets/30aaa977-8fd1-432c-9ec2-ae9be5ae76a6)

### Integration Component API
| URI                                | Method | 설명              |
| ---------------------------------- | ------ | --------------- |
| `/proxy/api/address-to-coordinate` | POST   | 주소 → 좌표 변환      |
| `/proxy/api/route`                 | POST   | 목적지까지의 교통 정보 반환 |
