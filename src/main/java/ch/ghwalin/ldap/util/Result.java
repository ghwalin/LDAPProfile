
package ch.ghwalin.ldap.util;

/**
 * provides services for authentication
 * <p>
 * LDAP Profile
 *
 * @author Marcel Suter
 * @version 1.0
 * @since 2019-03-04
 */
public enum Result {
    SUCCESS(0), // command was successfully executed
    NOACTION(1), // nothing to be done
    TRUNCATE(2), // data truncated
    NODATA(3),   // no data found
    DUPLICATE(4), // duplicate entry in unique field
    ERROR(9); // there was an error => throw exception

    private int code;

    private Result(int code) {
        this.setCode(code);
    }

    /**
     * @return the code
     */
    public int getCode() {
        return code;
    }

    /**
     * @param code
     *            the code to set
     */
    public void setCode(int code) {
        this.code = code;
    }

}
