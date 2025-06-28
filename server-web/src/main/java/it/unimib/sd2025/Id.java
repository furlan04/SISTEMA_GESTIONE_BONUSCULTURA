package it.unimib.sd2025;

/**
 * Id is a utility class that provides methods to generate unique IDs.
 * It maintains a static counter that is incremented each time a new ID is requested.
 * The IDs are returned as strings.
 */
public class Id {
    private static int currentId = 10;
    private static Object _lock = new Object();

    private Id() {
        // Private constructor to prevent instantiation
        // This class is intended to be a utility class for generating unique IDs.
    }
    public static String getNextId() {
        synchronized (_lock) {
            return String.valueOf(currentId++);
        }
    }
    
    public static void reset() {
        currentId = 0;
    }
    
}       