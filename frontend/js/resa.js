const urlReservation = 'http://localhost:8080/api/liste-reservations';
const urlRestaurants = 'http://localhost:8080/api/restaurants';
const urlTables = 'http://localhost:8080/api/tables';


const reservationsContainer = document.getElementById('reservations-container');
//Affichaeg des réservations
Promise.all([
    fetch(urlReservation).then(res => res.json()),
    fetch(urlRestaurants).then(res => res.json()),
    fetch(urlTables).then(res => res.json())
]).then(([reservations, restaurants, tables]) => {
    reservationsContainer.innerHTML = '';

            if (reservations.length === 0) {
                reservationsContainer.innerHTML = '<p>Aucune réservation n\'a été trouvée.</p>';
                return;
            }
            reservations.forEach(resa => {
        
            const date = new Date(resa.dateReservation).toLocaleDateString('fr-FR', {
                year: 'numeric',
                month: 'long',
                day: 'numeric'
            });

            let nomRestaurant = "Restaurant inconnu";
            let numTableAffiche = resa.idTab;
            const tableTrouvee = tables.find(t => t.id === resa.idTab);
            
            if (tableTrouvee) {
                numTableAffiche = tableTrouvee.numTable; 
                const restoTrouve = restaurants.find(r => r.id === tableTrouvee.idRes);
                if (restoTrouve) {
                    nomRestaurant = restoTrouve.name;
                }
            }

                const card = document.createElement('div');
                card.className = 'reservation-card'; 
                card.innerHTML = `
                    <h3>${nomRestaurant}</h3>
                    <p><strong>Réservation n° :</strong> ${resa.id}</p>
                    <p><strong>Date :</strong> ${date}</p>
                    <p><strong>Durée :</strong> ${resa.duree} h</p>
                    <p><strong>Table :</strong> ${numTableAffiche}</p>
                    <p><strong>Couverts :</strong> ${resa.nbConvives}</p>
                `;
                
                reservationsContainer.appendChild(card);
            });
}).catch(error => {
    console.error('Erreur lors de la récupération des données :', error);
});
    