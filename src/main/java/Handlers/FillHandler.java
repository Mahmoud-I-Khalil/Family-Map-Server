package Handlers;

import DataAccess.DataAccessException;
import DataAccess.Database;
import DataAccess.UserDao;
import Model.UserMod;
import Request.FillRequest;
import Request.LoginRequest;
import Result.FillResult;
import Result.LoginResult;
import Service.Fill;
import Service.Login;
import com.google.gson.Gson;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.net.HttpURLConnection;
import java.sql.Connection;

public class FillHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        try {



            if (exchange.getRequestMethod().toUpperCase().equals("POST")) {

                // Get the HTTP request headers
                Database db = new Database();

                Connection conn = db.getConnection();
                UserDao uDao = new UserDao(conn);
                Headers reqHeaders = exchange.getRequestHeaders();
                String url = exchange.getRequestURI().toString();
                String[] parameters = url.split("/");
                String username = parameters[2];
                int generations = 4;
                FillResult fillResult = null;
                UserMod userIfFound = uDao.find(username);

                db.closeConnection(false);

                if(userIfFound != null) {

                    if (parameters.length == 4) {
                        generations = Integer.parseInt(parameters[3]);
                        if (generations < 0) {
                            throw new DataAccessException("Error: please enter a valid username");
                        }
                    }
                    FillRequest fillRequest = new FillRequest(username, generations);
                    Fill fill = new Fill();
                    fillResult = fill.fillService(fillRequest);
                    if (!fillResult.isSuccess()) {
                        exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
                    } else {
                        exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                    }
                }

                else{
                    exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST,0);
                    fillResult = new FillResult(false,"Username is not in database error");
                }

                OutputStream responseBody = exchange.getResponseBody();
                String json = (new Gson()).toJson(fillResult);
                writeString(json, responseBody);
                System.out.println(exchange.getResponseHeaders());
                responseBody.close();


            } else {
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
            }


            exchange.getResponseBody().close();
        } catch (IOException | DataAccessException e) {

            exchange.sendResponseHeaders(HttpURLConnection.HTTP_INTERNAL_ERROR, 0);


            exchange.getResponseBody().close();

            e.printStackTrace();
        }catch (Exception e){
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