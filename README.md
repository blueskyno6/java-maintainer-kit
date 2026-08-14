# Java Maintainer Kit (JMK)

**PR impact analysis, Maven dependency diffs, and release changelogs — built for Java maintainers.**

[![CI](https://github.com/blueskyno6/java-maintainer-kit/actions/workflows/ci.yml/badge.svg)](https://github.com/blueskyno6/java-maintainer-kit/actions/workflows/ci.yml)
[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](LICENSE)
[![Java](https://img.shields.io/badge/Java-17%2B-orange)](https://openjdk.org/)

Open-source Java repositories burn maintainer time on the same questions every PR:

- Which Maven modules actually changed?
- What dependencies were added, removed, or bumped?
- Which paths look risky (API / auth / CI)?
- What should reviewers focus on first?

**Java Maintainer Kit** answers those in one CLI and one GitHub Action — no SaaS, no lock-in.

## Features

- **Module impact mapping** for multi-module Maven reactors
- **POM dependency diffs** (direct + dependencyManagement)
- **Risk signals** for API / security / workflow paths
- **Review recommendations** tailored to Java maintainers
- **Conventional Commits changelog** generation
- **GitHub Action** that comments on PRs and writes a job summary
- **Dogfooded in CI** on this repository

## Quick start (CLI)

Requirements: Java 17+, Maven 3.9+, git.

```bash
git clone https://github.com/blueskyno6/java-maintainer-kit.git
cd java-maintainer-kit
mvn -q -pl jmk-cli -am package
java -jar jmk-cli/target/jmk-cli.jar analyze --base origin/main --head HEAD
```

Generate a changelog:

```bash
java -jar jmk-cli/target/jmk-cli.jar changelog --base v0.1.0 --head HEAD --title v0.2.0
```

## GitHub Action

```yaml
name: Maintainer analysis
on:
  pull_request:

permissions:
  contents: read
  pull-requests: write

jobs:
  jmk:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
        with:
          fetch-depth: 0

      - name: Java Maintainer Kit
        uses: blueskyno6/java-maintainer-kit@v0.1.0
        with:
          base-ref: origin/${{ github.base_ref }}
          head-ref: HEAD
          comment-on-pr: 'true'
```

### Action inputs

| Input | Default | Description |
|---|---|---|
| `base-ref` | _required_ | Base git ref |
| `head-ref` | `HEAD` | Head git ref |
| `repo-path` | `.` | Repository path |
| `format` | `markdown` | `markdown` or `json` |
| `comment-on-pr` | `true` | Post/update PR comment |
| `github-token` | `${{ github.token }}` | Token for commenting |

## Example report

```markdown
## Java Maintainer Kit Report

Comparing `origin/main` → `HEAD`

### Impacted modules
- `jmk-core`
- `jmk-cli`

### Dependency changes
#### Module `jmk-cli`
- ~ info.picocli:picocli 4.7.5 → 4.7.6

### Recommendations
- Run focused tests for impacted modules: `jmk-core`, `jmk-cli`
```

## Project layout

```
java-maintainer-kit/
├── jmk-core/     # analysis engine
├── jmk-cli/      # picocli CLI (shaded jar)
├── action.yml    # composite GitHub Action
└── docs/         # guides + application notes
```

## Why this project exists

Maintaining Java OSS means reviewing reactors, BOM bumps, and release notes under time pressure. JMK turns that into a repeatable, automatable workflow — the same class of maintainer automation OpenAI’s Codex for Open Source program is designed to support.

## Roadmap

- [ ] Gradle / Kotlin DSL support
- [ ] Optional japicmp binary-compatibility checks
- [ ] Maven Central publishing
- [ ] Prebuilt Action jar download (skip on-the-fly build)
- [ ] SARIF export for security-sensitive path hits

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md). Bug reports and small, focused PRs are welcome.

## Security

Please see [SECURITY.md](SECURITY.md).

## License

Apache License 2.0 — see [LICENSE](LICENSE).
