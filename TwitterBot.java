package org.cis1200;

import java.io.BufferedWriter;
import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.io.BufferedReader;
import java.util.Iterator;
import java.io.IOException;
import java.io.FileWriter;

/**
 * When run as an application, this
 * program builds a Markov Chain from the training data in the CSV file,
 * generates 10 random tweets, and prints them to the terminal.
 * <p>
 * This class also provides the writeTweetsToFile method, which can be used to
 * create a file containing randomly generated tweets.
 * 
 */
public class TwitterBot {

    /**
     * This is a path to the CSV file containing the tweets. The main method
     * below uses the tweets in this file when calling Twitterbot. 
     */
    static final String PATH_TO_TWEETS = "files/dog_feelings_tweets.csv";
    /** Column in the PATH_TO_TWEETS CSV file to read tweets from */
    static final int TWEET_COLUMN = 2;
    /** File to store generated tweets */
    static final String PATH_TO_OUTPUT_TWEETS = "files/generated_tweets.txt";

    /** The MarkovChain to generate tweets */
    MarkovChain mc;
    /** RandomNumber generator to pick random numbers */
    NumberGenerator ng;

    /**
     * Given a column and a buffered reader, initializes the TwitterBot by
     * training the MarkovChain with sentences sourced from the reader. Uses
     * the RandomNumberGenerator().
     *
     * @param br          - a buffered reader containing tweet data
     * @param tweetColumn - the column in the reader where the text of the tweet
     *                    itself is stored
     */
    public TwitterBot(BufferedReader br, int tweetColumn) {
        this(br, tweetColumn, new RandomNumberGenerator());
    }

    /**
     * Given a column and a buffered reader, initializes the TwitterBot by
     * training the MarkovChain with all the sentences obtained as training data
     * from the buffered reader.
     *
     * @param br          - a buffered reader containing tweet data
     * @param tweetColumn - the column in the buffered reader where the text
     *                    of the tweet itself is stored
     * @param ng          - A NumberGenerator for the ng field, also to be
     *                    passed to MarkovChain
     */
    public TwitterBot(BufferedReader br, int tweetColumn, NumberGenerator ng) {
        mc = new MarkovChain(ng);
        this.ng = ng;
        // Complete this method.
        List<List<String>> sentencesList = TweetParser.csvDataToTrainingData(br, tweetColumn);
        for (int i = 0; i < sentencesList.size(); i++) {
            Iterator<String> iter = sentencesList.get(i).iterator();
            mc.train(iter);
        }
    }

    /**
     * Given a List of Strings, prints those Strings to a file (one String per
     * line in the file). 
     *
     * @param stringsToWrite - A List of Strings to write to the file
     * @param filePath       - the string containing the path to the file where
     *                       the tweets should be written
     * @param append         - a boolean indicating whether the new tweets
     *                       should be appended to the current file or should
     *                       overwrite its previous contents
     */
    public void writeStringsToFile(
            List<String> stringsToWrite, String filePath,
            boolean append
    ) {
        File file = Paths.get(filePath).toFile();
        BufferedWriter bw;
        if (file == null) {
            return;
        }
        try {
            bw = new BufferedWriter(new FileWriter(file, append));
            for (int i = 0; i < stringsToWrite.size(); i++) {
                bw.write(stringsToWrite.get(i));
                bw.newLine();
            }
            bw.flush();
            bw.close();
        } catch (IOException e) {
        }
    }

    /**
     * Generates tweets and writes them to a file.
     *
     * @param numTweets - the number of tweets that should be written
     * @param numChars  - the number of characters in each tweet
     * @param filePath  - the path to a file to write the tweets to
     * @param append    - a boolean indicating whether the new tweets should be
     *                  appended to the current file or should overwrite its
     *                  previous contents
     */
    public void writeTweetsToFile(
            int numTweets, int numChars, String filePath,
            boolean append
    ) {
        writeStringsToFile(generateTweets(numTweets, numChars), filePath, append);
    }

    /**
     * Generates a tweet of a given number of words by using the populated
     * MarkovChain. 
     *
     * @param numWords - The desired number of words of the tweet to be
     *                 produced
     * @return a String representing a generated tweet
     * @throws IllegalArgumentException if numWords is negative
     */
    public String generateTweet(int numWords) {
        this.mc.reset();
        if (numWords < 0) {
            throw new IllegalArgumentException();
        }
        if (numWords == 0 || !this.mc.hasNext()) {
            return "";
        }
        int tweetWords = 0;
        String tweet = "";
        while (tweetWords < numWords) {
            tweetWords++;
            tweet = tweet + mc.next();
            if ((!mc.hasNext()) && (tweetWords < numWords)) {
                tweet = tweet + randomPunctuation();
                this.mc.reset();
            }
            if (tweetWords < numWords) {
                tweet = tweet + " ";
            }
        }
        tweet = tweet + randomPunctuation();
        return tweet; // Complete this method.
    }

