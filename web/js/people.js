/**
 * view-controller for people.html
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
    readPeople();
    $("#peopleList").on("click", "button", function(){
        window.location.href="./profile.html?dn=" + this.value;
    });

});

/**
 * reads the user profile
 */
function readPeople() {
    var filter = $.urlParam("filter");
    var urlParam = "";
    if (filter !== null)
        urlParam = "?filter=" + filter;
    $
        .ajax({
            url: "./resource/profile/people" + urlParam,
            dataType: "json",
            type: "GET",
        })
        .done(showPeople)
        .fail(function (xhr, status, errorThrown) {
            if (xhr.status == "403") {
                window.location.href("./login.html");
            } else {
                $("#message").text("Es ist ein Fehler aufgetreten");
            }
        })
}

/**
 * shows the list of all people
 * @param peopleMap profile data as json arrays
 */
function showPeople(peopleMap) {
    $("#message").val("");
    var table = "";
    $.each(peopleMap, function (personId, person) {
        table += "<tr>";
        table += makeCell(person.username);
        table += makeCell(person.firstname);
        table += makeCell(person.lastname);
        var groups = person.memberOf;
        var text = "";
        $.each(groups, function(group) {
            text += group + " / ";
        });
        table += makeCell(text);
        table += makeCell("<button type='button' name='edit' value='" + personId + "'>Edit</button>");
        table += "</tr>";
    });
    $("#peopleList > tbody").append(table);
}

/**
 * makes a table cell
 * @param value
 * @returns {string}
 */
function makeCell(value) {
    return "<td>" + value + "</td>";
}