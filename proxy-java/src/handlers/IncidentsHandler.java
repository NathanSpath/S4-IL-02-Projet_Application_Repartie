package handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import services.OpenData;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;

public class IncidentsHandler implements HttpHandler {

    private OpenData dataClient = new OpenData();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");

        String responseBody = "";
        int httpStatus = 200;

        try {
            responseBody = dataClient.getIncidents();

        } catch (Exception e) {
            httpStatus = 500;
            JSONObject erreur = new JSONObject();
            erreur.put("erreur", "Problème de récupération : " + e.getMessage());
            responseBody = erreur.toString();
        }

        byte[] bytes = responseBody.getBytes("UTF-8");
        exchange.sendResponseHeaders(httpStatus, bytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(bytes);
        os.close();
    }
}