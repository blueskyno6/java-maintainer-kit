package io.github.javamaintainers.jmk.model;

import java.util.Objects;

/** Maven dependency coordinates used for diffing. */
public final class DependencyKey implements Comparable<DependencyKey> {
  private final String groupId;
  private final String artifactId;
  private final String type;
  private final String classifier;

  public DependencyKey(String groupId, String artifactId, String type, String classifier) {
    this.groupId = nullToEmpty(groupId);
    this.artifactId = nullToEmpty(artifactId);
    this.type = (type == null || type.isBlank()) ? "jar" : type;
    this.classifier = nullToEmpty(classifier);
  }

  public String groupId() {
    return groupId;
  }

  public String artifactId() {
    return artifactId;
  }

  public String type() {
    return type;
  }

  public String classifier() {
    return classifier;
  }

  public String ga() {
    return groupId + ":" + artifactId;
  }

  @Override
  public int compareTo(DependencyKey other) {
    int c = ga().compareTo(other.ga());
    if (c != 0) {
      return c;
    }
    c = type.compareTo(other.type);
    if (c != 0) {
      return c;
    }
    return classifier.compareTo(other.classifier);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof DependencyKey that)) {
      return false;
    }
    return groupId.equals(that.groupId)
        && artifactId.equals(that.artifactId)
        && type.equals(that.type)
        && classifier.equals(that.classifier);
  }

  @Override
  public int hashCode() {
    return Objects.hash(groupId, artifactId, type, classifier);
  }

  @Override
  public String toString() {
    if (classifier.isEmpty()) {
      return groupId + ":" + artifactId + ":" + type;
    }
    return groupId + ":" + artifactId + ":" + type + ":" + classifier;
  }

  private static String nullToEmpty(String value) {
    return value == null ? "" : value;
  }
}
