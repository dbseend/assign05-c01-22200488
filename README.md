# 웹 서비스 개발 5주차 과제 - 영화 관리 REST API (Spring Boot Memory CRUD + 배포)

| 구분 | 링크 |
| :--- | :--- |
| GitHub (Organization) | https://github.com/2026-2-WebService/assign05-c01-22200488 |
| GitHub (Personal) | https://github.com/dbseend/assign05-c01-22200488 |
| 배포 URL (Render) | https://assign05-movie-api.onrender.com/api/movies |

> 표시된 **ToDo**는 직접 작성하거나 캡처를 넣어야 하는 부분입니다. 제출 전 모두 채우고 이 안내문은 삭제하세요.

---

## ① 프로젝트 소개

### 1) 프로젝트 주제
- **Spring Boot 기반 영화 정보 관리 REST API (Movie CRUD)**
- Java Collection(`List<Movie>`)을 메모리 저장소로 사용 (DB 없음)
- Controller - Service - Repository 계층 구조(Layered Architecture) 적용
- 4주차 과제를 기반으로 **입력 검증(400)**, **장르 필터**, **Docker + Render 배포**를 추가

---

### 2) 관리하는 데이터 (Entity: `Movie`)

| 필드명 | Java 타입 | 설명 | 검증 규칙 | 예시 |
| :--- | :--- | :--- | :--- | :--- |
| `id` | `Long` | 식별자 (자동 증가) | 서버 자동 생성 | `1` |
| `title` | `String` | 영화 제목 | 비어 있으면 안 됨 | `"Interstellar"` |
| `director` | `String` | 감독 | 비어 있으면 안 됨 | `"Christopher Nolan"` |
| `genre` | `String` | 장르 | 비어 있으면 안 됨 | `"SF"` |
| `releaseYear` | `Integer` | 개봉 연도 | 1888 ~ 2100 | `2014` |
| `rating` | `Double` | 평점 | 0.0 ~ 10.0 | `8.7` |
| `runningTime` | `Integer` | 상영 시간(분) | 1 이상 | `169` |

---

### 3) 프로젝트 구조
```text
HTTP Request → Controller → Service → Repository (Interface) → MemoryRepository (List) → JSON Response
```

#### 디렉토리 구조
```text
.
├── Dockerfile                                   # 멀티 스테이지 빌드 (Render 배포용)
├── .dockerignore
├── build.gradle
└── src/
    ├── main/
    │   ├── java/com/webservice/week05/
    │   │   ├── Week05MovieCrudApplication.java  # Spring Boot 실행 메인 클래스
    │   │   ├── controller/
    │   │   │   └── MovieController.java         # REST Controller, @Valid
    │   │   ├── domain/
    │   │   │   └── Movie.java                   # 도메인 Entity
    │   │   ├── dto/
    │   │   │   ├── MovieRequest.java            # 요청 DTO (record) + Bean Validation
    │   │   │   └── MovieResponse.java           # 응답 DTO (record)
    │   │   ├── exception/
    │   │   │   └── GlobalExceptionHandler.java  # 400 응답 처리
    │   │   ├── repository/
    │   │   │   ├── MovieRepository.java         # Repository Interface
    │   │   │   └── MemoryMovieRepository.java   # List 기반 메모리 저장소, ID 자동 생성
    │   │   └── service/
    │   │       └── MovieService.java            # 비즈니스 로직, 404, 장르 필터
    │   └── resources/
    │       └── application.properties           # server.port=${PORT:8080}
    └── test/java/com/webservice/week05/
        └── MovieApiTest.java                    # CRUD / 404 / 400 / 장르 필터 테스트
```

#### 로컬 실행
```bash
./gradlew bootRun     # http://localhost:8080
./gradlew test
```

---

### 4) API Endpoint

| HTTP Method | Endpoint | 기능 | 요청 Body | 응답 Status |
| :--- | :--- | :--- | :--- | :--- |
| **POST** | `/api/movies` | 영화 등록 | `MovieRequest` | **201 Created** (잘못된 입력 시 400) |
| **GET** | `/api/movies` | 전체 목록 조회 (`?genre=` 필터) | 없음 | **200 OK** |
| **GET** | `/api/movies/{id}` | 단건 조회 | 없음 | **200 OK** (미존재 시 404) |
| **PUT** | `/api/movies/{id}` | 영화 정보 수정 | `MovieRequest` | **200 OK** (400, 미존재 시 404) |
| **DELETE** | `/api/movies/{id}` | 영화 삭제 | 없음 | **204 No Content** (미존재 시 404) |

