# Consumer example workflow

Copy into `.github/workflows/jmk.yml` of any public Maven repository:

```yaml
name: Java Maintainer Kit
on:
  pull_request:

permissions:
  contents: read
  pull-requests: write

jobs:
  analyze:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
        with:
          fetch-depth: 0
      - uses: blueskyno6/java-maintainer-kit@v0.1.1
        with:
          base-ref: origin/${{ github.base_ref }}
          head-ref: HEAD
          # Optional: pin the CLI jar release (default is "latest")
          jmk-version: v0.1.1
```

Notes:

- Consumer CI downloads `jmk-cli.jar` from GitHub Releases — no Maven build.
- Prefer pinning `@v0.1.1` (action) and `jmk-version: v0.1.1` (jar) for reproducible runs.
- `build-from-source: true` is only for developing JMK itself.
