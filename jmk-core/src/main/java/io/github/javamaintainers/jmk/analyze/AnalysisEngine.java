package io.github.javamaintainers.jmk.analyze;

import io.github.javamaintainers.jmk.git.GitDiffReader;
import io.github.javamaintainers.jmk.maven.ModuleIndexer;
import io.github.javamaintainers.jmk.maven.PomDependencyDiffer;
import io.github.javamaintainers.jmk.model.AnalysisReport;
import io.github.javamaintainers.jmk.model.ChangedFile;
import io.github.javamaintainers.jmk.model.DependencyChange;
import io.github.javamaintainers.jmk.model.MavenModule;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/** Orchestrates PR / commit-range analysis for a Java/Maven repository. */
public final class AnalysisEngine {
  private final Path repoRoot;
  private final GitDiffReader git;
  private final ModuleIndexer indexer;
  private final PomDependencyDiffer dependencyDiffer;

  public AnalysisEngine(Path repoRoot) {
    this.repoRoot = repoRoot.toAbsolutePath().normalize();
    this.git = new GitDiffReader(this.repoRoot);
    this.indexer = new ModuleIndexer(this.repoRoot);
    this.dependencyDiffer = new PomDependencyDiffer();
  }

  public AnalysisReport analyze(String baseRef, String headRef) throws IOException {
    List<MavenModule> modules = indexer.discover();
    List<ChangedFile> changedFiles = git.listChangedFiles(baseRef, headRef);

    Set<String> impacted = new LinkedHashSet<>();
    List<DependencyChange> dependencyChanges = new ArrayList<>();
    AnalysisReport.Builder builder =
        AnalysisReport.builder().baseRef(baseRef).headRef(headRef).changedFiles(changedFiles);

    for (ChangedFile file : changedFiles) {
      indexer
          .findModuleForPath(modules, file.path())
          .ifPresent(module -> impacted.add(module.artifactId()));

      String lower = file.path().toLowerCase(Locale.ROOT);
      if (lower.endsWith("pom.xml")) {
        String moduleId =
            indexer
                .findModuleForPath(modules, file.path())
                .map(MavenModule::artifactId)
                .orElse("root");
        String oldXml = safeShow(baseRef, file.previousPath() != null ? file.previousPath() : file.path());
        String newXml =
            file.type() == ChangedFile.ChangeType.DELETED
                ? ""
                : safeShow(headRef, file.path());
        dependencyChanges.addAll(dependencyDiffer.diff(oldXml, newXml, moduleId));
      }

      if (looksLikePublicApi(file.path())) {
        builder.risk("Possible public API surface change: `" + file.path() + "`");
      }
      if (looksLikeWorkflow(file.path())) {
        builder.risk("CI / workflow file changed: `" + file.path() + "`");
      }
      if (looksLikeSecuritySensitive(file.path())) {
        builder.risk("Security-sensitive path touched: `" + file.path() + "`");
      }
    }

    builder.impactedModules(impacted);
    builder.dependencyChanges(dependencyChanges);

    if (impacted.isEmpty()) {
      builder.recommendation("No Maven modules mapped from changed files — confirm the repo root is correct.");
    } else {
      builder.recommendation(
          "Run focused tests for impacted modules: "
              + String.join(", ", impacted.stream().map(id -> "`" + id + "`").toList()));
    }
    if (!dependencyChanges.isEmpty()) {
      builder.recommendation(
          "Review dependency diffs carefully; prefer running `mvn -q -DskipTests dependency:tree` on impacted modules.");
    }
    if (changedFiles.stream().anyMatch(f -> f.path().endsWith(".java"))) {
      builder.recommendation("Ensure public API and binary compatibility if this module is consumed downstream.");
    }
    builder.recommendation(
        "Maintainers: use this report to prioritize review on high-risk files and dependency bumps.");

    return builder.build();
  }

  private String safeShow(String ref, String path) {
    try {
      if (!git.fileExistsAtRef(ref, path)) {
        return "";
      }
      return git.showFileAtRef(ref, path);
    } catch (IOException ex) {
      return "";
    }
  }

  private static boolean looksLikePublicApi(String path) {
    String p = path.replace('\\', '/').toLowerCase(Locale.ROOT);
    return p.contains("/api/")
        || p.contains("/spi/")
        || p.endsWith("service.java")
        || p.contains("/public/");
  }

  private static boolean looksLikeWorkflow(String path) {
    String p = path.replace('\\', '/').toLowerCase(Locale.ROOT);
    return p.startsWith(".github/workflows/") || p.endsWith("jenkinsfile");
  }

  private static boolean looksLikeSecuritySensitive(String path) {
    String p = path.replace('\\', '/').toLowerCase(Locale.ROOT);
    return p.contains("security")
        || p.contains("auth")
        || p.contains("oauth")
        || p.contains("jwt")
        || p.contains("crypto")
        || p.endsWith(".pem")
        || p.endsWith(".key");
  }
}
