package com.example.vote;

import com.example.vote.factory.RepositoryFactory;
import com.example.vote.model.Vote;
import com.example.vote.observer.LoggingVoteListener;
import com.example.vote.observer.AuditVoteListener;
import com.example.vote.service.VoteService;
import com.example.vote.strategy.PluralityCountingStrategy;
import com.example.vote.strategy.MajorityCountingStrategy;

import java.util.Map;
import java.util.Scanner;

/**
 * Application CLI pour tester le système de vote.
 */
public class App {
    public static void main(String[] args) {
        System.out.println("=== Système de Vote Refactoré ===");
        System.out.println("Implémenté avec 3 Design Patterns:");
        System.out.println("1. Factory Method (RepositoryFactory)");
        System.out.println("2. Strategy (CountingStrategy)");
        System.out.println("3. Observer (VoteListener)");
        System.out.println("===============================");

        // Créer le service via Factory Method
        VoteService service = new VoteService(
                RepositoryFactory.createDefaultRepository()
        );

        // Ajouter des observers (Observer Pattern)
        service.addListener(new LoggingVoteListener());
        service.addListener(new AuditVoteListener());

        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            printMenu();
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    castVote(service, scanner);
                    break;
                case "2":
                    showResults(service, scanner);
                    break;
                case "3":
                    resetSystem(service);
                    break;
                case "4":
                    showStats(service);
                    break;
                case "5":
                    running = false;
                    System.out.println("Au revoir !");
                    break;
                default:
                    System.out.println("Option invalide !");
            }
        }
        scanner.close();
    }

    private static void printMenu() {
        System.out.println("\n--- Menu Principal ---");
        System.out.println("1. Voter");
        System.out.println("2. Voir les résultats");
        System.out.println("3. Réinitialiser le système");
        System.out.println("4. Statistiques");
        System.out.println("5. Quitter");
        System.out.print("Votre choix: ");
    }

    private static void castVote(VoteService service, Scanner scanner) {
        System.out.print("ID du votant: ");
        String voterId = scanner.nextLine().trim();

        System.out.print("ID du candidat: ");
        String candidateId = scanner.nextLine().trim();

        try {
            Vote vote = new Vote(voterId, candidateId);
            service.cast(vote);
            System.out.println("✅ Vote enregistré avec succès !");
        } catch (IllegalArgumentException e) {
            System.err.println("❌ Erreur: " + e.getMessage());
        } catch (IllegalStateException e) {
            System.err.println("❌ Erreur: " + e.getMessage());
        }
    }

    private static void showResults(VoteService service, Scanner scanner) {
        System.out.println("\n--- Méthode de comptage ---");
        System.out.println("1. Pluralité (le plus de votes gagne)");
        System.out.println("2. Majorité absolue (> 50%)");
        System.out.print("Votre choix: ");

        String choice = scanner.nextLine();

        if (choice.equals("1")) {
            Map<String, Integer> results = service.count(new PluralityCountingStrategy());
            displayResults(results, "Pluralité");

            String winner = new PluralityCountingStrategy().getWinner(results);
            System.out.println("🏆 Gagnant (Pluralité): " + (winner != null ? winner : "Aucun"));

        } else if (choice.equals("2")) {
            Map<String, Integer> results = service.count(new MajorityCountingStrategy());
            displayResults(results, "Majorité");

            MajorityCountingStrategy strategy = new MajorityCountingStrategy();
            String winner = strategy.getMajorityWinner(results, service.getTotalVotes());
            System.out.println("🏆 Gagnant (Majorité > 50%): " +
                    (winner != null ? winner : "Aucun (pas de majorité absolue)"));
        } else {
            System.out.println("Option invalide !");
        }
    }

    private static void displayResults(Map<String, Integer> results, String method) {
        System.out.println("\n=== Résultats (" + method + ") ===");
        if (results.isEmpty()) {
            System.out.println("Aucun vote enregistré.");
            return;
        }

        int total = results.values().stream().mapToInt(Integer::intValue).sum();

        results.forEach((candidate, votes) -> {
            double percentage = total > 0 ? (votes * 100.0) / total : 0;
            System.out.printf("  %s: %d votes (%.1f%%)\n",
                    candidate, votes, percentage);
        });
        System.out.println("Total votes: " + total);
    }

    private static void resetSystem(VoteService service) {
        service.reset();
        System.out.println("✅ Système réinitialisé avec succès !");
    }

    private static void showStats(VoteService service) {
        Map<String, Object> stats = service.getStatistics();
        System.out.println("\n=== Statistiques ===");
        System.out.println("Votes totaux: " + stats.get("totalVotes"));
        System.out.println("Votants uniques: " + stats.get("uniqueVoters"));
        System.out.println("Observateurs actifs: " + stats.get("listenersCount"));
    }
}