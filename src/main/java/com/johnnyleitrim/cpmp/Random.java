package com.johnnyleitrim.cpmp;

import java.util.List;

public class Random {

  private static ThreadLocal<java.util.Random> RANDOM = ThreadLocal.withInitial(java.util.Random::new);

  public static void setRandom(java.util.Random random) {
    Random.RANDOM = ThreadLocal.withInitial(() -> random);
  }

  public static void setRandomSeed(long seed) {
    RANDOM.get().setSeed(seed);
  }

  public static int getRandomIndex(int bounds) {
    return RANDOM.get().nextInt(bounds);
  }

  public static <T> int getRandomIndex(List<T> items) {
    return getRandomIndex(items.size());
  }

  public static <T> T getRandomItem(List<T> items) {
    return items.get(getRandomIndex(items));
  }
}
