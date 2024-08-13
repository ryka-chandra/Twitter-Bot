package org.cis1200;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import java.util.NoSuchElementException;

import java.util.ArrayList;
import java.util.Arrays;

/** Tests for MarkovChain */
public class MarkovChainTest {

    /* **** ****** **** **** ADD BIGRAMS TESTS **** **** ****** **** */

 
    @Test
    public void testAddBigram() {
        MarkovChain mc = new MarkovChain();
        mc.addBigram("1", "2");
        assertTrue(mc.chain.containsKey("1"));
        ProbabilityDistribution<String> pd = mc.chain.get("1");
        assertTrue(pd.getRecords().containsKey("2"));
        assertEquals(1, pd.count("2"));
    }

    @Test
    public void testAddBigramMultipleOccurrences() {
        MarkovChain mcMultipleOccurrences = new MarkovChain();
        mcMultipleOccurrences.addBigram("word1", "word2");
        mcMultipleOccurrences.addBigram("word1", "word2");
        ProbabilityDistribution<String> pdMultiple = mcMultipleOccurrences.chain.get("word1");
        assertEquals(2, pdMultiple.count("word2"));
    }

    @Test
    public void testAddBigramExistingFirstWord() {
        MarkovChain mcExistingFirstWord = new MarkovChain();
        mcExistingFirstWord.addBigram("word1", "word2");
        mcExistingFirstWord.addBigram("word1", "word3");
        ProbabilityDistribution<String> pdExistingFirst = mcExistingFirstWord.chain.get("word1");
        assertTrue(pdExistingFirst.getRecords().containsKey("word2"));
        assertTrue(pdExistingFirst.getRecords().containsKey("word3"));
        assertEquals(1, pdExistingFirst.count("word2"));
        assertEquals(1, pdExistingFirst.count("word3"));
    }

    @Test
    public void testAddBigramWithWhitespace() {
        MarkovChain mc = new MarkovChain();
        mc.addBigram(" word1 ", "word2");
        assertTrue(mc.chain.containsKey(" word1 "));
        ProbabilityDistribution<String> pd = mc.chain.get(" word1 ");
        assertTrue(pd.getRecords().containsKey("word2"));
        assertEquals(1, pd.count("word2"));
    }

    /* ***** ****** ***** ***** TRAIN TESTS ***** ***** ****** ***** */

 
    @Test
    public void testTrain() {
        MarkovChain mc = new MarkovChain();
        String sentence = "1 2 3";
        mc.train(Arrays.stream(sentence.split(" ")).iterator());
        assertEquals(3, mc.chain.size());
        ProbabilityDistribution<String> pd1 = mc.chain.get("1");
        assertTrue(pd1.getRecords().containsKey("2"));
        assertEquals(1, pd1.count("2"));
        ProbabilityDistribution<String> pd2 = mc.chain.get("2");
        assertTrue(pd2.getRecords().containsKey("3"));
        assertEquals(1, pd2.count("3"));
        ProbabilityDistribution<String> pd3 = mc.chain.get("3");
        assertTrue(pd3.getRecords().containsKey(MarkovChain.END_TOKEN));
        assertEquals(1, pd3.count(MarkovChain.END_TOKEN));
    }

    @Test
    public void testTrainWithEmptySentence() {
        MarkovChain mc = new MarkovChain();
        mc.train(Arrays.stream("".split(" ")).iterator());
        assertFalse(mc.chain.isEmpty());
    }

    @Test
    public void testTrainWithSingleWordSentence() {
        MarkovChain mc = new MarkovChain();
        mc.train(Arrays.stream("word".split(" ")).iterator());
        assertEquals(1, mc.chain.size());
        ProbabilityDistribution<String> pd = mc.chain.get("word");
        assertTrue(pd.getRecords().containsKey(MarkovChain.END_TOKEN));
    }

