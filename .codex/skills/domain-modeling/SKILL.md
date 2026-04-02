---
name: domain-modeling
description: Use when designing domain entities and responsibilities for solving problems and tracking solve history in a learning platform.
---

# Domain Modeling

## Goal

Model the domain so behavior is expressed clearly, not just data structure.

## Use When

- interpreting assignment requirements into domain language
- deciding where grading, solving state, and statistics rules should live
- preventing application services from becoming rule-heavy coordinators

## Inputs

- requirement brief
- existing code structure if present
- core business rules and edge cases
- target API/use case expectations

## Deliverables

- core domain concepts with 1-line responsibility each
- entity / value object / domain service split
- grading, solving, and statistics responsibility placement
- aggregate root candidates and why they are roots

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

## Non-Goals

- deciding persistence table structure in detail
- designing controller/request/response DTOs
- forcing DDD patterns where simple entities are sufficient

## Design Guidance

- Question → validates correctness
- SolveAttempt → aggregates answers and calculates result
- Domain services → only when logic doesn’t belong to a single entity

## Exit Criteria

- core concepts are reduced to a stable set rather than a long noun list
- each major business rule has an explicit home in entity, value object, or domain service
- the implementer does not need to decide where grading or solving rules belong
- the model supports both solve flow and solve history use cases

## Handoff

- `api-delivery` can define commands/results and DTOs using the chosen domain language
- `persistence-and-indexing` can map storage based on decided aggregates and relationships
- `submission-readme-and-review` can explain why responsibilities were placed this way
