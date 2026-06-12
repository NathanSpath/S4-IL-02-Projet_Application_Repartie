package handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONObject;
import services.OpenData;

import java.io.IOException;
import java.io.OutputStream;

public class ListeReservationHandler implements HttpHandler {

    private OpenData dataClient = new OpenData();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");

        String responseBody = "";
        int httpStatus = 200;

        try {
            responseBody = dataClient.getAllReservation();

        } catch (Exception e) {
            httpStatus = 500;
            JSONObject erreur = new JSONObject();
            erreur.put("erreur", "Problème de récupération des réservations : " + e.getMessage());
            responseBody = erreur.toString();
        }

        byte[] bytes = responseBody.getBytes("UTF-8");
        exchange.sendResponseHeaders(httpStatus, bytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(bytes);
        os.close();
    }
}