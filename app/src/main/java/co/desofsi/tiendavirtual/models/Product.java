package co.desofsi.tiendavirtual.models;

import java.io.Serializable;

public class Product implements Serializable {
    private int id,stock;
    private String name,description,sale_price,url_image;

    public Product() {
    }

    public Product(int id, int stock, String name, String description, String sale_price, String url_image) {
        this.id = id;
        this.stock = stock;
        this.name = name;
        this.description = description;
        this.sale_price = sale_price;
        this.url_image = url_image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSale_price() {
        return sale_price;
    }

    public void setSale_price(String sale_price) {
        this.sale_price = sale_price;
    }

    public String getUrl_image() {
        return url_image;
    }

    public void setUrl_image(String url_image) {
        this.url_image = url_image;
    }
}
