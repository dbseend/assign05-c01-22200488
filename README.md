# 웹 서비스 개발 5주차 과제 - 영화 관리 REST API (Spring Boot Memory CRUD + 배포)

- GitHub (Organization): https://github.com/2026-2-WebService/assign05-c01-22200488
- GitHub (Personal): TODO
- 배포 URL (Render): TODO

## ① 프로젝트 소개

### 주제와 관리하는 데이터 (`Movie`)

| 필드 | 타입 | 검증 규칙 |
| :--- | :--- | :--- |
| id | Long | 서버 자동 생성 |
| title | String | 비어 있으면 안 됨 |
| director | String | 비어 있으면 안 됨 |
| genre | String | 비어 있으면 안 됨 |
| releaseYear | Integer | 1888 ~ 2100 |
| rating | Double | 0.0 ~ 10.0 |
| runningTime | Integer | 1 이상 (분) |

### 프로젝트 구조

```text
Controller → Service → MovieRepository(interface) → MemoryMovieRepository → List<Movie>
```

```text
src/main/java/com/webservice/week05/
├── Week05MovieCrudApplication.java
├── controller/MovieController.java          # REST 엔드포인트, @Valid
├── service/MovieService.java                # 비즈니스 로직, 404, 장르 필터
├── repository/MovieRepository.java          # 저장소 인터페이스
├── repository/MemoryMovieRepository.java    # List 기반 메모리 저장소, ID 자동 생성
├── domain/Movie.java
├── dto/MovieRequest.java, MovieResponse.java
└── exception/GlobalExceptionHandler.java    # 400 응답
```

### 로컬 실행

```bash
./gradlew bootRun     # http://localhost:8080
./gradlew test
```

### API Endpoint

| Method | URL | 기능 | 성공 | 실패 |
| :--- | :--- | :--- | :--- | :--- |
| POST | /api/movies | 등록 | 201 | 400 |
| GET | /api/movies | 전체 조회 (`?genre=` 필터) | 200 | - |
| GET | /api/movies/{id} | 단건 조회 | 200 | 404 |
| PUT | /api/movies/{id} | 수정 | 200 | 400, 404 |
| DELETE | /api/movies/{id} | 삭제 | 204 | 404 |

요청 JSON:

```json
{"title":"Interstellar","director":"Christopher Nolan","genre":"SF","releaseYear":2014,"rating":8.7,"runningTime":169}
```

응답 JSON (201):

```json
{"id":1,"title":"Interstellar","director":"Christopher Nolan","genre":"SF","releaseYear":2014,"rating":8.7,"runningTime":169}
```

## ② 개발환경 및 Dependency

| 항목 | 내용 |
| :--- | :--- |
| IDE | IntelliJ IDEA (TODO: 버전) |
| JDK | 17 (Gradle toolchain) |
| Spring Boot | 3.5.5 |
| Build Tool | Gradle (Wrapper 9.7.1) |
| 데이터 저장 | `ArrayList<Movie>` |
| 배포 환경 | Render (Docker) |

- `spring-boot-starter-web`: REST Controller, JSON 변환, 내장 Tomcat
- `spring-boot-starter-validation`: `@NotBlank`, `@DecimalMin` 등 입력 검증 (STEP 5-A, 잘못된 입력 400 처리)

## ③ Solution 분석

(sb_book_crud_solution `STUDY_GUIDE.md` 기준)

**Q1. 요청은 어떤 순서로 처리되는가?**
`POST /api/books` 요청은 `BookController.create()` → `BookService.create()` → `MemoryBookRepository.save()` 순서로 처리되고, 응답은 역순으로 반환된다.

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

## ④ 개발 과정 요약

TODO (5단계: 프로젝트 생성 → Domain/DTO → Repository → Service/Controller → 검증·필터·테스트·Docker)

## ⑤ 기능 수정·확장

### A. 잘못된 입력 처리 (400)
- 이유: 빈 제목, 범위를 벗어난 평점 등이 저장되는 것을 막기 위함
- 수정: `MovieRequest`(Bean Validation 어노테이션), `MovieController`(`@Valid`), `GlobalExceptionHandler.handleValidation()`
- 테스트 요청/예상/실제 결과: TODO

### B. 장르 필터 조회
- 이유: 전체 목록에서 장르별로 좁혀 보기 위함
- 수정: `MovieController.findAll(genre)`, `MovieService.findAll(genre)` (대소문자 무시)
- 요청 `GET /api/movies?genre=sf` / 예상: 장르가 SF인 영화만 / 실제 결과: TODO

## ⑥ 배포 과정 요약

- 추가·수정 파일: `Dockerfile`(멀티 스테이지: JDK17 빌드 + 테스트 → JRE17 실행), `.dockerignore`, `application.properties`의 `server.port=${PORT:8080}`
- 배포 순서 / 문제와 해결 / 배포 URL 요청·응답: TODO

## ⑦ Weekly Report

TODO (Key Learning 3 / Problem & Solution / Code Review / AI Usage / Reflection)
