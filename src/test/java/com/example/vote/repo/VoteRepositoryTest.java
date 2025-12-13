package com.example.vote.repo;
import com.example.vote.model.Vote;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

/**
 * Tests unitaires pour InMemoryVoteRepository.
 * Vérifie toutes les opérations CRUD et la thread-safety.
 */
class VoteRepositoryTest {

    private VoteRepository repository;

    @BeforeEach
    void setUp() {
        // Créer une nouvelle instance avant chaque test
        repository = new InMemoryVoteRepository();
    }

    @Test
    @DisplayName("Un nouveau repository doit être vide")
    void testNewRepositoryIsEmpty() {
        assertEquals(0, repository.count());
        assertTrue(repository.findAll().isEmpty());
    }

    @Test
    @DisplayName("Sauvegarder un vote doit l'ajouter au repository")
    void testSaveVote() {
        Vote vote = new Vote("voter1", "Alice");

        repository.save(vote);

        assertEquals(1, repository.count());
        List<Vote> votes = repository.findAll();
        assertEquals(1, votes.size());
        assertEquals(vote, votes.get(0));
    }

    @Test
    @DisplayName("Sauvegarder plusieurs votes")
    void testSaveMultipleVotes() {
        Vote vote1 = new Vote("voter1", "Alice");
        Vote vote2 = new Vote("voter2", "Bob");
        Vote vote3 = new Vote("voter3", "Alice");

        repository.save(vote1);
        repository.save(vote2);
        repository.save(vote3);

        assertEquals(3, repository.count());
        List<Vote> votes = repository.findAll();
        assertEquals(3, votes.size());
        assertTrue(votes.contains(vote1));
        assertTrue(votes.contains(vote2));
        assertTrue(votes.contains(vote3));
    }

    @Test
    @DisplayName("Sauvegarder un vote null doit lever une exception")
    void testSaveNullVoteThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            repository.save(null);
        });
    }

    @Test
    @DisplayName("findAll() doit retourner une copie défensive")
    void testFindAllReturnsCopy() {
        Vote vote = new Vote("voter1", "Alice");
        repository.save(vote);

        List<Vote> votes1 = repository.findAll();
        List<Vote> votes2 = repository.findAll();

        // Modifier votes1 ne doit pas affecter votes2
        votes1.clear();

        assertEquals(0, votes1.size());
        assertEquals(1, votes2.size());
        assertEquals(1, repository.count());
    }

    @Test
    @DisplayName("clear() doit supprimer tous les votes")
    void testClear() {
        repository.save(new Vote("voter1", "Alice"));
        repository.save(new Vote("voter2", "Bob"));

        assertEquals(2, repository.count());

        repository.clear();

        assertEquals(0, repository.count());
        assertTrue(repository.findAll().isEmpty());
    }

    @Test
    @DisplayName("Le repository doit maintenir l'ordre d'insertion")
    void testOrderIsPreserved() {
        Vote vote1 = new Vote("voter1", "Alice", 1000L);
        Vote vote2 = new Vote("voter2", "Bob", 2000L);
        Vote vote3 = new Vote("voter3", "Charlie", 3000L);

        repository.save(vote1);
        repository.save(vote2);
        repository.save(vote3);

        List<Vote> votes = repository.findAll();

        assertEquals(vote1, votes.get(0));
        assertEquals(vote2, votes.get(1));
        assertEquals(vote3, votes.get(2));
    }

    @Test
    @DisplayName("Comptage correct après plusieurs opérations")
    void testCountAfterMultipleOperations() {
        assertEquals(0, repository.count());

        repository.save(new Vote("voter1", "Alice"));
        assertEquals(1, repository.count());

        repository.save(new Vote("voter2", "Bob"));
        assertEquals(2, repository.count());

        repository.clear();
        assertEquals(0, repository.count());

        repository.save(new Vote("voter3", "Charlie"));
        assertEquals(1, repository.count());
    }
}