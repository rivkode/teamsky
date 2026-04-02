---
name: spring-architecture
description: Use for Java 21 Spring Boot assignments that must show sound architecture, DDD-oriented boundaries, efficient answer-rate and query handling, Swagger and README documentation, and Docker Compose based local startup.
---

# Spring Architecture

Use this skill when the assignment evaluates more than endpoint completion and expects architecture quality to be visible in the codebase.

## Goal

Guide implementation toward a reviewer-friendly Spring Boot structure on Java 21.

## Inputs

- requirement brief or plan
- target Java/Spring version assumptions
- existing module/package structure if present
- expected deliverables such as Swagger, README, and Docker Compose

## Deliverables

- architecture direction for module/package boundaries
- explicit boundary rules between API, application, domain, and infrastructure
- key performance considerations for answer-rate and read paths
- delivery checklist covering docs and local startup readiness

This skill is the architectural counterpart to the existing project skills:

- `task-read-and-plan` for requirement extraction
- `domain-modeling` for core domain design
- `api-delivery` for API implementation
- `persistence-and-indexing` for schema and index choices
- `query-and-performance` for read-path efficiency
- `test-and-verify` for validation
- `submission-readme-and-review` for final submission quality
- `assignment-guardrails` to avoid overengineering

## Use It For

- deciding whether multi-module is justified
- keeping package or module boundaries aligned with DDD
- making solve submission and history lookup flows testable
- checking answer-rate calculation and query performance
- ensuring Swagger, `README.md`, and Docker Compose are part of the deliverable

## Orchestration Role

- use this skill to set architectural direction before implementation details sprawl
- it should not replace `domain-modeling`, `api-delivery`, or `persistence-and-indexing`
- it should constrain those skills so they make compatible decisions

## Non-Goals

- implementing concrete APIs, repositories, or tests by itself
- replacing detailed domain or persistence design work
- justifying multi-module by default when package boundaries are sufficient

## Architecture Rules

- Prefer visible boundaries between domain, application, infrastructure, and API.
- Use multi-module only if it improves clarity enough to justify the extra structure.
- If the project stays single-module, mirror the same boundaries with packages.
- Keep controllers thin and do not let persistence concerns spread through the domain model.
- Keep use cases testable without booting unnecessary framework layers.

## Performance Rules

- Treat answer-rate calculation as a hot path.
- Avoid repeated in-memory scans when grouped queries or projections fit better.
- Separate list and detail read models when their payloads differ materially.
- Check indexing and query count for solve history retrieval.

## Delivery Rules

- Swagger must be accurate enough for manual reviewer testing.
- `README.md` must explain architecture, run steps, tests, and tradeoffs.
- `docker-compose.yml` should start the application and DB with explicit environment settings.

## Exit Criteria

- Java version and framework assumptions are explicit and consistent
- architecture can be explained briefly without hand-waving
- module/package boundaries are stable enough that implementation skills do not need to redefine them
- answer-rate and read-path risks have architectural owners, not just code-level fixes
- docs and local startup expectations are clear enough for final delivery

## Handoff

- `domain-modeling`, `api-delivery`, and `persistence-and-indexing` can proceed within stable boundaries
- `submission-readme-and-review` can reuse the architectural rationale directly
