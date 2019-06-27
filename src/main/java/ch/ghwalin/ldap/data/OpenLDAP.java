package ch.ghwalin.ldap.data;


import ch.ghwalin.ldap.service.JerseyConfig;
import ch.ghwalin.ldap.util.Result;
import com.unboundid.ldap.sdk.*;
import com.unboundid.ldap.sdk.extensions.PasswordModifyExtendedRequest;

import java.util.Map;


/**
 * provides services for authentication
 * <p>
 * LDAP Profile
 *
 * @author Marcel Suter
 * @version 1.0
 * @since 2019-03-04
 */

public final class OpenLDAP {
    private static final OpenLDAP instance = new OpenLDAP();
    private static LDAPConnection connection;

    /**
     * default constructor: defeat instantiation
     */
    private OpenLDAP() {
    }

    /**
     * @return get the instance
     */
    public static OpenLDAP getInstance() {
        return OpenLDAP.instance;
    }

    /**
     * authenticate a user
     *
     * @param username commonName or mail of the user to be authenticated
     * @param password password to be authenticated
     * @return distinguished name of the user / null=not found
     */
    public static String authenticate(String username, String password) {
        String userDN = null;
        try {
            Filter userFilter = Filter.createORFilter(
                    Filter.createEqualityFilter("cn", username),
                    Filter.createEqualityFilter("mail", username)
            );
            SearchResult searchResult = searchUser(userFilter);
            if (searchResult.getEntryCount() == 1) {
                SearchResultEntry entry = searchResult.getSearchEntries().get(0);
                BindRequest bindRequest = new SimpleBindRequest(entry.getDN(), password);
                getConnection().bind(bindRequest);
                userDN = entry.getDN();
            }
        } catch (LDAPException exLdap) {
            exLdap.printStackTrace();
            System.out.println(exLdap.getResultCode());
            System.out.println(exLdap.getDiagnosticMessage());
            return null;
        }
        return userDN;
    }

    /**
     * reads an ldap entry identified by the dn
     * @param userDN the distinguished name of the entry
     * @return the user entry
     */
    public static SearchResultEntry readEntry(String userDN) {

        try {
            return getConnection().getEntry(userDN);
        } catch (LDAPException exLdap) {
            exLdap.printStackTrace();
            System.out.println(exLdap.getResultCode());
            System.out.println(exLdap.getDiagnosticMessage());
            return null;
        }
    }

    /**
     * search a ldap user by common name or mail
     *
     * @param filter ldap search filter
     * @return searchResult
     */
    public static SearchResult searchUser(Filter filter) {
        SearchResult searchResult = null;

        SearchRequest searchRequest = new SearchRequest(
                JerseyConfig.getProperty("ldapUserBase"),
                SearchScope.SUB,
                filter);

        try {
            searchResult = getConnection().search(searchRequest);
        } catch (LDAPException exLdap) {
            exLdap.printStackTrace();
            System.out.println(exLdap.getResultCode());
            System.out.println(exLdap.getDiagnosticMessage());
        }

        return searchResult;
    }

    /**
     * changes the password of the user
     * @param userDN the distinguished name
     * @param password the password to be set
     * @return Result
     */
    public static Result changePassword(String userDN, String password) {
        if (password != null && !password.equals("")) {
            PasswordModifyExtendedRequest passwordModifyRequest = new PasswordModifyExtendedRequest(
                    userDN,
                    (String) null,
                    password);

            try {
                getConnection().processExtendedOperation(passwordModifyRequest);

            } catch (LDAPException exLdap) {
                exLdap.printStackTrace();
                System.out.println(exLdap.getResultCode());
                System.out.println(exLdap.getDiagnosticMessage());
                return Result.ERROR;
            }
            return Result.SUCCESS;
        }
        return Result.NOACTION;
    }

    /**
     * modifies an attributes in the map
     * @param attributes map of key/values of the attributes
     * @param userDN the distinguished name
     * @return Result
     */
    public static Result modifyAttr(Map<String, String> attributes, String userDN) {
        Result result = Result.NOACTION;
        for(Map.Entry<String, String> entry : attributes.entrySet()) {

            if (entry.getValue() != null && !entry.getValue().equals("")) {
                Modification modification = new Modification(ModificationType.REPLACE, entry.getKey(), entry.getValue());

                try {
                    getConnection().modify(new ModifyRequest(userDN, modification));
                    result = Result.SUCCESS;
                } catch (LDAPException exLdap) {
                    exLdap.printStackTrace();
                    System.out.println(exLdap.getResultCode());
                    System.out.println(exLdap.getDiagnosticMessage());
                    return Result.ERROR;
                }
            }
        }
        return result;
    }

    /**
     * @return the connection
     */
    public static LDAPConnection getConnection() throws LDAPException {
        if (connection == null) {
            setConnection(new LDAPConnection(
                    JerseyConfig.getProperty("ldapHost"),
                    Integer.parseInt(JerseyConfig.getProperty("ldapPort")),
                    JerseyConfig.getProperty("ldapRDN"),
                    JerseyConfig.getProperty("ldapPassword"))
            );
        }
        return connection;
    }

    /**
     * @param connection the connection to set
     */
    public static void setConnection(LDAPConnection connection) {
        OpenLDAP.connection = connection;
    }

}
