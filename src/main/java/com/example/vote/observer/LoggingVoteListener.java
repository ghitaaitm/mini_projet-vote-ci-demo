package com.example.vote.observer;

import com.example.vote.model.Vote;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Implémentation d'un VoteListener qui log les votes dans la console.
 *
 * DESIGN PATTERN: Observer Pattern
 *
 * Responsabilité: Afficher les informations de chaque vote enregistré.
 * Utile pour le debugging et le monitoring en temps réel.
 */
public class LoggingVoteListener implements VoteListener {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Log le vote dans la console avec timestamp formaté.
     *
     * @param vote Le vote qui vient d'être enregistré
     */
    @Override
    public void onVote(Vote vote) {
        if (vote == null) {
            System.err.println("[LOG] Tentative de log d'un vote null");
            return;
        }

        // Formater le timestamp
        LocalDateTime dateTime = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(vote.timestamp()),
                ZoneId.systemDefault()
        );

        String formattedTime = dateTime.format(FORMATTER);

        // Log avec format lisible
        System.out.println(String.format(
                "[LOG] %s | Votant: %s → Candidat: %s",
                formattedTime,
                vote.voterId(),
                vote.candidateId()
        ));
    }

    /**
     * Version simplifiée du log (sans formatting).
     *
     * @param vote Le vote à logger
     */
    public void logSimple(Vote vote) {
        System.out.println("[LOG] Vote reçu: " + vote);
    }
}