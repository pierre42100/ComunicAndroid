package org.communiquons.android.comunic.client.data.posts;

/**
 * Post users access enum
 *
 * @author Pierre HUBERT
 * Created by pierre on 3/25/18.
 */

public enum PostUserAccess {

    /**
     * No access to the post
     */
    NO_ACCESS,

    /**
     * Basic access to the post
     * > Can see the post, put comments within it, like it...
     */
    BASIC_ACCESS,

    /**
     * Intermediate access to the post
     * > Can delete the post
     */
    INTERMEDIATE_ACCESS,

    /**
     * Full access to the post
     */
    FULL_ACCESS

}
