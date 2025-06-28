package it.unimib.sd2025.Model;

public class Error {
    private String message;
    public Error(String message) {
        this.message = message;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
}
