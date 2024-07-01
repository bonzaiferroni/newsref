var host = 'http://localhost:8080';

function init(h, eventId) {
    host = h;
    getRequests(eventId);
    setInterval(() => getRequests(eventId), 30000);
}

function makeRequest(id, eventId) {
    let button = document.getElementById('song-button-' + id);
    button.disabled = true;
    fetch(host + '/api/v1/event_profile/' + eventId + '/' + id, {
        method: 'POST',
    })
        .then(response => response.json())
        .then(data => getRequests(eventId))
        .catch(error => console.error('Error:', error));
}

function getRequests(eventId) {
    console.log('getting requests');
    fetch(host + '/api/v1/event_profile/' + eventId, {
        method: 'GET',
    })
        .then(response => response.json())
        .then(data => acceptRequests(data))
        .catch(error => console.error('Error:', error));
}

function acceptRequests(requests) {
    // output to console.log
    let requestSpan = document.getElementById('requests');
    // document.getElementsByClassName('song-button').forEach(x => x.disabled = false);
    let buttons = document.getElementsByClassName('song-button');
    for (let i = 0; i < buttons.length; i++) {
        buttons[i].disabled = false;
        buttons[i].textContent = 'Request';
    }
    requests.forEach(x => {
        if (x.performed) return;
        let button = document.getElementById('song-button-' + x.performance_id);
        if (button) {
            button.disabled = true;
            button.textContent = 'Requested';
        }
    });
    requestSpan.innerHTML = requests
        .filter(x => !x.performed)
        .map(x => x.performance_name).join(', ');
}