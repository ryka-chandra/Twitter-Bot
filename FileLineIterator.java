package org.cis1200;

import java.util.Iterator;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileReader;
import java.util.NoSuchElementException;

public class FileLineIterator implements Iterator<String> {

    private BufferedReader bufferedReader;
    private boolean next;
    private String nextLine;

    /**
     * Creates a FileLineIterator for the reader. 
     *
     * @param reader - A reader to be turned to an Iterator
     * @throws IllegalArgumentException if reader is null
     */
    public FileLineIterator(BufferedReader reader) {
        // Complete this constructor.
        bufferedReader = reader;
        if (reader == null) {
            throw new IllegalArgumentException();
        } else {
            try {
                nextLine = bufferedReader.readLine();
                if (nextLine == null) {
                    next = false;
                } else {
                    next = true;
                }
            } catch (IOException e) {
                next = false;
            }
        }
    }

    /**
     * Creates a FileLineIterator from a provided filePath by creating a
     * FileReader and BufferedReader for the file.
     * @param filePath - a string representing the file
     * @throws IllegalArgumentException if filePath is null or if the file
     *                                  doesn't exist
     */
    public FileLineIterator(String filePath) {
        this(fileToReader(filePath));
    }

    /**
     * Takes in a filename and creates a BufferedReader.
     *
     * @param filePath - the path to the CSV file to be turned to a
     *                 BufferedReader
     * @return a BufferedReader of the provided file contents
     * @throws IllegalArgumentException if filePath is null or if the file
     *                                  doesn't exist
     */
    public static BufferedReader fileToReader(String filePath) {
        if (filePath == null) {
            throw new IllegalArgumentException();
        }
        try {
            FileReader fileReader = new FileReader(filePath);
            return new BufferedReader(fileReader);   
        } catch (IOException e) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Returns true if there are lines left to read in the file, and false
     * otherwise.
     *
     * @return a boolean indicating whether the FileLineIterator can produce
     *         another line from the file
     */
    @Override
    public boolean hasNext() {
        if (!next) {
            try {
                bufferedReader.close();
            } catch (IOException e) {
                System.out.println("IOException during the closing process"); // UNSURE
            }
        }
        return next;   
    }

    /**
     * Returns the next line from the file, or throws a NoSuchElementException
     * if there are no more strings left to return (i.e. hasNext() is false).
     *
     * @return the next line in the file
     * @throws java.util.NoSuchElementException if there is no more data in the
     *                                          file
     */
    @Override
    public String next() {
        if (next) {
            String current = nextLine;
            try {
                nextLine = bufferedReader.readLine();
            } catch (IOException e) {
                next = false;
            }
            if (nextLine == null) {
                next = false;
            }
            return current;
        } else {
            throw new NoSuchElementException();
        } 
 
    }
}
