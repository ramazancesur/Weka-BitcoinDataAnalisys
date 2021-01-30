package tr.edu.gazi.bilisim.veri.madencik.model;

import tr.edu.gazi.bilisim.veri.madencik.customAnnotation.DataMatch;

import java.io.Serializable;

/**
 * Created by Ramazan CESUR on 29.1.2021.
 */
public class DataModel implements Serializable {
    @DataMatch(index = 1)
    private double acilis;
    @DataMatch(index = 2)
    private double yuksek;
    @DataMatch(index = 3)
    private double dusuk;
    @DataMatch(index = 4)
    private double kapanis;
    @DataMatch(index = 5)
    private double hacim;
    @DataMatch(index = 6)
    private double fark;
    @DataMatch(index = 7, dataClass = true)
    private int sinif;

    public double getKapanis() {
        return kapanis;
    }

    public void setKapanis(double kapanis) {
        this.kapanis = kapanis;
    }

    public double getAcilis() {
        return acilis;
    }

    public void setAcilis(double acilis) {
        this.acilis = acilis;
    }

    public double getYuksek() {
        return yuksek;
    }

    public void setYuksek(double yuksek) {
        this.yuksek = yuksek;
    }

    public double getDusuk() {
        return dusuk;
    }

    public void setDusuk(double dusuk) {
        this.dusuk = dusuk;
    }

    public double getHacim() {
        return hacim;
    }

    public void setHacim(double hacim) {
        this.hacim = hacim;
    }

    public double getFark() {
        return fark;
    }

    public void setFark(double fark) {
        this.fark = fark;
    }

    public int getSinif() {
        return sinif;
    }

    public void setSinif(int sinif) {
        this.sinif = sinif;
    }
}
