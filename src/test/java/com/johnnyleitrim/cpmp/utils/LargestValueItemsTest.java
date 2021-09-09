package com.johnnyleitrim.cpmp.utils;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class LargestValueItemsTest {

  @Test
  public void itWorks() {
    LargestValueItems<String> longestStrings = new LargestValueItems<>(10);
    assertThat(longestStrings.getItems()).isEmpty();

    String one = "one";
    longestStrings.add(one.length(), one);
    assertThat(longestStrings.getItems()).contains(one);

    String two = "two";
    longestStrings.add(two.length(), two);
    assertThat(longestStrings.getItems()).containsExactly(one, two);

    String three = "three";
    longestStrings.add(three.length(), three);
    assertThat(longestStrings.getItems()).containsExactly(three);
  }
}
