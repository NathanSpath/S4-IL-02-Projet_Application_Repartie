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


