package com.johnnyleitrim.cpmp.problem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.johnnyleitrim.cpmp.Problem;

public class CVProblemProvider implements ProblemProvider {
  private final String filePrefix;

  private static final String BASE_DIR = System.getProperty("user.home") + "/Datasets/MSc/CRPTestcases_Caserta/";

  public CVProblemProvider(String filePrefix) {
    this.filePrefix = filePrefix;
  }

  @Override
  public Iterable<Problem> getProblems() {
    try (Stream<Path> walk = Files.walk(Paths.get(BASE_DIR))) {
      return walk.filter(Files::isRegularFile)
          .filter(path -> path.getFileName().toString().startsWith(filePrefix))
          .map(CVProblemProvider::read)
          .collect(Collectors.toList());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static Problem read(Path file) {
    try {
      return CVProblemReader.fromLines(file.getFileName().toString(), Files.readAllLines(file));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
