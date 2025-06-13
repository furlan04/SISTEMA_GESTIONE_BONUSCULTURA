package it.unimib.sd2025;

public class Id {
    private static int currentId = 0;

    private Id() {
        // Private constructor to prevent instantiation
        // This class is intended to be a utility class for generating unique IDs.
    }
    public synchronized static int getNextId() {
        return currentId++;
    }
    
    public static void reset() {
        currentId = 0;
    }
}       