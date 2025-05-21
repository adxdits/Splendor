package fr.uge.splendor.controller;

public record GameSettings(boolean useSimplePlay, int playerCount, int tokenMax, int borrowedCardsMax) {
    public GameSettings{
        if (playerCount < 2 || playerCount > 4) {
            throw new IllegalArgumentException("Player count must be between 2 and 4");
        }
        if (tokenMax < 0) {
            throw new IllegalArgumentException("Token max cannot be negative");
        }
        if (borrowedCardsMax < 0) {
            throw new IllegalArgumentException("Borrowed cards max cannot be negative");
        }
    }

    public GameSettings(boolean useSimplePlay,int playerCount) {
        this(useSimplePlay,playerCount, 10, 3);
    }

}
