package fr.musclegarage.deviseur.model;

public class Option {
    private int id;
    private String optionName;

    public Option() {
    }

    public Option(int id, String optionName) {
        this.id = id;
        this.optionName = optionName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOptionName() {
        return optionName;
    }

    @Override
    public String toString() {
        return optionName;
    }

    public void setOptionName(String optionName) {
        this.optionName = optionName;
    }
}