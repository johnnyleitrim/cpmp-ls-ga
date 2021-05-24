package com.johnnyleitrim.cpmp;

import java.util.List;

public class Random {

  private static java.util.Random RANDOM = new java.util.Random();

  public static void setRandom(java.util.Random random) {
    Random.RANDOM = random;
  }

  public static void setRandomSeed(long seed) {
    Random.RANDOM = new java.util.Random(seed);
  }

  public static int getRandomIndex(int bounds) {
    return RANDOM.nextInt(bounds);
  }

  public static <T> int getRandomIndex(List<T> items) {
    return getRandomIndex(items.size());
  }

  public static <T> T getRandomItem(List<T> items) {
    return items.get(getRandomIndex(items));
  }
}
