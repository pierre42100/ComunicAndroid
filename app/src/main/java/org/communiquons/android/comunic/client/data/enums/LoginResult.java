package org.communiquons.android.comunic.client.data.enums;

/**
 * Login result state
 *
 * @author Pierre HUBERT
 */
public enum LoginResult {

    /**
     * Login succeeded
     */
    SUCCESS,

    /**
     * Too many login attempts
     */
    TOO_MANY_ATTEMPTS,

    /**
     * Invalid credentials
     */
    INVALID_CREDENTIALS,

    /**
     * Server error
     */
    SERVER_ERROR
}
