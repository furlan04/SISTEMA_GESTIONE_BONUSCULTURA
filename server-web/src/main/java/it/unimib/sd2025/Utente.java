package it.unimib.sd2025;


public class Utente {
    public String nome;
    
    public String cognome;
    
    public String email;
    
    public String codiceFiscale;

    // Costruttore vuoto OBBLIGATORIO per JSON parsing
    public Utente() {
    }

    // Costruttore con parametri
    public Utente(String nome, String cognome, String email, String codiceFiscale) {
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.codiceFiscale = codiceFiscale;
    }

    // Getter e setter
    public String getNome() {
        return nome;
    }
    
    public void setNome(String nome) {
        this.nome = nome;
    }
    
    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getCodiceFiscale() {
        return codiceFiscale;
    }
    
    public void setCodiceFiscale(String codiceFiscale) {
        this.codiceFiscale = codiceFiscale;
    }

    @Override
    public String toString() {
        return "Utente{" +
                "Nome='" + nome + '\'' +
                ", Cognome='" + cognome + '\'' +
                ", Email='" + email + '\'' +
                ", CodiceFiscale='" + codiceFiscale + '\'' +
                '}';
    }
}