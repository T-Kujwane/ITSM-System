// Function to show a notification
function showNotification(message, isError = false) {
    const notification = document.createElement('div');
    notification.className = 'notification';
    if (isError) {
        notification.classList.add('failure');
    }
    notification.innerText = message;
    document.body.appendChild(notification);

    // Show the notification
    setTimeout(() => {
        notification.classList.add('show');
    }, 100);

    // Hide the notification after 3 seconds
    setTimeout(() => {
        notification.classList.add('hide');
        // Remove the notification element from DOM after it fades out
        setTimeout(() => {
            document.body.removeChild(notification);
        }, 500);
    }, 3000);
}
