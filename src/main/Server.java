package main;

/**
 * Created by Ricardo Morais on 20/06/2017.
 */
public class Server {

    private String host;
    private String name;
    private String description;


    public Server(String host, String name) {
        this(host, name, "");
    }

    public Server(String host, String name, String description) {
        this.host = host;
        this.name = name;
        this.description = description;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
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


    @Override
    public String toString() {
        return '{' +
                "\"name\": \"" + name + "\",\n" +
                "\"host\": \"" + host + "\",\n" +
                "\"description=\": \"" + description + "\"\n" +
                '}';
    }
}
