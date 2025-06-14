package it.unimib.sd2025;

public class Id {
    private static int currentId = 0;
    private static Object _lock = new Object();

    private Id() {
        // Private constructor to prevent instantiation
        // This class is intended to be a utility class for generating unique IDs.
    }
    public static int getNextId() {
        synchronized (_lock) {
            return currentId++;
        }
    }
    
    public static void reset() {
        currentId = 0;
    }
    public static void setCurrentId(int id) {
        synchronized (_lock) {
            currentId = id;
        }
    }
}       