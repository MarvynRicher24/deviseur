package fr.musclegarage.deviseur.model;

public class Category {
    private int id;
    private String name;
    private String imageFilename;

    public Category(int id, String name, String imageFilename) {
        this.id = id;
        this.name = name;
        this.imageFilename = imageFilename;
    }

    public Category() {
    } // pour création « vide »

    // getters & setters
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

    public String getImageFilename() {
        return imageFilename;
    }

    public void setImageFilename(String f) {
        this.imageFilename = f;
    }
}