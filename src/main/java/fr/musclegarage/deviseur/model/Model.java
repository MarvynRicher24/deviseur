package fr.musclegarage.deviseur.model;

public class Model {
    private int id;
    private int categoryId;
    private String modelName;
    private int modelPrice;
    private String modelImage;

    public Model() {
    }

    public Model(int id, int categoryId, String modelName, int modelPrice, String modelImage) {
        this.id = id;
        this.categoryId = categoryId;
        this.modelName = modelName;
        this.modelPrice = modelPrice;
        this.modelImage = modelImage;
    }

    // getters & setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getModelName() {
        return modelName;
    }

    @Override
    public String toString() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public int getModelPrice() {
        return modelPrice;
    }

    public void setModelPrice(int modelPrice) {
        this.modelPrice = modelPrice;
    }

    public String getModelImage() {
        return modelImage;
    }

    public void setModelImage(String modelImage) {
        this.modelImage = modelImage;
    }
}