    /**
     * Generates a series of tweets using generateTweetChars().
     *
     * @param numTweets - the number of tweets to generate
     * @param numChars  - the number of characters that each generated tweet
     *                  should have.
     * @return a List of Strings where each element is a tweet
     */
    public List<String> generateTweets(int numTweets, int numChars) {
        List<String> tweets = new ArrayList<>();
        while (numTweets > 0) {
            tweets.add(generateTweetChars(numChars));
            numTweets--;
        }
        return tweets;
    }

    /**
     * Generates a tweet using generateTweet().
     *
     * @param numChars - The desired number of characters of the tweet to be
     *                 produced
     * @return a String representing a generated tweet
     * @throws IllegalArgumentException if numChars is negative
     */
    public String generateTweetChars(int numChars) {
        if (numChars < 0) {
            throw new IllegalArgumentException(
                    "tweet length cannot be negative"
            );
        }

        String newTweet = generateTweet(1);
        if (newTweet == null || newTweet.length() == 0) {
            return "";
        }

        String tweet = "";
        int numWords = 1;
        while (true) {
            newTweet = generateTweet(numWords);
            if (newTweet.length() > numChars) {
                return tweet;
            }
            tweet = newTweet;
            numWords++;
        }
    }

    /**
     * A helper function for providing a random punctuation String.
     *
     * @return a string containing just one punctuation character, specifically
     *         '.' 70% of the time and ';', '?', and '!' each 10% of the time.
     */
    public String randomPunctuation() {
        char[] puncs = { ';', '?', '!' };
        int m = ng.next(10);
        if (m < puncs.length) {
            return String.valueOf(puncs[m]);
        }
        return ".";
    }

    /**
     * A helper function to return the numerical index of the punctuation.
     *
     * @param punc - an input char to return the index of
     * @return the numerical index of the punctuation
     */
    public int fixPunctuation(char punc) {
        return switch (punc) {
            case ';' -> 0;
            case '?' -> 1;
            case '!' -> 2;
            default -> 3;
        };
    }

    /**
     * Returns true if the passed in string is punctuation.
     *
     * @param s - a string to check whether it's punctuation
     * @return true if the string is punctuation, false otherwise.
     */
    public boolean isPunctuation(String s) {
        return s.equals(";") || s.equals("?") || s.equals("!") || s.equals(".");
    }

    /**
     * A helper function to determine if a string ends in punctuation.
     *
     * @param s - an input string to check for punctuation
     * @return true if the string s ends in punctuation
     */
    public static boolean isPunctuated(String s) {
        if (s == null || s.equals("")) {
            return false;
        }
        char[] puncs = TweetParser.getPunctuation();
        for (char c : puncs) {
            if (s.charAt(s.length() - 1) == c) {
                return true;
            }
        }
        return false;
    }

    /**
     * Prints ten generated tweets to the console so you can see how your bot is
     * performing!
     */
    public static void main(String[] args) {
        BufferedReader br = FileLineIterator.fileToReader(PATH_TO_TWEETS);
        TwitterBot t = new TwitterBot(br, TWEET_COLUMN);
        List<String> tweets = t.generateTweets(10, 280); // 280 chars in a tweet
        for (String tweet : tweets) {
            System.out.println(tweet);
        }

        // You can also write randomly generated tweets to a file by
        // uncommenting the following code:
        // t.writeTweetsToFile(10, 280, PATH_TO_OUTPUT_TWEETS, false);
    }

    /**
     * Modifies all MarkovChains to output sentences in the order specified.
     * 
     *
     * @param tweet - an ordered list of words and punctuation that the
     *              MarkovChain should output.
     */
    public void fixDistribution(List<String> tweet) {
        List<String> puncs = java.util.Arrays.asList(".", "?", "!", ";");

        if (tweet == null) {
            throw new IllegalArgumentException(
                    "fixDistribution(): tweet argument must not be null."
            );
        } else if (tweet.size() == 0) {
            throw new IllegalArgumentException(
                    "fixDistribution(): tweet argument must not be empty."
            );
        } else if (!puncs.contains(tweet.get(tweet.size() - 1))) {
            throw new IllegalArgumentException(
                    "fixDistribution(): Passed in tweet must be punctuated."
            );
        }

        mc.fixDistribution(
                tweet.stream().map(x -> puncs.contains(x) ? null : x)
                        .collect(java.util.stream.Collectors.toList()),
                true
        );
        List<Integer> puncIndices = new LinkedList<>();
        for (String curWord : tweet) {
            if (isPunctuation(curWord)) {
                puncIndices.add(fixPunctuation(curWord.charAt(0)));
            }
        }
        ng = new ListNumberGenerator(puncIndices);
    }
}
