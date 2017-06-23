/*
 * Simple Spark web application
 *
 */

package main;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
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
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;


import static spark.Spark.*;


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

    private static boolean addServer(Server server) {
        JSONParser parser = new JSONParser();
        try {
            String pathFile = System.getProperty("user.dir") + "/src/utils/server.json";
            JSONObject object = (JSONObject)parser.parse(new FileReader(pathFile));
            JSONArray servers = (JSONArray)object.get("servers");

            if(!Main.serverExists(server.getHost(), server.getName(), servers)) {
                servers.add(server);
                // save changes on file
                FileWriter writer = new FileWriter(pathFile);
                writer.write((object.toJSONString()).replace("\\/", "/"));
                writer.flush();
                writer.close();

                return true;
            }
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static void removeServer(String name) {
        JSONParser parser = new JSONParser();
        try {
            JSONObject object = (JSONObject)parser.parse(new FileReader(System.getProperty("user.dir") + "/src/utils/server.json"));
            JSONArray servers = (JSONArray)object.get("servers");
            int indexToRemove = -1;
            for(int i = 0; i<servers.size(); i++) {
                if(((Server)servers.get(i)).getName().toLowerCase().equals(name.toLowerCase())){
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
                if(((JSONObject)servers.get(i)).get("name").toString().toLowerCase().equals(name.toLowerCase())){
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

    private static boolean serverExists(String host, String name, List servers){
        String hostEscaped;
        String serverName;

        for(int i = 0; i < servers.size(); i++) {
            hostEscaped = ((String)((JSONObject) servers.get(i)).get("host")).replace("\\/", "/");
            serverName = ((String) ((JSONObject) servers.get(i)).get("name"));

            if(hostEscaped.equals(host) || serverName.equals(name)){

                return true;
            }
        }
        return false;
    }

    private static Object getServersWithEntities(JSONObject jsonServers){
        String server;
        String host;
        String jsonEntityList;
        Type typeEntityList;
        List<Entity> entities;
        List servers = (List) jsonServers.get("servers");

        for(int i = 0; i < ((List) jsonServers.get("servers")).size(); i++){
            server = (String) ((JSONObject) servers.get(i)).get("name");
            host = getServerHost(server);

            if(host == null) {
                throw new RuntimeException("Unreachable host");
            }

            // get entities
            jsonEntityList = Main.request(host + "/api/entity", "");

            //Parse the attributes json
            typeEntityList = new TypeToken<List<Map<String,String>>>() {}.getType();
            entities = Main.gson.fromJson(jsonEntityList, typeEntityList);

            ((JSONObject) servers.get(i)).put("entities", entities);
        }

        return jsonServers.get("servers");
    }

    private static boolean isValidLogin(String email, String password){
        JSONObject jsonServers = Main.getServers();
        JSONArray users = (JSONArray) jsonServers.get("usersAllowed");
        JSONObject userObject;
        String passwordHash = Main.stringToHash(password, "SHA-256");

        for (Object user : users) {
            userObject = ((JSONObject) user);
            if (userObject.get("email").equals(email) && userObject.get("password").equals(passwordHash)) {
                // user found
                return true;
            }
        }
        // not user found with email and password received
        return false;
    }

    private static boolean addUser(User user){
        String pathFile = System.getProperty("user.dir") + "/src/utils/server.json";
        try {
            JSONObject jsonServers = getServers();
            List arrayUsers;

            String passwordHash = stringToHash(user.getPassword(), "SHA-256");

            // create object with user
            JSONObject newUser = new JSONObject();
            newUser.put("name", user.getFullName());
            newUser.put("email", user.getEmail());
            newUser.put("password", passwordHash);

            arrayUsers = (JSONArray) jsonServers.get("usersAllowed");

            if(arrayUsers == null){
                // not user registered
                List<JSONObject> listUsers = new ArrayList<>();
                listUsers.add(newUser);
                jsonServers.put("usersAllowed", listUsers);
            }else{
                for(int i = 0; i < arrayUsers.size(); i++){
                    JSONObject userOnFile = (JSONObject) arrayUsers.get(i);
                    if(userOnFile.get("email").equals(user.getEmail())){
                        // user already exists with that email
                        return false;
                    }
                }
                // server already have users registered
                List listUsers = arrayUsers;
                listUsers.add(newUser);
            }

            // save changes on file
            FileWriter writer = new FileWriter(pathFile);
            writer.write((jsonServers.toJSONString()).replace("\\/", "/"));
            writer.flush();
            writer.close();

            return true;

        } catch (IOException e) {
            System.out.println(e);
            e.printStackTrace();
        }
        return false;
    }

    private static String stringToHash(String str, String hashFunction){
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance(hashFunction);
            byte[] digested = digest.digest(str.getBytes(StandardCharsets.UTF_8));
            String hash  = String.format("%064x", new java.math.BigInteger(1, digested));

            return hash;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {


        // Configure Spark
        port(3000);
        staticFiles.externalLocation("src/resources");


        // Configure freemarker engine
        FreemarkerEngine engine = new FreemarkerEngine("src/templates");

        //     DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");


        before((request,response)->{
            String method = request.requestMethod();
            if(!(request.uri().equals("/authenticate") || request.uri().equals("/register"))){
                if(method.equals("GET")){
                    String emailSession = request.session().attribute("email");
                    if(emailSession == null){
//                        halt(401, "User Unauthorized");
                        //return page of authorization?
                        response.redirect("/authenticate");
                    }
                }
            }
        });

        get("/authenticate", (request, response) -> {
            Map<String, Object> model = new HashMap<>();
            try {
                JSONObject jsonServers = getServers();
                Object jsonServerWithEntities = getServersWithEntities(jsonServers);

                model.put("servers", jsonServerWithEntities);

                return engine.render(model, "authenticate.ftl");
            }catch(RuntimeException e){
                System.out.println(e);
                response.status(500);
                return engine.render(null, "500.ftl");
            }

        });

        post("/authenticate", (request, response) ->{
            String email = request.queryParams("email");
            String password = request.queryParams("password");

            boolean result = Main.isValidLogin(email, password);
            if(result){
                //create a session with email
                request.session(true);
                request.session().attribute("email", email);

                Map<String, Object> model = new HashMap<>();
                try {
                    JSONObject jsonServers = getServers();
                    Object jsonServerWithEntities = getServersWithEntities(jsonServers);
                    model.put("servers", jsonServerWithEntities);

                    response.redirect("/");
                    return "success";
                }catch(RuntimeException e){
                    System.out.println(e);
                    response.status(500);
                    return engine.render(null, "500.ftl");
                }
            }else{
                response.redirect("/authenticate");
                return "";
            }
        });

        get("/register", (request, response) -> {
            Map<String, Object> model = new HashMap<>();
            try {
                JSONObject jsonServers = getServers();
                Object jsonServerWithEntities = getServersWithEntities(jsonServers);

                model.put("servers", jsonServerWithEntities);

                return engine.render(model, "register.ftl");
            }catch(RuntimeException e){
                System.out.println(e);
                response.status(500);
                return engine.render(null, "500.ftl");
            }

        });

        post ("/register", (request, response) -> {
            Map<String, Object> model = new HashMap<>();
            try{
                String name = request.queryParams("name");
                String email = request.queryParams("email");
                String password = request.queryParams("password");
                String checkPassword = request.queryParams("checkPassword");

                if(password.equals(checkPassword)){
                    User user = new User(name, email, password);

                    boolean result = Main.addUser(user);
                    if(result){
                        //create a session
                        request.session(true);
                        request.session().attribute("email", email);

                        JSONObject jsonServers = getServers();
                        Object jsonServerWithEntities = getServersWithEntities(jsonServers);
                        model.put("servers", jsonServerWithEntities);

                        response.redirect("/");
                        return "success";

                    }else{
                        response.status(400);
                        return engine.render(null, "400.ftl");
                    }
                }else{
                    response.redirect("/register");
                    return "unsuccessful";
                }
            }catch(Exception e){
                System.out.println(e);
                response.status(500);
                return engine.render(model, "500.ftl");
            }
        });

        get("/logout", (request, response) -> {
            // clear session
            request.session().removeAttribute("email");
            response.redirect("/authenticate");
            return "";
        });


        get("/", (request, response) -> {
                Map<String, Object> model = new HashMap<>();
                try{
                    JSONObject jsonServers = getServers();
                    Object jsonServerWithEntities = getServersWithEntities(jsonServers);

                    model.put("servers", jsonServerWithEntities);

                    return engine.render(model, "server/index.ftl");

                    //return model;

                }catch(RuntimeException e){
                    System.out.println(e);
                    response.status(500);
                    return engine.render(null, "500.ftl");
                }
        });


        get("/server/add", (request, response) -> {
            Map<String, Object> model = new HashMap<>();
            try{
                JSONObject jsonServers = getServers();
                Object jsonServerWithEntities = getServersWithEntities(jsonServers);

                model.put("servers", jsonServerWithEntities);

                return engine.render(model, "server/add.ftl");

            }catch(RuntimeException e){
                System.out.println(e);
                response.status(500);
                return engine.render(null, "500.ftl");
            }
        });

        post("/server/add", (request, response) -> {
            Type type = new TypeToken<Map<String, String>>(){}.getType();
            Map<String, String> map = gson.fromJson(request.body(), type);
            String serverName = map.get("name");
            String serverHost = map.get("host");
            String description = map.get("description");

            Server server = new Server(serverHost, serverName, description);
            boolean result = Main.addServer(server);
            if(result){
                response.redirect("/");
                return "success";
            }else{
                response.status(400);
                return engine.render(null,"400.ftl");
            }
        });

        get("/server/remove", (request, response) -> {
            Map<String, Object> model = new HashMap<>();
            try{
                JSONObject jsonServers = getServers();
                Object jsonServerWithEntities = getServersWithEntities(jsonServers);

                model.put("servers", jsonServerWithEntities);

                return engine.render(model, "server/remove.ftl");

            }catch(RuntimeException e){
                System.out.println(e);
                response.status(500);
                return engine.render(null, "500.ftl");
            }

        });

        get("/server/:server", (request, response) -> {
            JSONObject jsonServers = getServers();
            String server = request.params("server");
            Map<String, Object> model = new HashMap<>();

            try{
                Object jsonServerWithEntities = getServersWithEntities(jsonServers);

                String host = getServerHost(server);
                if(host == null) {
                    response.status(400);
                    return engine.render(null, "404.ftl");
                }

                //Get the server entities
                String jsonEntityList = Main.request(host + "/api/entity", "");

                //Parse the attributes json
                Type typeEntityList = new TypeToken<List<Entity>>() {}.getType();
                List<Entity> entities = Main.gson.fromJson(jsonEntityList, typeEntityList);


                model.put("servers", jsonServerWithEntities);
                model.put("host", host);
                model.put("server", server);
                model.put("entities", entities);

                return engine.render(model, "server/server.ftl");

            }catch(RuntimeException e){
                System.out.println(e);
                response.status(500);
                return engine.render(null, "500.ftl");
            }

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
            Map<String, Object> model = new HashMap<>();
            try {
                JSONObject jsonServers = getServers();
                Object jsonServerWithEntities = getServersWithEntities(jsonServers);

                //Get the params from the request
                String server = request.params("server");
                String entity = request.params("entity");

                String host = getServerHost(server);
                if(host == null) {
                    response.status(404);
                    return engine.render(null, "404.ftl");
                }

                //Get the attributes of the entity
                String jsonAttributeList = Main.request(host + "/api/entity/" + entity +
                        "/attributes" , "");

                //Get the instances of the entity
                String jsonInstanceList = Main.request(host + "/api/entity/" + entity +
                        "/instance" , "");

                //Parse the attributes json
                Type typeAttributesList = new TypeToken<List<Map<String, Object>>>() {}.getType();
                List<Map<String, Object>> attributes = Main.gson.fromJson(jsonAttributeList, typeAttributesList);

                //Parse the the instances json
                Type typeInstanceList = new TypeToken<List<Map<String, Object>>>() {}.getType();
                List<Map<String, Object>> instances = gson.fromJson(jsonInstanceList, typeInstanceList);

                model.put("server", server);
                model.put("host", host);
                model.put("servers", jsonServerWithEntities);
                model.put("attributes", attributes);
                model.put("instances", instances);
                model.put("entity", entity);
//            return instances;
                return engine.render(model, "entity/entity.ftl");

            }catch(RuntimeException e){
                System.out.println(e);
                response.status(500);
                return engine.render(null, "500.ftl");
            }
        });

        //Instance adding page
        get("/server/:server/entity/:entity/instance/add", (request, response) -> {
            Map<String, Object> model = new HashMap<>();

            try {
                JSONObject jsonServers = getServers();
                Object jsonServerWithEntities = getServersWithEntities(jsonServers);

                //Get the params from the request
                String server = request.params("server");
                String entity = request.params("entity");

                String host = getServerHost(server);
                if(host == null) {
                    response.status(404);
                    return engine.render(null, "404.ftl");
                }

                //Get the attributes of the entity
                String jsonAttributeList = Main.request(host + "/api/entity/" + entity +
                        "/attributes" , "");

                //Parse the attributes json
                Type typeAttributesList = new TypeToken<List<Map<String, String>>>() {}.getType();
                List<Map<String, String>> attributes = Main.gson.fromJson(jsonAttributeList, typeAttributesList);

                model.put("server", server);
                model.put("host", host);
                model.put("entity", entity);
                model.put("attributes", attributes);
                model.put("servers", jsonServerWithEntities);

                return engine.render(model, "instance/add.ftl");

            }catch(RuntimeException e){
                System.out.println(e);
                response.status(500);
                return engine.render(null, "500.ftl");
            }
        });
        //Instance information and edit page
        get("/server/:server/entity/:entity/instance/:instance", (request, response) -> {
            Map<String, Object> model = new HashMap<>();
            try {
                JSONObject jsonServers = getServers();
                Object jsonServerWithEntities = getServersWithEntities(jsonServers);

                //Get the params from the request
                String server = request.params("server");
                String entity = request.params("entity");
                String instance = request.params("instance");

                String host = getServerHost(server);
                if(host == null) {
                    response.status(404);
                    return engine.render(null, "404.ftl");
                }

                //Get the attributes of the entity
                String jsonAttributeList = Main.request(host + "/api/entity/" + entity +
                        "/attributes" , "");

                //Get the instances of the entity
                String jsonInstance = Main.request(host + "/api/entity/" + entity +
                        "/instance/" + instance , "");

                //Parse the attributes json
                Type typeAttributesList = new TypeToken<List<Map<String, Object>>>() {}.getType();
                List<Map<String, Object>> attributes = Main.gson.fromJson(jsonAttributeList, typeAttributesList);

                //Parse the the instances json
                Type typeInstance = new TypeToken<Map<String, Object>>() {}.getType();
                Map<String, Object> instanceMap = gson.fromJson(jsonInstance, typeInstance);

                model.put("host", host);
                model.put("servers", jsonServerWithEntities);
                model.put("server", server);
                model.put("entity", entity);
                model.put("attributes", attributes);
                model.put("instance", instanceMap);

                return engine.render(model, "instance/instance.ftl");

            }catch(RuntimeException e){
                System.out.println(e);
                response.status(500);
                return engine.render(null, "500.ftl");
            }
        });


        get("*", (request, response) ->{
            try{
                return engine.render(null, "404.ftl");

            }catch(RuntimeException e){
                System.out.println(e);
                response.status(500);
                return engine.render(null, "500.ftl");
            }
        });
    }
}