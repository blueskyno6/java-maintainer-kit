package io.github.javamaintainers.jmk.changelog;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Builds a Conventional Commits inspired changelog section. */
public final class ChangelogGenerator {
  private static final Pattern CONVENTIONAL =
      Pattern.compile(
          "^(?<type>feat|fix|docs|style|refactor|perf|test|build|ci|chore|revert)"
              + "(?:\\((?<scope>[^)]+)\\))?(?<breaking>!)?:\\s*(?<description>.+)$",
          Pattern.CASE_INSENSITIVE);

  public String generate(List<String> commitSubjects, String versionLabel) {
    Map<String, List<String>> buckets = new LinkedHashMap<>();
    buckets.put("Features", new ArrayList<>());
    buckets.put("Bug Fixes", new ArrayList<>());
    buckets.put("Performance", new ArrayList<>());
    buckets.put("Documentation", new ArrayList<>());
    buckets.put("Refactoring", new ArrayList<>());
    buckets.put("Build / CI", new ArrayList<>());
    buckets.put("Other", new ArrayList<>());
    List<String> breaking = new ArrayList<>();

    for (String subject : commitSubjects) {
      if (subject == null || subject.isBlank()) {
        continue;
      }
      Matcher matcher = CONVENTIONAL.matcher(subject.trim());
      if (!matcher.matches()) {
        buckets.get("Other").add(subject.trim());
        continue;
      }
      String type = matcher.group("type").toLowerCase(Locale.ROOT);
      String scope = matcher.group("scope");
      boolean isBreaking =
          matcher.group("breaking") != null || subject.toUpperCase(Locale.ROOT).contains("BREAKING CHANGE");
      String description = matcher.group("description").trim();
      String line = scope == null || scope.isBlank() ? description : "**" + scope + "**: " + description;
      if (isBreaking) {
        breaking.add(line);
      }
      switch (type) {
        case "feat" -> buckets.get("Features").add(line);
        case "fix" -> buckets.get("Bug Fixes").add(line);
        case "perf" -> buckets.get("Performance").add(line);
        case "docs" -> buckets.get("Documentation").add(line);
        case "refactor" -> buckets.get("Refactoring").add(line);
        case "build", "ci" -> buckets.get("Build / CI").add(line);
        default -> buckets.get("Other").add(line);
      }
    }

    StringBuilder md = new StringBuilder();
    md.append("## ").append(versionLabel == null || versionLabel.isBlank() ? "Changelog" : versionLabel);
    md.append("\n\n");
    if (!breaking.isEmpty()) {
      md.append("### Breaking Changes\n\n");
      for (String item : breaking) {
        md.append("- ").append(item).append('\n');
      }
      md.append('\n');
    }
    for (Map.Entry<String, List<String>> entry : buckets.entrySet()) {
      if (entry.getValue().isEmpty()) {
        continue;
      }
      md.append("### ").append(entry.getKey()).append("\n\n");
      for (String item : entry.getValue()) {
        md.append("- ").append(item).append('\n');
      }
      md.append('\n');
    }
    if (md.toString().trim().equals("## " + (versionLabel == null || versionLabel.isBlank() ? "Changelog" : versionLabel))) {
      md.append("_No commits found in range._\n");
    }
    return md.toString().trim() + "\n";
  }
}
