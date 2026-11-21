# Repository Guidelines

## Project Structure & Module Organization

- `src/main/java/com/fzgs/personnel/`: controllers, services, repositories, models, DTOs, and shared infrastructure.
- `src/main/resources/db/migration/`: Flyway schema and demo data.
- `src/main/webapp/WEB-INF/jsp/`: JSP views and layout fragments.
- `src/test/java/`: JUnit service tests and real-MySQL integration tests.
- `docs/superpowers/`: approved design and implementation plan.

Keep generated output such as `target/` and local CodeGraph indexes out of version control.

## Build, Test, and Development Commands

```sh
docker compose up -d mysql  # Start the dedicated MySQL 8 container on port 3307.
mvn spring-boot:run        # Run the JSP application on port 8080.
mvn test                   # Run unit and integration tests.
mvn -B verify              # Run the complete quality gate used by CI.
mvn clean package          # Build target/personnel-management.war.
```

## Coding Style & Naming Conventions

- Use Java 17 conventions: 4-space indentation, `PascalCase` types, and `camelCase` methods/fields.
- Keep SQL in repositories, business rules in services, and request/view logic in controllers.
- Place arrays/collections in plurals; model database statuses with the existing enums.
- User-visible text is Chinese; code identifiers are English.
- Use `BigDecimal` for money and never render or log passwords.

## Testing Guidelines

Tests use JUnit 5, MockMvc, and Testcontainers with MySQL 8. Name service tests
`<Feature>ServiceTest` and page flows `<Feature>FlowTest` or `<Feature>PageTest`.
Add a focused regression test for every business-rule or security fix. Run
`mvn -B verify` before handoff.

## Commit & Pull Request Guidelines

Use Conventional Commits such as `feat: add employee search` or
`fix: validate department IDs`. Pull requests should summarize behavior,
verification, migrations/config changes, and include UI screenshots when visible
pages change. Ensure GitHub Actions is green before requesting review.

## Security & Configuration

The `admin`, `hr`, and `employee` accounts use the local-only password `123456`;
never reuse them outside the course demo. Provide database settings through
`DB_URL`, `DB_USERNAME`, and `DB_PASSWORD`. Never commit personal employee data,
production credentials, or stack traces containing secrets.

## CodeGraph

This repository maintains a local CodeGraph index for structural queries. Use it
for symbol definitions, callers, callees, impact, and focused task context. After
changing code, run `codegraph sync .`; initialize a fresh checkout with
`codegraph init -i .`. Do not commit `.codegraph/`.
