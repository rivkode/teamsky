# Skills Guide

이 디렉터리의 skill은 Codex가 백엔드 과제/프로젝트를 반복 가능한 방식으로 수행하도록 돕는 작업 단위 가이드입니다.

핵심 원칙:

- 각 skill은 **언제 쓰는지** (`Use When`)가 명확해야 합니다.
- 각 skill은 **무엇을 남기면 끝나는지** (`Deliverables`, `Exit Criteria`)가 명확해야 합니다.
- 각 skill은 **무엇을 하지 않는지** (`Non-Goals`)가 명확해야 합니다.
- 각 skill은 다음 단계에 무엇을 넘기는지 (`Handoff`)가 명확해야 합니다.

## Recommended Sequence

일반적인 과제/기능 구현 흐름은 아래 순서를 기본값으로 둡니다.

1. `task-read-and-plan`
   - 요구사항, 숨은 제약, 핵심 리스크를 정리
2. `spring-architecture`
   - 상위 구조와 boundary를 고정
3. `domain-modeling`
   - domain concept와 책임 위치를 결정
4. `query-and-performance`
   - read path와 성능 리스크를 먼저 정리
5. `persistence-and-indexing`
   - schema, repository, index 방향 결정
6. `api-delivery`
   - request/response DTO와 endpoint 구현
7. `test-and-verify`
   - 핵심 시나리오와 회귀 리스크 검증
8. `submission-readme-and-review`
   - reviewer-facing 설명과 제출 품질 정리
9. `assignment-guardrails`
   - 전 과정에서 상시 적용하는 제약 조건

## How To Use

- 설계가 먼저 필요한 문제면 `task-read-and-plan`부터 시작합니다.
- skill은 가능한 한 한 번에 하나의 핵심 결정을 끝내는 데 집중합니다.
- 한 skill이 끝나면 `Handoff`에 따라 다음 skill로 넘깁니다.
- skill이 요구하지 않는 영역은 `Non-Goals`에 따라 억지로 건드리지 않습니다.

## Success Criteria For The Skill Set

- implementer가 다음 단계에서 다시 큰 결정을 하지 않아도 된다
- skill 간 경계가 겹치지 않는다
- 과제마다 같은 품질 기준을 반복 적용할 수 있다
- AI가 너무 일찍 멈추거나 불필요하게 과하게 작업하지 않는다
