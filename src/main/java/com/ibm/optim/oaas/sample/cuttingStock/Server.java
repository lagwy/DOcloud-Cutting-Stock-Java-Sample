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
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.ibm.optim.oaas.sample.cuttingStock.Server.Row;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import com.ibm.optim.oaas.sample.cuttingStock.model.MasterData;
import com.ibm.optim.oaas.sample.cuttingStock.model.MasterResult;
import com.ibm.optim.oaas.sample.cuttingStock.model.SubproblemData;
import com.ibm.optim.oaas.sample.cuttingStock.model.Pattern;
import com.ibm.optim.oaas.sample.cuttingStock.model.Item;
import com.ibm.optim.oaas.sample.cuttingStock.model.Slice;
import com.ibm.optim.oaas.sample.cuttingStock.model.Parameters;
import com.ibm.optim.oaas.sample.cuttingStock.model.Output;
import com.ibm.optim.oaas.sample.cuttingStock.model.Usage;

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

            List<Integer> possibleCalculate = new ArrayList<Integer>();
            List<Row> desiredCalculate = new ArrayList<Row>();

            MasterData result = new MasterData("Cutting Stock Data Set from Endpoint");
            SubproblemData subproblemData = new SubproblemData("Cutting Stock Data Set from Endpoint");

            try {
                JsonNode actualObj = mapper.readTree(body);

                List<Integer> possible = new ArrayList<Integer>();
                List<Row> desired = new ArrayList<Row>();


                // Generate MasterData object
                result.patterns = new Pattern.List();
                result.items = new Item.List();
                result.slices = new Slice.List();
                subproblemData.parameters = new Parameters( actualObj.get("PossibleLengths").get(0).asInt() , actualObj.get("DesiredLengths").size() );
                subproblemData.items = new Item.List();

                for ( int i = 0; i < actualObj.get("PossibleLengths").size(); i++ ){
                    possible.add( actualObj.get("PossibleLengths").get(i).asInt() );
                }

                Row row;
                for ( int i = 0; i < actualObj.get("DesiredLengths").size(); i++ ){
                    row = new Row( actualObj.get("DesiredLengths").get(i).get("Length").asInt(), actualObj.get("DesiredLengths").get(i).get("Quantity").asInt() );
                    result.patterns.add( new Pattern( i, 1 ) );
                    result.items.add( new Item( "" + row.length, row.length, row.quantity ) );
                    result.slices.add( new Slice( "" + row.length, i, 1 ) );

                    subproblemData.items.add(new Item("" + row.length, row.length, row.quantity));
                    System.out.println( row.length );
                    desired.add( row );
                }

                possibleCalculate = possible;
                desiredCalculate = desired;

            } catch (Exception e){
                System.out.println( "Could not map requirement from JSON: " + e.getMessage() );
            }


            System.out.println( result.items.toString() );
            // Create the controller
            ColumnGeneration ctrl = new ColumnGeneration(baseURL,
            apiKeyClientId,
            "CuttingStock",
            "opl/cuttingStock.mod",
            "opl/cuttingStock-sub.mod");

            System.out.println( "Found " + desiredCalculate.size() + " desired lengths in the request." );
    
            ctrl.optimize( result , subproblemData );

            MasterResult masterResult = ctrl.getMasterResult();

            // ObjectMapper objectMapper = new ObjectMapper();
            try{
                ArrayList<Output> outputs = new ArrayList<>();
                Map<Integer, String> patternSlices = result.getSlices().patternSlicesToStrings();

                for(Usage u: masterResult.getUse()) {
                    if(u.getNumber() > 0.0) {
                        Output output = new Output(u.getPattern(),u.getNumber(),patternSlices.get(u.getPattern()));
                        outputs.add(output);
                    }
                }

                String arrayToJson = mapper.writeValueAsString( outputs) ;
                OutputStream os = t.getResponseBody();
                System.out.println("1. Convert List of person objects to JSON :");
                t.sendResponseHeaders(200, arrayToJson.length());
                t.getResponseHeaders().set("Content-Type", "application/json");

                System.out.println( arrayToJson );
                os.write( arrayToJson.getBytes() );
                os.close();
            }catch(JsonProcessingException e){
                System.out.println( "JsonProcessingException: " + e.getMessage() );
            }

            // OutputStream os = t.getResponseBody();;
            // os.write( body.getBytes() );
            // os.close();
        }

    }

    public static class Requirement {
        @JsonProperty("DesiredLengths")
        public static List<Row> desired;

        @JsonProperty("PossibleLengths")
        public static List<Integer> possible;

        public Requirement(){

        }

        public Requirement(List<Row> rows, List<Integer> possible){
            this.desired = rows;
            this.possible = possible;
        }

        public static List<Row> getDesired(){
            return desired;
        }

        public static void setDesired(List<Row> rows){
            desired = rows;
        }

        public static List<Integer> getPossible(){
            return possible;
        }

        public static void setPossible(List<Integer> rows){
            possible = rows;
        }

    }

    public static class Row{
        @JsonProperty("Length")
        public static int length;

        @JsonProperty("Quantity")
        public static int quantity;

        public Row(){
            
        }

        public Row(int length, int quantity){
            this.length = length;
            this.quantity = quantity;
        }

    }

}