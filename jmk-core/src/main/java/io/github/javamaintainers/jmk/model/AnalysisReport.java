package io.github.javamaintainers.jmk.model;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/** Aggregated analysis result for a PR / commit range. */
public final class AnalysisReport {
  private final String baseRef;
  private final String headRef;
  private final List<ChangedFile> changedFiles;
  private final Set<String> impactedModules;
  private final List<DependencyChange> dependencyChanges;
  private final List<String> risks;
  private final List<String> recommendations;

  public AnalysisReport(
      String baseRef,
      String headRef,
      List<ChangedFile> changedFiles,
      Set<String> impactedModules,
      List<DependencyChange> dependencyChanges,
      List<String> risks,
      List<String> recommendations) {
    this.baseRef = Objects.requireNonNull(baseRef, "baseRef");
    this.headRef = Objects.requireNonNull(headRef, "headRef");
    this.changedFiles = List.copyOf(changedFiles);
    this.impactedModules = Set.copyOf(impactedModules);
    this.dependencyChanges = List.copyOf(dependencyChanges);
    this.risks = List.copyOf(risks);
    this.recommendations = List.copyOf(recommendations);
  }

  public String baseRef() {
    return baseRef;
  }

  public String headRef() {
    return headRef;
  }

  public List<ChangedFile> changedFiles() {
    return changedFiles;
  }

  public Set<String> impactedModules() {
    return impactedModules;
  }

  public List<DependencyChange> dependencyChanges() {
    return dependencyChanges;
  }

  public List<String> risks() {
    return risks;
  }

  public List<String> recommendations() {
    return recommendations;
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private String baseRef = "BASE";
    private String headRef = "HEAD";
    private final List<ChangedFile> changedFiles = new ArrayList<>();
    private final Set<String> impactedModules = new LinkedHashSet<>();
    private final List<DependencyChange> dependencyChanges = new ArrayList<>();
    private final List<String> risks = new ArrayList<>();
    private final List<String> recommendations = new ArrayList<>();

    public Builder baseRef(String baseRef) {
      this.baseRef = baseRef;
      return this;
    }

    public Builder headRef(String headRef) {
      this.headRef = headRef;
      return this;
    }

    public Builder changedFiles(List<ChangedFile> files) {
      this.changedFiles.clear();
      this.changedFiles.addAll(files);
      return this;
    }

    public Builder impactedModules(Set<String> modules) {
      this.impactedModules.clear();
      this.impactedModules.addAll(modules);
      return this;
    }

    public Builder dependencyChanges(List<DependencyChange> changes) {
      this.dependencyChanges.clear();
      this.dependencyChanges.addAll(changes);
      return this;
    }

    public Builder risk(String risk) {
      this.risks.add(risk);
      return this;
    }

    public Builder recommendation(String recommendation) {
      this.recommendations.add(recommendation);
      return this;
    }

    public AnalysisReport build() {
      return new AnalysisReport(
          baseRef,
          headRef,
          changedFiles,
          impactedModules,
          dependencyChanges,
          risks,
          recommendations);
    }
  }
}
