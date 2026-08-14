# Contributing to Java Maintainer Kit

Thanks for helping improve maintainer tooling for the Java ecosystem.

## Development setup

1. Install JDK 17+ and Maven 3.9+
2. Clone the repository
3. Run tests:

```bash
mvn verify
```

## Project conventions

- Keep PRs focused and small
- Add/adjust unit tests for core behavior changes
- Use [Conventional Commits](https://www.conventionalcommits.org/) (`feat:`, `fix:`, `docs:`, ...)
- Update README/docs when user-facing behavior changes

## Local CLI check

```bash
mvn -q -pl jmk-cli -am package
java -jar jmk-cli/target/jmk-cli.jar analyze --base HEAD~1 --head HEAD
```

## Reporting issues

Please include:

- JMK version / commit SHA
- JDK and OS
- Minimal reproduction (repo snippet or command)

## Code of conduct

Please follow [CODE_OF_CONDUCT.md](CODE_OF_CONDUCT.md).
