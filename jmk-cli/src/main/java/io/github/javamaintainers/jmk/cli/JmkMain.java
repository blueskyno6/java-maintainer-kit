package io.github.javamaintainers.jmk.cli;

import io.github.javamaintainers.jmk.analyze.AnalysisEngine;
import io.github.javamaintainers.jmk.changelog.ChangelogGenerator;
import io.github.javamaintainers.jmk.git.GitDiffReader;
import io.github.javamaintainers.jmk.model.AnalysisReport;
import io.github.javamaintainers.jmk.report.ReportRenderer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.Callable;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(
    name = "jmk",
    mixinStandardHelpOptions = true,
    version = "java-maintainer-kit 0.1.0-SNAPSHOT",
    description = "Java Maintainer Kit - PR impact analysis and changelogs for Maven repos.",
    subcommands = {AnalyzeCommand.class, ChangelogCommand.class})
public final class JmkMain implements Callable<Integer> {
  public static void main(String[] args) {
    int code = new CommandLine(new JmkMain()).execute(args);
    System.exit(code);
  }

  @Override
  public Integer call() {
    new CommandLine(this).usage(System.out);
    return 0;
  }
}

@Command(
    name = "analyze",
    description = "Analyze impact between two git refs for a Java/Maven repository.")
class AnalyzeCommand implements Callable<Integer> {
  @Option(
      names = {"-r", "--repo"},
      description = "Repository root (default: current directory)",
      defaultValue = ".")
  Path repo;

  @Option(
      names = {"-b", "--base"},
      description = "Base git ref",
      required = true)
  String base;

  @Option(
      names = {"-H", "--head"},
      description = "Head git ref",
      defaultValue = "HEAD")
  String head;

  @Option(
      names = {"-f", "--format"},
      description = "Output format: markdown|json",
      defaultValue = "markdown")
  String format;

  @Option(
      names = {"-o", "--output"},
      description = "Write report to file instead of stdout")
  Path output;

  @Override
  public Integer call() throws Exception {
    Path root = repo.toAbsolutePath().normalize();
    AnalysisEngine engine = new AnalysisEngine(root);
    AnalysisReport report = engine.analyze(base, head);
    ReportRenderer renderer = new ReportRenderer();
    String body =
        "json".equalsIgnoreCase(format) ? renderer.toJson(report) : renderer.toMarkdown(report);
    if (output != null) {
      Files.writeString(output, body, StandardCharsets.UTF_8);
      System.out.println("Wrote report to " + output.toAbsolutePath());
    } else {
      System.out.print(body);
    }
    return 0;
  }
}

@Command(
    name = "changelog",
    description = "Generate a Conventional Commits changelog between two refs.")
class ChangelogCommand implements Callable<Integer> {
  @Option(
      names = {"-r", "--repo"},
      description = "Repository root (default: current directory)",
      defaultValue = ".")
  Path repo;

  @Option(
      names = {"-b", "--base"},
      description = "Base git ref",
      required = true)
  String base;

  @Option(
      names = {"-H", "--head"},
      description = "Head git ref",
      defaultValue = "HEAD")
  String head;

  @Option(
      names = {"--title"},
      description = "Changelog heading",
      defaultValue = "Changelog")
  String title;

  @Option(
      names = {"-o", "--output"},
      description = "Write changelog to file instead of stdout")
  Path output;

  @Parameters(arity = "0..1", description = "Optional path ignored; reserved for future use")
  List<String> ignored;

  @Override
  public Integer call() throws Exception {
    Path root = repo.toAbsolutePath().normalize();
    GitDiffReader git = new GitDiffReader(root);
    List<String> subjects = git.logMessages(base, head);
    String md = new ChangelogGenerator().generate(subjects, title);
    if (output != null) {
      Files.writeString(output, md, StandardCharsets.UTF_8);
      System.out.println("Wrote changelog to " + output.toAbsolutePath());
    } else {
      System.out.print(md);
    }
    return 0;
  }
}
