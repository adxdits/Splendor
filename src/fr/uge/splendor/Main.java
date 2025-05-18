package fr.uge.splendor;

import java.util.Scanner;

public class Main {
    private static int askPlayers() {
        Scanner scanner = new Scanner(System.in);
        int numPlayers;
        do {
            System.out.print("Combien de joueurs ? (2-4) : ");
            while (!scanner.hasNextInt()) {
                System.out.print("Veuillez entrer un nombre valide (2-4) : ");
                scanner.next();
            }
            numPlayers = scanner.nextInt();
        } while (numPlayers < 2 || numPlayers > 4);
        return numPlayers;
    }

    public static void main(String[] args) {
        System.out.println("🚀 Démarrage du Splendor Phase 1 !");
        int numPlayers = askPlayers();

        new Game(numPlayers).play();
    }
}
