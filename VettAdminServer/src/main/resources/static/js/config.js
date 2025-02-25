axios.defaults.paramsSerializer = function (paramObj) {
    const params = new URLSearchParams();
    for (const key in paramObj) {
        params.append(key, paramObj[key]);
    }
    return params.toString();
};

// GeoLocation
async function getForCurrentLocation (callbackFunction) {
    await navigator.geolocation.getCurrentPosition(callbackFunction)
}
const notyf = new Notyf({
    position: {
        x: 'center',  // 'left', 'center', 'right' 가능
        y: 'top'      // 'top', 'bottom' 가능
    }
});