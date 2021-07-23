package com.wokconns.customer.dto;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

public class CurrencyDTO implements Serializable {
    String id = "";
    String currency_symbol = "";
    String currency_name = "";
    String code = "";
    String status = "";
    String created_at = "";

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCurrency_symbol() {
        return currency_symbol;
    }

    public void setCurrency_symbol(String currency_symbol) {
        this.currency_symbol = currency_symbol;
    }

    public String getCurrency_name() {
        return currency_name;
    }

    public void setCurrency_name(String currency_name) {
        this.currency_name = currency_name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    @NotNull
    @Override
    public String toString() {
        return "(" + currency_symbol + ")" + currency_name;
    }
}
