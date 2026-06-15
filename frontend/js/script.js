const latitude = 48.692054;
const longitude = 6.184417;
const zoom = 14;
const map = L.map('map').setView([latitude, longitude], zoom);

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

const formulaire = document.querySelector('.booking-form');

const velibGroup = L.layerGroup().addTo(map);
const restoGroup = L.layerGroup().addTo(map);
const incidentGroup = L.layerGroup().addTo(map);

// Récupération des données Velib depuis l'API GBFS
Promise.all([
    fetch(CONFIG.URL_VELIB_INFO).then(res => res.json()),
    fetch(CONFIG.URL_VELIB_STATUS).then(res => res.json())
]).then(([infoData, statusData]) => {
        const stationsInfo = infoData.data.stations;
    const stationsStatus = statusData.data.stations;
    stationsInfo.forEach(station => {
        const status = stationsStatus.find(s => s.station_id === station.station_id);
        if (status) {
            const marker = L.marker([station.lat, station.lon], {icon: velibIcon}).addTo(velibGroup);
            const popupContent = `
                <b>${station.name}</b><br>
                Vélo disponibles: ${status.num_bikes_available}<br>
                Emplacements disponibles: ${status.num_docks_available}
            `;
            marker.bindPopup(popupContent);
        }
    });
}).catch(error => {
    console.error("Erreur lors de la récupération des données vélos :", error);
});

// Récupération des incidents depuis l'API
fetch(`${CONFIG.API_BASE_URL}/incidents`).then(response => response.json()).then(data => {
    data.forEach(incident => {
        const marker = L.marker([incident.latitude, incident.longitude], {icon: incidentIcon}).addTo(incidentGroup);

        if(incident.cause) {
            marker.bindPopup(`<b>Incident:</b> ${incident.cause}`);
        }
    });
}).catch(error => {
    console.error("Erreur lors de la récupération des incidents :", error);
});


// Récupération des restaurants et des tables depuis l'API
Promise.all([
    fetch(`${CONFIG.API_BASE_URL}/restaurants`).then(res => res.json()),
    fetch(`${CONFIG.API_BASE_URL}/tables`).then(res => res.json())
]).then(([restaurants, tables]) => {
    
    restaurants.forEach(restaurant => {
        if (restaurant.coordonnees) {
            const coords = restaurant.coordonnees.split(',');
            const lat = parseFloat(coords[0].trim());
            const lng = parseFloat(coords[1].trim());

            const marker = L.marker([lat, lng], {icon: restaurantIcon}).addTo(restoGroup);

            const restaurantTables = tables.filter(table => table.idRes === restaurant.id);

            let tablesHtml = "";
            if (restaurantTables.length > 0) {
                tablesHtml = restaurantTables.map(table => `
                    <li>
                        Table ${table.numTable} (${table.nbPlaces} couverts) 
                        <button onclick="preRemplirFormulaire('${table.id}', '${table.nbPlaces}')" class="choose-btn">
                            Choisir
                        </button>
                    </li>
                `).join('');
            } else {
                tablesHtml = "<li>Aucune table disponible</li>";
            }

            let popupContent = `
                <b>${restaurant.name}</b><br>
                Adresse: ${restaurant.adresse}<br>
                <p class="bold">Tables disponibles :</p>
                <ul>
                    ${tablesHtml}
                </ul>
            `;

            marker.bindPopup(popupContent);
        }     
    });
}).catch(error => {
    console.error("Erreur lors de la récupération des restaurants ou des tables :", error);
});


// Ensemble des listener d'événements pour les checkboxes
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

formulaire.addEventListener('submit', function(event) {
    event.preventDefault();

    const nom     = document.getElementById('nom').value;
    const prenom  = document.getElementById('prenom').value;
    const numTel  = document.getElementById('numTel').value;
    const idTable = document.getElementById('table').value;
    const nbPers  = document.getElementById('couverts').value;
    const date  = document.getElementById('date').value;
    const duree  = document.getElementById('duree').value;

    const url = `${CONFIG.API_BASE_URL}/reservations?idTable=${encodeURIComponent(idTable)}&Nom=${encodeURIComponent(nom)}&Prenom=${encodeURIComponent(prenom)}&NumTel=${encodeURIComponent(numTel)}&nbPers=${nbPers}&duree=${encodeURIComponent(duree)}&dateReservation=${encodeURIComponent(date)}`;

    fetch(url)
        .then(response => response.json())
        .then(data => {
            if (data === true) {
                // Réservation réussie
                formulaire.reset();
                afficherPopup('✅ Réservation confirmée !', 'success');
            } else if (data.erreur) {
                afficherPopup('❌ ' + data.erreur, 'error');
            } else {
                afficherPopup('❌ La réservation a échoué (table déjà réservée ?)', 'error');
            }
        })
        .catch(error => {
            console.error('Erreur lors de la réservation :', error);
            afficherPopup('❌ Erreur de connexion au serveur.', 'error');
        });
});

//méthode pour afficher un popup de notification après une réservation
function afficherPopup(message, type) {
    const existing = document.getElementById('popup-reservation');
    if (existing) existing.remove();

    const popup = document.createElement('div');
    popup.id = 'popup-reservation';
    popup.textContent = message;
    popup.style.cssText = `
        position: fixed;
        top: 20px;
        left: 50%;
        transform: translateX(-50%);
        padding: 15px 25px;
        border-radius: 8px;
        font-weight: bold;
        font-size: 16px;
        z-index: 9999;
        box-shadow: 0 4px 12px rgba(0,0,0,0.3);
        background-color: ${type === 'success' ? '#4CAF50' : '#f44336'};
        color: white;
        transition: opacity 0.5s;
    `;

    document.body.appendChild(popup);
    setTimeout(() => {
        popup.style.opacity = '0';
        setTimeout(() => popup.remove(), 500);
    }, 3000);
}

//Méthode pour pré-remplir le formulaire de réservation avec les informations de la table sélectionnée
function preRemplirFormulaire(idTable, nbCouverts) {
    document.getElementById('table').value = idTable;
    document.getElementById('couverts').value = nbCouverts;
}