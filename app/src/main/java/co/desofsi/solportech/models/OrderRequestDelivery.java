package co.desofsi.solportech.models;

import java.io.Serializable;

public class OrderRequestDelivery implements Serializable {
    private int id, id_delivery,id_company,id_order;
    private  String datetime, date,total, status, order_number,name_company,name_customer,url_order,url_request;
    private String longitude_company,latitude_company,longitude_order,latitude_order,total_request,status_request,company_address;

    public String getCompany_address() {
        return company_address;
    }

    public void setCompany_address(String company_address) {
        this.company_address = company_address;
    }

    public String getStatus_request() {
        return status_request;
    }

    public void setStatus_request(String status_request) {
        this.status_request = status_request;
    }

    public int getId_order() {
        return id_order;
    }

    public void setId_order(int id_order) {
        this.id_order = id_order;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_delivery() {
        return id_delivery;
    }

    public void setId_delivery(int id_delivery) {
        this.id_delivery = id_delivery;
    }

    public int getId_company() {
        return id_company;
    }

    public void setId_company(int id_company) {
        this.id_company = id_company;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOrder_number() {
        return order_number;
    }

    public void setOrder_number(String order_number) {
        this.order_number = order_number;
    }

    public String getName_company() {
        return name_company;
    }

    public void setName_company(String name_company) {
        this.name_company = name_company;
    }

    public String getName_customer() {
        return name_customer;
    }

    public void setName_customer(String name_customer) {
        this.name_customer = name_customer;
    }

    public String getUrl_order() {
        return url_order;
    }

    public void setUrl_order(String url_order) {
        this.url_order = url_order;
    }

    public String getUrl_request() {
        return url_request;
    }

    public void setUrl_request(String url_request) {
        this.url_request = url_request;
    }

    public String getLongitude_company() {
        return longitude_company;
    }

    public void setLongitude_company(String longitude_company) {
        this.longitude_company = longitude_company;
    }

    public String getLatitude_company() {
        return latitude_company;
    }

    public void setLatitude_company(String latitude_company) {
        this.latitude_company = latitude_company;
    }

    public String getLongitude_order() {
        return longitude_order;
    }

    public void setLongitude_order(String longitude_order) {
        this.longitude_order = longitude_order;
    }

    public String getLatitude_order() {
        return latitude_order;
    }

    public void setLatitude_order(String latitude_order) {
        this.latitude_order = latitude_order;
    }

    public String getTotal_request() {
        return total_request;
    }

    public void setTotal_request(String total_request) {
        this.total_request = total_request;
    }
}
