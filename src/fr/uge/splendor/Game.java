package fr.uge.splendor;
import java.util.*;

public class Game {
    private final List<Player> players = new ArrayList<>();
    private final Map<GameColor,TokenStack> tokenStack = new HashMap<>();
    private final List<CardStack> cardStack;
    private final NobleStack nobleStack = new NobleStack();
    private final ArrayList<Noble> noblesShow = new ArrayList<>();
    private final ArrayList<ArrayList<Card>> cardsShow = new ArrayList<>();
    private int tourNumber = 0;


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
            //break;

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

    private void showState(Player player) {
        System.out.println("C'est le tour de " + player.getName());
        showBoard();
        System.out.println();
        System.out.println("État du joueur :");
        player.showState();
    }

    private void showBoard() {
        showNobles();
        showCards();
        showTokens();
        showScore();
    }

    private void showScore() {
        System.out.print("Scores : ");
        StringJoiner joiner = new StringJoiner("\t");
        for (Player player : players) {
            joiner.add(player.getName() + ": " + player.getPrestigePoints() + "PT");
        }
        System.out.println(joiner);
    }

    private void showCards() {
        cardsShow.forEach(cards -> {
            System.out.print("Niveau " + (cards.get(0).level())+ " : ");
            StringJoiner joiner = new StringJoiner("\t");
            for (Card card : cards) {
                joiner.add(card.toString());
            }
            System.out.println(joiner);
        });
    }

    private void showNobles() {
        System.out.print("Nobles : ");
        StringJoiner joiner = new StringJoiner("\t");
        for (Noble noble : noblesShow) {
            joiner.add(noble.toString());
        }
        System.out.println(joiner);
    }

    private void showTokens() {
        System.out.print("Jetons : ");
        StringJoiner joiner = new StringJoiner("\t");
        for (TokenStack tokenStack : tokenStack.values()) {
            joiner.add(tokenStack.toString());
        }
        System.out.println(joiner);
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
            ArrayList<Card> cards = cardsShow.get(i);
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
    private void playerTakeCard(Player player, Card card){

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
            System.out.println(player.getName() + ", choisissez une action :");
            System.out.println("1. Prendre des jetons");
            System.out.println("2. Acheter une carte");
            System.out.println("3. Réserver une carte");

            Scanner scanner = new Scanner(System.in);
            int choice = scanner.nextInt();
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
                    System.out.println("Choix invalide.");
                    continue;
                }
            }
            break;
        }
    }

    private boolean askPlayerReserveCard(Player player) {
        if (!player.canBorrowCard()){
            System.out.println("Vous ne pouvez pas réserver de carte. Vous avez déjà 3 cartes réservées.");
            return false;
        }
        while (true){
            for (int i = 0; i < cardsShow.size(); i++) {
                for (int j = 0; j < cardsShow.get(i).size(); j++) {
                    if (cardsShow.get(i).get(j) == null) {
                        continue;
                    }
                    int index = i * 4 + j;
                    System.out.print(index + " = " + cardsShow.get(i).get(j) + "\t");
                }
                System.out.println();
            }
            System.out.println("Choisissez une carte à réserver : (-1 pour annuler)");
            Scanner scanner = new Scanner(System.in);
            int cardIndex = scanner.nextInt();
            if (cardIndex == -1) {
                return false;
            }
            int columnIndex = cardIndex / 4;
            int rowIndex = cardIndex % 4;
            if (columnIndex < 0 || columnIndex >= cardsShow.size() || rowIndex < 0 || rowIndex >= cardsShow.get(columnIndex).size()) {
                System.out.println("Index invalide. Veuillez réessayer.");
                continue;
            }
            Card card = cardsShow.get(columnIndex).get(rowIndex);
            if (card == null) {
                System.out.println("Pas de carte disponible à cette position.");
                continue;
            }
            player.borrowCard(card);
            cardsShow.get(columnIndex).set(rowIndex, null);
            System.out.println("Vous avez réservé la carte : " + card);
            return true;
        }
    }

    private boolean askPlayerBuyCard(Player player) {
        //System.out.println("Choisissez une carte à acheter :");

    }

    private boolean askPlayerTakeTokens(Player player) {
        System.out.println("Choisissez les jetons à prendre :");
        HashMap<GameColor, Integer> tmpTokens = new HashMap<>();
        var listTokenCanBeTaken = tokenStack.keySet().stream().filter(color -> color!= GameColor.YELLOW).toList();

        Scanner scanner = new Scanner(System.in);
        while (true){
            for (int i = 0; i < listTokenCanBeTaken.size() ; i++) {
                GameColor color = listTokenCanBeTaken.get(i);
                System.out.println(i + " = " + color + " : " + tokenStack.get(color).remainingTokens());
            }
            int tokenTake = tmpTokens.values().stream().reduce(0, Integer::sum);
            boolean twoSameColor = tmpTokens.values().stream().anyMatch(v -> v == 2);
            if (tokenTake == 3 || twoSameColor){
                break;
            }

            System.out.println("Choisissez l'index de la couleur du jeton :");
            int colorIndex = scanner.nextInt();
            if (colorIndex < 0 || colorIndex >= listTokenCanBeTaken.size()) {
                System.out.println("Index invalide. Veuillez réessayer.");
                continue;
            }
            GameColor color = listTokenCanBeTaken.get(colorIndex);
            if (tokenStack.get(color).remainingTokens() <= 0) {
                System.out.println("Pas de jetons disponibles de cette couleur.");
                continue;
            }
            if(tmpTokens.getOrDefault(color, 0) == 1 && tokenTake == 2){
                System.out.println("Vous ne pouvez pas prendre 2 jetons de la même couleur.");
                continue;
            }
            tmpTokens.merge(color, 1, Integer::sum);
            tokenStack.get(color).takeOne();
            player.addTokens(color, 1);


        }
        System.out.println("Vous avez pris : " + tmpTokens);
        return true;
    }


}