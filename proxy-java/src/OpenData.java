import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ProxySelector;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import com.sun.net.httpserver.HttpServer;
import org.json.JSONObject;
import org.json.JSONArray;

public class OpenData {
    /***
     * Main pour lancer le serveur
     * @param args argument de lancement
     */
    public static void main(String[] args) throws IOException{
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/data", new DataHandler());
        server.setExecutor(null); // creates a default executor
        System.out.println("Proxy démarré sur http://localhost:8080/data");
        server.start();
    }

    /**
     * Handler pour le serveur web
     */
    static class DataHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException{
            //Autorise tout le monde à lire le proxy
            exchange.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");

            String response = "";
            int httpStatus = 200;

            try {
                HttpClient client = HttpClient.newBuilder()
                        .proxy(ProxySelector.of(new InetSocketAddress("www-cache", 3128)))
                        .connectTimeout(Duration.ofSeconds(10))
                        .build();

                //Connection Api Vélib
                String nameApi = "nancy";
                String apiKey = "ccff3ae3c87530ebf6054e6b9b2dc66bec0a4fee";
                String url = "https://api.jcdecaux.com/vls/v1/stations?contract="+nameApi + "&apiKey=" +apiKey;

                HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET().build();

                HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
                response = httpResponse.body();

                if (httpResponse.statusCode() == 200) {

                    //On lit toute la réponse JSON
                    JSONObject jsonResponse = new JSONObject(response);

                    //On extrait les données qui nous intéressent
                    JSONArray jsonArray = jsonResponse.getJSONArray("data");

                    //On crée un nouvel objet JSON pour stocker les données extraites
                    JSONArray filterData = jsonArray.getJSONArray(0);

                    //On boucle sur tous les problèmes
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject incident = jsonArray.getJSONObject(i);
                        JSONObject location = incident.getJSONObject("location");

                        JSONObject filteredIncident = new JSONObject();
                        filteredIncident.put("cause", incident.getString("short_description"));
                        filteredIncident.put("details", incident.getString("description"));
                        filteredIncident.put("date_debut", incident.getString("starttime"));
                        filteredIncident.put("date_fin", incident.getString("endtime"));

                        filteredIncident.put("rue", location.getString("street"));
                        filteredIncident.put("coordonnees", location.getString("polyline")); // CRUCIAL pour Leaflet

                        filterData.put(filteredIncident);
                    }
                    response = filterData.toString();
                }
                else {
                    httpStatus = httpResponse.statusCode();
                    JSONObject error = new JSONObject();
                    error.put("erreur", "API distante indisponible. Code : " + httpStatus);
                    response = error.toString();
                }
            } catch (Exception e) {
                httpStatus = 500;
                JSONObject erreur = new JSONObject();
                erreur.put("erreur", "Erreur réseau du proxy : " + e.getMessage());
                response = erreur.toString();
            }

            try {
                byte[] bytes = response.getBytes("UTF-8");
                exchange.sendResponseHeaders(httpStatus, bytes.length);
                OutputStream os = exchange.getResponseBody();
                os.write(bytes);
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
