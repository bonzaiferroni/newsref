function makeRequest(id, eventId) {
    let button = document.getElementById('song-button-' + id);
    button.disabled = true;
    fetch('http://localhost:8080/api/v1/event_profile/' + eventId + '/' + id, {
        method: 'POST',
    })
        .then(response => response.json())
        .then(data => getRequests(eventId))
        .catch(error => console.error('Error:', error));
}

function getRequests(eventId) {
    fetch('http://localhost:8080/api/v1/event_profile/' + eventId, {
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
    console.log(document.getElementsByClassName('song-button'))
    requests.forEach(x => {
        let button = document.getElementById('song-button-' + x.id);
        if (button) button.disabled = true;
    });
    requestSpan.innerHTML = requests.map(x => x.performanceName).join(', ');
}