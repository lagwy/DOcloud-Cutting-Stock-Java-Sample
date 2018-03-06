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

    public static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    static class MyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            InputStream is = t.getRequestBody();
            String body = convertStreamToString(is);

            while (is.read() != -1);
            is.close();

            System.out.println( baseURL );
            System.out.println( apiKeyClientId );

            t.sendResponseHeaders(200, body.length());
            t.getResponseHeaders().set("Content-Type", "application/json");


            OutputStream os = t.getResponseBody();;
            os.write( body.getBytes() );
            os.close();

        }
    }

    static class SolHandler implements HttpHandler {
        
        @Override
        public void handle(HttpExchange t) throws IOException {
            InputStream is = t.getRequestBody();
            String body = convertStreamToString(is);

            while (is.read() != -1);
            is.close();

            try {
                Requirement actualObj = mapper.readValue(body, Requirement.class);
            } catch (Exception e){
                System.out.println( "Could not map requirement from JSON." );
            }
            

            System.out.println( baseURL );
            System.out.println( apiKeyClientId );

            // Create the controller
            ColumnGeneration ctrl = new ColumnGeneration(baseURL,
            apiKeyClientId,
            "CuttingStock",
            "opl/cuttingStock.mod",
            "opl/cuttingStock-sub.mod");

            // Optimize the model
            // ctrl.optimize(MasterData.default1(), SubproblemData.default1());

            t.sendResponseHeaders(200, body.length());
            t.getResponseHeaders().set("Content-Type", "application/json");


            OutputStream os = t.getResponseBody();;
            os.write( body.getBytes() );
            os.close();
        }

    }

    public static class Requirement {
        @JsonProperty("DesiredLengths")
        public List<Row> desired;

        @JsonProperty("PossibleLengths")
        public List<Integer> possible;

    }

    public static class Row{
        @JsonProperty("Length")
        public int length;

        @JsonProperty("Quantity")
        public int quantity;

        public Row(){
            
        }

        public Row(int length, int quantity){
            this.length = length;
            this.quantity = quantity;
        }

    }

}