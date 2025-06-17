package it.unimib.sd2025;

import java.sql.Date;

import jakarta.json.bind.annotation.JsonbProperty;

public class Buono {
    private String id;
    private double valore;
    private String tipologia;
    private Date dataCreazione;
    private Date dataConsumo = null;

    public Buono(double valore, String tipologia) {
        this.id = String.valueOf(Id.getNextId());
        this.valore = valore;
        this.tipologia = tipologia;
        this.dataCreazione = new Date(System.currentTimeMillis());
        this.dataConsumo = null;
    }
    public Buono(String id, double valore, String tipologia, Date dataCreazione, Date dataConsumo) {
        this.id = id;
        this.valore = valore;
        this.tipologia = tipologia;
        this.dataCreazione = dataCreazione;
        this.dataConsumo = dataConsumo;
    }
    public Buono() {
        // Default constructor for JSON deserialization
    }

    @JsonbProperty("id")
    public String getId() {
        return id;
    }

    @JsonbProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonbProperty("valore")
    public double getValore() {
        return valore;
    }

    @JsonbProperty("valore")
    public void setValore(double valore) {
        this.valore = valore;
    }

    @JsonbProperty("tipologia")
    public String getTipologia() {
        return tipologia;
    }

    @JsonbProperty("tipologia")
    public void setTipologia(String tipologia) {
        this.tipologia = tipologia;
    }

    @JsonbProperty("dataCreazione")
    public Date getDataCreazione() {
        return dataCreazione;
    }

    @JsonbProperty("dataCreazione")
    public void setDataCreazione(Date dataCreazione) {
        this.dataCreazione = dataCreazione;
    }

    @JsonbProperty("dataConsumo")
    public Date getDataConsumo() {
        return dataConsumo;
    }

    @JsonbProperty("dataConsumo")
    public void setDataConsumo(Date dataConsumo) {
        this.dataConsumo = dataConsumo;
    }
}
