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

        get("/server/:server/entity/:entity", (request, response) -> {
            //Get the params from the request
            String server = request.params("server");
            String entity = request.params("entity");

            String host = getServerHost(server);
            if(host == null) {
                //todo - redirect to not found
            }

            //Get the instances of the entity
            String jsonInstanceList = Main.request("http://localhost/api/model/" + modelName +
                    "/entity/" + entityId + "/list", "");

            //Parse the attributes json
            Type typeAttributesList = new TypeToken<List<Attribute>>() {
            }.getType();
            List<Attribute> attributes = Main.gson.fromJson(request.body(), typeAttributesList);

            //Parse the the instances json
            Type typeInstanceList = new TypeToken<List<Map<String, String>>>() {
            }.getType();
            List<Map<String, String>> instances = gson.fromJson(request.body(), typeAttributesList);

            Map<String, Object> model = new HashMap<>();
            model.put("attributes", attributes);
            model.put("instances", instances);

            return engine.render(model, "list.html");
        });

        get("/server/:server/entity/:entity/instance/:instance", (request, response) -> {

        });

        get("/server/:server/entity/:entity/instance/:instance/add", (request, response) -> {

        });

        get("/server/:server/entity/:entity/instance/:instance/add", (request, response) -> {

        });

        get("/server/:server/entity/:entity/instance/:instance/edit", (request, response) -> {

        });


        /**
         * Show the list of instances of certain entity of a model
         * Expects a json
         * [
         *
         *
         * ]
         */
//        get("server/:server/model/:model/entity/:entity", (request, response) -> {
//            //Get the params from the requestn
//            String modelName = request.params("id");
//            String entityId = request.params("entity");
//            String serverId = request.params("server");
//
//            //Get the attributes of the entity
//            String jsonAttributeList = Main.request("http://localhost/api/model/" + modelName +
//                    "/entity/" + entityId + "/attributes" , "");
//
//            //Get the instances of the entity
//            String jsonInstanceList = Main.request("http://localhost/api/model/" + modelName +
//                    "/entity/" + entityId + "/list" , "");
//
//            //Parse the attributes json
//            Type typeAttributesList = new TypeToken<List<Attribute>>(){}.getType();
//            List<Attribute> attributes = Main.gson.fromJson(request.body(), typeAttributesList);
//
//            //Parse the the instances json
//            Type typeInstanceList = new TypeToken<List<Map<String, String>>>(){}.getType();
//            List<Map<String, String>> instances = gson.fromJson(request.body(), typeInstanceList);
//
//            Map<String, Object> model = new HashMap<>();
//            model.put("attributes", attributes);
//            model.put("instances", instances);
//
//            return engine.render(model, "list.html");
//        });
//
//        //todo - list all the entities available in the model
//        get("/model/:model", (request, response) -> {
//
//        });
//
//        // Set up endpoints
//        get("/", (request, response) -> {
////            Map<String, Object> map = new HashMap<>();
////            map.put("authorRows", Author.all().size());
////            map.put("bookRows", Book.all().size());
////            map.put("pageRows", Page.all().size());
////            map.put("personRows", Person.all().size());
////            map.put("entityRows", Entity.all().size());
////            return engine.render(map, "index.html");
//            return null;
//        });
//
//        get("/author/list", (request, response) -> {
////            Map<String, Object> map = new HashMap<>();
////            map.put("authors", Author.all());
////            map.put("name", "author");
////            return engine.render(map, "list.html");
//            return null;
//        });
//
//        get("/author/get", (request, response) -> {
////            int id = Integer.parseInt(request.queryParams("id"));
////            Author author = Author.get(id);
////            Map<String, Object> map = new HashMap<>();
////            map.put("author", author);
////            map.put("name", "author");
////            return engine.render(map, "get.html");
//            return null;
//        });
//
//        post("/author/update", (request, response) -> {
////
////            int id = Integer.parseInt(request.queryParams("id"));
////            boolean tangible = Boolean.parseBoolean(request.queryParams("tangible"));
////            String firstName = request.queryParams("firstName");
////            String lastName = request.queryParams("lastName");
////            String email = request.queryParams("email");
////
////            Author p = Author.get(id);
////            p.setEmail(email);
////            p.save();
////
////            response.redirect("/author/get?id=" + id);
////            return "";
//            return null;
//        });
//
//        get("/author/delete", (request, response) -> {
////            int id = Integer.parseInt(request.queryParams("id"));
////            Author p = Author.get(id);
////            p.delete();
////
////            response.redirect("/author/list");
////            return "";
//            return null;
//        });

    }
}