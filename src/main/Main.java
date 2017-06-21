/*
 * Simple Spark web application
 *
 */

package main;

import utils.FreemarkerEngine;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Date;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import static spark.Spark.*;

/*
todo - The server must provide a way to persist the servers hosts maybe in a json file
 */
public class Main {

    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private static String request(String url, String params) {

        try {
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            // optional default is GET
            con.setRequestMethod("GET");

            int responseCode = con.getResponseCode();

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            return response.toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static JSONArray getServers() {
        return (JSONObject) parser.parse(new FileReader("/utils/server.json"));
    }

    private static void addServer(Server server) {
        JSONObject object = (JSONObject)parser.parse(new FileReader("/utils/server.json"));
        JSONArray servers = (JSONArray)object.get("servers");
        servers.add(server);
    }

    private static void removeServer(String name) {
        JSONObject object = (JSONObject)parser.parse(new FileReader("/utils/server.json"));
        JSONArray servers = (JSONArray)object.get("servers");
        Iterator iterator = servers.iterator();
        while(iterator.hasNext()) {
            if(iterator.next().get(name).getName().equals(name)){
                iterator.remove();
                return;
            }
        }
    }

    private static String getServerHost(String name) {
        JSONObject object = (JSONObject)parser.parse(new FileReader("/utils/server.json"));
        JSONArray servers = (JSONArray)object.get("servers");
        Iterator iterator = servers.iterator();
        while(iterator.hasNext()) {
            Server server = (Server)iterator.next();
            if(iterator.next().get(name).getName().equals(name)){
                return server.getHost();
            }
        }
        return null;
    }

    public static void main(String[] args) {

        // Configure Spark
        port(8000);
        staticFiles.externalLocation("src/resources");


        // Configure freemarker engine
        FreemarkerEngine engine = new FreemarkerEngine("src/templates");

        //     DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

        get("/", (request, response) -> {
            try {
                JSONObject server = getServers();
                return engine.render(server, "server/server.ftl");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        get("/server", (request, response) -> {
            JSONObject server = getServers();
            return engine.render(server, "server/server.ftl");
        });

        get("/server/add", (request, response) -> {
            JSONObject server = getServers();
            return engine.render(server, "server/server.ftl");
        });

        post("/server/add"), (request, response) -> {
            Type type = new TypeToken<Map<String, String>>(){}.getType();
            Map<String, String> map = gson.fromJson(request.body(), type);
            String serverName = map.get("name");
            String serverHost = map.get("host");
            //Todo - validate if the server doesn't already exists
            Server server = new Server(serverName, serverHost);
            Main.addServer(server);
        });

        get("/server/remove", (request, response) -> {
            JSONObject server = getServers();
            return engine.render(server, "server/server.ftl");
        });

        post("/server/remove"), (request, response) -> {
            Type type = new TypeToken<Map<String, String>>(){}.getType();
            Map<String, String> map = gson.fromJson(request.body(), type);
            String serverName = map.get("name");
            Main.removeServer(serverName);
        });

        //Entity instance edit
        get("/server/:server/entity/:entity", (request, response) -> {
            //Get the params from the request
            String server = request.params("server");
            String entity = request.params("entity");

            String host = getServerHost(server);
            if(host == null) {
                //todo - redirect to not found
                return null;
            }

            //Get the attributes of the entity
            String jsonAttributeList = Main.request(host + "/api/model/" + modelName +
                    "/entity/" + entityId + "/attributes" , "");

            //Get the instances of the entity
            String jsonInstanceList = Main.request(host + "/api/model/" + modelName +
                    "/entity/" + entityId + "/list" , "");

            //Parse the attributes json
            Type typeAttributesList = new TypeToken<List<Attribute>>() {}.getType();
            List<Attribute> attributes = Main.gson.fromJson(request.body(), typeAttributesList);

            //Parse the the instances json
            Type typeInstanceList = new TypeToken<List<Map<String, String>>>() {}.getType();
            List<Map<String, String>> instances = gson.fromJson(request.body(), typeInstanceList);

            Map<String, Object> model = new HashMap<>();
            model.put("host", host);
            model.put("attributes", attributes);
            model.put("instances", instances);

            return engine.render(model, "list.html");
        });

        //Instance information and edit page
        get("/server/:server/entity/:entity/instance/:instance", (request, response) -> {
            //Get the params from the request
            String server = request.params("server");
            String entity = request.params("entity");
            String instance = request.params("instance");

            String host = getServerHost(server);
            if(host == null) {
                //todo - redirect to not found
                return null;
            }

            //Get the attributes of the entity
            String jsonAttributeList = Main.request(host + "/api/model/" + modelName +
                    "/entity/" + entityId + "/attributes" , "");

            //Get the instances of the entity
            String jsonInstance = Main.request(host + "/api/model/" + modelName +
                    "/entity/" + entityId + "/instance/" + instance , "");

            //Parse the attributes json
            Type typeAttributesList = new TypeToken<List<Attribute>>() {}.getType();
            List<Attribute> attributes = Main.gson.fromJson(request.body(), typeAttributesList);

            //Parse the the instances json
            Type typeInstance = new TypeToken<<Map<String, String>>() {}.getType();
            Map<String, String> instance = gson.fromJson(request.body(), typeInstance);

            Map<String, Object> model = new HashMap<>();
            model.put("host", host);
            model.put("attributes", attributes);
            model.put("instance", instance);

            return engine.render(model, "instance/instance.ftl");
        });

        //Instance adding page
        get("/server/:server/entity/:entity/instance/:instance/add", (request, response) -> {
            //Get the params from the request
            String server = request.params("server");
            String entity = request.params("entity");
            String instance = request.params("instance");

            String host = getServerHost(server);
            if(host == null) {
                //todo - redirect to not found
                return null;
            }

            //Get the attributes of the entity
            String jsonAttributeList = Main.request(host + "/api/model/" + modelName +
                    "/entity/" + entityId + "/attributes" , "");

            Map<String, Object> model = new HashMap<>();
            model.put("host", host);
            model.put("attributes", attributes);

            return engine.render(model, "instance/add.ftl");
        });

    }
}