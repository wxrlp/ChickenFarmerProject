package builder.world;

import org.junit.Test;

import static org.junit.Assert.*;

public class WorldLoadExceptionTest {

    @Test
    public void testBasicConstructor() {
        WorldLoadException exception = new WorldLoadException("Test message");

        assertEquals("Test message", exception.getMessage());
    }

    @Test
    public void testConstructorWithRow() {
        WorldLoadException exception =
                new WorldLoadException("Error occurred", 5);

        String message = exception.getMessage();
        assertTrue(message.contains("Error occurred"));
        assertTrue(message.contains("line 6")); // Row 5 = line 6
    }

    @Test
    public void testConstructorWithRowAndColumn() {
        WorldLoadException exception =
                new WorldLoadException("Invalid character", 3, 7);

        String message = exception.getMessage();
        assertTrue(message.contains("Invalid character"));
        assertTrue(message.contains("line 4")); // Row 3 = line 4
        assertTrue(message.contains("character 8")); // Column 7 = character 8
    }

    @Test
    public void testMessageWithRowZero() {
        WorldLoadException exception = new WorldLoadException("First line error", 0);

        assertTrue(exception.getMessage().contains("line 1"));
    }

    @Test
    public void testMessageWithRowAndColumnZero() {
        WorldLoadException exception =
                new WorldLoadException("First position error", 0, 0);

        String message = exception.getMessage();
        assertTrue(message.contains("line 1"));
        assertTrue(message.contains("character 1"));
    }

    @Test
    public void testMessageFormatWithLargeNumbers() {
        WorldLoadException exception = new WorldLoadException("Large numbers", 99, 99);

        String message = exception.getMessage();
        assertTrue(message.contains("line 100"));
        assertTrue(message.contains("character 100"));
    }
    @Test
    public void testOnlyRowVsRowAndColumn() {
        WorldLoadException exceptionWithRow = new WorldLoadException("Has row", 5);
        WorldLoadException exceptionWithBoth = new WorldLoadException("Has both", 5, 10);

        assertNotEquals(exceptionWithRow.getMessage(), exceptionWithBoth.getMessage());
    }
}