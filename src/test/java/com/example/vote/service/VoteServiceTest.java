package com.example.vote.service;

import com.example.vote.model.Vote;
import com.example.vote.repo.*;
import com.example.vote.strategy.*;
import com.example.vote.observer.VoteListener;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

/**
 * Tests unitaires pour VoteService.
 */
class VoteServiceTest {

    private VoteRepository repository;
    private VoteService service;

    @BeforeEach
    void setUp() {
        repository = new InMemoryVoteRepository();
        service = new VoteService(repository);
    }

    @Test
    @DisplayName("Le constructeur avec repository null doit lever une exception")
    void testConstructorWithNullRepository() {
        assertThrows(IllegalArgumentException.class, () -> {
            new VoteService(null);
        });
    }

    @Test
    @DisplayName("cast() doit sauvegarder un vote")
    void testCastVote() {
        Vote vote = new Vote("voter1", "Alice");
        service.cast(vote);
        assertEquals(1, service.getTotalVotes());
        assertTrue(service.hasVoted("voter1"));
    }

    @Test
    @DisplayName("cast() avec vote null doit lever une exception")
    void testCastNullVote() {
        assertThrows(IllegalArgumentException.class, () -> {
            service.cast(null);
        });
    }

    @Test
    @DisplayName("cast() doit empêcher le double vote")
    void testPreventDoubleVoting() {
        Vote vote1 = new Vote("voter1", "Alice");
        Vote vote2 = new Vote("voter1", "Bob");

        service.cast(vote1);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> service.cast(vote2)
        );

