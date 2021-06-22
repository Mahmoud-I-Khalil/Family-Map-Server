package Handlers;

import DataAccess.*;
import Model.AuthTokenMod;
import Request.EventRequest;
import Request.PersonRequest;
import Result.EventResult;
import Result.PersonResult;
import Service.Event;
import Service.Person;
import com.google.gson.Gson;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.sql.Connection;

public class PersonHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {

        try {



            // Determine the HTTP request type (GET, POST, etc.).
            // Only allow GET requests for this operation.
            // This operation requires a GET request, because the
            // client is "getting" information from the server, and
            // the operation is "read only" (i.e., does not modify the
            // state of the server).
            if (exchange.getRequestMethod().toUpperCase().equals("GET")) {

                // Get the HTTP request headers
                Headers reqHeaders = exchange.getRequestHeaders();

                // Check to see if an "Authorization" header is present
                if (reqHeaders.containsKey("Authorization")) {

                    // Extract the auth token from the "Authorization" header
                    String authToken = reqHeaders.getFirst("Authorization");

                    // Verify that the auth token is the one we're looking for
                    // (this is not realistic, because clients will use different
                    // auth tokens over time, not the same one all the time). A
                    // realistic example would do a database lookup to confirm that
                    // the auto token is valid and would retrieve the user data
                    // associated with the auth token.
                    Database db = new Database();
                    db.openConnection();
                    Connection conn = db.getConnection();
                    PersonDao personDao = new PersonDao(conn);
                    AuthTokenDoa authTokenDoa = new AuthTokenDoa(conn);
                    AuthTokenMod authTokenMod = authTokenDoa.find(authToken);
                    db.closeConnection(false);
                    if (authTokenMod != null) {


                        // Start sending the HTTP response to the client, starting with
                        // the status code and any defined headers.
                        exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);


                        PersonRequest personRequest = new PersonRequest(authTokenMod.getUserName());
                        Person person = new Person();
                        PersonResult personResult = person.personService(personRequest);

                        String json = (new Gson()).toJson(personResult);
                        // Now that the status code and headers have been sent to the client,
                        // next we send the JSON data in the HTTP response body.

                        // Get the response body output stream.
                        OutputStream respBody = exchange.getResponseBody();

                        // Write the JSON string to the output stream.
                        writeString(json, respBody);

                        // Close the output stream.  This is how Java knows we are done
                        // sending data and the response is complete
                        respBody.close();

                    } else {
                        // The auth token was invalid somehow, so we return a "not authorized"
                        // status code to the client.
                        exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
                        PersonResult personResult = new PersonResult(false,"Error: Authentication Failed");
                        String json = (new Gson()).toJson(personResult);
                        OutputStream respBody = exchange.getResponseBody();
                        writeString(json, respBody);
                        respBody.close();
                    }
                } else {
                    // We did not get an auth token, so we return a "not authorized"
                    // status code to the client.
                    exchange.sendResponseHeaders(HttpURLConnection.HTTP_UNAUTHORIZED, 0);

                }
            } else {
                // We expected a GET but got something else, so we return a "bad request"
                // status code to the client.
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
            }

            exchange.getRequestBody().close();


        } catch (IOException | DataAccessException e) {
            // Some kind of internal error has occurred inside the server (not the
            // client's fault), so we return an "internal server error" status code
            // to the client.
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_SERVER_ERROR, 0);
            // Since the server is unable to complete the request, the client will
            // not receive the list of games, so we close the response body output stream,
            // indicating that the response is complete.
            exchange.getResponseBody().close();

            // Display/log the stack trace
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
