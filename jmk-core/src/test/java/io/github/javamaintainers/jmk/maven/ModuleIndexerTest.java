package io.github.javamaintainers.jmk.maven;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.javamaintainers.jmk.model.MavenModule;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ModuleIndexerTest {
  @TempDir Path temp;

  @Test
  void mapsChangedFileToNearestModule() throws Exception {
    Path rootPom = temp.resolve("pom.xml");
    Files.writeString(
        rootPom,
        """
        <project>
          <modelVersion>4.0.0</modelVersion>
          <groupId>demo</groupId>
          <artifactId>root</artifactId>
          <version>1.0.0</version>
          <packaging>pom</packaging>
          <modules><module>api</module></modules>
        </project>
        """);
    Path apiDir = temp.resolve("api");
    Files.createDirectories(apiDir);
    Files.writeString(
        apiDir.resolve("pom.xml"),
        """
        <project>
          <modelVersion>4.0.0</modelVersion>
          <parent>
            <groupId>demo</groupId>
            <artifactId>root</artifactId>
            <version>1.0.0</version>
          </parent>
          <artifactId>api</artifactId>
        </project>
        """);
    Files.createDirectories(apiDir.resolve("src/main/java"));
    Files.writeString(apiDir.resolve("src/main/java/Api.java"), "class Api {}");

    ModuleIndexer indexer = new ModuleIndexer(temp);
    List<MavenModule> modules = indexer.discover();
    assertEquals(2, modules.size());
    assertTrue(
        indexer
            .findModuleForPath(modules, "api/src/main/java/Api.java")
            .map(MavenModule::artifactId)
            .orElseThrow()
            .equals("api"));
  }
}
