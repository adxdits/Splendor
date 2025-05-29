package fr.uge.splendor.view;

import fr.uge.splendor.model.*;

import java.util.List;

public interface Displayer {
    void askToBorrowCard();

    void askToBuyCard();

    void askToTakeTokens();

    void cantBorrowCard(int nbBorrowedCards);

    void cantBuyCard();

    void displayActions(boolean useSimpleGame);

    int displayBoard(Board board, Integer interactibleKey);

    void displayBorrowedCards(List<Card> borrowedCards, Integer interactibleKey);

    void displayNobles(List<Noble> nobles);

    void displayPlayer(Player player);

    void displayPlayerTokenWithAdvantages(TokensBundle tokens, TokensBundle advantages);

    int displayPlayerTokens(TokensBundle tokens, Integer interactibleKey);

    void displayPlayerTurn(Player player);

    void displayPlayersPoints(List<Player> players);

    int displayStacksTaken(List<GameColor> colorOrder, TokensBundle tokenStacks, TokensBundle tokensTaken);

    void displayTokensStack(TokensBundle tokens);

    void displayWinner(Player winner);

    void noMoreTokens();

    void showInvalidChoice();

    void throwTokens(TokensBundle playerToken);

    void tokenThrow(GameColor color);

    void warn2TokensImpossibleMove();

    void warn2TokensPerColor();

}
