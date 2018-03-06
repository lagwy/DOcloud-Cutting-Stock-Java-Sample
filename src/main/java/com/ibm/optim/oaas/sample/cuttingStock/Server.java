package com.ibm.optim.oaas.sample.cuttingStock;


import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.*;
import java.io.InputStream;
import java.lang.Throwable;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.optim.oaas.sample.cuttingStock.Server.Row;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import com.ibm.optim.oaas.sample.cuttingStock.model.MasterData;
import com.ibm.optim.oaas.sample.cuttingStock.model.SubproblemData;

public class Server {

    // Mapper for Java --> JSON serialization
    protected static ObjectMapper mapper = new ObjectMapper();
    private static String baseURL = "";
    private static String apiKeyClientId = "";

    public static void main(String[] args) throws Exception {
        baseURL = args[0];
		apiKeyClientId = args[1];
        int port = 80;

        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/test", new MyHandler());
        server.createContext("/solution", new SolHandler());
        server.setExecutor(null); // creates a default executor

        System.out.println( "Server running on port " + port );
        server.start();
    }

    static class MyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            String response = "Test response";
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    static class SolHandler implements HttpHandler {
        
        @Override
        public void handle(HttpExchange t) throws IOException {
            Map<String, String> params = queryToMap(t.getRequestURI().getQuery()); 

            InputStream is = t.getRequestBody();
            String body = convertStreamToString(is);

            while (is.read() != -1);
            is.close();

            Requirement actualObj = mapper.readValue(body, Requirement.class);

            // System.out.println( baseURL );
            // System.out.println( apiKeyClientId );

            // Create the controller
            ColumnGeneration ctrl = new ColumnGeneration(baseURL,
            apiKeyClientId,
            "CuttingStock",
            "opl/cuttingStock.mod",
            "opl/cuttingStock-sub.mod");

            // Optimize the model
            ctrl.optimize(MasterData.default1(), SubproblemData.default1());

            t.sendResponseHeaders(200, body.length());
            t.getResponseHeaders().set("Content-Type", "application/json");


            OutputStream os = t.getResponseBody();;
            os.write( body.getBytes() );
            os.close();
        }


        public Map<String, String> queryToMap(String query){
            Map<String, String> result = new HashMap<String, String>();
            for (String param : query.split("&")) {
                String pair[] = param.split("=");
                if (pair.length>1) {
                    result.put(pair[0], pair[1]);
                }else{
                    result.put(pair[0], "");
                }
            }
            return result;
        }

        static String convertStreamToString(java.io.InputStream is) {
            java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
            return s.hasNext() ? s.next() : "";
        }

    }

    public static class Requirement {
        @JsonProperty("DesiredLengths")
        public List<Row> desired;

        @JsonProperty("PossibleLengths")
        public List<Integer> possible;

    }

    public static class Row{
        @JsonProperty("length")
        public int length;

        @JsonProperty("quantity")
        public int quantity;

        public Row(){
            
        }

        public Row(int length, int quantity){
            this.length = length;
            this.quantity = quantity;
        }

    }

}