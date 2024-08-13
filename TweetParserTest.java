package org.cis1200;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;

/** Tests for TweetParser */
public class TweetParserTest {

    // A helper function to create a singleton list from a word
    private static List<String> singleton(String word) {
        List<String> l = new LinkedList<String>();
        l.add(word);
        return l;
    }

    // A helper function for creating lists of strings
    private static List<String> listOfArray(String[] words) {
        List<String> l = new LinkedList<String>();
        for (String s : words) {
            l.add(s);
        }
        return l;
    }

    // Cleaning and filtering tests -------------------------------------------
    @Test
    public void removeURLsTest() {
        assertEquals("abc . def.", TweetParser.removeURLs("abc http://www.cis.upenn.edu. def."));
        assertEquals("abc", TweetParser.removeURLs("abc"));
        assertEquals("abc ", TweetParser.removeURLs("abc http://www.cis.upenn.edu"));
        assertEquals("abc .", TweetParser.removeURLs("abc http://www.cis.upenn.edu."));
        assertEquals(" abc ", TweetParser.removeURLs("http:// abc http:ala34?#?"));
        assertEquals(" abc  def", TweetParser.removeURLs("http:// abc http:ala34?#? def"));
        assertEquals(" abc  def", TweetParser.removeURLs("https:// abc https``\":ala34?#? def"));
        assertEquals("abchttp", TweetParser.removeURLs("abchttp"));
    }

    @Test
    public void testCleanWord() {
        assertEquals("abc", TweetParser.cleanWord("abc"));
        assertEquals("abc", TweetParser.cleanWord("ABC"));
        assertNull(TweetParser.cleanWord("@abc"));
        assertEquals("ab'c", TweetParser.cleanWord("ab'c"));
    }

    /* **** ****** ***** **** EXTRACT COLUMN TESTS **** **** ****** ***** */

    @Test
    public void testExtractColumnGetsCorrectColumn() {
        assertEquals(
                " This is a tweet.",
                TweetParser.extractColumn(
                        "wrongColumn, wrong column, wrong column!, This is a tweet.", 3
                )
        );
    }

    @Test
    public void testExtractColumn() {
        // Test with a valid CSV and middle column
        assertEquals("value", TweetParser.extractColumn("column1,column2,value,column4", 2));

        // Test with a valid CSV and last column
        assertEquals("column4", TweetParser.extractColumn("column1,column2,value,column4", 3));

        // Test with a null CSV 
        assertNull(TweetParser.extractColumn(null, 1));

        // Test with a negative column index
        assertNull(TweetParser.extractColumn("column1,column2,value,column4", -1));

        // Test with an out of bounds column index
        assertNull(TweetParser.extractColumn("column1,column2,value,column4", 4));

        // Test with empty CSV 
        assertEquals("", TweetParser.extractColumn("", 0));
    }

    /* **** ****** ***** ***** CSV DATA TO TWEETS ***** **** ****** ***** */

    @Test
    public void testCsvDataToTweetsSimpleCSV() {
        StringReader sr = new StringReader(
                "0, The end should come here.\n" +
                        "1, This comes from data with no duplicate words!"
        );
        BufferedReader br = new BufferedReader(sr);
        List<String> tweets = TweetParser.csvDataToTweets(br, 1);
        List<String> expected = new LinkedList<String>();
        expected.add(" The end should come here.");
        expected.add(" This comes from data with no duplicate words!");
        assertEquals(expected, tweets);
    }

    @Test
    public void testCsvDataToTweetsValidCsvAndValidColumn() {
        StringReader validReader = new StringReader(
            "column1,column2,value,column4\nline2,line3,line4,line5");
        BufferedReader validBr = new BufferedReader(validReader);
        List<String> expectedValid = listOfArray(new String[]{"value", "line4"});
        assertEquals(expectedValid, TweetParser.csvDataToTweets(validBr, 2));
    }

    @Test
    public void testCsvDataToTweetsValidCsvAndLastColumn() {
        StringReader lastColumnReader = new StringReader(
            "column1,column2,value,column4\nline2,line3,line4,line5");
        BufferedReader lastColumnBr = new BufferedReader(lastColumnReader);
        List<String> expectedLastColumn = listOfArray(new String[]{"column4", "line5"});
        assertEquals(expectedLastColumn, TweetParser.csvDataToTweets(lastColumnBr, 3));
    }
    
    @Test
    public void testCsvDataToTweetsNegativeColumnIndex() {
        StringReader invalidColumnReader = new StringReader(
            "column1,column2,value,column4\nline2,line3,line4,line5");
        BufferedReader invalidColumnBr = new BufferedReader(invalidColumnReader);
        List<String> expectedInvalidColumn = new LinkedList<String>();
        assertEquals(expectedInvalidColumn, TweetParser.csvDataToTweets(invalidColumnBr, -1));
    }

