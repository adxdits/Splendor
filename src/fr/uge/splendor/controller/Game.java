package fr.uge.splendor.controller;
import fr.uge.splendor.model.*;
import fr.uge.splendor.view.TerminalDisplayer;
import fr.uge.splendor.tools.TerminalTools;

import java.util.*;

public class Game {
    private final GameSettings gameSettings;
    private final List<Player> players = new ArrayList<>();
    private final Map<GameColor, TokenStack> tokenStack = new TreeMap<>();
    private final List<CardStack> cardStack;
    private final NobleStack nobleStack = new NobleStack();
    private final ArrayList<Noble> noblesShow = new ArrayList<>();
    private final List<List<Card>> cardsShow = new ArrayList<>();
    private int tourNumber = 0;
    private final TerminalDisplayer displayer = new TerminalDisplayer();

    public Game(GameSettings gameSettings) {
        this.gameSettings = gameSettings;
        CardStack.loadCardFromCSV();
        if(gameSettings.useSimplePlay()){
            cardStack = List.of(new CardStack(true));
        }else{
            cardStack = List.of(new CardStack(1),new CardStack(2),new CardStack(3));
        }

        for (int i = 0; i < gameSettings.playerCount(); i++) {
            players.add(new Player(i+1));
        }
        prepareBoard();

    }

    private int getNoblesToShow(){
        return players.size()+1;
    }

    private void prepareNobles() {
        if (gameSettings.useSimplePlay()){
            return;
        }
        int noblesToShow = getNoblesToShow();
        for (int i = 0; i < noblesToShow; i++) {
            Noble noble = nobleStack.takeOne();
            noblesShow.add(noble);
        }
    }


    private void prepareBoard() {
        prepareTokens();
        prepareCards();
        prepareNobles();

    }

    private void prepareTokens() {
        Arrays.stream(GameColor.values()).forEach(color -> tokenStack.put(color, new TokenStack(color, players.size())));
    }

    private void prepareCards() {
        for (CardStack stack : cardStack) {
            ArrayList<Card> cards = new ArrayList<>();
            for (int i = 0; i < 4; i++) {
                if (!stack.isEmpty()) {
                    Card card = stack.takeOne();
                    cards.add(card);
                }
            }
            cardsShow.add(cards);
        }
    }

