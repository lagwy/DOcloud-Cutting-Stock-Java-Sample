package com.ibm.optim.oaas.sample.cuttingStock;


import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.*;
import java.io.InputStream;
import java.lang.Throwable;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class Server {

    public static void main(String[] args) throws Exception {
        int port = 8000;

        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/test", new MyHandler());
        server.createContext("/solution", new SolutionHandler());
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

    static class SolutionHandler implements HttpHandler {
        
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

        @Override
        public void handle(HttpExchange t) throws IOException {
            Map<String, String> params = queryToMap(t.getRequestURI().getQuery()); 

            InputStream is = t.getRequestBody();
            String body = convertStreamToString(is);

            while (is.read() != -1);
            is.close();

            t.sendResponseHeaders(200, body.length());
            t.getResponseHeaders().set("Content-Type", "application/json");


            OutputStream os = t.getResponseBody();;
            os.write( body.getBytes() );
            os.close();
        }
    }

}