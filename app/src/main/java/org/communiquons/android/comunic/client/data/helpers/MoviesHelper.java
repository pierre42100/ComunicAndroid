package org.communiquons.android.comunic.client.data.helpers;

import android.content.Context;
import android.support.annotation.NonNull;

import org.communiquons.android.comunic.client.data.models.Movie;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Movies helper
 *
 * @author Pierre HUBERT
 */

public class MoviesHelper extends BaseHelper {

    MoviesHelper(Context context) {
        super(context);
    }

    /**
     * Parse a JSON object into a movie object
     *
     * @param object The object to parse
     * @return Parsed movie object
     * @throws JSONException If JSON object could not be correctly read
     */
    Movie parse_json_object(@NonNull JSONObject object) throws JSONException {

        Movie movie = new Movie();

        movie.setId(object.getInt("id"));
        movie.setUrl(object.getString("url"));
        movie.setUserID(object.getInt("userID"));
        movie.setName(object.getString("name"));
        movie.setFileType(object.getString("file_type"));
        movie.setSize(object.getInt("size"));

        return movie;

    }
}
