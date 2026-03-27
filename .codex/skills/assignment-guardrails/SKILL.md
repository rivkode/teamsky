---
name: assignment-guardrails
description: Use when implementing assignment to prevent overengineering and keep solution practical and evaluation-friendly.
---

# Assignment Guardrails

## Goal

Prevent overengineering and keep the solution aligned with assignment expectations.

## Rules

- Do not introduce unnecessary abstractions
- Avoid excessive design patterns
- Do not overuse interfaces or generics
- Keep class count reasonable
- Prefer clarity to cleverness

## Anti-Patterns

- giant service with all logic ❌
- overly deep package structure ❌
- unnecessary DDD layers ❌
- premature optimization ❌

## Balance

- good design > complex design
- readable code > abstract code
- practical solution > “perfect” architecture

## Done Criteria

- solution is understandable within minutes
- no unnecessary complexity
- design decisions are explainable