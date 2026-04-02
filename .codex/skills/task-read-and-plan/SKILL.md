---
name: task-read-and-plan
description: Use when starting a backend assignment to extract requirements, hidden constraints, domain concepts, API scope, and produce a concrete implementation plan before coding.
---

# Task Read And Plan

## Goal

Turn a short assignment brief into a structured implementation plan before writing code.

## Use When

- starting a new assignment or feature from a brief
- requirements are short but imply hidden domain or performance concerns
- the next step should be planning rather than coding

## Inputs

- assignment brief or ticket
- existing repo context if available
- explicit constraints such as stack, deadlines, and deliverables

## Deliverables

- functional requirements list
- hidden constraints and assumptions
- core domain concepts
- API candidates
- persistence outline
- performance risks
- test plan

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

## Non-Goals

- choosing concrete class/file names for implementation
- writing production code or tests
- finalizing low-level schema or API payload details unless required by the brief

## Output Format

- Requirements
- Domain concepts
- API list
- Persistence outline
- Performance risks
- Test plan

## Exit Criteria

- requirements are concrete enough that implementation can start without reopening product questions
- assumptions are explicit rather than hidden in the plan
- domain, API, persistence, performance, and test sections are all present
- the implementer does not need to make first-order design decisions before coding

## Handoff

- `spring-architecture` can choose structure from the plan
- `domain-modeling` can begin with named concepts and rules
- `api-delivery`, `persistence-and-indexing`, and `test-and-verify` can execute without rediscovering scope
