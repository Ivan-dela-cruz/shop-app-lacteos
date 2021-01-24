package co.desofsi.tiendavirtual.models;

import java.io.Serializable;

public class TypeCompany implements Serializable {
    private int id;
    private String name,description,url_image;

    public TypeCompany() {

    }

    public TypeCompany(int id, String name, String description, String url_image) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.url_image = url_image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getUrl_image() {
        return url_image;
    }

    public void setUrl_image(String url_image) {
        this.url_image = url_image;
    }
}
