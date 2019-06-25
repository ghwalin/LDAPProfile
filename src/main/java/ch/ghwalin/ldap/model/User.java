package ch.ghwalin.ldap.model;

import ch.ghwalin.ldap.data.OpenLDAP;
import ch.ghwalin.ldap.util.Result;
import com.unboundid.ldap.sdk.*;
import org.hibernate.validator.constraints.Email;


import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.ws.rs.FormParam;
import java.util.*;

/**
 * provides services for authentication
 * <p>
 * LDAP Profile
 *
 * @author Marcel Suter
 * @version 1.0
 * @since 2019-03-04
 */
public class User {
    private String distinguishedName;
    @NotNull
    @FormParam("username")
    private String username;

    @NotNull
    @FormParam("firstname")
    private String firstname;

    @NotNull
    @FormParam("lastname")
    private String lastname;

    @Email
    @FormParam("email")
    private String email;

    @Pattern(regexp = "(\\+41)\\s?(\\d{2})\\s?(\\d{3})\\s?(\\d{2})\\s?(\\d{2})")
    @FormParam("mobile")
    private String mobile;

    @Pattern(regexp="(^$|.{8,30})")
    @FormParam("password")
    private String password;

    /**
     * default constructor
     */
    public User() {
        setDistinguishedName(null);
    }

    /**
     * constructor: load user data
     * @param userDN the dn of the user
     */
    public User (String userDN) {
        readUserEntry(userDN);
    }

    /**
     * constructor: search for a user
     * @param username  the username
     * @param mobile    the mobile phone number
     * @param email     the email address
     */
    public User (String username, String mobile, String email) {
        List<Filter> filterList = new ArrayList<>();
        if (username != null && username.length() > 3){
            filterList.add(Filter.createEqualityFilter("cn", username));
        }
        if (mobile != null && mobile.length() > 10) {
            filterList.add(Filter.createEqualityFilter("mobile",mobile));
        }
        if (email != null && email.length() > 5) {
            filterList.add(Filter.createEqualityFilter("mail", email));
        }
        Filter filter = Filter.createANDFilter(filterList);

        SearchResult searchResult = OpenLDAP.searchUser(filter);
        if (searchResult.getEntryCount() == 1) {
            SearchResultEntry entry = searchResult.getSearchEntries().get(0);
            readUserEntry( entry.getDN() );
        }
    }

    /**
     * authenticates the user with username / password
     * @param username  the username to be authenticated
     * @param password  the password to be authenticated
     * @return the user role
     */
    public String authenticate(String username, String password) {
        setDistinguishedName(OpenLDAP.authenticate(username, password));
        return getDistinguishedName();
    }

    /**
     * saves the user to the ldap
     * @return Result
     */
    public Result save() {
        Result result = Result.SUCCESS;
        if (getPassword() != null && getPassword().length() >= 8) {
            result = OpenLDAP.changePassword(getDistinguishedName(), getPassword());
        }

        if (result == Result.SUCCESS) {
            Map<String, String> modifyMap = new HashMap<>();
            modifyMap.put("mail", getEmail());
            modifyMap.put("mobile", getMobile());
            result = OpenLDAP.modifyAttr(modifyMap, getDistinguishedName());
        }
        return result;
    }

    /**
     * reads a ldap entry identified by the dn
     * @param userDN the distinguished name
     */
    private void readUserEntry(String userDN) {
        SearchResultEntry entry = OpenLDAP.readEntry(userDN);
        if (entry != null) {
            setDistinguishedName(userDN);
            setUsername(entry.getAttributeValue("cn"));
            setFirstname(entry.getAttributeValue("givenname"));
            setLastname(entry.getAttributeValue("sn"));
            setEmail(entry.getAttributeValue("mail"));
            setMobile(entry.getAttributeValue("mobile"));
        }
    }

    /**
     * Gets the distinguishedName
     *
     * @return value of distinguishedName
     */
    public String getDistinguishedName() {
        return distinguishedName;
    }

    /**
     * Sets the distinguishedName
     *
     * @param distinguishedName the value to set
     */

    public void setDistinguishedName(String distinguishedName) {
        this.distinguishedName = distinguishedName;
    }

    /**
     * Gets the username
     *
     * @return value of username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username
     *
     * @param username the value to set
     */

    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the firstname
     *
     * @return value of firstname
     */
    public String getFirstname() {
        return firstname;
    }

    /**
     * Sets the firstname
     *
     * @param firstname the value to set
     */

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    /**
     * Gets the lastname
     *
     * @return value of lastname
     */
    public String getLastname() {
        return lastname;
    }

    /**
     * Sets the lastname
     *
     * @param lastname the value to set
     */

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    /**
     * Gets the email
     *
     * @return value of email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email
     *
     * @param email the value to set
     */

    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the mobile
     *
     * @return value of mobile
     */
    public String getMobile() {
        return mobile;
    }

    /**
     * Sets the mobile
     *
     * @param mobile the value to set
     */

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    /**
     * Gets the password
     *
     * @return value of password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password
     *
     * @param password the value to set
     */

    public void setPassword(String password) {
        this.password = password;
    }
}
