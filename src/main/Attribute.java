package main;

/**
 * Created by Ricardo Morais on 20/06/2017.
 */
public class Attribute {

    private String Attribute;
    private String type;

    public Attribute(String Attribute, String type) {
        this.Attribute = Attribute;
        this.type = type;
    }

    public String getAttribute() {
        return Attribute;
    }

    public void setAttribute(String attribute) {
        this.Attribute = attribute;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
