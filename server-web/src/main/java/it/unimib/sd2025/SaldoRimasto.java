package it.unimib.sd2025;

import jakarta.json.bind.annotation.JsonbProperty;

public class SaldoRimasto {
    private double saldo;
    private double saldo_consumato;
    private double saldo_non_consumato;
    public SaldoRimasto(double saldo, double saldo_consumato, double saldo_non_consumato) {
        this.saldo = saldo;
        this.saldo_consumato = saldo_consumato;
        this.saldo_non_consumato = saldo_non_consumato;
    }
    public SaldoRimasto() {
        // Default constructor for JSON serialization/deserialization
    }
    @JsonbProperty("saldo")
    public double getSaldo() {
        return saldo;
    }
    @JsonbProperty("saldo")
    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }
    @JsonbProperty("saldo_consumato")
    public double getSaldo_consumato() {
        return saldo_consumato;
    }
    @JsonbProperty("saldo_consumato")
    public void setSaldo_consumato(double saldo_consumato) {
        this.saldo_consumato = saldo_consumato;
    }
    @JsonbProperty("saldo_non_consumato")
    public double getSaldo_non_consumato() {
        return saldo_non_consumato;
    }
    @JsonbProperty("saldo_non_consumato")
    public void setSaldo_non_consumato(double saldo_non_consumato) {
        this.saldo_non_consumato = saldo_non_consumato;
    }
}
