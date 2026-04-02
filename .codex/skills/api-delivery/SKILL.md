---
name: api-delivery
description: Use when implementing APIs for problem solving and solve history with clean layered architecture and DTO separation.
---

# API Delivery

## Goal

Implement APIs with clear boundaries and maintainable structure.

## Use When

- adding or refactoring public endpoints
- defining request/response DTOs
- connecting controller -> application -> domain flow

## Inputs

- requirement brief
- domain-modeling output
- existing API conventions and error policy

## Deliverables

- endpoint list with method and path
- request/response DTOs
- controller methods wired to application layer
- exception-to-HTTP mapping for major failure cases
- at least minimal controller test coverage for success and failure flows

## Instructions

1. Define request/response DTOs
2. Implement controllers (thin)
3. Route through application/facade layer
4. Delegate business logic to domain
5. Persist via repository
6. Return DTO only

## Rules

- Controllers must remain thin
- Do not expose entities directly
- Separate list vs detail responses
- Explicit DTO fields only

## Non-Goals

- moving business rules into controllers
- designing storage/index strategy in detail
- using API implementation to compensate for unresolved domain decisions

## Error Handling

Cover:
- invalid chapter
- invalid question
- mismatched chapter-question
- duplicate or invalid submission
- unauthorized access

## Exit Criteria

- each endpoint has a fixed request/response shape
- controllers do not contain domain or persistence logic
- entities are not exposed directly to API consumers
- at least one success case and one failure case per endpoint can be verified by tests
- the implementer does not need to make additional API contract decisions

## Handoff

- `test-and-verify` can extend controller/service tests from a stable contract
- `submission-readme-and-review` can document the API without guessing response shapes
- `spring-architecture` can validate that boundaries remain thin and explainable
