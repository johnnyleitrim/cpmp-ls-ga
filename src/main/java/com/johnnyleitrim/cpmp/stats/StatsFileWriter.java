package com.johnnyleitrim.cpmp.stats;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import com.johnnyleitrim.cpmp.Problem;
import com.johnnyleitrim.cpmp.ls.IterativeLocalSearchStrategyConfig;

public class StatsFileWriter implements StatsWriter {

  public static final char FILE_TAG = 'F';
  public static final char CONFIG_TAG = 'C';
  public static final char SOLUTION_TAG = 'S';
  public static final char SEED_TAG = 'R';
  public static final String VALUE_DELIMITER = ",";
  public static final String NEW_LINE = System.lineSeparator();

  private final BufferedWriter writer;

  public StatsFileWriter(String problemCategory, IterativeLocalSearchStrategyConfig strategyConfig, int runs, int maxSolutions, long baseSeed) {
    try {
      writer = Files.newBufferedWriter(getOutputFile(problemCategory, strategyConfig));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    writeConfig(strategyConfig, runs, maxSolutions, baseSeed);
  }

  @Override
  public void writeSeed(long seed) {
    writeLine(SEED_TAG, String.valueOf(seed));
  }

  @Override
  public void writeProblemName(Problem problem) {
    writeLine(FILE_TAG, problem.getName());
  }

  @Override
  public void writeSolution(int nMoves, int nLocalSearchMoves, int nPerturbationMoves, long durationMs) {
    writeLine(SOLUTION_TAG, String.valueOf(nMoves), String.valueOf(nLocalSearchMoves), String.valueOf(nPerturbationMoves), String.valueOf(durationMs));
  }

  @Override
  public void close() throws IOException {
    writer.flush();
    writer.close();
  }

  private Path getOutputFile(String problemCategory, IterativeLocalSearchStrategyConfig strategyConfig) {
    StringBuilder filename = new StringBuilder();
    filename.append(problemCategory);
    for (Map.Entry<String, Object> fieldValue : strategyConfig.getFieldValues().entrySet()) {
      filename.append("_");
      filename.append(fieldValue.getValue().toString());
    }
    filename.append("_");
    filename.append(System.currentTimeMillis());
    filename.append(".log");
    return Paths.get("output", "ls", filename.toString());
  }

  private void writeConfig(IterativeLocalSearchStrategyConfig strategyConfig, int runs, int maxSolutions, long baseSeed) {
    writeLine(CONFIG_TAG, "Base Seed", String.valueOf(baseSeed));
    writeLine(CONFIG_TAG, "Runs", String.valueOf(runs));
    writeLine(CONFIG_TAG, "Max Solutions", String.valueOf(maxSolutions));
    for (Map.Entry<String, Object> fieldValue : strategyConfig.getFieldValues().entrySet()) {
      writeLine(CONFIG_TAG, fieldValue.getKey(), fieldValue.getValue().toString());
    }
  }

  private void writeLine(char tag, String... values) {
    try {
      writer.write(tag);
      writer.write(':');
      writer.write(String.join(VALUE_DELIMITER, values));
      writer.write(NEW_LINE);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
