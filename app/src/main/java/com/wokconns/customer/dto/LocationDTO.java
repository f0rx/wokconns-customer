package com.wokconns.customer.dto;

import java.io.Serializable;

public class LocationDTO implements Serializable {

    String lati = "";
    String longi = "";

    public String getLati() {
        return lati;
    }

    public void setLati(String lati) {
        this.lati = lati;
    }

    public String getLongi() {
        return longi;
    }

    public void setLongi(String longi) {
        this.longi = longi;
    }
}
