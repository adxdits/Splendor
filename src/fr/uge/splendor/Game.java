package fr.uge.splendor;
import java.util.*;

public class Game {
    private static final int TOKEN_MAX = 15;
    private final List<Player> players = new ArrayList<>();
    private final Map<GameColor,TokenStack> tokenStack = new TreeMap<>();
    private final List<CardStack> cardStack;
    private final NobleStack nobleStack = new NobleStack();
    private final ArrayList<Noble> noblesShow = new ArrayList<>();
    private final List<List<Card>> cardsShow = new ArrayList<>();
    private int tourNumber = 0;
    private TerminalDisplayer displayer = new TerminalDisplayer();

    public Game(int numPlayers) {
        CardStack.loadCardFromCSV();
        cardStack = List.of(new CardStack(1),new CardStack(2),new CardStack(3));

        for (int i = 0; i < numPlayers; i++) {
            players.add(new Player(i+1));
        }
        prepareBoard();

    }

    private int getNoblesToShow(){
        return players.size()+1;
    }

    private void prepareNobles() {
        int noblesToShow = getNoblesToShow();
        for (int i = 0; i < noblesToShow; i++) {
            Noble noble = (Noble) nobleStack.takeOne();
            noblesShow.add(noble);
        }
    }


    private void prepareBoard() {
        prepareTokens();
        prepareNobles();
        prepareCards();
    }

    private void prepareTokens() {
        Arrays.stream(GameColor.values()).forEach(color -> tokenStack.put(color, new TokenStack(color, players.size())));
    }

