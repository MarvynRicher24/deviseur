package fr.musclegarage.deviseur.model;

public class OptionChoice {
    private int id;
    private int modelId;
    private int optionId;
    private String optionChoiceName;
    private int optionChoicePrice;
    private String optionChoiceImage;

    public OptionChoice() {
    }

    public OptionChoice(int id, int modelId, int optionId,
            String optionChoiceName, int optionChoicePrice,
            String optionChoiceImage) {
        this.id = id;
        this.modelId = modelId;
        this.optionId = optionId;
        this.optionChoiceName = optionChoiceName;
        this.optionChoicePrice = optionChoicePrice;
        this.optionChoiceImage = optionChoiceImage;
    }

    // getters & setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getModelId() {
        return modelId;
    }

    public void setModelId(int modelId) {
        this.modelId = modelId;
    }

    public int getOptionId() {
        return optionId;
    }

    public void setOptionId(int optionId) {
        this.optionId = optionId;
    }

    public String getOptionChoiceName() {
        return optionChoiceName;
    }

    public void setOptionChoiceName(String optionChoiceName) {
        this.optionChoiceName = optionChoiceName;
    }

    public int getOptionChoicePrice() {
        return optionChoicePrice;
    }

    public void setOptionChoicePrice(int optionChoicePrice) {
        this.optionChoicePrice = optionChoicePrice;
    }

    public String getOptionChoiceImage() {
        return optionChoiceImage;
    }

    public void setOptionChoiceImage(String optionChoiceImage) {
        this.optionChoiceImage = optionChoiceImage;
    }
}