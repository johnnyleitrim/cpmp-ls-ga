package com.johnnyleitrim.cpmp.problem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.johnnyleitrim.cpmp.Problem;

public class BFProblemProvider implements ProblemProvider {
  private final Path directory;

  private static final String BASE_DIR = System.getProperty("user.home") + "/Datasets/MSc/BF";

  public BFProblemProvider(String path) {
    this(Paths.get(BASE_DIR, path));
  }

  public BFProblemProvider(int bfNo) {
    this(Paths.get(BASE_DIR, "BF" + bfNo));
  }

  private BFProblemProvider(Path directory) {
    this.directory = directory;

    if (!Files.isDirectory(directory)) {
      throw new IllegalArgumentException("Not a directory: " + directory);
    }
  }

  @Override
  public Iterable<Problem> getProblems() {
    try (Stream<Path> walk = Files.walk(directory)) {
      return walk.filter(Files::isRegularFile)
          .map(BFProblemProvider::read)
          .collect(Collectors.toList());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static Problem read(Path file) {
    try {
      return BFProblemReader.fromLines(file.toString(), Files.readAllLines(file));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
