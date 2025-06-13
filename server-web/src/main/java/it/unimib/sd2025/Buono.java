package it.unimib.sd2025;

import java.sql.Date;

public class Buono {
    private String id;
    private double valore;
    private String tipologia;
    private Date dataCreazione;
    private Date dataScadenza;

    public Buono(String id, double valore, String tipologia, Date dataCreazione, Date dataScadenza) {
        this.id = id;
        this.valore = valore;
        this.tipologia = tipologia;
        this.dataCreazione = dataCreazione;
        this.dataScadenza = dataScadenza;
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
    public Date getDataScadenza() {
        return dataScadenza;
    }
    public void setDataScadenza(Date dataScadenza) {
        this.dataScadenza = dataScadenza;
    }
}
