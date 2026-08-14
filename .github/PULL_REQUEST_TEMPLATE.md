# Pull Request Template

## Summary

<!-- What changed and why? -->

## Checklist

- [ ] Tests added/updated when behavior changes
- [ ] Docs updated when user-facing behavior changes
- [ ] Conventional Commit style title (`feat:`, `fix:`, ...)

## Test plan

- [ ] `mvn verify`
- [ ] Optional: `java -jar jmk-cli/target/jmk-cli.jar analyze --base origin/main --head HEAD`
