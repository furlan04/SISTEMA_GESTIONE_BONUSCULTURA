package it.unimib.sd2025;

public class Utente {
    private String Nome;
    private String Cognome;
    private String Email;
    private String CodiceFiscale;

    public Utente(String nome, String cognome, String email, String codiceFiscale) {
        this.Nome = nome;
        this.Cognome = cognome;
        this.Email = email;
        this.CodiceFiscale = codiceFiscale;
    }
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
}
