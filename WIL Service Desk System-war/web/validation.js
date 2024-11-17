function validateForm() {
    var username = document.forms["userForm"]["username"].value;
    var password = document.forms["userForm"]["password"].value;

    if (username === "" || password === "") {
        alert("Both fields must be filled out");
        return false;
    }
    return true;
}
