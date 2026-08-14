package io.github.javamaintainers.jmk.maven;

import io.github.javamaintainers.jmk.model.DependencyChange;
import io.github.javamaintainers.jmk.model.DependencyKey;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;

/** Diffs dependencies between two POM XML documents. */
public final class PomDependencyDiffer {
  public List<DependencyChange> diff(String oldXml, String newXml, String moduleArtifactId)
      throws IOException {
    Map<DependencyKey, String> oldDeps =
        oldXml == null || oldXml.isBlank() ? Map.of() : extract(oldXml);
    Map<DependencyKey, String> newDeps =
        newXml == null || newXml.isBlank() ? Map.of() : extract(newXml);

    List<DependencyChange> changes = new ArrayList<>();
    for (Map.Entry<DependencyKey, String> entry : newDeps.entrySet()) {
      DependencyKey key = entry.getKey();
      String newVersion = entry.getValue();
      if (!oldDeps.containsKey(key)) {
        changes.add(
            new DependencyChange(
                DependencyChange.Kind.ADDED, key, null, newVersion, moduleArtifactId));
      } else if (!Objects.equals(oldDeps.get(key), newVersion)) {
        changes.add(
            new DependencyChange(
                DependencyChange.Kind.UPDATED,
                key,
                oldDeps.get(key),
                newVersion,
                moduleArtifactId));
      }
    }
    for (Map.Entry<DependencyKey, String> entry : oldDeps.entrySet()) {
      if (!newDeps.containsKey(entry.getKey())) {
        changes.add(
            new DependencyChange(
                DependencyChange.Kind.REMOVED,
                entry.getKey(),
                entry.getValue(),
                null,
                moduleArtifactId));
      }
    }
    changes.sort((a, b) -> a.key().compareTo(b.key()));
    return changes;
  }

  private Map<DependencyKey, String> extract(String xml) throws IOException {
    Model model = ModuleIndexer.readModel(xml);
    Map<DependencyKey, String> map = new TreeMap<>();
    List<Dependency> dependencies = new ArrayList<>();
    if (model.getDependencies() != null) {
      dependencies.addAll(model.getDependencies());
    }
    if (model.getDependencyManagement() != null
        && model.getDependencyManagement().getDependencies() != null) {
      dependencies.addAll(model.getDependencyManagement().getDependencies());
    }
    for (Dependency dependency : dependencies) {
      DependencyKey key =
          new DependencyKey(
              dependency.getGroupId(),
              dependency.getArtifactId(),
              dependency.getType(),
              dependency.getClassifier());
      String version = dependency.getVersion() == null ? "(managed)" : dependency.getVersion();
      map.put(key, version);
    }
    return new LinkedHashMap<>(map);
  }
}
