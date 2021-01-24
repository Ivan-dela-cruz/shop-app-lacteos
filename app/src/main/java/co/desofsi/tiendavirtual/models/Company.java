package co.desofsi.tiendavirtual.models;

import java.io.Serializable;

public class Company implements Serializable {
    private int id;
    private String company_name, company_address, company_phone, company_description, latitude, longitude, url_merchant;

    public Company() {
    }

    public Company(int id, String company_name, String company_address, String company_phone, String company_description, String latitude, String longitude, String url_merchant) {
        this.id = id;
        this.company_name = company_name;
        this.company_address = company_address;
        this.company_phone = company_phone;
        this.company_description = company_description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.url_merchant = url_merchant;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public String getCompany_address() {
        return company_address;
    }

    public void setCompany_address(String company_address) {
        this.company_address = company_address;
    }

    public String getCompany_phone() {
        return company_phone;
    }

    public void setCompany_phone(String company_phone) {
        this.company_phone = company_phone;
    }

    public String getCompany_description() {
        return company_description;
    }

    public void setCompany_description(String company_description) {
        this.company_description = company_description;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getUrl_merchant() {
        return url_merchant;
    }

    public void setUrl_merchant(String url_merchant) {
        this.url_merchant = url_merchant;
    }
}
