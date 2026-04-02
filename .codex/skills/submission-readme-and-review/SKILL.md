---
name: submission-readme-and-review
description: Use when preparing final assignment submission to clearly explain design, decisions, performance, and test coverage.
---

# Submission README And Review

## Goal

Make the solution easy to evaluate and understand.

## Use When

- preparing the final assignment or take-home submission
- README must explain not just features but design reasoning
- an evaluator needs to quickly understand tradeoffs and quality signals

## Inputs

- final or near-final implementation
- architecture and domain decisions
- performance and index considerations
- test coverage and local run steps

## Deliverables

- concise reviewer-facing README or equivalent docs
- architecture/design explanation in Korean
- performance and index rationale
- test coverage summary
- run instructions and assumptions

## Include

1. Assignment summary
2. Assumptions
3. Domain model
4. API summary
5. Design decisions
6. Performance considerations
7. Index strategy
8. Test coverage
9. Run instructions

## Emphasis

- Why domain model is structured this way
- Why list/detail separation exists
- How performance was considered
- What tradeoffs were made

## Review Checklist

- DTO separation
- pagination applied
- no N+1 risk
- error handling present
- tests included

## Exit Criteria

- a technical reviewer can understand the architecture and tradeoffs in a few minutes
- run steps, tests, and key design decisions are all present
- README tone is natural Korean and does not read like translated boilerplate
- the implementer does not need to decide what is important enough to document

## Handoff

- final submission is reviewer-ready
- interview or review discussion can reuse the documented decisions directly

## Rules

- README must be written in Korean
- Use clear and natural Korean (avoid awkward translation tone)
- Keep technical terms (API, DTO, index, pagination) in English if needed
- Do not mix Korean and English inconsistently

## Non-Goals

- turning README into a full design novel
- documenting every internal class or low-level detail
- hiding tradeoffs to make the solution look artificially perfect

## Writing Style

- Explain as if writing for a technical reviewer
- Be concise but clear
- Prefer structured sections over long paragraphs

## Tone

- Focus on design reasoning, not just feature description
- Highlight why decisions were made
