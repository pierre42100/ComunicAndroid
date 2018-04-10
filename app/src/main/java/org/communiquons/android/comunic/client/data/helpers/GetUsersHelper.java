package org.communiquons.android.comunic.client.data.helpers;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.ArrayMap;
import android.util.Log;

import org.communiquons.android.comunic.client.api.APIRequest;
import org.communiquons.android.comunic.client.api.APIRequestParameters;
import org.communiquons.android.comunic.client.api.APIResponse;
import org.communiquons.android.comunic.client.data.models.AdvancedUserInfo;
import org.communiquons.android.comunic.client.data.models.UserInfo;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Get information about the users
 *
 * @author Pierre HUBERT
 * Created by pierre on 12/10/17.
 */

public class GetUsersHelper {

    /**
     * Debug tag
     */
    private final String TAG = "GetUsersHelper";

    /**
     * The context of the application
     */
    private Context mContext;

    /**
     * User information database helper
     */
    private UsersInfosDbHelper udbHelper = null;

    /**
     * Public constructor of the class
     *
     * @param context The context of the application
     */
    public GetUsersHelper(@NonNull Context context){
        this(context, DatabaseHelper.getInstance(context));
    }

    /**
     * Public constructor of the class
     *
     * @param context The context of execution of the application
     * @param udbHelper User database helper
     */
    public GetUsersHelper(@NonNull Context context, @NonNull UsersInfosDbHelper udbHelper){
        mContext = context;
        this.udbHelper = udbHelper;
    }

    /**
     * Public constructor of the class
     *
     * @param context The context of execution of the application
     * @param dbHelper Databasehelpepr
     */
    public GetUsersHelper(@NonNull Context context, @NonNull DatabaseHelper dbHelper){
        mContext = context;
        this.udbHelper = new UsersInfosDbHelper(dbHelper);
    }

    /**
     * Get information about a single user from the server
     *
     * @param id The ID of the user to get informations on
     * @param force Force the informations to be fetched from the server
     * @return User information or null in case of failure
     */
    @Nullable
    public UserInfo getSingle(int id, boolean force){

        //Check, if we are allowed, if the user isn't already in the local database
        if(!force){
            if(udbHelper.exists(id))
                return udbHelper.get(id);
        }

        //Else fetch user information from server
        ArrayList<Integer> IDs = new ArrayList<>();
        IDs.add(id);

        ArrayMap<Integer, UserInfo> result = getMultipleOnServer(IDs);

        if(result != null) {
            if (result.containsKey(id)) {

                UserInfo infos = result.get(id);

                if (infos != null) {

                    //Add the user to the database
                    udbHelper.insertOrUpdate(infos);

                    //Return user informations
                    return infos;

                }

            }
        }

        //If we got there, an error occurred
        Log.e(TAG, "Couldn't get information about a single user !");
        return null;

    }