        assertTrue(exception.getMessage().contains("voter1"));
        assertEquals(1, service.getTotalVotes());
    }

    @Test
    @DisplayName("castUnchecked() doit permettre plusieurs votes du même votant")
    void testCastUnchecked() {
        Vote vote1 = new Vote("voter1", "Alice");
        Vote vote2 = new Vote("voter1", "Bob");

        service.castUnchecked(vote1);
        service.castUnchecked(vote2);

        assertEquals(2, service.getTotalVotes());
    }

    @Test
    @DisplayName("addListener() doit enregistrer un observer")
    void testAddListener() {
        TestVoteListener listener = new TestVoteListener();
        service.addListener(listener);
        assertEquals(1, service.getListenerCount());
    }

    @Test
    @DisplayName("addListener() avec null doit lever une exception")
    void testAddNullListener() {
        assertThrows(IllegalArgumentException.class, () -> {
            service.addListener(null);
        });
    }

    @Test
    @DisplayName("cast() doit notifier les listeners")
    void testListenerNotification() {
        TestVoteListener listener = new TestVoteListener();
        service.addListener(listener);

        Vote vote = new Vote("voter1", "Alice");
        service.cast(vote);

        assertEquals(1, listener.getNotificationCount());
        assertEquals(vote, listener.getLastVote());
    }

    @Test
    @DisplayName("cast() doit notifier plusieurs listeners")
    void testMultipleListeners() {
        TestVoteListener listener1 = new TestVoteListener();
        TestVoteListener listener2 = new TestVoteListener();
        TestVoteListener listener3 = new TestVoteListener();

        service.addListener(listener1);
        service.addListener(listener2);
        service.addListener(listener3);

        Vote vote = new Vote("voter1", "Alice");
        service.cast(vote);

        assertEquals(1, listener1.getNotificationCount());
        assertEquals(1, listener2.getNotificationCount());
        assertEquals(1, listener3.getNotificationCount());
    }

    @Test
    @DisplayName("removeListener() doit retirer un observer")
    void testRemoveListener() {
        TestVoteListener listener = new TestVoteListener();
        service.addListener(listener);
        assertEquals(1, service.getListenerCount());

        service.removeListener(listener);
        assertEquals(0, service.getListenerCount());
    }

    @Test
    @DisplayName("count() avec PluralityStrategy")
    void testCountWithPluralityStrategy() {
        service.castUnchecked(new Vote("v1", "Alice"));
        service.castUnchecked(new Vote("v2", "Alice"));
        service.castUnchecked(new Vote("v3", "Bob"));

        CountingStrategy strategy = new PluralityCountingStrategy();
        Map<String, Integer> results = service.count(strategy);

        assertEquals(2, results.get("Alice"));
        assertEquals(1, results.get("Bob"));
    }

    @Test
    @DisplayName("count() avec MajorityStrategy")
    void testCountWithMajorityStrategy() {
        service.castUnchecked(new Vote("v1", "Alice"));
        service.castUnchecked(new Vote("v2", "Alice"));
        service.castUnchecked(new Vote("v3", "Alice"));
        service.castUnchecked(new Vote("v4", "Bob"));

        CountingStrategy strategy = new MajorityCountingStrategy();
        Map<String, Integer> results = service.count(strategy);

        assertEquals(3, results.get("Alice"));
        assertEquals(1, results.get("Bob"));

        // Tester la majorité
        String winner = ((MajorityCountingStrategy) strategy)
                .getMajorityWinner(results, 4);
        assertEquals("Alice", winner);
    }

    @Test
    @DisplayName("count() avec stratégie null doit lever une exception")
    void testCountWithNullStrategy() {
        assertThrows(IllegalArgumentException.class, () -> {
            service.count(null);
        });
    }

    @Test
    @DisplayName("reset() doit effacer tous les votes et tracking")
    void testReset() {
        service.cast(new Vote("voter1", "Alice"));
        service.cast(new Vote("voter2", "Bob"));

        assertEquals(2, service.getTotalVotes());
        assertTrue(service.hasVoted("voter1"));

        service.reset();

        assertEquals(0, service.getTotalVotes());
        assertFalse(service.hasVoted("voter1"));
        assertFalse(service.hasVoted("voter2"));
    }

    @Test
    @DisplayName("hasVoted() doit retourner false pour votant inconnu")
    void testHasVotedUnknownVoter() {
        assertFalse(service.hasVoted("unknown"));
    }

    @Test
    @DisplayName("getAllVotes() doit retourner tous les votes")
    void testGetAllVotes() {
        Vote vote1 = new Vote("voter1", "Alice");
        Vote vote2 = new Vote("voter2", "Bob");

        service.cast(vote1);
        service.cast(vote2);

        List<Vote> allVotes = service.getAllVotes();

        assertEquals(2, allVotes.size());
        assertTrue(allVotes.contains(vote1));
        assertTrue(allVotes.contains(vote2));
    }

    @Test
    @DisplayName("getStatistics() doit retourner les bonnes stats")
    void testGetStatistics() {
        TestVoteListener listener = new TestVoteListener();
        service.addListener(listener);

        service.cast(new Vote("voter1", "Alice"));
        service.cast(new Vote("voter2", "Bob"));

        Map<String, Object> stats = service.getStatistics();

        assertEquals(2, stats.get("totalVotes"));
        assertEquals(2, stats.get("uniqueVoters"));
        assertEquals(1, stats.get("listenersCount"));
    }

    @Test
    @DisplayName("Test d'intégration complet: scénario réel avec tout")
    void testFullIntegrationScenario() {
        // Setup: 2 listeners
        TestVoteListener logger = new TestVoteListener();
        TestVoteListener auditor = new TestVoteListener();
        service.addListener(logger);
        service.addListener(auditor);

        // Votes
        service.cast(new Vote("voter1", "Alice"));
        service.cast(new Vote("voter2", "Alice"));
        service.cast(new Vote("voter3", "Bob"));
        service.cast(new Vote("voter4", "Alice"));

        // Vérifications
        assertEquals(4, service.getTotalVotes());
        assertEquals(4, logger.getNotificationCount());
        assertEquals(4, auditor.getNotificationCount());

        // Comptage Plurality
        PluralityCountingStrategy pluralityStrategy = new PluralityCountingStrategy();
        Map<String, Integer> pluralityResults = service.count(pluralityStrategy);

        assertEquals(3, pluralityResults.get("Alice"));
        assertEquals(1, pluralityResults.get("Bob"));
        assertEquals("Alice", pluralityStrategy.getWinner(pluralityResults));

        // Comptage Majority
        MajorityCountingStrategy majorityStrategy = new MajorityCountingStrategy();
        Map<String, Integer> majorityResults = service.count(majorityStrategy);

        assertEquals(3, majorityResults.get("Alice"));

        // Alice a 75% > 50%, donc doit gagner
        String majorityWinner = majorityStrategy.getMajorityWinner(majorityResults, 4);
        assertEquals("Alice", majorityWinner);
    }

    @Test
    @DisplayName("Un listener qui lance une exception ne doit pas bloquer les autres")
    void testListenerExceptionHandling() {
        // Listener qui plante
        VoteListener faultyListener = vote -> {
            throw new RuntimeException("Test error");
        };

        TestVoteListener goodListener = new TestVoteListener();

        service.addListener(faultyListener);
        service.addListener(goodListener);

        Vote vote = new Vote("voter1", "Alice");

        // Ne doit pas lever d'exception
        assertDoesNotThrow(() -> service.cast(vote));

        // Le bon listener doit quand même être notifié
        assertEquals(1, goodListener.getNotificationCount());
    }

    /**
     * Classe helper pour tester les notifications.
     */
    private static class TestVoteListener implements VoteListener {
        private int count = 0;
        private Vote lastVote = null;

        @Override
        public void onVote(Vote vote) {
            count++;
            lastVote = vote;
        }

        public int getNotificationCount() {
            return count;
        }

        public Vote getLastVote() {
            return lastVote;
        }
    }
}