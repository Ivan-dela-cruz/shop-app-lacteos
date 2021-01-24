package co.desofsi.tiendavirtual.models;

import java.io.Serializable;
import java.util.ArrayList;

public class Order implements Serializable {
    private int id, id_customer,id_user,id_company;
    private String date, total, status, order_number,name_company,name_customer,date_format,url_order,longitude_company,latitude_company,longitude_order,latitude_order;
    private ArrayList<DetailOrder> list_detail;
    private Company company;

    public Order() {
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
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

    public String getUrl_order() {
        return url_order;
    }

    public void setUrl_order(String url_order) {
        this.url_order = url_order;
    }

    public String getDate_format() {
        return date_format;
    }

    public void setDate_format(String date_format) {
        this.date_format = date_format;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_customer() {
        return id_customer;
    }

    public void setId_customer(int id_customer) {
        this.id_customer = id_customer;
    }

    public int getId_user() {
        return id_user;
    }

    public void setId_user(int id_user) {
        this.id_user = id_user;
    }

    public int getId_company() {
        return id_company;
    }

    public void setId_company(int id_company) {
        this.id_company = id_company;
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

    public ArrayList<DetailOrder> getList_detail() {
        return list_detail;
    }

    public void setList_detail(ArrayList<DetailOrder> list_detail) {
        this.list_detail = list_detail;
    }
}
