package ch.ghwalin.ldap.service;


import ch.ghwalin.ldap.model.User;
import ch.ghwalin.ldap.util.Result;
import ch.ghwalin.ldap.util.TokenHandler;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;


/**
 * provides services for user profiles
 * <p>
 * LDAP Profile
 *
 * @author Marcel Suter
 * @version 1.0
 * @since 2019-03-04
 */
@Path("profile")
public class ProfileService {

    /**
     * default constructor
     */
    public ProfileService() {

    }

    /**
     * saves the changes to the profile
     *
     * @param user  a valid user object
     * @param token encrypted authentication token
     * @return empty String
     */
    @POST
    @Path("save")
    @Produces(MediaType.TEXT_PLAIN)
    public Response login(
            @Valid @BeanParam User user,
            @CookieParam("token") String token
    ) {
        int status = 403;

        Map<String, String> claimMap = TokenHandler.readClaims(token);
        user.setDistinguishedName(claimMap.getOrDefault("subject", null));
        if (user.getDistinguishedName() != null) {
            status = 200;
            Result result = user.save();
            if (result == Result.ERROR) status = 500;
        }

        return buildResponse(status, "");
    }

    @GET
    @Path("link")
    @Produces(MediaType.APPLICATION_JSON)
    public Response readLink(
            @QueryParam("token") String token
    ) {
        return readProfile(token);
    }

    /**
     * reads the user profile
     *
     * @param token encrypted authentication token
     * @return Response
     */
    @GET
    @Path("read")
    @Produces(MediaType.APPLICATION_JSON)
    public Response readProfile(
            @CookieParam("token") String token
    ) {
        int status = 403;
        User user = null;

        Map<String, String> claimMap = TokenHandler.readClaims(token);
        String userDN = claimMap.getOrDefault("subject", null);
        if (userDN != null) {
            user = new User(userDN);
            if (user.getUsername() != null)
                status = 200;
        }


        return buildResponse(status, user);
    }

    /**
     * builds a response object
     *
     * @param status the http status
     * @param entity the user object to be sent
     * @return response
     */
    private Response buildResponse(int status, Object entity) {
        return Response
                .status(status)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Credentials", "true")
                .header("Access-Control-Allow-Headers",
                        "origin, content-type, accept, authorization")
                .header("Access-Control-Allow-Methods",
                        "GET, POST, DELETE")
                .entity(entity)
                .build();
    }
}