package com.wokconns.customer.dto;

import java.io.Serializable;

/**
 * Created by Hemant on 08/01/2020.
 */

public class LanguageDTO implements Serializable {
    String language_id = "";
    String language_code = "";
    String language_name = "";
    String status = "";
    String created_at = "";
    String updated_at = "";

    public LanguageDTO(String language_name, String language_code) {
        this.language_name = language_name;
        this.language_code = language_code;
    }

    public String getLanguage_id() {
        return language_id;
    }

    public void setLanguage_id(String language_id) {
        this.language_id = language_id;
    }

    public String getLanguage_code() {
        return language_code;
    }

    public void setLanguage_code(String language_code) {
        this.language_code = language_code;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLanguage_name() {
        return language_name;
    }

    public void setLanguage_name(String language_name) {
        this.language_name = language_name;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }
}