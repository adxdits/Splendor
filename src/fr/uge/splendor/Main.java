package fr.uge.splendor;

import fr.uge.splendor.controller.Game;
import fr.uge.splendor.controller.GameSettings;
import fr.uge.splendor.tools.TerminalTools;

import java.util.Scanner;

public class Main {

    private static boolean askSimplePlay() {
        Scanner scanner = new Scanner(System.in);
        String response = "";
        while (!response.equals("O") && !response.equals("N")) {
            System.out.print(TerminalTools.askText("Souhaitez-vous jouer en mode simple ? (O/N) : "));
            response = scanner.nextLine().trim().toUpperCase();
        }
        return response.equals("O");
    }

    private static int askPlayers() {
        int numPlayers;
        do {
            System.out.print(TerminalTools.askText("Combien de joueurs ? (2-4) : "));
            numPlayers = TerminalTools.getSecurisedInput();
        } while (numPlayers < 2 || numPlayers > 4);
        return numPlayers;
    }

    public static void main(String[] args) {
        boolean simplePlay = askSimplePlay();
        int numPlayers = askPlayers();
        GameSettings settings = new GameSettings(simplePlay,numPlayers);
        new Game(settings).play();
    }
}
