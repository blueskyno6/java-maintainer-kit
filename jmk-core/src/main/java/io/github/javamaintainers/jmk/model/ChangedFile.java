package io.github.javamaintainers.jmk.model;

import java.util.Objects;

/** A changed file reported by git. */
public final class ChangedFile {
  public enum ChangeType {
    ADDED,
    MODIFIED,
    DELETED,
    RENAMED,
    COPIED,
    UNKNOWN
  }

  private final String path;
  private final ChangeType type;
  private final String previousPath;

  public ChangedFile(String path, ChangeType type, String previousPath) {
    this.path = Objects.requireNonNull(path, "path");
    this.type = Objects.requireNonNull(type, "type");
    this.previousPath = previousPath;
  }

  public static ChangedFile of(String path, ChangeType type) {
    return new ChangedFile(path, type, null);
  }

  public String path() {
    return path;
  }

  public ChangeType type() {
    return type;
  }

  public String previousPath() {
    return previousPath;
  }
}
