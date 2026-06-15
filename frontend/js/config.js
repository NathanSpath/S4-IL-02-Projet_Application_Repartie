const CONFIG = {
    API_BASE_URL: 'http://localhost:8080/api',
    JCD_CONTRACT: 'nancy'
};

CONFIG.URL_VELIB_INFO = `https://api.cyclocity.fr/contracts/${CONFIG.JCD_CONTRACT}/gbfs/station_information.json`;
CONFIG.URL_VELIB_STATUS = `https://api.cyclocity.fr/contracts/${CONFIG.JCD_CONTRACT}/gbfs/station_status.json`;