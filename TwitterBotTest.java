package org.cis1200;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Tests for TwitterBot class */
public class TwitterBotTest {
    @Test
    public void simpleTwitterBotShortTest() {
        List<String> desiredTweet = new ArrayList<>(
                Arrays.asList(
                        "hello", "cis1200", "world", "!"
                )
        );
        String words = "0, simple test.\n"
                + "1, hello cis1200 world!\n"
                + "2, this is amazing.";
        StringReader sr = new StringReader(words);
        BufferedReader br = new BufferedReader(sr);
        TwitterBot t = new TwitterBot(br, 1);
        t.fixDistribution(desiredTweet);

        String expected = "hello cis1200 world!";
        String actual = t.generateTweet(3);
        assertEquals(expected, actual);
    }

    @Test
    public void simpleTwitterBotLongTest() {
        List<String> desiredTweet = new ArrayList<>(
                Arrays.asList(
                        "this", "comes", "from", "data", "with", "no", "duplicate", "words", ".",
                        "the", "end", "should", "come", "."
                )
        );
        String words = "0, The end should come here.\n"
                + "1, This comes from data with no duplicate words!";
        StringReader sr = new StringReader(words);
        BufferedReader br = new BufferedReader(sr);
        TwitterBot t = new TwitterBot(br, 1);
        t.fixDistribution(desiredTweet);

        String expected = "this comes from data with no duplicate words. the end should come.";
        String actual = TweetParser.replacePunctuation(t.generateTweet(12));
        assertEquals(expected, actual);
    }

    /*
     * This is the empty file test case.
     */
    @Test
    public void emptyFileCreatesEmptyTweet() {
        // Checks that your program does not go into an infinite loop
        assertTimeoutPreemptively(
                Duration.ofSeconds(10), () -> {
                    // No exceptions are thrown if file is empty
                    TwitterBot tb = new TwitterBot(
                            FileLineIterator.fileToReader("./files/empty.csv"), 2
                    );
                    // Checks that the bot creates an empty tweet
                    assertEquals(0, tb.generateTweet(10).length());
                }
        );
    }


    @Test
    public void testTwitterBot1() {
        List<String> desiredTweet = new ArrayList<>(Arrays.asList(
            "this", "comes", "from", "data", "with", "no", "duplicate", "words", ".",
            "the", "end", "should", "come", "."));
        String words = "0, The end should come here.\n"
                + "1, This comes from data with no duplicate words!";

        StringReader sr = new StringReader(words);
        BufferedReader br = new BufferedReader(sr);
        TwitterBot twitterBot = new TwitterBot(br, 1);

        twitterBot.fixDistribution(desiredTweet);

        String generatedTweet = twitterBot.generateTweet(12);
        String expectedTweet = "this comes from data with no duplicate words. the end should come.";

        assertTrue(TwitterBot.isPunctuated(generatedTweet));
        assertEquals(expectedTweet, TweetParser.replacePunctuation(generatedTweet));
    }

    @Test
    public void testTwitterBot2() {
        String words = "0, The end should come here.\n"
                + "1, This comes from data with no duplicate words!";
        StringReader sr = new StringReader(words);
        BufferedReader br = new BufferedReader(sr);
        TwitterBot twitterBot = new TwitterBot(br, 1);

        List<String> generatedTweets = twitterBot.generateTweets(3, 50);
        assertEquals(3, generatedTweets.size());
        for (String tweet : generatedTweets) {
            assertNotNull(tweet);
            assertTrue(TwitterBot.isPunctuated(tweet));
        }
    }

    @Test
    public void testTwitterBotNegativeNumWords() {
        String words = "0, The end should come here.\n"
                + "1, This comes from data with no duplicate words!";
        StringReader sr = new StringReader(words);
        BufferedReader br = new BufferedReader(sr);
        TwitterBot twitterBot = new TwitterBot(br, 1);

        assertThrows(IllegalArgumentException.class, () -> twitterBot.generateTweet(-5));
    }

    @Test
    public void testTwitterBotPunctuationCheck() {
        String words = "0, The end should come here.\n"
                + "1, This comes from data with no duplicate words!";
        StringReader sr = new StringReader(words);
        BufferedReader br = new BufferedReader(sr);
        TwitterBot twitterBot = new TwitterBot(br, 1);

        List<String> generatedTweets = twitterBot.generateTweets(5, 50);
        for (String tweet : generatedTweets) {
            assertTrue(TwitterBot.isPunctuated(tweet));
        }
    }

}
