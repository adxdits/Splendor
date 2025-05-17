package fr.uge.splendor;

import java.util.Map;

public record Card(
        int level,
        GameColor color,
        int prestigePoints,
        Map<GameColor, Integer> cost
) implements Item {}