    @Test
    public void testTrainWithMultipleWordsSentence() {
        MarkovChain mc = new MarkovChain();
        String sentence = "1 2 3";
        mc.train(Arrays.stream(sentence.split(" ")).iterator());
        assertEquals(3, mc.chain.size());
        ProbabilityDistribution<String> pd1 = mc.chain.get("1");
        assertTrue(pd1.getRecords().containsKey("2"));
        assertEquals(1, pd1.count("2"));
        ProbabilityDistribution<String> pd2 = mc.chain.get("2");
        assertTrue(pd2.getRecords().containsKey("3"));
        assertEquals(1, pd2.count("3"));
        ProbabilityDistribution<String> pd3 = mc.chain.get("3");
        assertTrue(pd3.getRecords().containsKey(MarkovChain.END_TOKEN));
        assertEquals(1, pd3.count(MarkovChain.END_TOKEN));
    }

    @Test
    public void testTrainWithRepeatedWords() {
        MarkovChain mc = new MarkovChain();
        String sentence = "1 2 1 2 1";
        mc.train(Arrays.stream(sentence.split(" ")).iterator());
        assertEquals(2, mc.chain.size());
        ProbabilityDistribution<String> pd1 = mc.chain.get("1");
        assertTrue(pd1.getRecords().containsKey("2"));
        assertEquals(2, pd1.count("2"));
    }

    /* **** ****** ****** MARKOV CHAIN CLASS TESTS ***** ****** ***** */

 
    @Test
    public void testWalk() {

        String[] expectedWords = { "CIS", "1200", "beats", "CIS", "1200", "rocks" };
        MarkovChain mc = new MarkovChain();

        String sentence1 = "CIS 1200 rocks";
        String sentence2 = "CIS 1200 beats CIS 1600";
        mc.train(Arrays.stream(sentence1.split(" ")).iterator());
        mc.train(Arrays.stream(sentence2.split(" ")).iterator());

        mc.reset("CIS"); // we start with "CIS" since that's the word our desired walk starts with
        mc.fixDistribution(new ArrayList<>(Arrays.asList(expectedWords)));

        for (int i = 0; i < expectedWords.length; i++) {
            assertTrue(mc.hasNext());
            assertEquals(expectedWords[i], mc.next());
        }

    }

    @Test
    public void testHasNextOnResetNonTrainedWord() {
        MarkovChain markovChain = new MarkovChain(new ListNumberGenerator(Arrays.asList(42)));
        markovChain.train(Arrays.asList(
            "the", "quick", "brown", "fox", "jumps", "over", "the", "lazy", "dog").iterator());
        markovChain.reset("apple");
        assertTrue(markovChain.hasNext());
        assertEquals("apple", markovChain.next());
        assertFalse(markovChain.hasNext());
    }

    @Test
    public void testResetWithStartWord() {
        MarkovChain mc = new MarkovChain();
        String[] words = {"apple", "orange", "banana"};
        mc.train(Arrays.stream(words).iterator());
        mc.reset("orange");
        assertTrue(mc.hasNext());
        assertEquals("orange", mc.next());
        assertTrue(mc.hasNext());
        assertEquals("banana", mc.next());
        assertFalse(mc.hasNext());
    }

    @Test
    public void testResetToEndToken() {
        MarkovChain mc = new MarkovChain();
        String[] words = {"apple", "orange", "banana"};
        mc.train(Arrays.stream(words).iterator());
        mc.reset(MarkovChain.END_TOKEN);
        assertFalse(mc.hasNext());
        try {
            mc.next();
        } catch (NoSuchElementException e) {
            // Success
        }
    }

    @Test
    public void testResetWithNonExistentWord() {
        MarkovChain mc = new MarkovChain();
        String[] words = {"apple", "orange", "banana"};
        mc.train(Arrays.stream(words).iterator());
        try {
            mc.reset("grape");
        } catch (IllegalArgumentException e) {
            // Success
        }
    }

    @Test
    public void testHasNextWithSingleWordSentence() {
        MarkovChain mc = new MarkovChain();
        String[] words = {"word"};
        mc.train(Arrays.stream(words).iterator());
        assertFalse(mc.hasNext());
    }

    @Test
    public void testNextWithNonExistentWord() {
        MarkovChain mc = new MarkovChain();
        String[] words = {"apple", "orange", "banana"};
        mc.train(Arrays.stream(words).iterator());
        mc.reset("apple");
        assertTrue(mc.hasNext());
        assertEquals("apple", mc.next());
        try {
            mc.next();
        } catch (NoSuchElementException e) {
            // Success
        }
    }

}
