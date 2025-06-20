package it.unimib.sd2025.Model;

import jakarta.json.bind.annotation.JsonbProperty;

public class Utente {
    private String nome;

    private String cognome;

    private String email;

    private String codiceFiscale;

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

    @JsonbProperty("nome")
    public String getNome() {
        return nome;
    }

    @JsonbProperty("nome")
    public void setNome(String nome) {
        this.nome = nome;
    }

    @JsonbProperty("cognome")
    public String getCognome() {
        return cognome;
    }

    @JsonbProperty("cognome")
    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    @JsonbProperty("email")
    public String getEmail() {
        return email;
    }

    @JsonbProperty("email")
    public void setEmail(String email) {
        this.email = email;
    }

    @JsonbProperty("codiceFiscale")
    public String getCodiceFiscale() {
        return codiceFiscale;
    }

    @JsonbProperty("codiceFiscale")
    public void setCodiceFiscale(String codiceFiscale) {
        this.codiceFiscale = codiceFiscale;
    }
}