    public void play() {
        // Boucle de jeu principale
        while (!haveWinner()){
            int playerIndex = tourNumber % players.size();
            Player currentPlayer = players.get(playerIndex);
            showState(currentPlayer);

            allNobleTryToVisitAllPlayer();
            refillCardsShowed();
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
        System.out.println(TerminalTools.askText("Vous avez trop de jetons. Veuillez en jeter"));
        Map<GameColor,Integer> playerToken = player.getTokens();
        while (true){

            displayer.displayPlayerTokens(playerToken, 0);
            System.out.println(TerminalTools.askText("Choisissez la couleur du jeton à jeter : "));
            int colorIndex = TerminalTools.getSecurisedInput();
            if (colorIndex == -1) {
                return;
            }
            if (colorIndex < 0 || colorIndex >= playerToken.size()) {
                displayer.showInvalidChoice();
                continue;
            }
            GameColor color = player.getTokens().keySet().stream().toList().get(colorIndex);
            int tokenTakeByColor = playerToken.getOrDefault(color, 0);
            if (tokenTakeByColor <= 0) {
                System.out.println(TerminalTools.warningText("Pas de jetons disponibles de cette couleur."));
                continue;
            }
            player.throwToken(new Token(color));
            tokenStack.get(color).refill(1);
            System.out.println(TerminalTools.confirmText("Vous avez jeté le jeton : " + color));
            break;



        }
    }

    private void showState(Player player) {
        showBoard();
        displayer.displayPlayerTurn(player);
        displayer.displayPlayer(player);
    }

    private void showBoard() {
        displayer.displayNobles(noblesShow);
        displayer.displayBoard(cardsShow, null);
        displayer.displayTokensStack(tokenStack.values().stream().toList());
        displayer.displayPlayersPoints(players.stream().toList());
    }


    private boolean haveWinner(){
        return players.stream().anyMatch(player -> player.getPrestigePoints() >= 15);
    }

    private void announceWinner() {
        players.stream()
                .max(Comparator.comparingInt(Player::getPrestigePoints))
                .ifPresent(winner -> System.out.println("Le gagnant est " + winner.getName() + " avec " + winner.getPrestigePoints() + " points de prestige !"));
    }

    private void refillCardsShowed(){
        for (int i = 0; i < cardsShow.size(); i++) { // we need i for know which level we need
            List<Card> cards = cardsShow.get(i);
            for (int j = 0; j < cards.size(); j++) {
                if (cards.get(j) == null) {
                    Card card = cardStack.get(i).takeOne();
                    cards.set(j, card);
                }
            }
        }
    }

    private void refillTokenStack(Map<GameColor, Integer> tokens){
        for (Map.Entry<GameColor, Integer> entry : tokens.entrySet()) {
            GameColor color = entry.getKey();
            int quantity = entry.getValue();
            tokenStack.computeIfPresent(color, (_, v) -> {
                v.refill(quantity);
                return v;
            });
        }

    }

    private void allNobleTryToVisitAllPlayer(){
        for (Player player : players) {
            for (Noble noble : noblesShow) {
                if (player.canGetNoble(noble)) {
                    player.takeNoble(noble);
                    noblesShow.remove(noble);
                    break;
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
                    refillCardsShowed();
                }
                case 3 -> {
                    if(gameSettings.useSimplePlay()){
                        displayer.showInvalidChoice();
                        continue;
                    }
                    if (!askPlayerReserveCard(player)){
                        continue;
                    }
                    refillCardsShowed();
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
            System.out.println(TerminalTools.warningText("Vous ne pouvez pas réserver de carte. Vous avez déjà " + gameSettings.borrowedCardsMax() + " cartes réservées."));
            return false;
        }
        while (true){
            displayer.displayBoard(cardsShow, 0);

            System.out.println(TerminalTools.askText("Choisissez une carte à réserver : (-1 pour annuler)"));
            int cardIndex = TerminalTools.getSecurisedInput();
            int columnIndex = cardIndex / 4;
            int rowIndex = cardIndex % 4;
            if (columnIndex < 0 || columnIndex >= cardsShow.size() || rowIndex < 0 || rowIndex >= cardsShow.get(columnIndex).size()) {
                displayer.showInvalidChoice();
                continue;
            }
            Card card = cardsShow.get(columnIndex).get(rowIndex);
            if (card == null) {
                System.out.println(TerminalTools.warningText("Pas de carte disponible à cette position."));
                continue;
            }
            player.borrowCard(card);
            player.addTokens(GameColor.YELLOW, 1);
            tokenStack.get(GameColor.YELLOW).takeOne();
            cardsShow.get(columnIndex).set(rowIndex, null);

            System.out.println(TerminalTools.confirmText("Vous avez réservé la carte : " + card));
            return true;
        }
    }

    private boolean askPlayerBuyCard(Player player) {
        while (true){
            List<Card> borrowedCards = player.getBorrowedCards();

            int index = displayer.displayBoard(cardsShow, 0);
            displayer.displayBorrowedCards(borrowedCards, index);
            System.out.println(TerminalTools.askText("Choisissez une carte à acheter : (-1 pour annuler)"));
            int cardIndex = TerminalTools.getSecurisedInput();
            if (cardIndex == -1) {
                return false;
            }
            if (cardIndex >= cardsShow.size() * cardsShow.getFirst().size()){
                int borrowedCardIndex = cardIndex - cardsShow.size() * cardsShow.getFirst().size();
                if (borrowedCardIndex < 0 || borrowedCardIndex >= borrowedCards.size()) {
                    displayer.showInvalidChoice();
                    continue;
                }
                Card card = borrowedCards.get(borrowedCardIndex);
                if (card == null) {
                    System.out.println(TerminalTools.warningText("Pas de carte disponible à cette position."));
                    continue;
                }
                if (player.canBuyCard(card)) {
                    Map<GameColor, Integer> removedTokens = player.buyCard(card);
                    System.out.println(TerminalTools.confirmText("Vous avez acheté la carte : " + card));
                    refillTokenStack(removedTokens);
                    return true;
                } else {
                    System.out.println(TerminalTools.warningText("Vous ne pouvez pas acheter cette carte."));
                }
            }
            int columnIndex = cardIndex / 4;
            int rowIndex = cardIndex % 4;
            if (columnIndex < 0 || columnIndex >= cardsShow.size() || rowIndex < 0 || rowIndex >= cardsShow.get(columnIndex).size()) {
                displayer.showInvalidChoice();
                continue;
            }
            Card card = cardsShow.get(columnIndex).get(rowIndex);
            if (card == null) {
                displayer.showInvalidChoice();
                continue;
            }
            if (player.canBuyCard(card)) {
                Map<GameColor, Integer> removedTokens = player.buyCard(card);
                System.out.println(TerminalTools.confirmText("Vous avez acheté la carte : " + card));
                refillTokenStack(removedTokens);
                cardsShow.get(columnIndex).set(rowIndex, null);
                return true;
            } else {
                System.out.println(TerminalTools.warningText("Vous ne pouvez pas acheter cette carte."));
            }
        }


    }

    private boolean askPlayerTakeTokens(Player player) {
        System.out.println(TerminalTools.askText("Choisissez les jetons à prendre : (-1 pour annuler)"));
        Map<GameColor, Integer> tmpTokens = new TreeMap<>();
        var listTokenCanBeTaken = tokenStack.keySet().stream().filter(color -> color!= GameColor.YELLOW).toList();

        while (true){
            displayer.displayStacksTaken(tokenStack.values().stream().toList(), tmpTokens);
            int tokenTake = tmpTokens.values().stream().reduce(0, Integer::sum);
            boolean twoSameColor = tmpTokens.values().stream().anyMatch(v -> v == 2);
            if (tokenTake == 3 || twoSameColor){
                break;
            }

            System.out.println(TerminalTools.askText("Choisissez l'index de la couleur du jeton :"));
            int colorIndex = TerminalTools.getSecurisedInput();
            if (colorIndex == -1) {
                return false;
            }
            if (colorIndex < 0 || colorIndex >= listTokenCanBeTaken.size()) {
                displayer.showInvalidChoice();
                continue;
            }
            GameColor color = listTokenCanBeTaken.get(colorIndex);
            int tokenTakeByColor = tmpTokens.getOrDefault(color, 0);
            if (tokenStack.get(color).remainingTokens() - tokenTakeByColor <= 0) {
                System.out.println(TerminalTools.warningText("Pas de jetons disponibles de cette couleur."));
                continue;
            }
            if(tmpTokens.getOrDefault(color, 0) == 1 && tokenTake == 2){
                System.out.println(TerminalTools.warningText("Vous ne pouvez pas prendre 2 jetons de la même couleur."));
                continue;
            }
            // Si le joueur essaye de prendre un second jeton alors que la pile est a moins de 4 jetons
            if (tokenTakeByColor == 1 && tokenStack.get(color).remainingTokens() < 4) {
                System.out.println(TerminalTools.warningText("Vous ne pouvez pas prendre un second jeton de cette couleur."));
                continue;
            }
            tmpTokens.merge(color, 1, Integer::sum);
        }

        for (Map.Entry<GameColor, Integer> entry : tmpTokens.entrySet()) {
            GameColor color = entry.getKey();
            int quantity = entry.getValue();
            player.addTokens(color, quantity);
            for (int i = 0; i < quantity; i++) {
                tokenStack.get(color).takeOne();
            }
        }

        System.out.println(TerminalTools.confirmText("Vous avez pris : " + tmpTokens));
        return true;
    }


}