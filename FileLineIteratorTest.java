package org.cis1200;

import org.junit.jupiter.api.Test;
import java.io.StringReader;
import java.io.BufferedReader;

import static org.junit.jupiter.api.Assertions.*;

/** Tests for FileLineIterator */
public class FileLineIteratorTest {
    @Test
    public void testHasNextAndNext() {
        String words = "0, The end should come here.\n"
                + "1, This comes from data with no duplicate words!";
        StringReader sr = new StringReader(words);
        BufferedReader br = new BufferedReader(sr);
        FileLineIterator li = new FileLineIterator(br);
        assertTrue(li.hasNext());
        assertEquals("0, The end should come here.", li.next());
        assertTrue(li.hasNext());
        assertEquals("1, This comes from data with no duplicate words!", li.next());
        assertFalse(li.hasNext());
    }

 
    @Test
    public void testEmpty() {
        StringReader sr = new StringReader("");
        BufferedReader br = new BufferedReader(sr);
        FileLineIterator li = new FileLineIterator(br);
        assertFalse(li.hasNext());
    }

    @Test
    public void testSingleLine() {
        StringReader sr = new StringReader("1, Single line test.");
        BufferedReader br = new BufferedReader(sr);
        FileLineIterator li = new FileLineIterator(br);
        assertTrue(li.hasNext());
        assertEquals("1, Single line test.", li.next());
        assertFalse(li.hasNext());
    }

    @Test
    public void testMultipleLines() {
        StringReader sr = new StringReader("1, First line.\n2, Second line.\n3, Third line.");
        BufferedReader br = new BufferedReader(sr);
        FileLineIterator li = new FileLineIterator(br);
        assertTrue(li.hasNext());
        assertEquals("1, First line.", li.next());
        assertTrue(li.hasNext());
        assertEquals("2, Second line.", li.next());
        assertTrue(li.hasNext());
        assertEquals("3, Third line.", li.next());
        assertFalse(li.hasNext());
    }

    @Test
    public void testBlankLines() {
        String words = "\n\n1, First line.\n\n\n2, Second line.\n\n";
        StringReader sr = new StringReader(words);
        BufferedReader br = new BufferedReader(sr);
        FileLineIterator li = new FileLineIterator(br);
        assertTrue(li.hasNext());
        assertEquals("", li.next());
        assertTrue(li.hasNext());
        assertEquals("", li.next());
        assertTrue(li.hasNext());
        assertEquals("1, First line.", li.next());
        assertTrue(li.hasNext());
        assertEquals("", li.next());
        assertTrue(li.hasNext());
        assertEquals("", li.next());
        assertTrue(li.hasNext());
        assertEquals("2, Second line.", li.next());
        assertEquals("", li.next());
        assertFalse(li.hasNext());
    }

    @Test
    public void testWhitespace() {
        String words = "  1, First line with leading spaces.\n" +
                        "2, Second line with trailing spaces.  \n";
        StringReader sr = new StringReader(words);
        BufferedReader br = new BufferedReader(sr);
        FileLineIterator li = new FileLineIterator(br);
        assertTrue(li.hasNext());
        assertEquals("  1, First line with leading spaces.", li.next());
        assertTrue(li.hasNext());
        assertEquals("2, Second line with trailing spaces.  ", li.next());
        assertFalse(li.hasNext());
    }    

}
