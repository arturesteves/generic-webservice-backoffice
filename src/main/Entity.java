package main;

/**
 * Created by Ricardo Morais on 21/06/2017.
 */
public class Entity {
    private String name;
    private String instanceCount;

    public Entity(String name, String instanceCount) {
        this.name = name;
        this.instanceCount = instanceCount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInstanceCount() {
        return instanceCount;
    }

    public void setInstanceCount(String instanceCount) {
        this.instanceCount = instanceCount;
    }
}
