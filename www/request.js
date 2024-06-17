function makeRequest(id, eventId) {
    let button = document.getElementById('button-' + id);
    button.disabled = true;
    fetch('http://localhost:8080/api/v1/event/request/' + eventId + '/' + id, {
        method: 'POST',
    })
        .then(response => response.json())
        .then(data => console.log(data))
        .catch(error => console.error('Error:', error));
}