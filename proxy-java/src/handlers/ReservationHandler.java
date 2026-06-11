package handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONObject;
import services.OpenData;

import java.io.IOException;
import java.io.OutputStream;

import java.util.HashMap;
import java.util.Map;

public class ReservationHandler implements HttpHandler {

    private OpenData dataClient = new OpenData();

    /**
     * Méthode qui transforme une chaîne de requête en une map de paramètres
     * @param query la chaîne de paramètres
     * @return la map
     */
    private Map<String,String> queryToMap(String query) {
        Map<String,String> map = new HashMap<>();

        if (query != null) {
            for (String key : query.split("&")) {
                String[] pair = key.split("=");
                if (pair.length > 1) {
                    map.put(pair[0], pair[1]);
                } else {
                    map.put(pair[0], "");
                }
            }
        }
        return map;
    }

    /**
     *
     * @param exchange the exchange containing the request from the
     *                 client and used to send the response
     * @throws IOException
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
            exchange.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");

            String responseBody = "";
            int httpStatus = 200;

        try {
            String query = exchange.getRequestURI().getQuery();

            Map<String, String> params = queryToMap(query);

            if (!params.containsKey("idTable") || !params.containsKey("Nom") ||  !params.containsKey("Prenom") || !params.containsKey("NumTel") || !params.containsKey("nbPers")) {
                httpStatus = 400;
                responseBody = "{\"erreur\": \"Paramètres manquants. idRestaurant, idClient et nbPers sont obligatoires.\"}";
            } else {
                String idTable = params.get("idTable");
                String nom = params.get("Nom");
                String prenom = params.get("Prenom");
                String numTel = params.get("NumTel");
                int nbPers = Integer.valueOf(params.get("nbPers"));

                responseBody = dataClient.reserverTable(idTable,nom,prenom,numTel, nbPers,2);
            }

        } catch (NumberFormatException e) {
            httpStatus = 400;
            responseBody = "{\"erreur\": \"Le nombre de personnes doit être un chiffre valide.\"}";
        } catch (Exception e) {
            httpStatus = 500;
            JSONObject erreur = new JSONObject();
            erreur.put("erreur", "Échec de la réservation : " + e.getMessage());
            responseBody = erreur.toString();
        }

            byte[] bytes = responseBody.getBytes("UTF-8");
            exchange.sendResponseHeaders(httpStatus, bytes.length);
            OutputStream os = exchange.getResponseBody();
            os.write(bytes);
            os.close();
    }
}
