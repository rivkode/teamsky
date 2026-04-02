---
name: test-and-verify
description: Use when validating business logic, persistence, and API behavior with focused and meaningful tests.
---

# Test And Verify

## Goal

Verify correctness and prevent regression with minimal but effective tests.

## Use When

- core business logic has been implemented
- query behavior or persistence correctness must be confirmed
- API behavior needs focused regression coverage

## Inputs

- implemented use cases or target code paths
- expected success/failure scenarios
- existing test structure and conventions
- known risks from planning or architecture review

## Deliverables

- targeted tests covering core business behavior
- failure-case tests for invalid input or invalid state
- persistence/query verification where behavior depends on storage
- clear pass/fail evidence for the intended acceptance scenarios

## Test Scope

1. Domain / Service tests
2. Integration tests
3. API tests (optional)

## Must Cover

- solve submission success
- grading correctness
- invalid input cases
- history list retrieval
- ordering and pagination
- history detail retrieval

## Rules

- Test one behavior per test
- Cover failure cases, not only success
- Prefer domain-level testing for rules

## Non-Goals

- maximizing test count for its own sake
- covering framework boilerplate with low-value tests
- replacing architecture or design review with tests alone

## Execution Strategy

1. Run smallest test first
2. Expand only if needed
3. Verify persistence behavior

## Exit Criteria

- each critical use case has at least one success path and one meaningful failure path covered
- business rules are validated at the lowest sensible layer
- persistence-sensitive behavior is tested where mocking would hide risk
- the implementer does not need to guess which scenarios are considered "enough" to ship

## Handoff

- `submission-readme-and-review` can summarize coverage and residual risks
- final reviewers can see explicit evidence for correctness, not just implementation intent
