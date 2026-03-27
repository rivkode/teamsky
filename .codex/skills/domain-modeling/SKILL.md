---
name: domain-modeling
description: Use when designing domain entities and responsibilities for solving problems and tracking solve history in a learning platform.
---

# Domain Modeling

## Goal

Model the domain so behavior is expressed clearly, not just data structure.

## Core Concepts

- Chapter
- Question
- SolveAttempt (or SolveHistory)
- SolveAnswer
- SolveResult / Score

## Instructions

- Define entities and their responsibilities
- Decide where grading logic lives
- Decide how solve attempts aggregate answers
- Model relationships clearly

## Rules

- Avoid anemic domain models
- Avoid putting all logic in service layer
- Prefer single-direction relationships
- Use value objects when they improve clarity

## Design Guidance

- Question → validates correctness
- SolveAttempt → aggregates answers and calculates result
- Domain services → only when logic doesn’t belong to a single entity

## Done Criteria

- Entities have clear responsibilities
- Business rules are not centralized in one service
- Model supports both solving and history querying