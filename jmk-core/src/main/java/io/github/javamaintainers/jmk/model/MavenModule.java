package io.github.javamaintainers.jmk.model;

import java.util.Objects;

/** A Maven module discovered in the repository. */
public final class MavenModule {
  private final String artifactId;
  private final String groupId;
  private final String version;
  private final String packaging;
  private final String relativeDir;
  private final String pomPath;

  public MavenModule(
      String groupId,
      String artifactId,
      String version,
      String packaging,
      String relativeDir,
      String pomPath) {
    this.groupId = groupId == null ? "" : groupId;
    this.artifactId = Objects.requireNonNull(artifactId, "artifactId");
    this.version = version == null ? "" : version;
    this.packaging = packaging == null || packaging.isBlank() ? "jar" : packaging;
    this.relativeDir = relativeDir == null ? "" : relativeDir;
    this.pomPath = Objects.requireNonNull(pomPath, "pomPath");
  }

  public String groupId() {
    return groupId;
  }

  public String artifactId() {
    return artifactId;
  }

  public String version() {
    return version;
  }

  public String packaging() {
    return packaging;
  }

  public String relativeDir() {
    return relativeDir;
  }

  public String pomPath() {
    return pomPath;
  }

  public String coordinates() {
    return groupId + ":" + artifactId + ":" + version;
  }

  @Override
  public String toString() {
    return coordinates() + " @ " + relativeDir;
  }
}
