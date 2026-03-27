---
name: api-delivery
description: Use when implementing APIs for problem solving and solve history with clean layered architecture and DTO separation.
---

# API Delivery

## Goal

Implement APIs with clear boundaries and maintainable structure.

## Target APIs

- Get chapter questions
- Submit solve answers
- Get solve history list
- Get solve history detail

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

## Error Handling

Cover:
- invalid chapter
- invalid question
- mismatched chapter-question
- duplicate or invalid submission
- unauthorized access

## Done Criteria

- APIs are complete and callable
- Layer separation is maintained
- DTOs are stable and explicit