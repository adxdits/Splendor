package fr.uge.splendor.controller;
import fr.uge.splendor.model.*;
import fr.uge.splendor.view.Displayer;
import fr.uge.splendor.view.TerminalDisplayer;
import fr.uge.splendor.tools.TerminalTools;

import java.util.*;
import java.util.stream.Collectors;

public class Game {
    private final GameSettings gameSettings;
    private final List<Player> players = new ArrayList<>();
    private final Board board;
    private int tourNumber = 0;
    private final Displayer displayer = new TerminalDisplayer();

    public Game(GameSettings gameSettings) {
        this.gameSettings = gameSettings;
        CardStack.loadCardFromCSV();
        board = new Board(gameSettings);
        for (int i = 0; i < gameSettings.playerCount(); i++) {
            players.add(new Player(i+1));
        }

    }


    public void play() {
        // Boucle de jeu principale
        while (!haveWinner()){
            int playerIndex = tourNumber % players.size();
            Player currentPlayer = players.get(playerIndex);
            showState(currentPlayer);

            allNobleTryToVisitAllPlayer();
            executeTurn(currentPlayer);

            tourNumber++;
        }
        announceWinner();
    }


    private void executeTurn(Player player) {
        askPlayerAction(player);
        while (player.countTokens() > gameSettings.tokenMax()){
            askPlayerThrowTokens(player);
        }

    }

    private void askPlayerThrowTokens(Player player) {
        TokensBundle playerToken = player.getTokens();
        while (true){
            displayer.throwTokens(playerToken);

            int colorIndex = TerminalTools.getSecurisedInput();
            if (colorIndex == -1) {
                return;
            }
            if (colorIndex < 0 || colorIndex >= GameColor.values().length) {
                displayer.showInvalidChoice();
                continue;
            }
            GameColor color = TokensBundle.getColorsSupported().get(colorIndex);
            int tokenTakeByColor = playerToken.getTokenCount(color);
            if (tokenTakeByColor <= 0) {
                displayer.noMoreTokens();
                continue;
            }
            player.throwToken(new Token(color));
            board.refillTokenStack(color,1);
            displayer.tokenThrow(color);
            break;



        }
    }

    private void showState(Player player) {
        showBoard();
        displayer.displayPlayerTurn(player);
        displayer.displayPlayer(player);
    }

    private void showBoard() {
        displayer.displayNobles(board.peekNobles());
        displayer.displayBoard(board, null);
        displayer.displayTokensStack(board.getStacksState());
        displayer.displayPlayersPoints(players.stream().toList());
    }


    private boolean haveWinner(){
        return players.stream().anyMatch(player -> player.getPrestigePoints() >= 15);
    }

    private void announceWinner() {
        players.stream()
                .max(Comparator.comparingInt(Player::getPrestigePoints))
                .ifPresent(displayer::displayWinner);
    }

    private void allNobleTryToVisitAllPlayer(){
        for (Player player : players) {
            for (int i = 0; i < board.getNobleCount();i++){
                Noble noble = board.peekNoble(i);
                if (player.getAdvantages().covers(noble.cost())) {
                    board.nobleHasBeenTaken(noble);
                    player.takeNoble(noble);
                }
            }
        }
    }

    private void askPlayerAction(Player player) {
        // Demander à chaque joueur de choisir une action
        while (true) {
            displayer.displayActions(gameSettings.useSimplePlay());
            int choice = TerminalTools.getSecurisedInput();
            switch (choice) {
                case 1 -> {
                    if (!askPlayerTakeTokens(player)){
                        continue;
                    }
                }
                case 2 -> {
                    if(!askPlayerBuyCard(player)){
                        continue;
                    }
                }
                case 3 -> {
                    if(gameSettings.useSimplePlay()){
                        displayer.showInvalidChoice();
                        continue;
                    }
                    if (!askPlayerReserveCard(player)){
                        continue;
                    }
                }
                default -> {
                    displayer.showInvalidChoice();
                    continue;
                }
            }
            break;
        }
    }

    private boolean askPlayerReserveCard(Player player) {
        if (!player.canBorrowCard()){
            displayer.cantBorrowCard(gameSettings.borrowedCardsMax());
            return false;
        }
        while (true){
            displayer.displayBoard(board, 0);
            displayer.askToBorrowCard();

            int cardIndex = TerminalTools.getSecurisedInput();
            int columnIndex = cardIndex / Board.CARDS_BY_LEVEL; // is it also the level of the card
            int rowIndex = cardIndex % Board.CARDS_BY_LEVEL;
            if (columnIndex < 0 || columnIndex >= board.getNbOfLevels() || rowIndex < 0 ) {
                displayer.showInvalidChoice();
                continue;
            }
            Card card = board.peekCard(columnIndex+1,rowIndex);
            if (card == null) {
                displayer.showInvalidChoice();
                continue;
            }
            player.borrowCard(card);
            board.cardTaken(card);
            player.addTokens(GameColor.YELLOW, 1);
            board.takeToken(GameColor.YELLOW, 1);
            return true;
        }
    }

