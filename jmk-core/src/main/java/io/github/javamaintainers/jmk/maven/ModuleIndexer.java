package io.github.javamaintainers.jmk.maven;

import io.github.javamaintainers.jmk.model.MavenModule;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

/** Discovers Maven modules under a repository root. */
public final class ModuleIndexer {
  private final Path repoRoot;

  public ModuleIndexer(Path repoRoot) {
    this.repoRoot = repoRoot.toAbsolutePath().normalize();
  }

  public List<MavenModule> discover() throws IOException {
    List<Path> pomFiles;
    try (Stream<Path> walk = Files.walk(repoRoot)) {
      pomFiles =
          walk.filter(Files::isRegularFile)
              .filter(p -> p.getFileName().toString().equals("pom.xml"))
              .filter(p -> !p.toString().contains(".git"))
              .sorted()
              .toList();
    }

    List<MavenModule> modules = new ArrayList<>();
    for (Path pom : pomFiles) {
      parsePom(pom).ifPresent(modules::add);
    }
    modules.sort(Comparator.comparing(MavenModule::relativeDir));
    return modules;
  }

  public Optional<MavenModule> findModuleForPath(List<MavenModule> modules, String relativePath) {
    String normalized = relativePath.replace('\\', '/');
    MavenModule best = null;
    int bestLen = -1;
    for (MavenModule module : modules) {
      String dir = module.relativeDir().replace('\\', '/');
      if (dir.isEmpty()) {
        if (best == null) {
          best = module;
          bestLen = 0;
        }
        continue;
      }
      if (normalized.equals(dir)
          || normalized.startsWith(dir + "/")
          || normalized.equals(dir + "/pom.xml")) {
        if (dir.length() > bestLen) {
          best = module;
          bestLen = dir.length();
        }
      }
    }
    return Optional.ofNullable(best);
  }

  public Optional<MavenModule> parsePom(Path pomFile) throws IOException {
    String xml = Files.readString(pomFile, StandardCharsets.UTF_8);
    return parsePomXml(xml, pomFile);
  }

  public Optional<MavenModule> parsePomXml(String xml, Path pomFile) throws IOException {
    Model model = readModel(xml);
    if (model.getArtifactId() == null || model.getArtifactId().isBlank()) {
      return Optional.empty();
    }
    Path relative = repoRoot.relativize(pomFile.toAbsolutePath().normalize().getParent());
    String relativeDir = relative.toString().replace('\\', '/');
    if (relativeDir.equals(".")) {
      relativeDir = "";
    }
    String groupId = firstNonBlank(model.getGroupId(), model.getParent() == null ? null : model.getParent().getGroupId());
    String version =
        firstNonBlank(model.getVersion(), model.getParent() == null ? null : model.getParent().getVersion());
    String packaging = model.getPackaging();
    String pomRelative = repoRoot.relativize(pomFile.toAbsolutePath().normalize()).toString().replace('\\', '/');
    return Optional.of(
        new MavenModule(groupId, model.getArtifactId(), version, packaging, relativeDir, pomRelative));
  }

  public static Model readModel(String xml) throws IOException {
    MavenXpp3Reader reader = new MavenXpp3Reader();
    try (Reader stringReader = new StringReader(xml)) {
      return reader.read(stringReader);
    } catch (XmlPullParserException ex) {
      throw new IOException("Failed to parse POM: " + ex.getMessage(), ex);
    }
  }

  private static String firstNonBlank(String first, String second) {
    if (first != null && !first.isBlank()) {
      return first;
    }
    if (second != null && !second.isBlank()) {
      return second;
    }
    return "";
  }
}
