# teamsky

학습 플랫폼의 핵심 기능인 `단원별 문제 풀이`를 중심으로 구현한 Spring Boot 3.3 / Java 21 멀티 모듈 프로젝트입니다.

## 구현 범위

- 랜덤 문제 조회
- 문제 넘기기
- 문제 제출
- 풀었던 문제 상세 조회

## 빠른 실행

```bash
docker compose up --build
./gradlew test
```

## 문서

### 아키텍처

- [01. 아키텍처 개요](docs/architecture/01.Architecture-Overview.md)
- [02. 도메인 설계](docs/architecture/02.Domain-Design.md)
- [03. 시퀀스 다이어그램](docs/architecture/03.Sequence-Diagrams.md)
- [04. ERD / JPA 설계](docs/architecture/04.ERD-and-JPA.md)
- [05. API 설계](docs/architecture/05.API-Design.md)
- [06. 테스트 전략](docs/architecture/06.Test-Strategy.md)

### 보고서

- [01. 정답률 계산과 경쟁 상태 분석](docs/report/01.CorrectRate-Concurrency-Report.md)
- [02. 캐시 전략과 배치 재동기화](docs/report/02.Cache-and-Batch-Report.md)
- [03. 성능 테스트 보고서](docs/report/03.Load-Test-Report.md)

## 실행 정보

### 애플리케이션

- Swagger: `http://localhost:8080/swagger-ui.html`
- Health: `http://localhost:8080/actuator/health`
- Prometheus: `http://localhost:8080/actuator/prometheus`

### 성능 테스트 환경

```bash
docker compose -f docker-compose.yml -f docker-compose.perf.yml up --build -d
```

```bash
docker compose -f docker-compose.yml -f docker-compose.perf.yml run --rm -e TEST_ID=random-cold-001 k6 run -o experimental-prometheus-rw /work/perf/k6/random.js
docker compose -f docker-compose.yml -f docker-compose.perf.yml run --rm -e TEST_ID=submit-hot-1002-001 -e HOT_PROBLEM_ID=1002 k6 run -o experimental-prometheus-rw /work/perf/k6/submit.js
docker compose -f docker-compose.yml -f docker-compose.perf.yml run --rm -e TEST_ID=detail-warm-001 -e DETAIL_USER_ID=1 -e DETAIL_PROBLEM_ID=1003 k6 run -o experimental-prometheus-rw /work/perf/k6/detail.js
docker compose -f docker-compose.yml -f docker-compose.perf.yml run --rm -e TEST_ID=mixed-001 -e HOT_PROBLEM_ID=1002 -e DETAIL_USER_ID=1 -e DETAIL_PROBLEM_ID=1003 k6 run -o experimental-prometheus-rw /work/perf/k6/mixed.js
```

- Grafana: `http://localhost:3000`
- Prometheus: `http://localhost:9090`
- 성능 테스트 가이드: [`perf/RESULTS.md`](/Users/jonghun/dev/project/java/teamsky/perf/RESULTS.md)

#### k6 Docker 실행 명령


## 핵심 설계 포인트

- 정답률은 `problem_statistics`를 기준 저장소로 두고 Redis 캐시를 우선 조회합니다.
- `problem_user_statistics`로 `distinct user` 기준 집계를 보장합니다.
- 제출 시 통계 갱신은 동기 처리하고, Redis write-through를 사용합니다.
- 별도 배치는 Redis 재동기화와 복구를 위한 보조 기능으로 동작합니다.