    private boolean askPlayerBuyCard(Player player) {
        while (true){
            List<Card> borrowedCards = player.getBorrowedCards();

            int index = displayer.displayBoard(board, 0);
            displayer.displayBorrowedCards(borrowedCards, index);
            displayer.askToBuyCard();
            int cardIndex = TerminalTools.getSecurisedInput();
            if (cardIndex == -1) {
                return false;
            }
            if (cardIndex >= board.getNbOfLevels() * Board.CARDS_BY_LEVEL){
                int borrowedCardIndex = cardIndex - board.getNbOfLevels() * Board.CARDS_BY_LEVEL;
                if (borrowedCardIndex < 0 || borrowedCardIndex >= borrowedCards.size()) {
                    displayer.showInvalidChoice();
                    continue;
                }
                Card card = borrowedCards.get(borrowedCardIndex);
                if (card == null) {
                    displayer.showInvalidChoice();
                    continue;
                }
                if (player.canBuyCard(card)) {
                    TokensBundle removedTokens = player.buyCard(card);
                    board.refillTokenStack(removedTokens);
                    return true;
                } else {
                    displayer.cantBuyCard();
                }
            }
            int columnIndex = cardIndex / Board.CARDS_BY_LEVEL;
            int rowIndex = cardIndex % Board.CARDS_BY_LEVEL;
            if (columnIndex < 0 || columnIndex >= board.getNbOfLevels() || rowIndex < 0) {
                displayer.showInvalidChoice();
                continue;
            }
            Card card = board.peekCard(columnIndex+1,rowIndex);
            if (card == null) {
                displayer.showInvalidChoice();
                continue;
            }
            if (player.canBuyCard(card)) {
                TokensBundle removedTokens = player.buyCard(card);
                board.refillTokenStack(removedTokens);
                board.cardTaken(card);
                return true;
            }
            displayer.cantBuyCard();
        }


    }

    private boolean askPlayerTakeTokens(Player player) {
        TokensBundle tmpTokens = new TokensBundle();
        var listTokenCanBeTaken = TokensBundle.getColorsSupported().stream().filter(color -> color!= GameColor.YELLOW).toList();
        TokensBundle dataStackTokenCount = new TokensBundle(listTokenCanBeTaken.stream().collect(Collectors.toMap(color -> color, board::getTokenCount)));
        while (true){
            displayer.displayStacksTaken(listTokenCanBeTaken,dataStackTokenCount, tmpTokens);
            displayer.askToTakeTokens();

            int tokenTake = tmpTokens.getTotalTokens();
            boolean twoSameColor = listTokenCanBeTaken.stream().mapToInt(tmpTokens::getTokenCount).anyMatch(e->e==2); // tmpTokens.values().stream().anyMatch(v -> v == 2);
            if (tokenTake == 3 || twoSameColor){
                break;
            }

            int colorIndex = TerminalTools.getSecurisedInput();
            if (colorIndex == -1) {
                return false;
            }
            if (colorIndex < 0 || colorIndex >= listTokenCanBeTaken.size()) {
                displayer.showInvalidChoice();
                continue;
            }
            GameColor color = listTokenCanBeTaken.get(colorIndex);
            int tokenTakeByColor = tmpTokens.getTokenCount(color);
            if (board.getTokenCount(color) - tokenTakeByColor <= 0) {
                displayer.noMoreTokens();
                continue;
            }
            if(tmpTokens.getTokenCount(color) == 1 && tokenTake == 2){
                displayer.warn2TokensPerColor();
                continue;
            }
            // Si le joueur essaye de prendre un second jeton alors que la pile est a moins de 4 jetons
            if (tokenTakeByColor == 1 && board.getTokenCount(color) < 4) {
                displayer.warn2TokensImpossibleMove();
                continue;
            }
            tmpTokens.addToken(color,1);
        }

        player.addTokens(tmpTokens);

        TokensBundle.getColorsSupported().forEach(color -> {
            int quantity = tmpTokens.getTokenCount(color);
            board.takeToken(color, quantity);
        });
        return true;
    }




}