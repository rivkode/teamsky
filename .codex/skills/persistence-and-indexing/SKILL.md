---
name: persistence-and-indexing
description: Use when designing database schema and repository access patterns aligned with real query use cases.
---

# Persistence And Indexing

## Goal

Design database structure and access patterns for both correctness and performance.

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

## Practical Tip

Even if DB is small, document index decisions in README.

## Done Criteria

- Schema supports use cases
- Repository methods match queries
- Index decisions are explainable