---
name: query-and-performance
description: Use when implementing or reviewing read APIs to ensure efficient queries, pagination, and no N+1 issues.
---

# Query And Performance

## Goal

Ensure read operations scale and avoid common ORM pitfalls.

## Focus Areas

- Solve history list
- Solve history detail
- Chapter question retrieval

## Instructions

- Apply pagination to list APIs
- Separate list and detail queries
- Use projection for list responses
- Prevent N+1 issues
- Limit response size

## Query Strategy

List:
- id
- chapter info
- score
- correct count
- createdAt

Detail:
- include answers only when needed

## Rules

- Do not fetch full entity graphs unnecessarily
- Do not mix list and detail responsibilities
- Always define sorting (latest-first)

## Done Criteria

- List queries are lightweight
- Detail queries are separated
- Pagination is applied
- N+1 risk is addressed