---
name: persistence-and-indexing
description: Use when designing database schema and repository access patterns aligned with real query use cases.
---

# Persistence And Indexing

## Goal

Design database structure and access patterns for both correctness and performance.

## Use When

- defining schema from use cases rather than from entities alone
- deciding repository methods and storage boundaries
- choosing indexes based on concrete access patterns

## Inputs

- domain-modeling output
- query-and-performance output
- requirement brief
- current schema or seed data if present

## Deliverables

- table structure aligned to core use cases
- repository method candidates tied to actual queries
- key relationships and ownership decisions
- major index candidates with rationale
- notes on tradeoffs or intentionally deferred optimizations

## Instructions

- Define table structure for:
    - chapter
    - question
    - solve_attempt
    - solve_answer

- Map relationships clearly
- Design repository methods based on real queries

## Index Strategy

Consider indexes on:
- user_id
- chapter_id
- created_at
- (user_id, created_at)
- (user_id, chapter_id)

## Rules

- Design for access patterns, not just object relationships
- Avoid generic findAll usage
- Prefer targeted query methods

## Non-Goals

- deciding business rule ownership in the domain model
- defining public API request/response contracts
- optimizing every table/index before concrete query needs exist

## Practical Tip

Even if DB is small, document index decisions in README.

## Exit Criteria

- schema choices can be explained from write/read use cases, not only from object structure
- repository methods map directly to concrete query needs
- key indexes are documented with query-based reasoning
- the implementer does not need to decide table shape or first-order index direction during coding

## Handoff

- `query-and-performance` can validate that repositories support the intended read paths
- `submission-readme-and-review` can document schema and index rationale
- implementation can proceed without reopening storage design questions
