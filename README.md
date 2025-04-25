# SPOT Server Ver.2 Repository입니다. 



1. [Intro](#intro)
2. [Architecture](#architecture)
3. [Package Structure](#package-structure)
4. [Assigned Tasks](#assigned-tasks)

---

### Intro
![image](https://github.com/user-attachments/assets/27badc56-59cf-4201-a79b-beb727c4d0a3)
![image](https://github.com/user-attachments/assets/9557ea21-1aa3-460b-870e-4aae7fa93533)


![image](https://github.com/user-attachments/assets/46545c0a-ac22-47d9-b075-0ca8a193eb2b)

### 📂 스팟이 제공하는 기능
**`1️⃣ 관심사/지역별 스터디 제공`**
스팟 유저의 스터디 관심사와,스터디를 진행하고자 하는 지역별로 나누어
스터디 라인업을 쉽게 볼 수 있도록 제공해요!

**`2️⃣ 내 스터디 관리`**
출석체크/일정캘린더/투두리스트/투표/게시판 및 공지 기능들로
내 스터디를 체계적으로 관리할 수 있도록 도와요 !

**`3️⃣ 스터디 메이킹/신청`**
내가 하고 싶은 스터디를 신청 메시지와 함께 신청하거나,
내가 하고 싶은 스터디를 만들 수 있어요 !

**`4️⃣ 정보 공유 커뮤니티`**
다양한 정보를 교류하며 인사이트를 얻고,
스팟 유저들과 여러 이야기를 나눌 수 있어요 !

---

### Architecture
<img width="1058" alt="스크린샷 2024-11-16 오후 9 52 04" src="https://github.com/user-attachments/assets/d9a970f6-6297-4732-b2ab-f4225822073c">


---

### Package Structure

```
  com.example.spot
├── api
│   ├── code.status
│   └── exception.handler
├── config
├── domain
│   ├── auth
│   ├── common
│   ├── enums
│   ├── mapping
│   └── study
├── repository
│   ├── querydsl.impl
│   └── verification
├── scheduler
├── security
│   ├── filters
│   └── utils
├── service
│   ├── auth
│   ├── member
│   ├── memberstudy
│   ├── message
│   ├── notification
│   ├── post
│   ├── s3
│   ├── study
│   └── studypost
├── validation
│   ├── annotation
│   └── validator
├── web
│   ├── controller
│   └── dto
│       ├── member
│       │   ├── kakao
│       │   └── naver
│       ├── memberstudy
│       │   ├── request.toDo
│       │   └── response
│       ├── notification
│       ├── post
│       ├── search
│       ├── study
│       │   ├── request
│       │   └── response
│       ├── token
│       └── util.response


```
### Assigned Tasks

| 기능                  | Server 담당자 |
|-----------------------|----------|
| 회원 기능            | [@msk226](https://github.com/msk226)  |
| 카카오 소셜 로그인 기능 | @msk226  |
| 스터디 조회 기능     | @msk226  |
| 모집중인 스터디      | @msk226  |
| 스터디 찜하기        | @msk226  |
| 스터디 상세 정보     | @msk226  |
| 알림 관련 기능       | @msk226  |
| 투두 리스트 기능     | @msk226  |
| 스터디 일정          | [@dvlp-sy](https://github.com/dvlp-sy)  |
| 스터디 게시글        | @dvlp-sy |
| 스터디 출석체크      | @dvlp-sy |
| 일반 로그인 관련 기능 | @dvlp-sy |
| 네이버 소셜 로그인 기능 | @dvlp-sy |
| 스터디 갤러리        | @dvlp-sy |
| 스터디 생성/참여/신청 | @dvlp-sy |
| 진행중인 스터디      | @dvlp-sy |
| 구글 소셜 로그인 기능 | [@FromKyoung](https://github.com/FromKyoung)          |
| 게시판 기능          | @FromKyoung         |
| 게시판 메인 페이지   | @FromKyoung          |
| 게시글 댓글 기능     | @FromKyoung          |



