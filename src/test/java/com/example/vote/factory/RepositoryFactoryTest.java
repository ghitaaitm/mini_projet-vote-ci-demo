package com.example.vote.factory;
import com.example.vote.repo.*;
import com.example.vote.model.Vote;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour RepositoryFactory.
 * Vérifie la création correcte des repositories et la gestion des erreurs.
 */
class RepositoryFactoryTest {

    @Test
    @DisplayName("createRepository avec 'memory' doit retourner InMemoryVoteRepository")
    void testCreateMemoryRepository() {
        VoteRepository repo = RepositoryFactory.createRepository("memory");

        assertNotNull(repo);
        assertInstanceOf(InMemoryVoteRepository.class, repo);
    }

    @Test
    @DisplayName("createRepository doit être insensible à la casse")
    void testCreateRepositoryCaseInsensitive() {
        VoteRepository repo1 = RepositoryFactory.createRepository("MEMORY");
        VoteRepository repo2 = RepositoryFactory.createRepository("Memory");
        VoteRepository repo3 = RepositoryFactory.createRepository("memory");

        assertInstanceOf(InMemoryVoteRepository.class, repo1);
        assertInstanceOf(InMemoryVoteRepository.class, repo2);
        assertInstanceOf(InMemoryVoteRepository.class, repo3);
    }

    @Test
    @DisplayName("createDefaultRepository doit retourner InMemoryVoteRepository")
    void testCreateDefaultRepository() {
        VoteRepository repo = RepositoryFactory.createDefaultRepository();

        assertNotNull(repo);
        assertInstanceOf(InMemoryVoteRepository.class, repo);
    }

    @Test
    @DisplayName("Type null doit lever une exception")
    void testNullTypeThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            RepositoryFactory.createRepository(null);
        });
    }

    @Test
    @DisplayName("Type vide doit lever une exception")
    void testEmptyTypeThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            RepositoryFactory.createRepository("");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            RepositoryFactory.createRepository("   ");
        });
    }

    @Test
    @DisplayName("Type inconnu doit lever une exception")
    void testUnknownTypeThrowsException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> RepositoryFactory.createRepository("unknown")
        );

        assertTrue(exception.getMessage().contains("unknown"));
    }

    @Test
    @DisplayName("Type 'file' doit lever UnsupportedOperationException (pas encore implémenté)")
    void testFileTypeNotImplemented() {
        assertThrows(UnsupportedOperationException.class, () -> {
            RepositoryFactory.createRepository("file");
        });
    }

    @Test
    @DisplayName("Type 'database' doit lever UnsupportedOperationException (pas encore implémenté)")
    void testDatabaseTypeNotImplemented() {
        assertThrows(UnsupportedOperationException.class, () -> {
            RepositoryFactory.createRepository("database");
        });
    }

    @Test
    @DisplayName("Chaque appel à createRepository doit retourner une nouvelle instance")
    void testFactoryCreatesNewInstances() {
        VoteRepository repo1 = RepositoryFactory.createRepository("memory");
        VoteRepository repo2 = RepositoryFactory.createRepository("memory");

        assertNotSame(repo1, repo2);
    }

    @Test
    @DisplayName("Le repository créé doit être fonctionnel")
    void testCreatedRepositoryIsUsable() {
        VoteRepository repo = RepositoryFactory.createRepository("memory");

        // Vérifier qu'on peut l'utiliser
        assertEquals(0, repo.count());

        Vote vote = new Vote("voter1", "Alice");
        repo.save(vote);

        assertEquals(1, repo.count());
    }
}