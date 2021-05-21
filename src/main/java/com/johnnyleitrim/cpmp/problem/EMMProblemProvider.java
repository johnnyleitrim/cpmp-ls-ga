package com.johnnyleitrim.cpmp.problem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.johnnyleitrim.cpmp.Problem;

public class EMMProblemProvider implements ProblemProvider {
  private static final String BASE_DIR = System.getProperty("user.home") + "/Datasets/MSc/EMM";
  private final Path directory;

  public EMMProblemProvider(String path) {
    this(Paths.get(BASE_DIR, path));
  }

  private EMMProblemProvider(Path directory) {
    this.directory = directory;

    if (!Files.isDirectory(directory)) {
      throw new IllegalArgumentException("Not a directory: " + directory);
    }
  }

  private static Problem read(Path file) {
    try {
      return BFProblemReader.fromLines(file.toString(), Files.readAllLines(file));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Iterable<Problem> getProblems() {
    try (Stream<Path> walk = Files.walk(directory)) {
      return walk.filter(Files::isRegularFile)
          .map(EMMProblemProvider::read)
          .collect(Collectors.toList());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
