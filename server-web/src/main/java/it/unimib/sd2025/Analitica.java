package it.unimib.sd2025;

import jakarta.json.bind.annotation.JsonbProperty;

public class Analitica {

    @JsonbProperty("utenti_registrati")
    private int utentiRegistrati;

    @JsonbProperty("buoni_totali")
    private int buoniTotali;

    @JsonbProperty("buoni_consumati")
    private int buoniConsumati;

    @JsonbProperty("buoni_non_consumati")
    private int buoniNonConsumati;

    @JsonbProperty("contributi_spesi")
    private double contributiSpesi;

    @JsonbProperty("contributi_assegnati")
    private double contributiAssegnati;

    @JsonbProperty("contributi_disponibili")
    private double contributiDisponibili;

    // Getters e setters

    public int getUtentiRegistrati() {
        return utentiRegistrati;
    }

    public void setUtentiRegistrati(int utentiRegistrati) {
        this.utentiRegistrati = utentiRegistrati;
    }

    public int getBuoniTotali() {
        return buoniTotali;
    }

    public void setBuoniTotali(int buoniTotali) {
        this.buoniTotali = buoniTotali;
    }

    public int getBuoniConsumati() {
        return buoniConsumati;
    }

    public void setBuoniConsumati(int buoniConsumati) {
        this.buoniConsumati = buoniConsumati;
    }

    public int getBuoniNonConsumati() {
        return buoniNonConsumati;
    }

    public void setBuoniNonConsumati(int buoniNonConsumati) {
        this.buoniNonConsumati = buoniNonConsumati;
    }
    
    public double getContributiSpesi() {
        return contributiSpesi;
    }

    public void setContributiSpesi(double contributiSpesi) {
        this.contributiSpesi = contributiSpesi;
    }

    public double getContributiAssegnati() {
        return contributiAssegnati;
    }

    public void setContributiAssegnati(double contributiAssegnati) {
        this.contributiAssegnati = contributiAssegnati;
    }

    public double getContributiDisponibili() {
        return contributiDisponibili;
    }

    public void setContributiDisponibili(double contributiDisponibili) {
        this.contributiDisponibili = contributiDisponibili;
    }
}
