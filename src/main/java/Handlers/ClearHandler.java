package Handlers;

import Request.LoginRequest;
import Result.ClearResult;
import Result.LoginResult;
import Service.Clear;
import Service.Login;
import com.google.gson.Gson;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.net.HttpURLConnection;

public class ClearHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        try {

            if (exchange.getRequestMethod().toUpperCase().equals("POST")) {

                // Get the HTTP request headers
                Headers reqHeaders = exchange.getRequestHeaders();

                Clear clear = new Clear();
                ClearResult clearResult = clear.clearService();
                if (!clearResult.isSuccess()) {
                    exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
                } else {
                    exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                }
                OutputStream responseBody = exchange.getResponseBody();
                String json = (new Gson()).toJson(clearResult);
                writeString(json, responseBody);
                System.out.println(exchange.getResponseHeaders());
                responseBody.close();


            } else {
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
                //write to the response body
            }


            exchange.getResponseBody().close();
        } catch (IOException e) {

            exchange.sendResponseHeaders(HttpURLConnection.HTTP_INTERNAL_ERROR, 0);


            exchange.getResponseBody().close();

            e.printStackTrace();
        }

    }

    private void writeString(String str, OutputStream os) throws IOException {
        OutputStreamWriter sw = new OutputStreamWriter(os);
        BufferedWriter bw = new BufferedWriter(sw);
        bw.write(str);
        bw.flush();
    }
}
