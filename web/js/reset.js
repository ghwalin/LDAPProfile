/**
 * view-controller for reset.html
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
    $("#resetForm").on("submit",submitForm);

});

/**
 * submits the form data
 * @param event
 */
function submitForm(event) {
    event.preventDefault();
    $
        .ajax({
            url: "./resource/user/reset",
            dataType: "text",
            type: "POST",
            data: $("#resetForm").serialize()
        })
        .done(function (jsonData) {
            window.location.href("./profile.html");
        })
        .fail(function (xhr, status, errorThrown) {
                $("#message").text("Es ist ein Fehler aufgetreten");
        })
}

