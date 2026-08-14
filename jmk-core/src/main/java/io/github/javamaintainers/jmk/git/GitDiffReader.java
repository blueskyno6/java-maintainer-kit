package io.github.javamaintainers.jmk.git;

import io.github.javamaintainers.jmk.model.ChangedFile;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/** Reads changed files between two git refs. */
public final class GitDiffReader {
  private final Path repoRoot;
  private final long timeoutSeconds;

  public GitDiffReader(Path repoRoot) {
    this(repoRoot, 60);
  }

  public GitDiffReader(Path repoRoot, long timeoutSeconds) {
    this.repoRoot = repoRoot;
    this.timeoutSeconds = timeoutSeconds;
  }

  public List<ChangedFile> listChangedFiles(String baseRef, String headRef) throws IOException {
    List<String> output =
        runGit("diff", "--name-status", "--find-renames", baseRef + "..." + headRef);
    List<ChangedFile> files = new ArrayList<>();
    for (String line : output) {
      if (line.isBlank()) {
        continue;
      }
      files.add(parseNameStatus(line));
    }
    return files;
  }

  public String showFileAtRef(String ref, String relativePath) throws IOException {
    List<String> lines = runGit("show", ref + ":" + toGitPath(relativePath));
    return String.join("\n", lines);
  }

  public boolean fileExistsAtRef(String ref, String relativePath) {
    try {
      runGit("cat-file", "-e", ref + ":" + toGitPath(relativePath));
      return true;
    } catch (IOException ex) {
      return false;
    }
  }

  public List<String> logMessages(String baseRef, String headRef) throws IOException {
    return runGit("log", "--pretty=format:%s", baseRef + ".." + headRef);
  }

  public List<String> runGit(String... args) throws IOException {
    List<String> command = new ArrayList<>();
    command.add("git");
    command.add("-C");
    command.add(repoRoot.toAbsolutePath().toString());
    command.addAll(List.of(args));

    ProcessBuilder pb = new ProcessBuilder(command);
    pb.redirectErrorStream(true);
    Process process = pb.start();
    List<String> lines = new ArrayList<>();
    try (BufferedReader reader =
        new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
      String line;
      while ((line = reader.readLine()) != null) {
        lines.add(line);
      }
    }
    try {
      if (!process.waitFor(timeoutSeconds, TimeUnit.SECONDS)) {
        process.destroyForcibly();
        throw new IOException("git timed out: " + String.join(" ", command));
      }
    } catch (InterruptedException ex) {
      Thread.currentThread().interrupt();
      throw new IOException("git interrupted", ex);
    }
    if (process.exitValue() != 0) {
      throw new IOException(
          "git failed (" + process.exitValue() + "): " + String.join("\n", lines));
    }
    return lines;
  }

  static ChangedFile parseNameStatus(String line) {
    String[] parts = line.split("\t");
    if (parts.length < 2) {
      return ChangedFile.of(line.trim(), ChangedFile.ChangeType.UNKNOWN);
    }
    String status = parts[0].trim().toUpperCase(Locale.ROOT);
    char code = status.charAt(0);
    return switch (code) {
      case 'A' -> ChangedFile.of(parts[1], ChangedFile.ChangeType.ADDED);
      case 'M' -> ChangedFile.of(parts[1], ChangedFile.ChangeType.MODIFIED);
      case 'D' -> ChangedFile.of(parts[1], ChangedFile.ChangeType.DELETED);
      case 'R' -> new ChangedFile(parts[2], ChangedFile.ChangeType.RENAMED, parts[1]);
      case 'C' -> new ChangedFile(parts[2], ChangedFile.ChangeType.COPIED, parts[1]);
      default -> ChangedFile.of(parts[1], ChangedFile.ChangeType.UNKNOWN);
    };
  }

  private static String toGitPath(String relativePath) {
    return relativePath.replace('\\', '/');
  }
}