    /**
     * Get advanced informations about a single user
     *
     * @param userID The user to get information about
     * @return Informations about the user / null in case of failure
     */
    @Nullable
    public AdvancedUserInfo get_advanced_infos(int userID){

        //Perform an API request
        APIRequestParameters params = new APIRequestParameters(mContext,
                "user/getAdvancedUserInfos");
        params.addInt("userID", userID);

        //Perform the request
        try {
            APIResponse response = new APIRequest().exec(params);

            //Parse user informations
            return parse_advanced_user_json(response.getJSONObject());

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * Get the list of missing users ID in a set of users information
     *
     * @param IDs The reference IDs list
     * @param usersInfo Information about the users
     * @return The list of missing IDs
     */
    public static ArrayList<Integer> get_missing_ids(@NonNull ArrayList<Integer> IDs,
                                                     @NonNull ArrayMap<Integer, UserInfo> usersInfo){
        ArrayList<Integer> missingIds = new ArrayList<>();

        //Process the list of IDs
        for(int user_id : IDs){
            if(!usersInfo.containsKey(user_id))
                missingIds.add(user_id);
        }

        return missingIds;
    }

    /**
     * Get information about multiple users from the database or from the server
     *
     * @param IDs The ID of teh users to get
     * @return users information / null in case of failure
     */
    @Nullable
    public ArrayMap<Integer, UserInfo> getMultiple(ArrayList<Integer> IDs){

        ArrayMap<Integer, UserInfo> usersInfo = new ArrayMap<>();
        ArrayList<Integer> usersToDownload = new ArrayList<>();

        //Check which users are available offline
        for(Integer id : IDs){
            if(udbHelper.exists(id)){
                usersInfo.put(id, udbHelper.get(id));
            }
            else {
                usersToDownload.add(id);
            }
        }

        //If required, get users online
        if(usersToDownload.size() > 0){
            ArrayMap<Integer, UserInfo> download = getMultipleOnServer(usersToDownload);

            if(download != null){

                //Process the list
                for(int id : usersToDownload){

                    if(download.containsKey(id)){
                        UserInfo info = download.get(id);
                        if(info != null) {
                            usersInfo.put(id, info);
                            udbHelper.insertOrUpdate(info);
                        }
                    }

                }
            }
        }



        return usersInfo;
    }

    /**
     * Search for user from online source
     *
     * @param query The query string
     * @param limit The maximum number of results
     * @return A list of users / false in case of failure
     */
    @Nullable
    public ArrayMap<Integer, UserInfo> search_users(String query, int limit){

        //Fetch users online
        ArrayList<Integer> usersID = search_users_online(query, limit);

        //Check for errors
        if(usersID == null)
            return null;

        return getMultiple(usersID);

    }

    /**
     * Search for users on the API
     *
     * @param query The query of the research
     * @param limit The maximum number of results
     * @return The ID of the corresponding users / false in case of failure
     */
    @Nullable
    private ArrayList<Integer> search_users_online(String query, int limit){

        //Make an API request
        APIRequestParameters params = new APIRequestParameters(mContext, "search/user");
        params.addString("query", query);
        params.addString("searchLimit", ""+limit);

        try {

            //Get and extract the response
            APIResponse response = new APIRequest().exec(params);
            JSONArray array = response.getJSONArray();

            //Make response
            ArrayList<Integer> IDs = new ArrayList<>();
            for(int i = 0; i < array.length(); i++){
                IDs.add(array.getInt(i));
            }
            return IDs;

        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get and return the information about multiple users from the server
     *
     * @param IDs The ID of the user to get information
     * @return Information about teh user / null in case of failure
     */
    @Nullable
    private ArrayMap<Integer, UserInfo> getMultipleOnServer(ArrayList<Integer> IDs){

        ArrayMap<Integer, UserInfo> uInfos = new ArrayMap<>();

        //Perform a request on the API server
        //Setup the request
        APIRequestParameters requestParameters = new APIRequestParameters(mContext,
                "user/getInfosMultiple");


        //Convert the IDs into a string
        String id_string = "";
        for(int id : IDs) {
            id_string += id + ",";
        }
        requestParameters.addString("usersID", id_string);


        try {

            //Perform the request
            APIResponse result = new APIRequest().exec(requestParameters);

            if(result != null) {

                //Try to extract user information
                JSONObject userObjectContainer = result.getJSONObject();

                if (userObjectContainer != null) {

                    //Process each user ID
                    for(int userID : IDs) {

                        UserInfo userInfos;

                        //Extract user object
                        JSONObject userObject = userObjectContainer.getJSONObject(""+userID);

                        //Continue only if we could extract required information
                        if (userObject != null) {
                            //Parse user information
                            userInfos = parse_user_json(userObject);

                            //Add the user to the list
                            uInfos.put(userID, userInfos);
                        }

                    }
                }
                else {
                    Log.e(TAG, "Couldn't parse response from server !");
                    return null;
                }

            }
            else
                return null;

        } catch (Exception e){
            Log.e(TAG, "Couldn't get user information from server !");
            e.printStackTrace();
            return null;
        }

        return uInfos;
    }

    /**
     * Parse a JSON object into a user object
     *
     * @param userObject Informations about the user in an object
     * @return User object in case of success, null else
     */
    @Nullable
    private UserInfo parse_user_json(JSONObject userObject){

        //Check if we got a null response
        if(userObject == null)
            return null;

        //Parse basic user infos
        return parse_base_user_json(new UserInfo(), userObject);
    }

    /**
     * Parse advanced user informations into an object
     *
     * @param userObject Source JSON object
     * @return The parse JSON object or null in case of failure
     */
    @Nullable
    private AdvancedUserInfo parse_advanced_user_json(JSONObject userObject){

        //Parse basic informations about the user
        AdvancedUserInfo advancedUserInfo = (AdvancedUserInfo)
                parse_base_user_json(new AdvancedUserInfo(), userObject);

        //Check for errors
        if(advancedUserInfo == null)
            return null;

        //Parse advanced user informations
        try {

            //Get account creation time
            advancedUserInfo.setAccount_creation_time(userObject.getInt("account_creation_time"));


        } catch (JSONException e){
            e.printStackTrace();
            return null;
        }

        //Return result
        return advancedUserInfo;
    }

    /**
     * Parse user basic informations into a user object
     *
     * @param userInfos The user informations object to fill
     * @param userObject The source JSON object
     * @return The filled user Infos object
     */
    @Nullable
    private UserInfo parse_base_user_json(UserInfo userInfos, JSONObject userObject){

        //Try to retrieve basic user informations
        try {

            //Retrieve all user informations
            userInfos.setId(userObject.getInt("userID"));
            userInfos.setFirstName(userObject.getString("firstName"));
            userInfos.setLastName(userObject.getString("lastName"));
            userInfos.setAccountImageURL(userObject.getString("accountImage"));

        } catch (JSONException e){
            e.printStackTrace();
            return null;
        }

        //Return result
        return userInfos;

    }

    /**
     * This interface is implemented on activities that can open users page
     */
    public interface onOpenUsersPageListener {

        /**
         * On a user page specified by its id
         *
         * @param userID The ID of the user to create page
         */
        void openUserPage(int userID);
    }
}
