---
name: test-and-verify
description: Use when validating business logic, persistence, and API behavior with focused and meaningful tests.
---

# Test And Verify

## Goal

Verify correctness and prevent regression with minimal but effective tests.

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

## Execution Strategy

1. Run smallest test first
2. Expand only if needed
3. Verify persistence behavior

## Done Criteria

- Core logic is tested
- Edge cases are covered
- Query behavior validated

