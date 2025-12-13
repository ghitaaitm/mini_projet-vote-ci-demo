package com.example.vote.observer;

import com.example.vote.model.Vote;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

/**
 * Tests unitaires pour les VoteListeners.
 */
class VoteListenerTest {

    @Test
    @DisplayName("LoggingVoteListener: onVote ne doit pas lever d'exception")
    void testLoggingListenerDoesNotThrow() {
        LoggingVoteListener listener = new LoggingVoteListener();
        Vote vote = new Vote("voter1", "Alice");
        assertDoesNotThrow(() -> listener.onVote(vote));
    }

    @Test
    @DisplayName("LoggingVoteListener: onVote avec null ne doit pas planter")
    void testLoggingListenerWithNull() {
        LoggingVoteListener listener = new LoggingVoteListener();
        assertDoesNotThrow(() -> listener.onVote(null));
    }

    @Test
    @DisplayName("AuditVoteListener: nouveau listener a un audit vide")
    void testAuditListenerStartsEmpty() {
        AuditVoteListener listener = new AuditVoteListener();
        assertTrue(listener.getAuditLog().isEmpty());
        assertEquals(0, listener.getAuditCount());
    }

    @Test
    @DisplayName("AuditVoteListener: onVote ajoute le vote à l'audit")
    void testAuditListenerAddsVote() {
        AuditVoteListener listener = new AuditVoteListener();
        Vote vote = new Vote("voter1", "Alice");

        listener.onVote(vote);

        assertEquals(1, listener.getAuditCount());
        assertTrue(listener.getAuditLog().contains(vote));
    }

    @Test
    @DisplayName("AuditVoteListener: onVote avec null ne l'ajoute pas")
    void testAuditListenerIgnoresNull() {
        AuditVoteListener listener = new AuditVoteListener();
        listener.onVote(null);
        assertEquals(0, listener.getAuditCount());
    }

    @Test
    @DisplayName("AuditVoteListener: plusieurs votes sont correctement audités")
    void testAuditListenerMultipleVotes() {
        AuditVoteListener listener = new AuditVoteListener();

        Vote vote1 = new Vote("voter1", "Alice");
        Vote vote2 = new Vote("voter2", "Bob");
        Vote vote3 = new Vote("voter3", "Alice");

        listener.onVote(vote1);
        listener.onVote(vote2);
        listener.onVote(vote3);

        assertEquals(3, listener.getAuditCount());
        List<Vote> log = listener.getAuditLog();
        assertEquals(vote1, log.get(0));
        assertEquals(vote2, log.get(1));
        assertEquals(vote3, log.get(2));
    }

    @Test
    @DisplayName("AuditVoteListener: getAuditLog retourne une copie défensive")
    void testAuditListenerReturnsDefensiveCopy() {
        AuditVoteListener listener = new AuditVoteListener();
        Vote vote = new Vote("voter1", "Alice");

        listener.onVote(vote);

        List<Vote> log1 = listener.getAuditLog();
        List<Vote> log2 = listener.getAuditLog();

        // Modifier log1 ne doit pas affecter log2
        log1.clear();

        assertEquals(0, log1.size());
        assertEquals(1, log2.size());
        assertEquals(1, listener.getAuditCount());
    }

    @Test
    @DisplayName("AuditVoteListener: clearAudit efface l'historique")
    void testAuditListenerClear() {
        AuditVoteListener listener = new AuditVoteListener();

        listener.onVote(new Vote("voter1", "Alice"));
        listener.onVote(new Vote("voter2", "Bob"));

        assertEquals(2, listener.getAuditCount());
        listener.clearAudit();
        assertEquals(0, listener.getAuditCount());
        assertTrue(listener.getAuditLog().isEmpty());
    }

    @Test
    @DisplayName("AuditVoteListener: getVotesByVoter retourne les bons votes")
    void testAuditListenerGetVotesByVoter() {
        AuditVoteListener listener = new AuditVoteListener();

        Vote vote1 = new Vote("alice", "Candidate1");
        Vote vote2 = new Vote("bob", "Candidate2");
        Vote vote3 = new Vote("alice", "Candidate3");

        listener.onVote(vote1);
        listener.onVote(vote2);
        listener.onVote(vote3);

        List<Vote> aliceVotes = listener.getVotesByVoter("alice");
        assertEquals(2, aliceVotes.size());
        assertTrue(aliceVotes.contains(vote1));
        assertTrue(aliceVotes.contains(vote3));
        assertFalse(aliceVotes.contains(vote2));
    }

    @Test
    @DisplayName("AuditVoteListener: getVotesForCandidate retourne les bons votes")
    void testAuditListenerGetVotesForCandidate() {
        AuditVoteListener listener = new AuditVoteListener();

        Vote vote1 = new Vote("voter1", "Alice");
        Vote vote2 = new Vote("voter2", "Bob");
        Vote vote3 = new Vote("voter3", "Alice");

        listener.onVote(vote1);
        listener.onVote(vote2);
        listener.onVote(vote3);

        List<Vote> aliceVotes = listener.getVotesForCandidate("Alice");
        assertEquals(2, aliceVotes.size());
        assertTrue(aliceVotes.contains(vote1));
        assertTrue(aliceVotes.contains(vote3));
        assertFalse(aliceVotes.contains(vote2));
    }
}