    @Test
    public void testCsvDataToTweetsOutOfBoundsColumnIndex() {
        StringReader outOfBoundsReader = new StringReader(
            "column1,column2,value,column4\nline2,line3,line4,line5");
        BufferedReader outOfBoundsBr = new BufferedReader(outOfBoundsReader);
        List<String> expectedOutOfBounds = new LinkedList<String>();
        assertEquals(expectedOutOfBounds, TweetParser.csvDataToTweets(outOfBoundsBr, 4));
    }

    @Test
    public void testCsvDataToTweetsEmptyCsv() {
        StringReader emptyLinesReader = new StringReader("\n\n\n\n");
        BufferedReader emptyLinesBr = new BufferedReader(emptyLinesReader);
        List<String> expectedEmptyLines = listOfArray(new String[]{"", "", "", ""});
        assertEquals(expectedEmptyLines, TweetParser.csvDataToTweets(emptyLinesBr, 0));
    }

    @Test
    public void testCsvDataToTweetsCsvWithEmptyColumn() {
        StringReader emptyColumnReader = new StringReader(
            "column1,column2,,column4\nline2,line3,line4,line5");
        BufferedReader emptyColumnBr = new BufferedReader(emptyColumnReader);
        List<String> expectedEmptyColumn = listOfArray(new String[]{"", "line4"});
        assertEquals(expectedEmptyColumn, TweetParser.csvDataToTweets(emptyColumnBr, 2));
    }

    @Test
    public void testCsvDataToTweetsCsvWithExtraColumns() {
        StringReader extraColumnsReader = new StringReader(
            "column1,column2,value,column4,extra\nline2,line3,line4,line5");
        BufferedReader extraColumnsBr = new BufferedReader(extraColumnsReader);
        List<String> expectedExtraColumns = listOfArray(new String[]{"value", "line4"});
        assertEquals(expectedExtraColumns, TweetParser.csvDataToTweets(extraColumnsBr, 2));
    }

    @Test
    public void testCsvDataToTweetsCsvWithFewerColumns() {
        StringReader fewerColumnsReader = new StringReader(
            "column1,column2,value\nline2,line3,line4");
        BufferedReader fewerColumnsBr = new BufferedReader(fewerColumnsReader);
        List<String> expectedFewerColumns = listOfArray(new String[]{"value", "line4"});
        assertEquals(expectedFewerColumns, TweetParser.csvDataToTweets(fewerColumnsBr, 2));
    }

    /* **** ****** ***** ** PARSE AND CLEAN SENTENCE ** ***** ****** ***** */

    @Test
    public void parseAndCleanSentenceNonEmptyFiltered() {
        List<String> sentence = TweetParser.parseAndCleanSentence("abc #@#F");
        List<String> expected = new LinkedList<String>();
        expected.add("abc");
        assertEquals(expected, sentence);
    }

    @Test
    public void parseAndCleanSentenceOneSentence() {
        String sentence = "This is a valid sentence with good words";
        List<String> expected = listOfArray(new String[]{
            "this", "is", "a", "valid", "sentence", "with", "good", "words"});
        assertEquals(expected, TweetParser.parseAndCleanSentence(sentence));
    }

    @Test
    public void parseAndCleanSentenceEmptySentence() {
        String emptySentence = "";
        List<String> expectedEmpty = new LinkedList<String>(); 
        assertEquals(expectedEmpty, TweetParser.parseAndCleanSentence(emptySentence));
    }

    @Test
    public void parseAndCleanSentenceBadWords() {
        String allBadWords = "@word !symbols";
        List<String> expectedAllBad = listOfArray(new String[]{});
        assertEquals(expectedAllBad, TweetParser.parseAndCleanSentence(allBadWords));
    }

    @Test
    public void parseAndCleanSentenceGoodAndBadWords() {
        String mixedWords = "Some good words and some bad @words!";
        List<String> expectedMixed = listOfArray(new String[]{
            "some", "good", "words", "and", "some", "bad"});
        assertEquals(expectedMixed, TweetParser.parseAndCleanSentence(mixedWords));
    }

    @Test
    public void parseAndCleanSentenceWhitespace() {
        String whitespaceSentence = "   Trim leading and trailing spaces.   ";
        List<String> expectedWhitespace = listOfArray(new String[]{
            "trim", "leading", "and", "trailing"});
        assertEquals(expectedWhitespace, TweetParser.parseAndCleanSentence(whitespaceSentence));
    }

    @Test
    public void parseAndCleanSentenceSpacesBetweenWords() {
        String multipleSpacesSentence = "   Multiple    Spaces    Between   Words.   ";
        List<String> expectedMultipleSpaces = listOfArray(new String[]{
            "multiple", "spaces", "between"});
        assertEquals(
            expectedMultipleSpaces, TweetParser.parseAndCleanSentence(multipleSpacesSentence));
    }

