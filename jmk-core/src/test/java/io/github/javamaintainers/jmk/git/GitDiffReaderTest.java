package io.github.javamaintainers.jmk.git;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.javamaintainers.jmk.model.ChangedFile;
import org.junit.jupiter.api.Test;

class GitDiffReaderTest {
  @Test
  void parsesNameStatusLines() {
    ChangedFile modified = GitDiffReader.parseNameStatus("M\tjmk-core/src/Main.java");
    assertEquals(ChangedFile.ChangeType.MODIFIED, modified.type());
    assertEquals("jmk-core/src/Main.java", modified.path());

    ChangedFile renamed = GitDiffReader.parseNameStatus("R100\told/Path.java\tnew/Path.java");
    assertEquals(ChangedFile.ChangeType.RENAMED, renamed.type());
    assertEquals("new/Path.java", renamed.path());
    assertEquals("old/Path.java", renamed.previousPath());
  }
}
