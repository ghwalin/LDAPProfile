package ch.ghwalin.ldap.service;


import ch.ghwalin.ldap.model.User;

import ch.ghwalin.ldap.util.TokenHandler;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;


/**
 * provides services for authentication
 * <p>
 * LDAP Profile
 *
 * @author Marcel Suter
 * @version 1.0
 * @since 2019-03-04
 */
@Path("user")
public class UserService {


    /**
     * default constructor
     */
    public UserService() {

    }

    /**
     * authenticate the user with username/password
     *
     * @param username the username
     * @param password the password
     * @return empty String
     */
    @POST
    @Path("login")
    @Produces(MediaType.TEXT_PLAIN)
    public Response login(
            @FormParam("username") String username,
            @FormParam("password") String password
    ) {
        User user = new User();
        int httpStatus = 200;

        String userDN = user.authenticate(username, password);
        if (userDN == null) {
            httpStatus = 401;
        }

        NewCookie tokenCookie = new NewCookie(
                "jwtoken", TokenHandler.buildToken(userDN, 500, "admin"), // FIXME
                "/",
                "",
                "Auth-Token",
                6000,
                false
        );

        return Response
                .status(httpStatus)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Credentials", "true")
                .header("Access-Control-Allow-Headers",
                        "origin, content-type, accept, authorization")
                .header("Access-Control-Allow-Methods",
                        "GET, POST, DELETE")
                .entity("")
                .cookie(tokenCookie)
                .build();
    }

    /**
     * logoff the user and destroy the session
     *
     * @return Response
     */
    @GET
    @Path("logoff")
    @Produces(MediaType.TEXT_PLAIN)
    public Response logoff() {
        NewCookie tokenCookie = new NewCookie(
                "jwtoken", "",
                "/",
                "",
                "Auth-Token",
                1,
                false
        );

        return Response
                .status(200)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Credentials", "true")
                .header("Access-Control-Allow-Headers",
                        "origin, content-type, accept, authorization")
                .header("Access-Control-Allow-Methods",
                        "GET, POST, DELETE")
                .entity("")
                .cookie(tokenCookie)
                .build();
    }

    /**
     * process a password reset request
     *
     * @param username the username
     * @param mobile   the mobile phone number
     * @param email    the email address
     * @return empty String
     */
    @POST
    @Path("reset")
    @Produces(MediaType.TEXT_PLAIN)
    public Response reset(
            @FormParam("username") String username,
            @FormParam("mobile") String mobile,
            @FormParam("email") String email
    ) {
        int countValues = 0;
        if (username != null && username.length() > 3) countValues++;
        if (mobile != null && mobile.length() > 10) countValues++;
        if (email != null && email.length() > 5) countValues++;

        int httpStatus = 404;
        User user = new User();
        String userDN = "";
        String userRole = "guest";
        if (countValues >= 2) {
            user = new User(username, mobile, email);
            if (user.getDistinguishedName() != null) {
                userDN = user.getDistinguishedName();
                userRole = user.getMemberOf().get(0).getGroupName();
                httpStatus = 200;
            }
        }

        NewCookie tokenCookie = new NewCookie(
                "jwtoken", TokenHandler.buildToken(userDN, 1, userRole),
                "/",
                "",
                "Auth-Token",
                6000,
                false
        );

        return Response
                .status(httpStatus)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Credentials", "true")
                .header("Access-Control-Allow-Headers",
                        "origin, content-type, accept, authorization")
                .header("Access-Control-Allow-Methods",
                        "GET, POST, DELETE")
                .entity("")
                .cookie(tokenCookie)
                .build();
    }

}