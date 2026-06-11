var latitude = 48.692054;
var longitude = 6.184417;
var zoom = 14;
var map = L.map('map').setView([latitude, longitude], zoom);
L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    maxZoom: 19,
    attribution: '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>'
}).addTo(map);

const velibIcon = L.divIcon({
    className: 'map-marker velib',
    iconSize: [20, 20],
    iconAnchor: [10, 10],
    popupAnchor: [0, -10]
});

const incidentIcon = L.divIcon({
    className: 'map-marker incident', 
    iconSize: [20, 20],
    iconAnchor: [10, 10],
    popupAnchor: [0, -10]
});

const restaurantIcon = L.divIcon({
    className: 'map-marker resto',
    iconSize: [20, 20],
    iconAnchor: [10, 10],
    popupAnchor: [0, -10]
});

const contractName = "nancy";
const apiKey = "ccff3ae3c87530ebf6054e6b9b2dc66bec0a4fee";
const urlAPI = `https://api.jcdecaux.com/vls/v1/stations?contract=${contractName}&apiKey=${apiKey}`;
const urlRestaurants = 'http://localhost:8080/api/restaurants';

const urlIncidents = 'http://localhost:8080/api/incidents';
const urlReservation = 'http://localhost:8080/api/reservations';
const formulaire = document.querySelector('#booking-form');

const velibGroup = L.layerGroup().addTo(map);
const restoGroup = L.layerGroup().addTo(map);
const incidentGroup = L.layerGroup().addTo(map);

fetch(urlAPI).then(response => response.json()).then(data => {
    data.forEach(station => {
        const marker = L.marker([station.position.lat, station.position.lng], {icon: velibIcon}).addTo(velibGroup);
        const popupContent = `
            <b>${station.name}</b><br>
            Adresse: ${station.address}<br>
            Statut: ${station.status}<br>
            Vélo disponibles: ${station.available_bikes}<br>
            Emplacements disponibles: ${station.available_bike_stands}
        `;
        marker.bindPopup(popupContent);
    });
}).catch(error => {
    console.error("Erreur lors de la récupération des données vélos :", error);
});

fetch(urlIncidents).then(response => response.json()).then(data => {
    data.forEach(incident => {
        const marker = L.marker([incident.latitude, incident.longitude], {icon: incidentIcon}).addTo(incidentGroup);

        if(incident.cause) {
            marker.bindPopup(`<b>Incident:</b> ${incident.cause}`);
        }
    });
}).catch(error => {
    console.error("Erreur lors de la récupération des incidents :", error);
});

fetch(urlRestaurants).then(response => response.json()).then(data => {
    data.forEach(restaurant => {
        if (restaurant.coordonnees) {
            const coords = restaurant.coordonnees.split(',');
            const lat = parseFloat(coords[0].trim());
            const lng = parseFloat(coords[1].trim());

            const marker = L.marker([lat, lng], {icon: restaurantIcon}).addTo(restoGroup);

            const popupContent = `
                    <b>${restaurant.name}</b><br>
                    Adresse: ${restaurant.adresse}<br>
                `;
            marker.bindPopup(popupContent);
        }
    });
}).catch(error => {
    console.error("Erreur lors de la récupération des restaurants :", error);
});

document.getElementById('velibCheckbox').addEventListener('change', function(e) {
    if (e.target.checked) {
        map.addLayer(velibGroup);
    } else {
        map.removeLayer(velibGroup);
    }
});

document.getElementById('incidentCheckbox').addEventListener('change', function(e) {
    if (e.target.checked) {
        map.addLayer(incidentGroup);
    } else {
        map.removeLayer(incidentGroup);
    }
});

document.getElementById('restoCheckbox').addEventListener('change', function(e) {
    if (e.target.checked) {
        map.addLayer(restoGroup);
    } else {
        map.removeLayer(restoGroup);
    }
});

form.addEventListener('submit', function(event) {
    const formData = new FormData(formulaire);

    const donnees = {
        nom: document.getElementById('nom').value,
        prenom: document.getElementById('prenom').value,
        numTel: document.getElementById('numTel').value,
        table: document.getElementById('table').value,
        couverts: document.getElementById('couverts').value,
    };
    const url = `http://localhost:8080/api/reservations?nom=${nom}&prenom=${prenom}&numTel=${numTel}&idRestaurant=${table}&nbPers=${couverts}`;

    fetch(url)
        .then(response => response.json())
        .then(data => {
            console.log('Réservation confirmée :', data);
        })
        .catch(error => {
            console.error('Erreur lors de la réservation :', error);
        });
});
