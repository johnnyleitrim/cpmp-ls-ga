package com.johnnyleitrim.cpmp.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;

public class LargestValueItems<T> {

  private final List<T> items;
  private OptionalInt largestValue = OptionalInt.empty();

  public LargestValueItems(int nValues) {
    items = new ArrayList<>(nValues);
  }

  public void add(int value, T item) {
    if (largestValue.isEmpty() || largestValue.getAsInt() < value) {
      items.clear();
      items.add(item);
      largestValue = OptionalInt.of(value);
    } else if (largestValue.getAsInt() == value) {
      items.add(item);
      largestValue = OptionalInt.of(value);
    }
  }

  public List<T> getItems() {
    return items;
  }
}
