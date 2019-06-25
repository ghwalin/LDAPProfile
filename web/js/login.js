/**
 * view-controller for login.html
 *
 * LDAP Profile
 *
 * @author  Marcel Suter
 * @since   2019-03-19
 * @version 1.0
 */

/**
 * register listeners
 */
$(document).ready(function () {

    /**
     * listener for submitting the form sends the login data to the web service
     */
    $("#loginForm").on("submit", submitForm);

});

/**
 * submits the login form
 * @param event
 */
function submitForm(event) {
    event.preventDefault();
    $
        .ajax({
            url: "./resource/user/login",
            dataType: "text",
            type: "POST",
            data: $("#loginForm").serialize()
        })
        .done(function (jsonData) {
            window.location.href = "./profile.html";
        })
        .fail(function (xhr, status, errorThrown) {
            if (xhr.status == "401") {
                $("#message").text("Benutzername/Passwort unbekannt");
            } else {
                $("#message").text("Es ist ein Fehler aufgetreten");
            }
        })
}

