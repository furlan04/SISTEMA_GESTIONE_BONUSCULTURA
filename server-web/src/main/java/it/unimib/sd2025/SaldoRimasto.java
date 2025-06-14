package it.unimib.sd2025;

public class SaldoRimasto {
    public double saldo;
    public SaldoRimasto(double saldo) {
        this.saldo = saldo;
    }
    public SaldoRimasto() {
        // Default constructor for JSON serialization/deserialization
    }
    public double getSaldo() {
        return saldo;
    }
    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }
}
