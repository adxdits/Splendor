package fr.uge.splendor;

import java.util.List;
import java.util.Map;

public class TerminalDisplayer {
    public TerminalDisplayer(){
    }

    public int displayBoard(List<List<Card>> cards, Integer interactibleKey){

        System.out.println("Cartes disponibles :");
        for (int i = 0; i < cards.size(); i++) {
            System.out.println("Level " + (i + 1) + ":");
            for (int j = 0; j < cards.get(i).size(); j++) {
                Card card = cards.get(i).get(j);
                String content = card != null ? card.toString() : "Vide";
                if (interactibleKey != null) {
                    System.out.println(TerminalTools.interactiveText("\t" + interactibleKey + " = " + content));
                    interactibleKey++;
                }
                else {
                    System.out.println("\t" + content);
                }
            }
        }
        return interactibleKey == null ? 0 : interactibleKey;
    }

    public void displayTokensStack(List<TokenStack> tokens){
        // display inline
        System.out.print("Pile de jetons : ");
        for (TokenStack tokenStack : tokens) {
            System.out.print(tokenStack.toString() + " | ");
        }
        System.out.println();
    }



    public int displayStacksTaken(List<TokenStack> tokens, Map<GameColor, Integer> takenStacks){
        int interactibleKey = 0;
        System.out.println("Jetons disponibles (jeton pris) : ");
        for (TokenStack tokenStack : tokens) {
            if (tokenStack.getColor() == GameColor.YELLOW) continue;
            String content = tokenStack.toString() + " (pris : " + takenStacks.getOrDefault(tokenStack.getColor(),0) + ")";
            System.out.println(TerminalTools.interactiveText("\t" + interactibleKey + " = " + content));
            interactibleKey++;

        }
        System.out.println();
        return interactibleKey;
    }

    public void displayBorrowedCards(List<Card> borrowedCards, Integer interactibleKey){
        System.out.println("Cartes empruntées :");
        for (Card card : borrowedCards) {
            String content = card.toString();
            if (interactibleKey != null) {
                System.out.println(TerminalTools.interactiveText("\t" + interactibleKey + " = " + content));
                interactibleKey++;
            }
            else {
                System.out.println("\t" + content);
            }
        }
    }

    public void displayNobles(List<Noble> nobles){
        System.out.println("Nobles :");
        for (Noble noble : nobles) {
            String content = noble.toString();
            System.out.println("\t" + content);
        }
    }

    public void displayPlayerTokenWithAdvantages(Map<GameColor, Integer> tokens, Map<GameColor, Integer> advantages){
        System.out.println("Jetons possédés (Bonus) :{ ");
        for (Map.Entry<GameColor, Integer> entry : tokens.entrySet()) {
            GameColor color = entry.getKey();
            int quantity = entry.getValue();
            int bonus = advantages.getOrDefault(color, 0);
            System.out.print(color + ":" + quantity + "(" + bonus + ") ");
        }
        System.out.println("}");
    }

    public void displayPlayerTurn(Player player) {
        System.out.println("C'est le tour de " + player.getName());
    }

    public void displayPlayer(Player player) {

        displayBorrowedCards(player.getBorrowedCards(), null);
        displayPlayerTokenWithAdvantages(player.getTokens(), player.getAdvantages());
    }

    public int displayPlayerTokens(Map<GameColor, Integer> tokens, Integer interactibleKey){
        System.out.print("Jetons possédés :{ ");
        for (Map.Entry<GameColor, Integer> entry : tokens.entrySet()) {
            GameColor color = entry.getKey();
            int quantity = entry.getValue();
            String content = color + ":" + quantity;
            if (interactibleKey != null) {
                System.out.print(TerminalTools.interactiveText("\t" + interactibleKey + " = " + content));
                interactibleKey++;
            }
            else {
                System.out.print("\t" + content);
            }
        }
        System.out.println(" }");
        System.out.println();

        return interactibleKey == null ? 0 : interactibleKey;
    }

    public void displayPlayersPoints(List<Player> players){
        System.out.print("Score des joueurs : ");
        for (Player player : players) {
            System.out.print(player.getName() + " : " + player.getPrestigePoints() + " points\t");
        }
        System.out.println();
    }


}
