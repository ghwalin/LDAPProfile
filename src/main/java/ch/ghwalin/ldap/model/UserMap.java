package ch.ghwalin.ldap.model;

import ch.ghwalin.ldap.data.OpenLDAP;
import com.unboundid.ldap.sdk.Filter;
import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.ldap.sdk.SearchResultEntry;

import java.util.HashMap;
import java.util.Map;

/**
 * a map of user profiles
 * <p>
 * LDAP Profile
 *
 * @author Marcel Suter
 * @version 1.0
 * @since 2019-03-04
 */
public class UserMap {
    private Map<String, User> users;

    /**
     * default constructor
     */
    public UserMap() {
        this("");
    }

    public UserMap(String filter) {
        setUsers(new HashMap<>());
        readUsers(filter);
    }

    /**
     * reads all users and applies the filter
     * @param filter
     */
    private void readUsers(String filter) {
        Filter userFilter = Filter.createORFilter(
                Filter.createSubAnyFilter("cn", filter),
                Filter.createSubAnyFilter("sn", filter),
                Filter.createSubAnyFilter("givenname", filter)
        );

        SearchResult searchResult = OpenLDAP.searchUser(userFilter);
        for (int i = 0; i < searchResult.getEntryCount(); i++) {
            SearchResultEntry entry = searchResult.getSearchEntries().get(i);
            User user = new User();
            user.setDistinguishedName(entry.getDN());
            user.setUsername(entry.getAttributeValue("cn"));
            user.setFirstname(entry.getAttributeValue("givenname"));
            user.setLastname(entry.getAttributeValue("sn"));
            user.setEmail(entry.getAttributeValue("mail"));
            user.setMobile(entry.getAttributeValue("mobile"));
            getUsers().put(user.getUsername(), user);
        }
    }

    /**
     * Gets the users
     *
     * @return value of users
     */
    public Map<String, User> getUsers() {
        return users;
    }

    /**
     * Sets the users
     *
     * @param users the value to set
     */

    public void setUsers(Map<String, User> users) {
        this.users = users;
    }
}
