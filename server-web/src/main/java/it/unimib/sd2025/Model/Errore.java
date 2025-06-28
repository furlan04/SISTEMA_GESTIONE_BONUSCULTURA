package it.unimib.sd2025.Model;

public class Errore {
    private String message;
    public Errore(String message) {
        this.message = message;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
}
