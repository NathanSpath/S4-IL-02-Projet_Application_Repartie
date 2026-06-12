const urlReservation = 'http://localhost:8080/api/liste-reservations';
const urlRestaurants = 'http://localhost:8080/api/restaurants';
const urlTables = 'http://localhost:8080/api/tables';


const reservationsContainer = document.getElementById('reservations-container');

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
                const card = document.createElement('div');
                card.className = 'reservation-card'; 
                card.innerHTML = `
                    <h3>Réservation Numéro : ${resa.id}</h3>
                    <p><strong>Téléphone :</strong> ${resa.numTel || resa.NumTel}</p>
                    <p><strong>Date :</strong> ${resa.dateReservation}</p>
                    <p><strong>Durée :</strong> ${resa.duree}</p>
                    <hr style="margin: 10px 0; border: 0; border-top: 1px solid #ccc;">
                    <p><strong>Table n° :</strong> ${resa.idTable}</p>
                    <p><strong>Couverts :</strong> ${resa.nbPers}</p>
                `;
                
                reservationsContainer.appendChild(card);
            });
}).catch(error => {
    console.error('Erreur lors de la récupération des données :', error);
});
    