package org.communiquons.android.comunic.client.data.likes;

import android.content.Context;

import org.communiquons.android.comunic.client.api.APIRequest;
import org.communiquons.android.comunic.client.api.APIRequestParameters;
import org.communiquons.android.comunic.client.api.APIResponse;

/**
 * Likes Helper
 *
 * @author Pierre HUBERT
 * Created by pierre on 4/7/18.
 */

public class LikesHelper {

    /**
     * The context of the application
     */
    private Context mContext;

    /**
     * Public constructor of the likes helper class
     *
     * @param context The context of the application
     */
    public LikesHelper(Context context){

        //Save the context
        mContext = context.getApplicationContext();

    }

    /**
     * Update the like other a content
     *
     * @param type The type of the element
     * @param id The ID of the target element
     * @param liking New liking status
     * @return TRUE for a success / FALSE for a failure
     */
    public boolean update(LikesType type, int id, boolean liking){

        //Perform an API request
        APIRequestParameters params = new APIRequestParameters(mContext, "likes/update");
        params.addInt("id", id);
        params.addBoolean("like", liking);

        //Put the kind of element
        switch (type){
            case POST:
                params.addString("type", "post");
                break;

            case COMMENT:
                params.addString("type", "comment");
                break;

            default:
                throw new RuntimeException("Unrecognized kind of post !");
        }

        //Intend to perform the request
        try {
            APIResponse response = new APIRequest().exec(params);

            return response.getResponse_code() == 200;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
