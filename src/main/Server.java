package main;

/**
 * Created by Ricardo Morais on 20/06/2017.
 */
public class Server {

    private String host;
    private String name;

    public Server(String host, String name) {
        this.host = host;
        this.name = name;
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
}
