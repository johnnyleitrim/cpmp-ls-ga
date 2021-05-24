package com.johnnyleitrim.cpmp.strategy;

import java.util.Objects;

public abstract class BaseStrategy implements Strategy {

  private final String name;

  protected BaseStrategy(String name) {
    this.name = name;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BaseStrategy that = (BaseStrategy) o;
    return name.equals(that.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name);
  }

  @Override
  public String toString() {
    return getName();
  }
}
