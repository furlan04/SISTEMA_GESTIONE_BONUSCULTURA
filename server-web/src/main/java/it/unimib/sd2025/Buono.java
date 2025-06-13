package it.unimib.sd2025;

import java.sql.Date;

public class Buono {
    public String id;
    public double valore;
    public String tipologia;
    public Date dataCreazione;
    public Date dataConsumo;

    public Buono(String id, double valore, String tipologia, Date dataCreazione, Date dataConsumo) {
        this.id = String.valueOf(Id.getNextId());
        this.valore = valore;
        this.tipologia = tipologia;
        this.dataCreazione = dataCreazione;
        this.dataConsumo = dataConsumo;
    }
    public Buono() {
        // Default constructor for JSON deserialization
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public double getValore() {
        return valore;
    }
    public void setValore(double valore) {
        this.valore = valore;
    }
    public String getTipologia() {
        return tipologia;
    }
    public void setTipologia(String tipologia) {
        this.tipologia = tipologia;
    }
    public Date getDataCreazione() {
        return dataCreazione;
    }
    public void setDataCreazione(Date dataCreazione) {
        this.dataCreazione = dataCreazione;
    }
    public Date getDataConsumo() {
        return dataConsumo;
    }
    public void setDataConsumo(Date dataConsumo) {
        this.dataConsumo = dataConsumo;
    }
}
