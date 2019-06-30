package ch.ghwalin.ldap.model;
/**
 * a group the users belong to
 * <p>
 * LDAP Profile
 *
 * @author Marcel Suter
 * @version 1.0
 * @since 2019-03-04
 */
public class Group {
    private String groupId;
    private String groupName;

    /**
     * Gets the groupId
     *
     * @return value of groupId
     */
    public String getGroupId() {
        return groupId;
    }

    /**
     * Sets the groupId
     *
     * @param groupId the value to set
     */

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    /**
     * Gets the groupName
     *
     * @return value of groupName
     */
    public String getGroupName() {
        return groupName;
    }

    /**
     * Sets the groupName
     *
     * @param groupName the value to set
     */

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