    private void prepareCards() {
        for (CardStack stack : cardStack) {
            ArrayList<Card> cards = new ArrayList<>();
            for (int i = 0; i < 4; i++) {
                if (!stack.isEmpty()) {
                    Card card = (Card) stack.takeOne();
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
        /*while (!cardStack.isEmpty()) {
            for (Player player : players) {
                executeTurn(player);
                if (cardStack.isEmpty()) break;
            }
        }
        announceWinner();*/
    }


    private void executeTurn(Player player) {
        askPlayerAction(player);
        while (player.countTokens() > TOKEN_MAX){
            askPlayerThrowTokens(player);
        }

        /*
        // Prendre 2 jetons de même couleur
        if (!tokenStack.isEmpty()) {
            Token token = (Token) tokenStack.get(0).takeOne();
            player.addTokens(token.color(), 2);
        }

        // Acheter une carte si possible
        if (!cardStack.isEmpty()) {
            Card card = (Card) cardStack.get(0).takeOne();
            if (player.canBuyCard(card)) {
                player.buyCard(card);
            }
        }*/
    }

    private void askPlayerThrowTokens(Player player) {
        System.out.println(TerminalTools.askText("Vous avez trop de jetons. Veuillez en jeter"));
        Map<GameColor,Integer> playerToken = player.getTokens();
        while (true){

            displayer.displayPlayerTokens(playerToken, 0);
            System.out.println(TerminalTools.askText("Choisissez la couleur du jeton à jeter : "));
            Scanner scanner = new Scanner(System.in);
            String choiceStr = scanner.next();
            int colorIndex;
            try {
                colorIndex = Integer.parseInt(choiceStr);
            } catch (NumberFormatException e) {
                System.out.println(TerminalTools.warningText("Choix invalide. Veuillez entrer un nombre."));
                continue;
            }
            if (colorIndex == -1) {
                return;
            }
            if (colorIndex < 0 || colorIndex >= playerToken.size()) {
                System.out.println(TerminalTools.warningText("Index invalide. Veuillez réessayer."));
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
                    Card card = (Card) cardStack.get(i).takeOne();
                    cards.set(j, card);
                }
            }
        }
    }

    private void refillTokenStack(Map<GameColor, Integer> tokens){
        for (Map.Entry<GameColor, Integer> entry : tokens.entrySet()) {
            GameColor color = entry.getKey();
            int quantity = entry.getValue();
            tokenStack.computeIfPresent(color, (k, v) -> {
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
            System.out.println(TerminalTools.askText( player.getName() + ", choisissez une action :"));
            System.out.println(TerminalTools.interactiveText("1. Prendre des jetons"));
            System.out.println(TerminalTools.interactiveText("2. Acheter une carte"));
            System.out.println(TerminalTools.interactiveText("3. Réserver une carte"));

            Scanner scanner = new Scanner(System.in);
            String choiceStr = scanner.next();
            int choice;
            try {
                choice = Integer.parseInt(choiceStr);
            } catch (NumberFormatException e) {
                System.out.println(TerminalTools.warningText("Choix invalide. Veuillez entrer un nombre."));
                continue;
            }
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
                    if (!askPlayerReserveCard(player)){
                        continue;
                    }
                    refillCardsShowed();
                }
                default -> {
                    System.out.println(TerminalTools.warningText("Choix invalide."));
                    continue;
                }
            }
            break;
        }
    }

    private boolean askPlayerReserveCard(Player player) {
        if (!player.canBorrowCard()){
            System.out.println(TerminalTools.warningText("Vous ne pouvez pas réserver de carte. Vous avez déjà 3 cartes réservées."));
            return false;
        }
        while (true){
            System.out.println("Cartes disponibles : ");
            displayer.displayBoard(cardsShow, 0);

            System.out.println(TerminalTools.askText("Choisissez une carte à réserver : (-1 pour annuler)"));
            Scanner scanner = new Scanner(System.in);
            String choiceStr = scanner.next();
            int cardIndex;
            try {
                cardIndex = Integer.parseInt(choiceStr);
            } catch (NumberFormatException e) {
                System.out.println(TerminalTools.warningText("Choix invalide. Veuillez entrer un nombre."));
                continue;
            }
            if (cardIndex == -1) {
                return false;
            }
            int columnIndex = cardIndex / 4;
            int rowIndex = cardIndex % 4;
            if (columnIndex < 0 || columnIndex >= cardsShow.size() || rowIndex < 0 || rowIndex >= cardsShow.get(columnIndex).size()) {
                System.out.println(TerminalTools.warningText("Index invalide. Veuillez réessayer."));
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

            displayer.displayBorrowedCards(borrowedCards, null);
            System.out.println(TerminalTools.askText("Choisissez une carte à acheter : (-1 pour annuler)"));
            Scanner scanner = new Scanner(System.in);
            String choiceStr = scanner.next();
            int cardIndex;
            try {
                cardIndex = Integer.parseInt(choiceStr);
            } catch (NumberFormatException e) {
                System.out.println(TerminalTools.warningText("Choix invalide. Veuillez entrer un nombre."));
                continue;
            }
            if (cardIndex == -1) {
                return false;
            }
            if (cardIndex >= cardsShow.size() * cardsShow.getFirst().size()){
                int borrowedCardIndex = cardIndex - cardsShow.size() * cardsShow.getFirst().size();
                if (borrowedCardIndex < 0 || borrowedCardIndex >= borrowedCards.size()) {
                    System.out.println(TerminalTools.warningText("Index invalide. Veuillez réessayer."));
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
                System.out.println(TerminalTools.warningText("Index invalide. Veuillez réessayer."));
                continue;
            }
            Card card = cardsShow.get(columnIndex).get(rowIndex);
            if (card == null) {
                System.out.println(TerminalTools.warningText("Pas de carte disponible à cette position."));
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

        Scanner scanner = new Scanner(System.in);
        while (true){
            displayer.displayStacksTaken(tokenStack.values().stream().toList(), tmpTokens);
            int tokenTake = tmpTokens.values().stream().reduce(0, Integer::sum);
            boolean twoSameColor = tmpTokens.values().stream().anyMatch(v -> v == 2);
            if (tokenTake == 3 || twoSameColor){
                break;
            }

            System.out.println(TerminalTools.askText("Choisissez l'index de la couleur du jeton :"));
            String choiceStr = scanner.next();
            int colorIndex;
            try {
                colorIndex = Integer.parseInt(choiceStr);
            } catch (NumberFormatException e) {
                System.out.println(TerminalTools.warningText("Choix invalide. Veuillez entrer un nombre."));
                continue;
            }
            if (colorIndex == -1) {
                return false;
            }
            if (colorIndex < 0 || colorIndex >= listTokenCanBeTaken.size()) {
                System.out.println(TerminalTools.warningText("Index invalide. Veuillez réessayer."));
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
            tmpTokens.merge(color, 1, Integer::sum);
        }
        for (Map.Entry<GameColor, Integer> entry : tmpTokens.entrySet()) {
            GameColor color = entry.getKey();
            int quantity = entry.getValue();
            player.addTokens(color, quantity);
            tokenStack.get(color).takeOne();
        }

        System.out.println(TerminalTools.confirmText("Vous avez pris : " + tmpTokens));
        return true;
    }


}