    @Test
    public void parseAndCleanSentenceDifferentTypesOfWhitespace() {
        String mixedWhitespaceSentence = "Tabs\tNewLines\nAndSpaces ";
        List<String> expectedMixedWhitespace = listOfArray(
            new String[]{"tabs", "newlines", "andspaces"});
        assertEquals(
            expectedMixedWhitespace, TweetParser.parseAndCleanSentence(mixedWhitespaceSentence));
    }

    @Test
    public void parseAndCleanSentenceUpperAndLowercase() {
        String mixedCaseSentence = "This seNtEnCe Has MIXED CasE";
        List<String> expectedMixedCase = listOfArray(new String[]{
            "this", "sentence", "has", "mixed", "case"});
        assertEquals(expectedMixedCase, TweetParser.parseAndCleanSentence(mixedCaseSentence));
    }

    /* **** ****** ***** **** PARSE AND CLEAN TWEET *** ***** ****** ***** */

    @Test
    public void testParseAndCleanTweetRemovesURLS1() {
        List<List<String>> sentences = TweetParser
                .parseAndCleanTweet("abc http://www.cis.upenn.edu");
        List<List<String>> expected = new LinkedList<List<String>>();
        expected.add(singleton("abc"));
        assertEquals(expected, sentences);
    }

    @Test
    public void testParseAndCleanTweetMultipleSentences() {
        String tweetWithSentences = "This is a valid tweet. Another sentence is here!";
        List<List<String>> expectedTweetWithSentences = new LinkedList<>();
        expectedTweetWithSentences.add(listOfArray(
            new String[]{"this", "is", "a", "valid", "tweet"}));
        expectedTweetWithSentences.add(listOfArray(
            new String[]{"another", "sentence", "is", "here"}));
        assertEquals(
            expectedTweetWithSentences, TweetParser.parseAndCleanTweet(tweetWithSentences));
    }

    @Test
    public void testParseAndCleanTweetWithSpaces() {
        String tweetWithMultipleSpaces = "This   tweet  has    multiple spaces.";
        List<List<String>> expectedTweetWithMultipleSpaces = new LinkedList<>();
        expectedTweetWithMultipleSpaces.add(listOfArray(
            new String[]{"this", "tweet", "has", "multiple", "spaces"}));
        assertEquals(expectedTweetWithMultipleSpaces, TweetParser.parseAndCleanTweet(
            tweetWithMultipleSpaces));
    }
   
    @Test
    public void testParseAndCleanTweetWhitespace() {
        String tweetWithDifferentWhitespace = "Tabs\tNewLines\nAndSpaces ";
        List<List<String>> expectedTweetWithDifferentWhitespace = new LinkedList<>();
        expectedTweetWithDifferentWhitespace.add(listOfArray(
            new String[]{"tabs", "newlines", "andspaces"}));
        assertEquals(expectedTweetWithDifferentWhitespace, TweetParser.parseAndCleanTweet(
            tweetWithDifferentWhitespace));
    }
    
    @Test
    public void testParseAndCleanTweetUpperAndLowercase() {
        String tweetWithMixedCase = "This TWEET has Mixed CaSe.";
        List<List<String>> expectedTweetWithMixedCase = new LinkedList<>();
        expectedTweetWithMixedCase.add(listOfArray(
            new String[]{"this", "tweet", "has", "mixed", "case"}));
        assertEquals(expectedTweetWithMixedCase, TweetParser.parseAndCleanTweet(
            tweetWithMixedCase));
    }

    @Test
    public void testParseAndCleanTweetEmptyTweet() {
        String emptyTweet = "";
        List<List<String>> expectedEmptyTweet = new LinkedList<>(); 
        assertEquals(expectedEmptyTweet, TweetParser.parseAndCleanTweet(emptyTweet));
    }

    @Test
    public void testParseAndCleanTweetBadWords() {
        String tweetWithBadWords = "@words! @mention.";
        List<List<String>> expectedTweetWithBadWords = new LinkedList<>(); 
        expectedTweetWithBadWords.add(new LinkedList<>()); 
        expectedTweetWithBadWords.add(new LinkedList<>());
        assertEquals(expectedTweetWithBadWords, TweetParser.parseAndCleanTweet(tweetWithBadWords));
    }

    @Test
    public void testParseAndCleanTweetGoodAndBadWords() {
        String tweetWithMixedWords = "Some good words and some bad @words!";
        List<List<String>> expectedTweetWithMixedWords = new LinkedList<>();
        expectedTweetWithMixedWords.add(listOfArray(
            new String[]{"some", "good", "words", "and", "some", "bad"}));
        assertEquals(expectedTweetWithMixedWords, TweetParser.parseAndCleanTweet(
            tweetWithMixedWords));
    }


