package io.github.javamaintainers.jmk.maven;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.javamaintainers.jmk.model.DependencyChange;
import java.util.List;
import org.junit.jupiter.api.Test;

class PomDependencyDifferTest {
  @Test
  void detectsAddedUpdatedAndRemovedDependencies() throws Exception {
    String oldXml =
        """
        <project>
          <modelVersion>4.0.0</modelVersion>
          <groupId>demo</groupId>
          <artifactId>demo</artifactId>
          <version>1.0.0</version>
          <dependencies>
            <dependency>
              <groupId>com.example</groupId>
              <artifactId>keep</artifactId>
              <version>1.0.0</version>
            </dependency>
            <dependency>
              <groupId>com.example</groupId>
              <artifactId>bump</artifactId>
              <version>1.0.0</version>
            </dependency>
            <dependency>
              <groupId>com.example</groupId>
              <artifactId>gone</artifactId>
              <version>1.0.0</version>
            </dependency>
          </dependencies>
        </project>
        """;
    String newXml =
        """
        <project>
          <modelVersion>4.0.0</modelVersion>
          <groupId>demo</groupId>
          <artifactId>demo</artifactId>
          <version>1.0.0</version>
          <dependencies>
            <dependency>
              <groupId>com.example</groupId>
              <artifactId>keep</artifactId>
              <version>1.0.0</version>
            </dependency>
            <dependency>
              <groupId>com.example</groupId>
              <artifactId>bump</artifactId>
              <version>2.0.0</version>
            </dependency>
            <dependency>
              <groupId>com.example</groupId>
              <artifactId>fresh</artifactId>
              <version>1.2.3</version>
            </dependency>
          </dependencies>
        </project>
        """;

    List<DependencyChange> changes = new PomDependencyDiffer().diff(oldXml, newXml, "demo");
    assertEquals(3, changes.size());
    assertTrue(changes.stream().anyMatch(c -> c.kind() == DependencyChange.Kind.ADDED));
    assertTrue(changes.stream().anyMatch(c -> c.kind() == DependencyChange.Kind.REMOVED));
    assertTrue(
        changes.stream()
            .anyMatch(
                c ->
                    c.kind() == DependencyChange.Kind.UPDATED
                        && "2.0.0".equals(c.newVersion())));
  }
}
