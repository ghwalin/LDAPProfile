/**
 * view-controller for profile.html
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
    readProfile();

    /**
     * listener for submitting the form
     */
    $("#profileForm").submit(saveProfile);

});

/**
 * sends the input to the webservice to save the profile
 * @param event the event activating the listener
 */
function saveProfile(event) {
    event.preventDefault();
    $
        .ajax({
            url: "./resource/profile/save",
            dataType: "text",
            type: "POST",
            data: $("#profileForm").serialize()
        })
        .done(readProfile)
        .fail(function (xhr, status, errorThrown) {
            if (xhr.status == "403") {
                window.location.href("./login.html");
            } else {
                $("#message").text("Es ist ein Fehler aufgetreten");
            }
        })
}

/**
 * reads the user profile
 */
function readProfile() {
    var path="read";
    if ($.urlParam("token") !== null) {
        path = "link?token=" + $.urlParam("token");
    }
    $
        .ajax({
            url: "./resource/profile/" + path,
            dataType: "json",
            type: "GET",
        })
        .done(showProfile)
        .fail(function (xhr, status, errorThrown) {
            if (xhr.status == "403") {
                window.location.href("./login.html");
            } else {
                $("#message").text("Es ist ein Fehler aufgetreten");
            }
        })
}

/**
 * shows the user profile
 * @param jsonData profile data as json array
 */
function showProfile(jsonData) {
    $("#message").empty();
    $("#username").val(jsonData.username);
    $("#firstname").val(jsonData.firstname);
    $("#lastname").val(jsonData.lastname);
    $("#email").val(jsonData.email);
    $("#mobile").val(jsonData.mobile);
    $("#password").val("");

}

