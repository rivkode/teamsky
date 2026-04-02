---
name: query-and-performance
description: Use when implementing or reviewing read APIs to ensure efficient queries, pagination, and no N+1 issues.
---

# Query And Performance

## Goal

Ensure read operations scale and avoid common ORM pitfalls.

## Use When

- designing or reviewing read APIs
- splitting list vs detail query responsibilities
- checking index needs for real access patterns

## Inputs

- use case list for each read path
- payload requirements for list and detail responses
- current repository/query structure
- expected sorting, pagination, and filtering behavior

## Deliverables

- read-path query strategy per use case
- list/detail split decision
- repository or projection method candidates
- major index candidates tied to concrete queries
- note of known performance risks or intentional tradeoffs

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

## Non-Goals

- redesigning the entire domain model
- inventing indexes without tying them to concrete access patterns
- adding pagination to flows where the assignment does not need it

## Exit Criteria

- each read use case has an explicit query approach
- list and detail are separated where payload shape differs materially
- pagination and sorting rules are fixed, not implied
- major N+1 risks and heavy fetch paths are either removed or explicitly accepted
- the implementer does not need to decide query shape or index direction during coding

## Handoff

- `persistence-and-indexing` can design schema/indexes from concrete access patterns
- `test-and-verify` can validate ordering, pagination, and query behavior
- `submission-readme-and-review` can explain performance considerations with evidence
