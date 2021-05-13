package com.johnnyleitrim.cpmp;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CommandOptions {
  private final List<String> arguments;

  public CommandOptions(String[] args) {
    arguments = new ArrayList<>(args.length);
    for (String arg : args) {
      arguments.add(arg);
    }
  }

  public int size() {
    return arguments.size();
  }

  public boolean hasArg(String option) {
    return arguments.stream().anyMatch(option::equalsIgnoreCase);
  }

  public Optional<String> getArg(String option) {
    for (int i = 0; i < arguments.size(); i++) {
      if (arguments.get(i).equalsIgnoreCase(option)) {
        return Optional.of(arguments.get(i + 1));
      }
    }
    return Optional.empty();
  }

  public Optional<Long> getLongArg(String option) {
    return getArg(option).map(Long::parseLong);
  }

  public Optional<Integer> getIntArg(String option) {
    return getArg(option).map(Integer::parseInt);
  }

  public Optional<Double> getDoubleArg(String option) {
    return getArg(option).map(Double::parseDouble);
  }
}
