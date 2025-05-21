package fr.uge.splendor;

import fr.uge.splendor.controller.Game;
import fr.uge.splendor.controller.GameSettings;
import fr.uge.splendor.view.TerminalTools;

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
        Scanner scanner = new Scanner(System.in);
        int numPlayers;
        do {
            System.out.print(TerminalTools.askText("Combien de joueurs ? (2-4) : "));
            while (!scanner.hasNextInt()) {
                System.out.print(TerminalTools.warningText("Veuillez entrer un nombre valide (2-4) : "));
                scanner.next();
            }
            numPlayers = scanner.nextInt();
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
