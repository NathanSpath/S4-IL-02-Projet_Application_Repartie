var latitude = 48.692054;
var longitude = 6.184417;
var zoom = 14;
var map = L.map('map').setView([latitude, longitude], zoom);
L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    maxZoom: 19,
    attribution: '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>'
}).addTo(map);



const contractName = "nancy";
const apiKey = "ccff3ae3c87530ebf6054e6b9b2dc66bec0a4fee";
const urlAPI = `https://api.jcdecaux.com/vls/v1/stations?contract=${contractName}&apiKey=${apiKey}`; 

const urlIncidents = 'http://localhost:8080/api/incidents';
const urlReservation = 'http://localhost:8080/api/reservations';
const formulaire = document.querySelector('#booking-form');

fetch(urlAPI).then(response => response.json()).then(data => {
    console.log(data);
    data.forEach(station => {
        const marker = L.marker([station.position.lat, station.position.lng]).addTo(map);
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
    console.error("Erreur lors de la récupération des données :", error);
});

fetch(urlIncidents).then(response => response.json()).then(data => {
    console.log("Incidents:");
    console.log(data);
    data.forEach(incident => {
        const marker = L.marker([incident.latitude, incident.longitude]).addTo(map);
    });
}).catch(error => {
    console.error("Erreur lors de la récupération des données :", error);
});

form.addEventListener('submit', function(event) {
    const formData = new FormData(formulaire);

    const donnees = {
        nom:      document.getElementById('nom').value,
        prenom:   document.getElementById('prenom').value,
        numTel:   document.getElementById('numTel').value,
        table:    document.getElementById('table').value,
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


