package com.example.vote.observer;

import com.example.vote.model.Vote;

/**
 * Interface définissant le contrat pour les observers d'événements de vote.
 *
 * DESIGN PATTERN: Observer Pattern
 *
 * Avantages:
 * - Découplage entre la logique métier et les actions déclenchées
 * - Facilite l'ajout de nouveaux comportements (logs, audit, notifications)
 * - Permet de réagir aux événements sans modifier le code principal
 *
 * Exemples d'implémentations:
 * - LoggingVoteListener: Logs dans la console
 * - AuditVoteListener: Historique d'audit
 * - EmailNotificationListener: Envoi d'emails
 * - StatisticsListener: Mise à jour des stats en temps réel
 */
public interface VoteListener {

    /**
     * Méthode appelée quand un vote est enregistré.
     *
     * @param vote Le vote qui vient d'être enregistré
     */
    void onVote(Vote vote);
}