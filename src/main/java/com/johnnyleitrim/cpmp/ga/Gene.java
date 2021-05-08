package com.johnnyleitrim.cpmp.ga;

import java.util.Objects;

public class Gene {

  private final int sourceStack;

  private final int destinationStack;

  public Gene(int sourceStack, int destinationStack) {
    assert (sourceStack != destinationStack);
    this.sourceStack = sourceStack;
    this.destinationStack = destinationStack;
  }

  public int getSourceStack() {
    return sourceStack;
  }

  public int getDestinationStack() {
    return destinationStack;
  }

  public Gene copy() {
    return new Gene(sourceStack, destinationStack);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o != null && getClass() == o.getClass()) {
      Gene other = (Gene) o;
      return sourceStack == other.sourceStack && destinationStack == other.destinationStack;
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hash(sourceStack, destinationStack);
  }

  @Override
  public String toString() {
    StringBuilder str = new StringBuilder();
    str.append(sourceStack);
    str.append("->");
    str.append(destinationStack);
    return str.toString();
  }
}
