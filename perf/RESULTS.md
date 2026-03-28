# Performance Test Guide

## 목표

- 현재 정답률 구조의 baseline 성능 측정
- Redis cache hit / miss 차이 확인
- submit 시 통계 갱신 병목 확인
- 개선 전/후 비교 보고서에 사용할 공통 포맷 확보

## 실행 환경

1. 기본 서비스 실행

```bash
docker compose up --build -d
```

2. 관측 서비스 실행

```bash
docker compose -f docker-compose.yml -f docker-compose.perf.yml up -d
```

3. Grafana / Prometheus 접속

- Grafana: `http://localhost:3000`
- Prometheus: `http://localhost:9090`

## k6 실행

Prometheus remote write로 보낼 때:

```bash
K6_PROMETHEUS_RW_SERVER_URL=http://localhost:9090/api/v1/write \
TEST_ID=random-cold-001 \
k6 run -o experimental-prometheus-rw perf/k6/random.js
```

Docker Compose의 `k6` 컨테이너로도 동일하게 실행할 수 있습니다.

```bash
docker compose -f docker-compose.yml -f docker-compose.perf.yml run --rm \
  -e TEST_ID=random-cold-001 \
  k6 run -o experimental-prometheus-rw /work/perf/k6/random.js
```

```bash
K6_PROMETHEUS_RW_SERVER_URL=http://localhost:9090/api/v1/write \
TEST_ID=submit-hot-001 \
HOT_PROBLEM_ID=1002 \
k6 run -o experimental-prometheus-rw perf/k6/submit.js
```

```bash
docker compose -f docker-compose.yml -f docker-compose.perf.yml run --rm \
  -e TEST_ID=submit-hot-001 \
  -e HOT_PROBLEM_ID=1002 \
  k6 run -o experimental-prometheus-rw /work/perf/k6/submit.js
```

```bash
K6_PROMETHEUS_RW_SERVER_URL=http://localhost:9090/api/v1/write \
TEST_ID=detail-warm-001 \
DETAIL_USER_ID=1 \
DETAIL_PROBLEM_ID=1003 \
k6 run -o experimental-prometheus-rw perf/k6/detail.js
```

```bash
docker compose -f docker-compose.yml -f docker-compose.perf.yml run --rm \
  -e TEST_ID=detail-warm-001 \
  -e DETAIL_USER_ID=1 \
  -e DETAIL_PROBLEM_ID=1003 \
  k6 run -o experimental-prometheus-rw /work/perf/k6/detail.js
```

```bash
K6_PROMETHEUS_RW_SERVER_URL=http://localhost:9090/api/v1/write \
TEST_ID=mixed-001 \
k6 run -o experimental-prometheus-rw perf/k6/mixed.js
```

```bash
docker compose -f docker-compose.yml -f docker-compose.perf.yml run --rm \
  -e TEST_ID=mixed-001 \
  k6 run -o experimental-prometheus-rw /work/perf/k6/mixed.js
```

## 테스트 실행 규칙

### 1. Cold Cache

- 목적: Redis miss + DB fallback 비용 확인
- 절차:
  1. `docker compose restart redis`
  2. 앱 로그에서 Redis 재연결 확인
  3. `TEST_ID=random-cold-*` 또는 `detail-cold-*`로 실행

### 2. Warm Cache

- 목적: 캐시 hit 상태 응답시간 확인
- 절차:
  1. 같은 스크립트를 1회 짧게 워밍업 실행
  2. 즉시 같은 조건으로 다시 본 실행
  3. `TEST_ID=random-warm-*`, `detail-warm-*`

### 3. Hot Problem

- 목적: 동일 `problemId` submit 집중 시 병목 확인
- 절차:
  1. `HOT_PROBLEM_ID`를 하나로 고정
  2. `submit.js` 실행
  3. `TEST_ID=submit-hot-*`

### 4. Mixed Load

- 목적: 읽기/쓰기 혼합 시 전체 성능 확인
- 절차:
  1. `mixed.js` 실행
  2. `TEST_ID=mixed-*`

## 결과 수집 포맷

각 테스트 run마다 아래를 기록합니다.

- `testid`
- scenario
- cache condition: cold / warm
- target endpoint
- p50
- p95
- p99
- throughput
- error rate
- dropped iterations
- Grafana 스크린샷

## 보고서 예시 표

| testid | scenario | endpoint | cache | p50 | p95 | p99 | throughput | error rate | note |
|---|---|---|---|---:|---:|---:|---:|---:|---|
| random-cold-001 | baseline | random | cold | - | - | - | - | - | Redis miss |
| random-warm-001 | baseline | random | warm | - | - | - | - | - | Redis hit |
| submit-hot-001 | hot_problem | submit | n/a | - | - | - | - | - | same problemId |
| mixed-001 | mixed_load | mixed | mixed | - | - | - | - | - | read/write mix |

## Grafana에서 확인할 항목

- `http_req_duration` p50 / p95 / p99
- `http_req_failed`
- `http_reqs`
- `iterations`
- `vus`
- endpoint tag 기준 분리
- `testid` 기준 비교

## 주의사항

- MySQL/Redis 데이터 상태를 매 테스트마다 동일하게 맞춰야 합니다.
- submit 테스트는 통계를 변경하므로, 비교 전 `docker compose down -v` 후 재기동이 가장 안전합니다.
- cold/warm 비교는 같은 시나리오와 같은 VU 설정으로 반복해야 의미가 있습니다.
