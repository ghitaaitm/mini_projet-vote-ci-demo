package com.example.vote.strategy;

import com.example.vote.model.Vote;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

/**
 * Tests unitaires pour les stratégies de comptage.
 * Vérifie PluralityCountingStrategy et MajorityCountingStrategy.
 */
class CountingStrategyTest {

    // ============ Tests pour PluralityCountingStrategy ============

    @Test
    @DisplayName("Plurality: compter avec une liste vide doit retourner une map vide")
    void testPluralityWithEmptyList() {
        PluralityCountingStrategy strategy = new PluralityCountingStrategy();
        Map<String, Integer> results = strategy.count(new ArrayList<>());

        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    @DisplayName("Plurality: compter avec liste null doit lever une exception")
    void testPluralityWithNullList() {
        PluralityCountingStrategy strategy = new PluralityCountingStrategy();

        assertThrows(IllegalArgumentException.class, () -> {
            strategy.count(null);
        });
    }

    @Test
    @DisplayName("Plurality: compter les votes correctement")
    void testPluralityBasicCounting() {
        PluralityCountingStrategy strategy = new PluralityCountingStrategy();

        List<Vote> votes = Arrays.asList(
                new Vote("v1", "Alice"),
                new Vote("v2", "Alice"),
                new Vote("v3", "Bob"),
                new Vote("v4", "Alice"),
                new Vote("v5", "Charlie")
        );

        Map<String, Integer> results = strategy.count(votes);

        assertEquals(3, results.get("Alice"));
        assertEquals(1, results.get("Bob"));
        assertEquals(1, results.get("Charlie"));
    }

    @Test
    @DisplayName("Plurality: getWinner doit retourner le candidat avec le plus de votes")
    void testPluralityGetWinner() {
        PluralityCountingStrategy strategy = new PluralityCountingStrategy();

        Map<String, Integer> results = new HashMap<>();
        results.put("Alice", 5);
        results.put("Bob", 3);
        results.put("Charlie", 2);

        String winner = strategy.getWinner(results);

        assertEquals("Alice", winner);
    }

    @Test
    @DisplayName("Plurality: getWinner avec map vide doit retourner null")
    void testPluralityGetWinnerEmptyMap() {
        PluralityCountingStrategy strategy = new PluralityCountingStrategy();

        String winner = strategy.getWinner(new HashMap<>());

        assertNull(winner);
    }

    @Test
    @DisplayName("Plurality: getWinner avec map null doit retourner null")
    void testPluralityGetWinnerNullMap() {
        PluralityCountingStrategy strategy = new PluralityCountingStrategy();

        String winner = strategy.getWinner(null);

        assertNull(winner);
    }

    @Test
    @DisplayName("Plurality: en cas d'égalité, retourner un des gagnants")
    void testPluralityTie() {
        PluralityCountingStrategy strategy = new PluralityCountingStrategy();

        List<Vote> votes = Arrays.asList(
                new Vote("v1", "Alice"),
                new Vote("v2", "Alice"),
                new Vote("v3", "Bob"),
                new Vote("v4", "Bob")
        );

        Map<String, Integer> results = strategy.count(votes);
        String winner = strategy.getWinner(results);

        // Un des deux doit gagner
        assertTrue(winner.equals("Alice") || winner.equals("Bob"));
    }

    @Test
    @DisplayName("Plurality: getStrategyName doit retourner un nom")
    void testPluralityStrategyName() {
        PluralityCountingStrategy strategy = new PluralityCountingStrategy();

        String name = strategy.getStrategyName();

        assertNotNull(name);
        assertFalse(name.isEmpty());
    }

    // ============ Tests pour MajorityCountingStrategy ============

    @Test
    @DisplayName("Majority: compter avec liste vide doit retourner une map vide")
    void testMajorityWithEmptyList() {
        MajorityCountingStrategy strategy = new MajorityCountingStrategy();
        Map<String, Integer> results = strategy.count(new ArrayList<>());

        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    @DisplayName("Majority: compter avec liste null doit lever une exception")
    void testMajorityWithNullList() {
        MajorityCountingStrategy strategy = new MajorityCountingStrategy();

        assertThrows(IllegalArgumentException.class, () -> {
            strategy.count(null);
        });
    }

    @Test
    @DisplayName("Majority: compter les votes correctement")
    void testMajorityBasicCounting() {
        MajorityCountingStrategy strategy = new MajorityCountingStrategy();

        List<Vote> votes = Arrays.asList(
                new Vote("v1", "Alice"),
                new Vote("v2", "Alice"),
                new Vote("v3", "Bob")
        );

        Map<String, Integer> results = strategy.count(votes);

        assertEquals(2, results.get("Alice"));
        assertEquals(1, results.get("Bob"));
    }

    @Test
    @DisplayName("Majority: getMajorityWinner avec majorité absolue")
    void testMajorityWithAbsoluteMajority() {
        MajorityCountingStrategy strategy = new MajorityCountingStrategy();

        Map<String, Integer> results = new HashMap<>();
        results.put("Alice", 6);  // 60%
        results.put("Bob", 4);    // 40%

        String winner = strategy.getMajorityWinner(results, 10);

        assertEquals("Alice", winner);
    }

    @Test
    @DisplayName("Majority: getMajorityWinner sans majorité absolue")
    void testMajorityWithoutAbsoluteMajority() {
        MajorityCountingStrategy strategy = new MajorityCountingStrategy();

        Map<String, Integer> results = new HashMap<>();
        results.put("Alice", 4);    // 40%
        results.put("Bob", 3);      // 30%
        results.put("Charlie", 3);  // 30%

        String winner = strategy.getMajorityWinner(results, 10);

        assertNull(winner); // Personne n'a > 50%
    }

    @Test
    @DisplayName("Majority: getMajorityWinner avec exactement 50% ne doit pas gagner")
    void testMajorityExactly50Percent() {
        MajorityCountingStrategy strategy = new MajorityCountingStrategy();

        Map<String, Integer> results = new HashMap<>();
        results.put("Alice", 5);  // Exactement 50%
        results.put("Bob", 5);    // Exactement 50%

        String winner = strategy.getMajorityWinner(results, 10);

        // Avec la logique actuelle, le premier avec >= requiredVotes gagne
        // requiredVotes = 10/2 + 1 = 6, donc personne ne gagne
        assertNull(winner);
    }

    @Test
    @DisplayName("Majority: hasMajority doit retourner true si > 50%")
    void testHasMajorityTrue() {
        MajorityCountingStrategy strategy = new MajorityCountingStrategy();

        Map<String, Integer> results = new HashMap<>();
        results.put("Alice", 6);
        results.put("Bob", 4);

        assertTrue(strategy.hasMajority("Alice", results, 10));
        assertFalse(strategy.hasMajority("Bob", results, 10));
    }

    @Test
    @DisplayName("Majority: hasMajority avec candidat inexistant")
    void testHasMajorityUnknownCandidate() {
        MajorityCountingStrategy strategy = new MajorityCountingStrategy();

        Map<String, Integer> results = new HashMap<>();
        results.put("Alice", 6);

        assertFalse(strategy.hasMajority("Unknown", results, 10));
    }

    @Test
    @DisplayName("Majority: getMajorityWinner avec totalVotes = 0")
    void testMajorityWithZeroVotes() {
        MajorityCountingStrategy strategy = new MajorityCountingStrategy();

        Map<String, Integer> results = new HashMap<>();
        results.put("Alice", 0);

        String winner = strategy.getMajorityWinner(results, 0);

        assertNull(winner);
    }

    @Test
    @DisplayName("Majority: getMajorityWinner avec map null")
    void testMajorityGetWinnerNullMap() {
        MajorityCountingStrategy strategy = new MajorityCountingStrategy();

        String winner = strategy.getMajorityWinner(null, 10);

        assertNull(winner);
    }

    @Test
    @DisplayName("Majority: getMajorityWinner avec map vide")
    void testMajorityGetWinnerEmptyMap() {
        MajorityCountingStrategy strategy = new MajorityCountingStrategy();

        String winner = strategy.getMajorityWinner(new HashMap<>(), 10);

        assertNull(winner);
    }

    @Test
    @DisplayName("Majority: getStrategyName doit retourner un nom")
    void testMajorityStrategyName() {
        MajorityCountingStrategy strategy = new MajorityCountingStrategy();

        String name = strategy.getStrategyName();

        assertNotNull(name);
        assertFalse(name.isEmpty());
    }

    // ============ Tests de comparaison entre stratégies ============

    @Test
    @DisplayName("Comparaison: Plurality vs Majority avec majorité claire")
    void testComparisonWithClearMajority() {
        List<Vote> votes = Arrays.asList(
                new Vote("v1", "Alice"),
                new Vote("v2", "Alice"),
                new Vote("v3", "Alice"),
                new Vote("v4", "Bob")
        );

        PluralityCountingStrategy plurality = new PluralityCountingStrategy();
        MajorityCountingStrategy majority = new MajorityCountingStrategy();

        Map<String, Integer> pluralityResults = plurality.count(votes);
        Map<String, Integer> majorityResults = majority.count(votes);

        // Les deux comptent pareil
        assertEquals(pluralityResults, majorityResults);

        // Plurality winner
        String pluralityWinner = plurality.getWinner(pluralityResults);
        assertEquals("Alice", pluralityWinner);

        // Majority winner (Alice a 75% > 50%)
        String majorityWinner = majority.getMajorityWinner(majorityResults, votes.size());
        assertEquals("Alice", majorityWinner);
    }

    @Test
    @DisplayName("Comparaison: Plurality gagne mais pas Majority")
    void testComparisonPluralityWinsButNotMajority() {
        List<Vote> votes = Arrays.asList(
                new Vote("v1", "Alice"),
                new Vote("v2", "Alice"),
                new Vote("v3", "Bob"),
                new Vote("v4", "Charlie")
        );

        PluralityCountingStrategy plurality = new PluralityCountingStrategy();
        MajorityCountingStrategy majority = new MajorityCountingStrategy();

        Map<String, Integer> results = plurality.count(votes);

        // Plurality: Alice gagne avec 50%
        String pluralityWinner = plurality.getWinner(results);
        assertEquals("Alice", pluralityWinner);

        // Majority: Personne n'a > 50%
        String majorityWinner = majority.getMajorityWinner(results, votes.size());
        assertNull(majorityWinner);
    }
}