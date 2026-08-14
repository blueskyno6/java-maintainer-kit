package io.github.javamaintainers.jmk.changelog;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class ChangelogGeneratorTest {
  @Test
  void groupsConventionalCommits() {
    String md =
        new ChangelogGenerator()
            .generate(
                List.of(
                    "feat(cli): add analyze command",
                    "fix(core): handle renamed files",
                    "docs: improve README",
                    "chore: random tidy",
                    "feat(api)!: remove deprecated endpoint"),
                "v0.1.0");

    assertTrue(md.contains("## v0.1.0"));
    assertTrue(md.contains("### Features"));
    assertTrue(md.contains("### Bug Fixes"));
    assertTrue(md.contains("### Breaking Changes"));
    assertTrue(md.contains("add analyze command"));
  }
}
