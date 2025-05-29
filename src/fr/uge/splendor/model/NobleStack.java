package fr.uge.splendor.model;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class NobleStack {
    private final java.util.Stack<Noble> stack;

    public NobleStack() {
        this.stack = new java.util.Stack<>();
        stack.addAll(getNobles());
        Collections.shuffle(stack);
    }

    public Noble takeOne() {
        if (stack.isEmpty()) {
            throw new IllegalStateException("No nobles left in the stack");
        }
        return stack.pop();
    }

    private List<Noble> getNobles() {
        return List.of(
                new Noble(3, new TokensBundle(Map.of(GameColor.GREEN, 3, GameColor.BLUE, 3,GameColor.RED,3))), // catherine
                new Noble(3, new TokensBundle(Map.of(GameColor.BLACK, 3, GameColor.BLUE, 3,GameColor.WHITE,3))), // elisabeth
                new Noble(3, new TokensBundle(Map.of(GameColor.BLACK, 4, GameColor.WHITE, 4))), // isabella
                new Noble(3, new TokensBundle(Map.of(GameColor.BLUE, 4, GameColor.WHITE, 4))), // niccolo
                new Noble(3, new TokensBundle(Map.of(GameColor.BLUE, 4,GameColor.GREEN, 4))), //suleiman
                new Noble(3, new TokensBundle(Map.of(GameColor.GREEN, 3, GameColor.BLUE, 3,GameColor.WHITE,3))), // anne
                new Noble(3, new TokensBundle(Map.of(GameColor.BLACK, 3, GameColor.RED, 3,GameColor.WHITE,3))), // charles
                new Noble(3, new TokensBundle(Map.of(GameColor.BLACK, 3, GameColor.RED, 3,GameColor.GREEN,3))), // francis
                new Noble(3, new TokensBundle(Map.of(GameColor.BLACK, 4,GameColor.RED, 4))), //henry
                new Noble(3, new TokensBundle(Map.of(GameColor.GREEN, 4,GameColor.RED, 4))) //mary
        );
    }
}
