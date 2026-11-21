# Repository Guidelines

## Project Status

This repository is at its initial baseline: `main` has no commits, and there are
currently no application sources, tests, build files, or assets. Inspect the
worktree before making assumptions about the technology stack. Update this guide
when a toolchain is introduced.

## Project Structure & Module Organization

At present, `AGENTS.md` is the only project file; `.git/` contains repository
metadata. When adding code, establish a clear top-level layout appropriate to the
chosen ecosystem, for example:

- `src/` for application source code.
- `tests/` for automated tests.
- `assets/` or `public/` for static files.
- `docs/` for design and operational documentation.

Keep generated output such as `build/`, `dist/`, `target/`, and coverage reports
out of version control.

## Build, Test, and Development Commands

No build, dependency, lint, or test commands are configured yet. Do not introduce
unverified commands in documentation. Once scaffolding exists, record the exact
installation, development, build, lint, and test commands here and in the README.

Useful repository checks that work today:

```sh
git status --short   # Review pending changes.
git diff --check     # Detect whitespace errors.
```

## Coding Style & Naming Conventions

No language, formatter, or linter has been selected. Adopt one formatter per
language, commit its configuration, and format changed files before submitting.
Avoid unrelated style-only changes.

Use lowercase, descriptive directory names and ecosystem-standard identifiers.
Unless the selected language dictates otherwise, use `PascalCase` for types,
`camelCase` for functions and variables, and short kebab-case file names.

## Testing Guidelines

There is no testing framework or coverage target yet. Add tests with the first
behavioral code and document the command that runs them. Name tests after the
behavior under test, following the framework's convention (for example,
`employee-validation.test.ts` or `EmployeeServiceTest.java`). New behavior and
bug fixes should include focused regression tests.

## Commit & Pull Request Guidelines

There is no commit history from which to derive a convention. Use Conventional
Commits, such as `feat: add employee search` or `fix: validate department IDs`.
Keep each commit focused and imperative.

Pull requests should explain the change and its rationale, list verification
steps, identify configuration or schema changes, and link relevant issues. Include
screenshots for visible UI changes. Ensure all configured checks pass before
requesting review.

## Security & Configuration

Never commit credentials, personal employee data, or environment-specific
secrets. Document required variables in an example configuration and use the
principle of least privilege for data access.
