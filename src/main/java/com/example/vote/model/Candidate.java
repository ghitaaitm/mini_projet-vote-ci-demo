package com.example.vote.model;


/**
 * Représente un candidat dans le système de vote.
 * Utilise un record Java (immutable par défaut).
 *
 * @param id   Identifiant unique du candidat
 * @param name Nom complet du candidat
 */
public record Candidate(String id, String name) {

    /**
     * Constructeur compact avec validation.
     */
    public Candidate {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("L'ID du candidat ne peut pas être vide");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Le nom du candidat ne peut pas être vide");
        }
    }
}