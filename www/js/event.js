var host = 'http://localhost:8080';

function init(h, eventId) {
    host = h;
    refreshEvent(eventId);
    setInterval(() => refreshEvent(eventId), 30000);
}

function makeRequest(id, eventId) {
    let button = document.getElementById('song-button-' + id);
    button.disabled = true;
    fetch(host + '/api/v1/event_profile/' + eventId + '/' + id, {
        method: 'POST',
    })
        .then(response => response.json())
        .then(data => refreshEvent(eventId))
        .catch(error => console.error('Error:', error));
}

function postRequest(id, eventId) {
    let button = document.getElementById('song-button-' + id);
    let singer = document.getElementById('requester-sings').checked ? "requester"
        : document.getElementById('duet').checked ? "duet" : "luke";
    let notes = `singer: ${singer}`;
    let requesterName = document.getElementById('requester-name').value;
    let otherNotes = document.getElementById('other-notes').value;
    if (otherNotes && otherNotes.length > 0) {
        notes += `\nnotes: ${otherNotes}`;
    }

    let requestBody = {
        id: 0,
        eventId: eventId,
        songId: id,
        notes: notes,
        requesterName: requesterName,
    };

    button.disabled = true;
    fetch(host + '/api/v1/event_profile/request', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(requestBody)
    })
        .then(response => response.json())
        .then(data => refreshEvent(eventId))
        .catch(error => console.error('Error:', error));
}

function refreshEvent(eventId) {
    console.log('refreshing event');
    fetch(host + '/api/v1/event_info/' + eventId, {
        method: 'GET',
    })
        .then(response => response.json())
        .then(data => acceptRequests(data))
        .catch(error => console.error('Error:', error));
}

function acceptRequests(eventInfo) {
    setRequests(eventInfo.requests);
    let nowPlaying = '';
    if (eventInfo.currentRequest) {
        nowPlaying = printRequest(eventInfo.currentRequest);
    }
    document.getElementById('now-playing').innerHTML = nowPlaying;
}

function setRequests(requests) {
    let requestSpan = document.getElementById('requests');
    let buttons = document.getElementsByClassName('song-button');
    for (let i = 0; i < buttons.length; i++) {
        buttons[i].disabled = false;
        buttons[i].textContent = 'Request';
    }
    requests.forEach(x => {
        if (x.performed) return;
        let button = document.getElementById('song-button-' + x.songId);
        if (button) {
            button.disabled = true;
            button.textContent = 'Requested';
        }
    });
    requestSpan.innerHTML = requests
        .filter(x => !x.performed)
        .map(printRequest).join(', ');
}

function printRequest(request) {
    let str = request.songName;
    if (request.requesterName) {
        str += ` (requested by ${request.requesterName})`;
    }
    return str;
}