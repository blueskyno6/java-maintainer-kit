package io.github.javamaintainers.jmk.model;

import java.util.Objects;

/** One dependency change between two POM revisions. */
public final class DependencyChange {
  public enum Kind {
    ADDED,
    REMOVED,
    UPDATED
  }

  private final Kind kind;
  private final DependencyKey key;
  private final String oldVersion;
  private final String newVersion;
  private final String moduleArtifactId;

  public DependencyChange(
      Kind kind, DependencyKey key, String oldVersion, String newVersion, String moduleArtifactId) {
    this.kind = Objects.requireNonNull(kind, "kind");
    this.key = Objects.requireNonNull(key, "key");
    this.oldVersion = oldVersion;
    this.newVersion = newVersion;
    this.moduleArtifactId = moduleArtifactId == null ? "" : moduleArtifactId;
  }

  public Kind kind() {
    return kind;
  }

  public DependencyKey key() {
    return key;
  }

  public String oldVersion() {
    return oldVersion;
  }

  public String newVersion() {
    return newVersion;
  }

  public String moduleArtifactId() {
    return moduleArtifactId;
  }

  public String summary() {
    return switch (kind) {
      case ADDED -> "+ " + key.ga() + ":" + newVersion;
      case REMOVED -> "- " + key.ga() + ":" + oldVersion;
      case UPDATED -> "~ " + key.ga() + " " + oldVersion + " → " + newVersion;
    };
  }
}
