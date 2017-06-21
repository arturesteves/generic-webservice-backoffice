/*
 * Simple Spark web application
 *
 */

package main;

import com.google.gson.JsonParser;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import spark.Response;
import utils.FreemarkerEngine;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.*;

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

    private static JSONObject getServers() {

        JSONParser parser = new JSONParser();
        try {
            return (JSONObject) parser.parse(new FileReader(System.getProperty("user.dir") + "/src/utils/server.json"));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }

    private static void addServer(Server server) {
        JSONParser parser = new JSONParser();
        try {
            JSONObject object = (JSONObject)parser.parse(new FileReader(System.getProperty("user.dir") + "/src/utils/server.json"));
            JSONArray servers = (JSONArray)object.get("servers");
            servers.add(server);
            //todo -- save in file
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void removeServer(String name) {
        JSONParser parser = new JSONParser();
        try {
            JSONObject object = (JSONObject)parser.parse(new FileReader(System.getProperty("user.dir") + "/src/utils/server.json"));
            JSONArray servers = (JSONArray)object.get("servers");
            int indexToRemove = -1;
            for(int i = 0; i<servers.size(); i++) {
                if(((Server)servers.get(i)).getName().equals(name)){
                    indexToRemove = i;
                }
            }

            if(indexToRemove != -1) {
                servers.remove(indexToRemove);
            }

            //todo -- save in file
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private static String getServerHost(String name) {
        JSONParser parser = new JSONParser();
        try {
            JSONObject object = (JSONObject)parser.parse(new FileReader(System.getProperty("user.dir") + "/src/utils/server.json"));
            JSONArray servers = (JSONArray)object.get("servers");
            int index = -1;
            for(int i = 0; i<servers.size(); i++) {
                if(((JSONObject)servers.get(i)).get("name").equals(name)){
                    index = i;
                }
            }

            if(index != -1) {
                return (String)((JSONObject)servers.get(index)).get("host");
            }

            //todo -- save in file
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;

    }

    private static Object getServersWithEntities(JSONObject jsonServers){
        String server;
        String host;
        String jsonEntityList;
        Type typeEntityList;
        List<Entity> entities;

        for(int i = 0; i < ((List) jsonServers.get("servers")).size(); i++){
            server = (String) ((JSONObject) ((List) jsonServers.get("servers")).get(i)).get("name");
            host = getServerHost(server);

            if(host == null) {
                throw new RuntimeException("Unreachable host");
            }

            // get entities
            jsonEntityList = Main.request(host + "/api/entity", "");

            //Parse the attributes json
            typeEntityList = new TypeToken<List<Map<String,String>>>() {}.getType();
            entities = Main.gson.fromJson(jsonEntityList, typeEntityList);

            ((JSONObject) ((List) jsonServers.get("servers")).get(i)).put("entities", entities);
        }

        return jsonServers.get("servers");
    }

    public static void main(String[] args) {

        // Configure Spark
        port(3000);
        staticFiles.externalLocation("src/resources");


        // Configure freemarker engine
        FreemarkerEngine engine = new FreemarkerEngine("src/templates");

        //     DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");


        get("/", (request, response) -> {
                Map<String, Object> model = new HashMap<>();
                JSONObject jsonServers = getServers();
                try{
                    Object jsonServerWithEntities = getServersWithEntities(jsonServers);

                    model.put("servers", jsonServerWithEntities);

                    return engine.render(model, "server/index.ftl");

                }catch(RuntimeException e){
                    response.status(404);
                    return gson.toJson(e.getMessage());
                }
        });

        get("/server/:server", (request, response) -> {
            JSONObject jsonServers = getServers();
            String server = request.params("server");

            try{
                Object jsonServerWithEntities = getServersWithEntities(jsonServers);

                //todo receive server
                String host = getServerHost(server);
                if(host == null) {
                    //todo - redirect to not found
                    response.status(404);
                    return gson.toJson("Not found");
                }

                //Get the server entities
                String jsonEntityList = Main.request(host + "/api/entity", "");

                //Parse the attributes json
                Type typeEntityList = new TypeToken<List<Entity>>() {}.getType();
                List<Entity> entities = Main.gson.fromJson(jsonEntityList, typeEntityList);

                Map<String, Object> model = new HashMap<>();
                model.put("servers", jsonServerWithEntities);
                model.put("host", host);
                model.put("server", server);
                model.put("entities", entities);

                return engine.render(model, "server/server.ftl");

            }catch(RuntimeException e){
                response.status(404);
                return gson.toJson(e.getMessage());
            }

        });

        get("/server/add", (request, response) -> {
            JSONObject server = getServers();
            return engine.render(server, "server/add.ftl");
        });

        post("/server/add", (request, response) -> {
            Type type = new TypeToken<Map<String, String>>(){}.getType();
            Map<String, String> map = gson.fromJson(request.body(), type);
            String serverName = map.get("name");
            String serverHost = map.get("host");
            //Todo - validate if the server doesn't already exists
            Server server = new Server(serverName, serverHost);
            Main.addServer(server);
            response.status(200);
            return gson.toJson("success");
        });

        get("/server/remove", (request, response) -> {
            JSONObject model = getServers();
            return engine.render(model, "server/remove.ftl");
        });

        post("/server/remove", (request, response) -> {
            Type type = new TypeToken<Map<String, String>>(){}.getType();
            Map<String, String> map = gson.fromJson(request.body(), type);
            String serverName = map.get("name");
            Main.removeServer(serverName);
            response.status(200);
            return gson.toJson("success");
        });

        get("/server/:server/entity/:entity", (request, response) -> {
            JSONObject servers = getServers();
            //Get the params from the request
            String server = request.params("server");
            String entity = request.params("entity");

            //todo receive server
            String host = getServerHost(server);
            if(host == null) {
                //todo - redirect to not found
                response.status(404);
                return gson.toJson("Not found");
            }

            //Get the attributes of the entity
            String jsonAttributeList = Main.request(host + "/api/entity/" + entity +
                    "/attributes" , "");

            //Get the instances of the entity
            String jsonInstanceList = Main.request(host + "/api/entity/" + entity +
                    "/instance" , "");

            //Parse the attributes json
            Type typeAttributesList = new TypeToken<List<Map<String, String>>>() {}.getType();
            List<Map<String, String>> attributes = Main.gson.fromJson(jsonAttributeList, typeAttributesList);

            //Parse the the instances json
            Type typeInstanceList = new TypeToken<List<Map<String, String>>>() {}.getType();
            List<Map<String, String>> instances = gson.fromJson(jsonInstanceList, typeInstanceList);

            Map<String, Object> model = new HashMap<>();
            model.put("host", host);
            model.put("servers", servers.get("servers"));
            model.put("attributes", attributes);
            model.put("instances", instances);
//            return instances;
            return engine.render(model, "entity/entity.ftl");
        });

        //Instance information and edit page
        get("/server/:server/entity/:entity/instance/:instance", (request, response) -> {
            JSONObject servers = getServers();
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
            String jsonAttributeList = Main.request(host + "/api/entity/" + entity +
                    "/attributes" , "");

            //Get the instances of the entity
            String jsonInstance = Main.request(host + "/api/entity/" + entity +
                    "/instance/" + instance , "");

            //Parse the attributes json
            Type typeAttributesList = new TypeToken<List<Map<String, String>>>() {}.getType();
            List<Map<String, String>> attributes = Main.gson.fromJson(jsonAttributeList, typeAttributesList);

            //Parse the the instances json
            Type typeInstance = new TypeToken<Map<String, String>>() {}.getType();
            Map<String, String> instanceMap = gson.fromJson(jsonInstance, typeInstance);

            Map<String, Object> model = new HashMap<>();
            model.put("servers", servers.get("servers"));
            model.put("attributes", attributes);
            model.put("instance", instanceMap);

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
            String jsonAttributeList = Main.request(host + "/api/entity/" + entity +
                    "/attributes" , "");

            //Parse the attributes json
            Type typeAttributesList = new TypeToken<List<Attribute>>() {}.getType();
            List<Attribute> attributes = Main.gson.fromJson(jsonAttributeList, typeAttributesList);

            Map<String, Object> model = new HashMap<>();
            model.put("attributes", attributes);

            return engine.render(model, "instance/add.ftl");
        });

    }
}