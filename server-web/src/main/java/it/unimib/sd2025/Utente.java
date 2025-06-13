package it.unimib.sd2025;


public class Utente {
    public String Nome;
    
    public String Cognome;
    
    public String Email;
    
    public String CodiceFiscale;

    // Costruttore vuoto OBBLIGATORIO per JSON parsing
    public Utente() {
    }

    // Costruttore con parametri
    public Utente(String nome, String cognome, String email, String codiceFiscale) {
        this.Nome = nome;
        this.Cognome = cognome;
        this.Email = email;
        this.CodiceFiscale = codiceFiscale;
    }

    // Getter e setter
    public String getNome() {
        return Nome;
    }
    
    public void setNome(String nome) {
        this.Nome = nome;
    }
    
    public String getCognome() {
        return Cognome;
    }

    public void setCognome(String cognome) {
        this.Cognome = cognome;
    }
    
    public String getEmail() {
        return Email;
    }
    
    public void setEmail(String email) {
        this.Email = email;
    }
    
    public String getCodiceFiscale() {
        return CodiceFiscale;
    }
    
    public void setCodiceFiscale(String codiceFiscale) {
        this.CodiceFiscale = codiceFiscale;
    }

    @Override
    public String toString() {
        return "Utente{" +
                "Nome='" + Nome + '\'' +
                ", Cognome='" + Cognome + '\'' +
                ", Email='" + Email + '\'' +
                ", CodiceFiscale='" + CodiceFiscale + '\'' +
                '}';
    }
}