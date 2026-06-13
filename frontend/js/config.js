const CONFIG = {
    API_BASE_URL: 'http://localhost:8080/api',
    JCD_CONTRACT: 'nancy',
    JCD_API_KEY: 'ccff3ae3c87530ebf6054e6b9b2dc66bec0a4fee'
};

CONFIG.URL_VELIB = `https://api.jcdecaux.com/vls/v1/stations?contract=${CONFIG.JCD_CONTRACT}&apiKey=${CONFIG.JCD_API_KEY}`;