package com.wokconns.customer.dto;

import java.io.Serializable;
import java.util.ArrayList;

public class WalletCurrencyDTO implements Serializable {
    String id = "";
    String user_id = "";
    String amount = "";
    String currency_type = "";
    String currency_code = "";
    String currency_id = "";
    String currency_name = "";
    ArrayList<WalletHistory> wallet_history = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCurrency_type() {
        return currency_type;
    }

    public void setCurrency_type(String currency_type) {
        this.currency_type = currency_type;
    }

    public String getCurrency_code() {
        return currency_code;
    }

    public void setCurrency_code(String currency_code) {
        this.currency_code = currency_code;
    }

    public String getCurrency_id() {
        return currency_id;
    }

    public void setCurrency_id(String currency_id) {
        this.currency_id = currency_id;
    }

    public String getCurrency_name() {
        return currency_name;
    }

    public void setCurrency_name(String currency_name) {
        this.currency_name = currency_name;
    }

    public ArrayList<WalletHistory> getWallet_history() {
        return wallet_history;
    }

    public void setWallet_history(ArrayList<WalletHistory> wallet_history) {
        this.wallet_history = wallet_history;
    }

    @Override
    public String toString() {
        return "(" + currency_code + ")" + currency_name;
    }
}
