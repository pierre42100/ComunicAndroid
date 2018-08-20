package org.communiquons.android.comunic.client.data.enums;

/**
 * Create account results
 *
 * @author Pierre HUBERT
 */
public enum CreateAccountResult {

    /**
     * The account was successfully created
     */
    SUCCESS,

    /**
     * Too many account creation requests
     */
    ERROR_TOO_MANY_REQUESTS,

    /**
     * Trying to login with an existing email address
     */
    ERROR_EXISTING_EMAIL,

    /**
     * Unspecified error occurred
     */
    ERROR


}
