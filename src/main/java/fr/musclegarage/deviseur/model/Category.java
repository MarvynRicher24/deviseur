package fr.musclegarage.deviseur.model;

public class Category {
    private int id;
    private String categoryName;
    private String imageFilename;

    public Category(int id, String categoryName, String imageFilename) {
        this.id = id;
        this.categoryName = categoryName;
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

    public String getCategoryName() {
        return categoryName;
    }

    @Override
    public String toString() {
        return categoryName; // ou getName()
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getImageFilename() {
        return imageFilename;
    }

    public void setImageFilename(String f) {
        this.imageFilename = f;
    }
}