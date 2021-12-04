package com.johnnyleitrim.cpmp.utils;

import java.util.Arrays;
import java.util.List;

import com.johnnyleitrim.cpmp.ls.Move;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MoveUtilsTest {

  @Test
  public void itFindsTransitiveMoves() {
    List<Move> moves = List.of(
        new Move(8, 4),
        new Move(8, 7),
        new Move(1, 2),
        new Move(3, 4),
        new Move(2, 5),
        new Move(4, 2)
    );

    List<List<Move>> transitiveMoves = MoveUtils.findTransientMoves(moves, 10);

    assertThat(transitiveMoves).containsExactly(
        Arrays.asList(new Move(1, 2), new Move(2, 5))
    );
  }

  @Test
  public void itRemovesTransitiveMoves() {
    List<Move> moves = List.of(
        new Move(1, 2),
        new Move(3, 4),
        new Move(2, 5),
        new Move(4, 2)
    );

    List<Move> newMoves = MoveUtils.removeTransientMoves(moves, 5);

    assertThat(newMoves).containsExactly(
        new Move(1, 5),
        new Move(3, 2)
    );
  }

  @Test
  public void itRemovesSequentialTransitiveMoves() {
    List<Move> moves = MoveUtils.parseMoves("[1->2, 2->3, 3->4, 2->3]");
    List<Move> newMoves = MoveUtils.removeTransientMoves(moves, 4);
    assertThat(newMoves).containsExactly(
        new Move(1, 4),
        new Move(2, 3)
    );
  }

  @Test
  public void itParsesMoves() {
    String movesString = "[10->2, 2->3, 1->4]";

    List<Move> actualMoves = MoveUtils.parseMoves(movesString);

    assertThat(actualMoves).containsExactly(
        new Move(10, 2),
        new Move(2, 3),
        new Move(1, 4)
    );
  }

}
