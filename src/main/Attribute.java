package main;

/**
 * Created by Ricardo Morais on 20/06/2017.
 */
public class Attribute {

    private String attribute;
    private String type;

    public Attribute(String Attribute, String type) {
        this.attribute = Attribute;
        this.type = type;
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
