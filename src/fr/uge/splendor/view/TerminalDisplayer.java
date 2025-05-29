package fr.uge.splendor.view;

import fr.uge.splendor.model.*;
import fr.uge.splendor.tools.TerminalTools;

import java.util.List;

public class TerminalDisplayer {
    public TerminalDisplayer(){
    }

    public int displayBoard(Board board, Integer interactibleKey){

        System.out.println("Cartes disponibles :");
        for (int i = 0; i < board.getNbOfLevels(); i++) {
            System.out.println("Level " + (i + 1) + ":");
            for (int j = 0; j < Board.CARDS_BY_LEVEL; j++) {
                Card card = board.peekCard(i+1,j);
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

    public void displayTokensStack(TokensBundle tokens){
        // display inline
        System.out.print("Pile de jetons : ");
        StringBuilder sb = new StringBuilder();
        for (GameColor color : TokensBundle.getColorsSupported()) {
            int count = tokens.getTokenCount(color);
            if (count > 0) {
                sb.append(color).append(":").append(count).append(" ");
            }
        }
        System.out.println(sb);
    }



    public int displayStacksTaken(List<GameColor> colorOrder, TokensBundle tokenStacks, TokensBundle tokensTaken){
        int interactibleKey = 0;
        System.out.println("Jetons disponibles (jeton pris) : ");
        for(GameColor color : colorOrder){
            int taken = tokensTaken.getTokenCount(color);
            int available = tokenStacks.getTokenCount(color);
            String content = color.toString() + available + " (pris : " + taken + ")";
            System.out.println(TerminalTools.interactiveText("\t" + interactibleKey + " = " + content));
            interactibleKey++;
        }

//        for (TokenStack tokenStack : tokens) {
//            if (tokenStack.getColor() == GameColor.YELLOW) continue;
//            String content = tokenStack.toString() + " (pris : " + takenStacks.getOrDefault(tokenStack.getColor(),0) + ")";
//            System.out.println(TerminalTools.interactiveText("\t" + interactibleKey + " = " + content));
//            interactibleKey++;
//
//        }
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

    public void displayPlayerTokenWithAdvantages(TokensBundle tokens, TokensBundle advantages){
        System.out.println("Jetons possédés (Bonus) :{ ");
        for (GameColor color: TokensBundle.getColorsSupported()) {
            int bonus = advantages.getTokenCount(color);
            int quantity = tokens.getTokenCount(color);
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

    public int displayPlayerTokens(TokensBundle tokens, Integer interactibleKey){
        System.out.print("Jetons possédés :{ ");
        for (GameColor color : TokensBundle.getColorsSupported()) {
            int quantity = tokens.getTokenCount(color);
            String content = color + ":" + quantity;
            if (interactibleKey != null) {
                System.out.print(TerminalTools.interactiveText("\t" + interactibleKey + " = " + content));
                interactibleKey++;
            }
            else {
                System.out.print("\t" + content);
            }
        }

//        for (Map.Entry<GameColor, Integer> entry : tokens.entrySet()) {
//            GameColor color = entry.getKey();
//            int quantity = entry.getValue();
//            String content = color + ":" + quantity;
//            if (interactibleKey != null) {
//                System.out.print(TerminalTools.interactiveText("\t" + interactibleKey + " = " + content));
//                interactibleKey++;
//            }
//            else {
//                System.out.print("\t" + content);
//            }
//        }
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

    public void displayActions(boolean useSimpleGame){
        System.out.println(TerminalTools.askText("Choisissez une action :"));
        System.out.println(TerminalTools.interactiveText("\t1. Prendre des jetons"));
        System.out.println(TerminalTools.interactiveText("\t2. Acheter une carte"));
        if (!useSimpleGame){
            System.out.println(TerminalTools.interactiveText("\t3. Réserver une carte"));
        }
    }

    public void showInvalidChoice() {
        System.out.println(TerminalTools.errorText("Choix invalide, veuillez réessayer."));
    }


}
