---
name: task-read-and-plan
description: Use when starting a backend assignment to extract requirements, hidden constraints, domain concepts, API scope, and produce a concrete implementation plan before coding.
---

# Task Read And Plan

## Goal

Turn a short assignment brief into a structured implementation plan before writing code.

## Instructions

1. Extract functional requirements
2. Identify hidden constraints (retries, validation, ordering, authorization)
3. Define core domain concepts
4. Propose API endpoints
5. Outline persistence strategy
6. Identify performance risks
7. Define a test plan

## Rules

- Do not start coding
- Do not assume simple CRUD
- Separate write use cases from read use cases
- Explicitly state assumptions for unclear requirements

## Output Format

- Requirements
- Domain concepts
- API list
- Persistence outline
- Performance risks
- Test plan

## Done Criteria

- Clear implementation plan exists
- Core domain is identified
- Performance and edge cases are considered