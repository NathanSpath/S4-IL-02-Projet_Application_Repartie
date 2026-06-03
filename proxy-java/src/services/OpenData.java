package services;

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
    private HttpClient client;

    /**
     * Constructeur qui configure le client HTTP pour utiliser le proxy
     */
    public OpenData() {
        // Version SANS proxy (pour tester chez soi)
        this.client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        /* // Version AVEC proxy (à décommenter quand tu seras sur les PC de l'IUT)
        this.client = HttpClient.newBuilder()
                .proxy(ProxySelector.of(new InetSocketAddress("www-cache", 3128)))
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        */
    }

    /**
     * Retourne la liste des incidents dans la métropole de Nancy
     * @return la liste des incidents
     * @throws Exception
     */
    public String getIncidents() throws Exception {
        String url = "https://carto.g-ny.eu/data/cifs/cifs_waze_v2.json";

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            JSONObject jsonResponse = new JSONObject(response.body());
            JSONArray jsonArray = jsonResponse.getJSONArray("incidents");
            JSONArray filterData = new JSONArray();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject incident = jsonArray.getJSONObject(i);
                JSONObject location = incident.getJSONObject("location");

                JSONObject filteredIncident = new JSONObject();
                filteredIncident.put("cause", incident.optString("short_description"));
                filteredIncident.put("details", incident.optString("description"));
                filteredIncident.put("date_debut", incident.optString("starttime"));
                filteredIncident.put("date_fin", incident.optString("endtime"));
                filteredIncident.put("rue", location.optString("street"));
                filteredIncident.put("coordonnees", location.optString("polyline"));

                filterData.put(filteredIncident);
            }
            return filterData.toString();
        }
        throw new Exception("Erreur API Incidents : " + response.statusCode());
    }

    /**
     * Retourne la liste des restaurants dans la métropole de Nancy
     * @return la liste des restaurants
     * @throws Exception
     */
    public String getRestaurants() throws Exception {
        String url = "rmi://localhost:1099/ServiceRestaurant";

        ServiceRestaurant service = (ServiceRestaurant) java.rmi.Naming.lookup(url);

        String jsonRestaurant = service.getRestaurant();

        return jsonRestaurant;
    }

    /**
     * Permet de réserver une table dans un restaurant
     * @param idRestaurant id du restaurant
     * @param idClient id du client
     * @param nbPers nombre de personnes
     * @return un message de confirmation ou d'erreur
     * @throws Exception
     */
    public String reserverTable(String idRestaurant,String idClient,int nbPers) throws Exception {
        String url = "rmi://localhost:1099/ServiceRestaurant";
        ServiceRestaurant service = (ServiceRestaurant) java.rmi.Naming.lookup(url);

        return service.reservation(idRestaurant, idClient, nbPers);
    }

}