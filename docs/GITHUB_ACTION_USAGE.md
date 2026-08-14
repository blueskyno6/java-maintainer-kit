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
      - uses: blueskyno6/java-maintainer-kit@v0.1.0
        with:
          base-ref: origin/${{ github.base_ref }}
          head-ref: HEAD
```

Until the first tagged release exists, pin to a commit SHA or use `main` carefully.