    /* **** ****** ***** ** CSV DATA TO TRAINING DATA ** ***** ****** **** */

    @Test
    public void testCsvDataToTrainingDataSimpleCSV() {
        StringReader sr = new StringReader(
                "0, The end should come here.\n" +
                        "1, This comes from data with no duplicate words!"
        );
        BufferedReader br = new BufferedReader(sr);
        List<List<String>> tweets = TweetParser.csvDataToTrainingData(br, 1);
        List<List<String>> expected = new LinkedList<List<String>>();
        expected.add(listOfArray("the end should come here".split(" ")));
        expected.add(listOfArray("this comes from data with no duplicate words".split(" ")));
        assertEquals(expected, tweets);
    }

    @Test
    public void testCsvToTrainingDataValid() {
        StringReader validReader = new StringReader(
            "column1,column2,value,column4\nline2,line3,line4,line5");
        BufferedReader validBr = new BufferedReader(validReader);
        List<List<String>> expectedValid = new LinkedList<>();
        expectedValid.add(listOfArray(new String[]{"value"}));
        expectedValid.add(listOfArray(new String[]{"line4"}));
        assertEquals(expectedValid, TweetParser.csvDataToTrainingData(validBr, 2));
    }

    @Test
    public void testCsvToTrainingDataEmptyCsv() {
        StringReader emptyLinesReader = new StringReader("\n\n\n\n");
        BufferedReader emptyLinesBr = new BufferedReader(emptyLinesReader);
        List<List<String>> expectedEmptyLines = new LinkedList<>();
        assertEquals(expectedEmptyLines, TweetParser.csvDataToTrainingData(emptyLinesBr, 0));
    }

    @Test
    public void testCsvToTrainingDataInvalidColumnIndex() {
        StringReader invalidColumnReader = new StringReader(
            "column1,column2,value,column4\nline2,line3,line4,line5");
        BufferedReader invalidColumnBr = new BufferedReader(invalidColumnReader);
        List<List<String>> expectedInvalidColumn = new LinkedList<>();
        assertEquals(
            expectedInvalidColumn, TweetParser.csvDataToTrainingData(invalidColumnBr, -1));
    }

    @Test
    public void testCsvToTrainingDataColumnOutOfBounds() {
        StringReader outOfBoundsReader = new StringReader(
            "column1,column2,value,column4\nline2,line3,line4,line5");
        BufferedReader outOfBoundsBr = new BufferedReader(outOfBoundsReader);
        List<List<String>> expectedOutOfBounds = new LinkedList<>();
        expectedOutOfBounds.add(listOfArray(new String[]{"column2"}));  
        expectedOutOfBounds.add(listOfArray(new String[]{"line3"}));
        assertEquals(expectedOutOfBounds, TweetParser.csvDataToTrainingData(outOfBoundsBr, 1));
    }

    @Test
    public void testCsvToTrainingDataMultipleColumns() {
        StringReader multiColumnReader = new StringReader(
            "column1,column2,value,column4\nline2,line3,line4,line5");
        BufferedReader multiColumnBr = new BufferedReader(multiColumnReader);
        List<List<String>> expectedMultiColumn = new LinkedList<>();
        expectedMultiColumn.add(listOfArray(new String[]{"column1"}));
        expectedMultiColumn.add(listOfArray(new String[]{"line2"}));
        assertEquals(expectedMultiColumn, TweetParser.csvDataToTrainingData(multiColumnBr, 0));
    }

    @Test
    public void testCsvDataToTrainingData() {
        String csvData = "0, This is tweet one\n"
                + "1, Another tweet with URL: http://example.com.\n"
                + "2, Last tweet!;";
        BufferedReader br = new BufferedReader(new StringReader(csvData));
        List<List<String>> trainingData = TweetParser.csvDataToTrainingData(br, 1);
        assertEquals(3, trainingData.size());

        List<String> firstSentence = trainingData.get(0);
        assertEquals(4, firstSentence.size());
        assertTrue(firstSentence.contains("this"));
        assertTrue(firstSentence.contains("is"));
        assertTrue(firstSentence.contains("tweet"));
        assertTrue(firstSentence.contains("one"));

        List<String> secondSentence = trainingData.get(1);
        assertEquals(3, secondSentence.size());
        assertTrue(secondSentence.contains("another"));
        assertTrue(secondSentence.contains("tweet"));
        assertTrue(secondSentence.contains("with"));

        List<String> thirdSentence = trainingData.get(2);
        assertEquals(2, thirdSentence.size());
        assertTrue(thirdSentence.contains("last"));
    }

}