---

### 5) Request / Response JSON 예시

- **Request Body (`MovieRequest`)**
```json
{
  "title": "Interstellar",
  "director": "Christopher Nolan",
  "genre": "SF",
  "releaseYear": 2014,
  "rating": 8.7,
  "runningTime": 169
}
```

- **Response Body (`MovieResponse`, 201 Created)**
```json
{
  "id": 1,
  "title": "Interstellar",
  "director": "Christopher Nolan",
  "genre": "SF",
  "releaseYear": 2014,
  "rating": 8.7,
  "runningTime": 169
}
```

---

## ② 개발환경 및 Dependency

| 항목 | 내용 |
| :--- | :--- |
| IDE | IntelliJ IDEA Ultimate 2026.1 (Build #IU-261.26222.65) |
| JDK | 17 (Gradle toolchain) |
| Spring Boot | 3.5.5 |
| Build Tool | Gradle (Wrapper) |
| 데이터 저장 | `ArrayList<Movie>` (메모리) |
| 배포 환경 | Render (Docker, Free, Singapore) |

| Dependency | 용도 |
| :--- | :--- |
| `spring-boot-starter-web` | REST Controller, JSON 변환, 내장 Tomcat |
| `spring-boot-starter-validation` | `@NotBlank`, `@DecimalMin` 등 입력 검증 (STEP 5-A, 400 처리) |
| `spring-boot-starter-test` | MockMvc 기반 API 테스트 |

---

## ③ Solution 분석

`sb_book_crud_solution`의 `STUDY_GUIDE.md`를 기준으로 분석했고, 내 프로젝트(Movie)와 대응시켜 정리했다.

**Q1. 요청은 어떤 순서로 처리되는가?**
`POST /api/books` 요청은 `BookController.create()` → `BookService.create()` → `MemoryBookRepository.save()` 순서로 처리되고, 응답은 역순으로 반환된다. 내 프로젝트는 `MovieController.create()` → `MovieService.create()` → `MemoryMovieRepository.save()` 순서다.

**Q2. BookRequest, Book, BookResponse의 역할은?**
`BookRequest`는 클라이언트 입력, `Book`은 저장되는 도메인 객체, `BookResponse`는 클라이언트에 내려주는 출력 DTO다. 외부 형식과 내부 저장 모델을 분리한다.

**Q3. 새 데이터의 ID는 어디서 생성되는가?**
`MemoryBookRepository.save()`에서 `nextId++`로 순차 부여한다. 내 프로젝트에서는 `MemoryMovieRepository.save()`의 `++sequence`가 같은 역할이다.

**Q4. 존재하지 않는 ID는 어떻게 404가 되는가?**
`BookService.findById()`가 `repository.findById()`의 빈 `Optional`에 `orElseThrow()`를 걸어 `ResponseStatusException(HttpStatus.NOT_FOUND)`를 던진다. 내 프로젝트의 `MovieService.getOrThrow()`가 같은 방식이다.

**Q5. Domain은 어떻게 Response DTO로 변환되는가?**
`BookService.toResponse()`가 `Book`을 `BookResponse`로 변환하고, `findAll()`은 `.map(this::toResponse).toList()`를 쓴다. 내 프로젝트는 `MovieResponse.from()`으로 변환한다.

**Q6. Service는 왜 Repository 구현체가 아닌 인터페이스에 의존하는가?**
`BookService`는 `BookRepository` 타입으로 주입받아, 저장소 구현을 바꿔도 Service 코드를 수정하지 않는다. 내 프로젝트도 `MovieService`가 `MovieRepository`에만 의존한다.

---

## ④ 개발 과정 요약

단계마다 커밋하며 진행했다 (총 14커밋 이상, 커밋 메시지는 `feat:`, `test:`, `build:`, `docs:` 규칙 사용).

| 단계 | 내용 | 주요 커밋 |
| :--- | :--- | :--- |
| 1 | 프로젝트 생성 (Gradle, JDK 17, Web, Validation), `server.port=${PORT:8080}` | `chore: Spring Boot 프로젝트 생성` |
| 2 | `Movie` 도메인, `MovieRequest`/`MovieResponse` DTO (record + `from()`) | `feat: Movie 도메인 객체 추가`, `feat: ... DTO 추가` |
| 3 | `MovieRepository` 인터페이스, `MemoryMovieRepository` (List, ID 자동 생성) | `feat: MemoryMovieRepository 구현` |
| 4 | `MovieService`(404 처리), `MovieController`(CRUD, 201/204) | `feat: MovieService CRUD 및 404 처리` |
| 5 | 입력 검증(400), 장르 필터, `MovieApiTest`, Dockerfile, README | `feat: 잘못된 입력 400 처리`, `feat: 장르별 필터 조회`, `build: Dockerfile 추가` |

---

## ⑤ 기능 수정·확장

### A. 잘못된 입력 처리 (400)
- **이유**: 빈 제목, 범위를 벗어난 평점, 0분 이하 상영 시간 같은 값이 저장되는 것을 막기 위함
- **수정 파일**: `MovieRequest`(Bean Validation 어노테이션), `MovieController`(`@Valid`), `GlobalExceptionHandler`(400 응답 형식 통일)
- **처리 범위**: 검증 실패(`MethodArgumentNotValidException`), JSON 형식/타입 오류(`HttpMessageNotReadableException`) 모두 400
- **테스트 요청**: `POST /api/movies` 에 `title=""`, `rating=11`, `runningTime=0`
- **예상 결과**: 400 Bad Request + 필드별 오류 메시지
- **실제 결과 (배포 서버, 400)**:

```json
{"status":400,"error":"Bad Request","errors":{"runningTime":"runningTime은 1분 이상이어야 합니다.","title":"title은 비어 있을 수 없습니다.","rating":"rating은 10.0 이하여야 합니다."}}
```

> **ToDo**: 400 응답 curl 캡처 이미지 첨부

### B. 장르 필터 조회
- **이유**: 전체 목록에서 장르별로 좁혀 보기 위함
- **수정 파일**: `MovieController.findAll(@RequestParam(required = false) String genre)`, `MovieService.findAll(genre)` (대소문자 무시, 값이 없으면 전체 반환)
- **테스트 요청**: `GET /api/movies?genre=sf` (Interstellar(SF), Parasite(Thriller) 등록 상태)
- **예상 결과**: 장르가 SF인 영화만 조회
- **실제 결과 (배포 서버, 200)**:

```json
[{"id":1,"title":"Interstellar","director":"Christopher Nolan","genre":"SF","releaseYear":2014,"rating":8.7,"runningTime":169}]
```

> **ToDo**: 장르 필터 curl 캡처 이미지 첨부

---

## ⑥ 배포 과정 요약

### 1) 추가·수정한 파일
- `Dockerfile`: 멀티 스테이지 (JDK 17에서 `./gradlew clean build`(테스트 포함) → JRE 17에서 jar 실행)
- `.dockerignore`: 빌드 산출물, IDE 설정 제외
- `application.properties`: `server.port=${PORT:8080}` (Render가 주입하는 `PORT` 사용)

### 2) 배포 순서
1. 개인 GitHub 레포(Public) 생성 후 `git remote add deploy ...`, `git push -u deploy main`
2. Render에서 Web Service 생성: Runtime `Docker`, Branch `main`, Region Singapore, Plan Free
3. Render가 `Dockerfile`로 빌드(테스트 포함) 후 배포
4. 배포 URL로 CRUD, 404, 400, 장르 필터 호출 확인

> **ToDo**: Render 대시보드 배포 성공(Live) 화면 캡처 첨부

### 3) 문제와 해결
- 배포 중 오류는 없었다. 로컬 JDK 17에서 `./gradlew test` 통과 후 같은 Dockerfile로 배포해서 첫 배포에 성공했다.
- Free 플랜은 일정 시간 요청이 없으면 슬립하므로 첫 요청이 느릴 수 있다.
- 메모리 저장소라서 서버가 재시작되면 데이터가 초기화된다. (과제 범위상 의도된 동작)

### 4) 배포 URL 요청·응답 (curl 실제 테스트 결과)

Base URL: `https://assign05-movie-api.onrender.com`

| 요청 | 상태 | 응답 |
| :--- | :--- | :--- |
| `POST /api/movies` (Interstellar) | 201 | `{"id":1,"title":"Interstellar",...,"runningTime":169}` |
| `POST /api/movies` (Parasite) | 201 | `{"id":2,"title":"Parasite",...,"runningTime":132}` |
| `GET /api/movies` | 200 | 등록된 영화 배열 |
| `GET /api/movies?genre=sf` | 200 | SF 영화만 |
| `GET /api/movies/1` | 200 | 단건 |
| `PUT /api/movies/1` (rating 9.0) | 200 | 수정된 영화 |
| `DELETE /api/movies/2` | 204 | 본문 없음 |
| `GET /api/movies/999` | 404 | `{"status":404,"error":"Not Found","path":"/api/movies/999"}` |
| `POST /api/movies` (잘못된 입력) | 400 | 필드별 오류 메시지 |

#### ① 데이터 등록 (POST)
> **ToDo**: 캡처 이미지 첨부

#### ② 전체 목록 조회 (GET)
> **ToDo**: 캡처 이미지 첨부

#### ③ 단건 조회 (GET /{id})
> **ToDo**: 캡처 이미지 첨부

#### ④ 데이터 수정 (PUT /{id})
> **ToDo**: 캡처 이미지 첨부

#### ⑤ 데이터 삭제 (DELETE /{id}) 및 삭제된 ID 조회 시 404 확인
> **ToDo**: 캡처 이미지 첨부

#### ⑥ 잘못된 입력 400, 장르 필터
> **ToDo**: 캡처 이미지 첨부 (⑤-A, ⑤-B와 동일 캡처 사용 가능)

---

## 이번 과제 키워드

- Spring Boot REST API (Memory CRUD)
- Bean Validation (`@Valid`, `@NotBlank`, `@DecimalMin`)
- `@RestControllerAdvice` / `@ExceptionHandler`
- `@RequestParam` (장르 필터)
- Docker 멀티 스테이지 빌드
- Render 배포, `PORT` 환경변수

---

## 핵심 내용 정리

1. **입력 검증**: `MovieRequest`의 Bean Validation + `@Valid`로 잘못된 입력을 Controller 진입 시점에 걸러 400을 반환한다.
2. **전역 예외 처리**: `GlobalExceptionHandler`로 400 응답 형식을 한곳에서 통일한다. 404는 `ResponseStatusException`으로 처리한다.
3. **배포**: Dockerfile로 빌드·실행 환경을 고정하고, `PORT` 환경변수로 로컬(8080)과 Render를 같은 코드로 실행한다.

---

## ⑦ Weekly Report

### 1) Key Learning (3가지)
1. **계층 분리**: Controller는 HTTP, Service는 로직과 404, Repository는 저장만 맡아서 저장소 구현을 바꿔도 Service가 영향을 받지 않는다.
2. **입력 검증**: Bean Validation(`@Valid` + `GlobalExceptionHandler`)으로 검증을 한곳에서 처리하고 400 응답 형식을 통일할 수 있다.
3. **배포 환경 분리**: 멀티 스테이지 Dockerfile과 `PORT` 환경변수로 로컬과 Render에서 같은 코드를 그대로 실행할 수 있다.

> **ToDo**: 본인이 실제로 배운 내용으로 수정 (위 3가지는 초안)

---

### 2) Problem & Solution (구현 중 발생한 문제와 해결 방법 1가지)
- **문제점**: 테스트가 저장소의 ID 증가 값(`sequence`)을 공유해서, 테스트 실행 순서에 따라 `GET /api/movies/1` 같은 검증이 실패할 수 있었다.
- **해결 방법**: `@DirtiesContext(AFTER_EACH_TEST_METHOD)`로 테스트마다 컨텍스트를 초기화해 ID 순서 의존을 제거했다. (커밋 `test: 테스트마다 컨텍스트 초기화`)

> **ToDo**: 실제로 겪은 문제가 다르면 교체

---

### 3) Code Review (가장 중요하다고 생각한 코드 1개와 이유)

```java
@ExceptionHandler(MethodArgumentNotValidException.class)
public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException e) {
    Map<String, String> errors = new LinkedHashMap<>();
    e.getBindingResult().getFieldErrors()
            .forEach(fe -> errors.putIfAbsent(fe.getField(), fe.getDefaultMessage()));
    Map<String, Object> body = new LinkedHashMap<>();
    body.put("status", 400);
    body.put("error", "Bad Request");
    body.put("errors", errors);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
}
```
- **이유**: 검증 실패를 한곳에서 필드별 메시지가 담긴 400 응답으로 바꿔서, Controller에 검증 코드를 반복하지 않고 응답 형식을 통일하기 때문이다.

> **ToDo**: 본인이 가장 중요하다고 생각하는 코드로 교체 가능

---

### 4) AI Usage
> **ToDo**: 어떤 작업에 AI를 어떻게 활용했는지 직접 작성 (사용 도구, 활용한 단계, AI 결과를 어떻게 확인·수정했는지)

---

### 5) Reflection
> **ToDo**: 이번 과제를 하며 느낀 점, 다음에 해보고 싶은 것 작성 (예: DB(JPA) 연동으로 데이터 영속화)

---

### 6) 건의사항
> **ToDo**: 없으면 "없음"
