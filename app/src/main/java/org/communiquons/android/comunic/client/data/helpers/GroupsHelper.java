package org.communiquons.android.comunic.client.data.helpers;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.ArrayMap;

import org.communiquons.android.comunic.client.data.enums.GroupPostsCreationLevel;
import org.communiquons.android.comunic.client.data.enums.GroupRegistrationLevel;
import org.communiquons.android.comunic.client.data.enums.GroupVisibility;
import org.communiquons.android.comunic.client.data.enums.GroupsMembershipLevels;
import org.communiquons.android.comunic.client.data.models.APIRequest;
import org.communiquons.android.comunic.client.data.models.APIResponse;
import org.communiquons.android.comunic.client.data.models.AdvancedGroupInfo;
import org.communiquons.android.comunic.client.data.models.GroupInfo;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Groups helper
 */
public class GroupsHelper extends BaseHelper {

    /**
     * Debug tag
     */
    private static final String TAG = GroupsHelper.class.getSimpleName();

    /**
     * Groups information cache
     */
    private static ArrayMap<Integer, GroupInfo> mInfoCache = new ArrayMap<>();

    /**
     * Groups constructor
     *
     * @param context The context of the application
     */
    public GroupsHelper(Context context) {
        super(context);
    }

    /**
     * Get the list of groups of the user
     *
     * @return The list of groups of the user / null in case of failure
     */
    @Nullable
    public ArrayList<Integer> getUserList(){
        APIRequest request = new APIRequest(getContext(), "groups/get_my_list");

        try {
            APIResponse response = new APIRequestHelper().exec(request);
            if(response.getResponse_code() != 200) return null;

            JSONArray array = response.getJSONArray();
            ArrayList<Integer> list = new ArrayList<>();
            for (int i = 0; i < array.length(); i++)
                list.add(array.getInt(i));
            return list;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * Get information about a single group
     *
     * @param groupID Target group ID
     * @param force Specify whether the request should be forced or not
     * @return null in case of failure / Group information else
     */
    @Nullable
    public GroupInfo getInfoSingle(int groupID, boolean force){

        //Prepare request
        ArrayList<Integer> ids = new ArrayList<>();
        ids.add(groupID);

        //Execute request
        ArrayMap<Integer, GroupInfo> list = getInfoMultiple(ids, force);
        if(list == null || !list.containsKey(groupID))
            return null;

        return list.get(groupID);
    }

    /**
     * Get information about multiple groups
     *
     * @param IDs The ID of the target groups
     * @return Information about the related groups
     */
    @Nullable
    public ArrayMap<Integer, GroupInfo> getInfoMultiple(ArrayList<Integer> IDs){
        return getInfoMultiple(IDs, false);
    }

    /**
     * Get information about multiple groups
     *
     * @param IDs The ID of the target groups
     * @param force Specify whether the request has to be forced or not (if set to true, the cache
     *              will be ignored)
     * @return Information about the related groups
     */
    @Nullable
    public ArrayMap<Integer, GroupInfo> getInfoMultiple(ArrayList<Integer> IDs, boolean force){

        //Process each group to check if its information are available in the cache or not
        ArrayList<Integer> toGet = new ArrayList<>();

        for(int id : IDs){
            if(!mInfoCache.containsKey(id) || force)
                toGet.add(id);
        }

        if(toGet.size() > 0){

            ArrayMap<Integer, GroupInfo> downloaded = downloadMultiple(toGet);

            if(downloaded == null)
                return null; // Can not get information about all the groups

            for(int id : toGet)
                mInfoCache.put(id, downloaded.get(id));
        }

        //Extract groups information from cache
        ArrayMap<Integer, GroupInfo> list = new ArrayMap<>();
        for(int id : IDs)
            list.put(id, mInfoCache.get(id));
        return list;
    }

    /**
     * Download information of multiple groups from the server
     *
     * @param IDs The IDs of the groups to get
     * @return Information about the groups / null in case of failure
     */
    @Nullable
    private ArrayMap<Integer, GroupInfo> downloadMultiple(ArrayList<Integer> IDs){

        //Make a request over the server
        APIRequest request = new APIRequest(getContext(), "groups/get_multiple_info");

        //Process the list of groups to get
        StringBuilder reqList = new StringBuilder();
        for(Integer id : IDs) {
            reqList.append(id);
            reqList.append(",");
        }
        request.addString("list", reqList.toString());


        try {

            APIResponse response = new APIRequestHelper().exec(request);
            JSONObject object = response.getJSONObject();
            ArrayMap<Integer, GroupInfo> list = new ArrayMap<>();

            if(object == null) return null;

            //Parse the list of keys
            for(Integer id : IDs){

                //Get raw information about the group
                JSONObject info = object.getJSONObject(id + "");

                //Check if we did not get anything
                if(info == null)
                    return null;

                list.put(id, parse_group_info(info));
            }

            return list;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get advanced information about a group
     *
     * @param groupID Target group to get information about
     * @return Information about the group / null in case of failure
     */
    @Nullable
    public AdvancedGroupInfo getAdvancedInformation(int groupID){
        APIRequest request = new APIRequest(getContext(), "groups/get_advanced_info");
        request.addInt("id", groupID);
        request.setTryContinueOnError(true);

        try {

            //Execute request and get result
            APIResponse response = new APIRequestHelper().exec(request);

            if (response.getResponse_code() != 200) {
                if(response.getResponse_code() == 401){
                    //Access was explicitly denied to the group
                    AdvancedGroupInfo groupInfo = new AdvancedGroupInfo();
                    groupInfo.setAccess_forbidden(true);
                    return groupInfo;
                }

                //Could not get group information
                return null;
            }

            //Parse and return result
            JSONObject object = response.getJSONObject();
            return parse_advanced_group_info(object);

        } catch (Exception e) {

            //Could not execute request
            e.printStackTrace();
            return null;

        }
    }

    /**
     * Send a group membership request
     *
     * @param groupID The ID of the target group
     * @return Depends of the success of the operation
     */
    public boolean sendRequest(int groupID){
        APIRequest request = new APIRequest(getContext(), "groups/send_request");
        request.addInt("id", groupID);
        return performMembershipUpdate(request);
    }

    /**
     * Cancel a group membership request
     *
     * @param groupID the ID of the target group
     * @return TRUE in case of success / FALSE else
     */
    public boolean cancelRequest(int groupID){
        APIRequest request = new APIRequest(getContext(), "groups/cancel_request");
        request.addInt("id", groupID);
        return performMembershipUpdate(request);
    }

    /**
     * Respond to a group membership invitation
     *
     * @param groupID The ID of the target group
     * @param accept TRUE to accept invitation / FALSE else
     */
    public boolean respondInvitation(int groupID, boolean accept){
        APIRequest request = new APIRequest(getContext(), "groups/respond_invitation");
        request.addInt("id", groupID);
        request.addBoolean("accept", accept);
        return performMembershipUpdate(request);
    }

    /**
     * Leave a group
     *
     * @param groupID The ID of the group to leave
     * @return TRUE for a success / FALSE else
     */
    public boolean leaveGroup(int groupID){
        APIRequest request = new APIRequest(getContext(), "groups/remove_membership");
        request.addInt("id", groupID);
        return performMembershipUpdate(request);
    }

    /**
     * Perform on the server a membership update
     *
     * @param request The request to perform
     * @return Depends of the success of the operation
     */
    private boolean performMembershipUpdate(APIRequest request){
        try {
            return new APIRequestHelper().exec(request).getResponse_code() == 200;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Overloaded method (see below)
     */
    private GroupInfo parse_group_info(@NonNull JSONObject object) throws JSONException {
        return parse_group_info(object, new GroupInfo());
    }

    /**
     * Parse group information into GroupInfo object
     *
     * @param object The object to parse
     * @param group Group object to complete with group information
     * @return Generated group object
     * @throws JSONException In case of failure
     */
    private GroupInfo parse_group_info(@NonNull JSONObject object, GroupInfo group) throws JSONException {

        group.setId(object.getInt("id"));
        group.setName(object.getString("name"));
        group.setIcon_url(object.getString("icon_url"));
        group.setNumber_members(object.getInt("number_members"));
        group.setMembershipLevel(parse_membership_level(object.getString("membership")));
        group.setVisibility(parse_group_visibility(object.getString("visibility")));
        group.setRegistrationLevel(parse_group_registration_level(object.getString("registration_level")));
        group.setPostCreationLevel(parse_post_creation_level(object.getString("posts_level")));
        group.setVirtualDirectory(object.getString("virtual_directory"));
        group.setFollowing(object.getBoolean("following"));

        return group;

    }

    /**
     * Parse advanced group information into AdvancedGroupInfo object
     *
     * @param object The object to parse
     * @return Generated group information object
     * @throws JSONException In case of failure
     */
    private AdvancedGroupInfo parse_advanced_group_info(@NonNull JSONObject object) throws JSONException {

        AdvancedGroupInfo group = new AdvancedGroupInfo();

        //Parse basic group information
        parse_group_info(object, group);

        group.setTime_create(object.getInt("time_create"));
        group.setUrl(object.getString("url"));
        group.setDescription(object.getString("description"));
        group.setNumber_likes(object.getInt("number_likes"));
        group.setIs_liking(object.getBoolean("is_liking"));

        return group;
    }

    /**
     * Parse membership level
     *
     * @param level Membership level to parse as string
     * @return Equivalent membership level
     */
    private GroupsMembershipLevels parse_membership_level(String level){
        switch (level){

            case "administrator":
                return GroupsMembershipLevels.ADMINISTRATOR;

            case "moderator":
                return GroupsMembershipLevels.MODERATOR;

            case "member":
                return GroupsMembershipLevels.MEMBER;

            case "invited":
                return GroupsMembershipLevels.INVITED;

            case "pending":
                return GroupsMembershipLevels.PENDING;

            case "visitor":
                return GroupsMembershipLevels.VISITOR;

            default:
                throw new RuntimeException("Unsupported membership level: " + level);
        }
    }

    /**
     * Parse group visibility level
     *
     * @param level The level to parse
     * @return Equivalent visibility level
     */
    private GroupVisibility parse_group_visibility(String level){
        switch (level){

            case "open":
                return GroupVisibility.OPEN;

            case "private":
                return GroupVisibility.PRIVATE;

            case "secrete":
                return GroupVisibility.SECRETE;

            default:
                throw new RuntimeException("Unsupported group visibility level: " + level);
        }
    }

    /**
     * Parse group registration level
     *
     * @param level The level to parse
     * @return Equivalent registration level
     */
    private GroupRegistrationLevel parse_group_registration_level(String level){
        switch (level){

            case "open":
                return GroupRegistrationLevel.OPEN;

            case "moderated":
                return GroupRegistrationLevel.MODERATED;

            case "closed":
                return GroupRegistrationLevel.CLOSED;

            default:
                throw new RuntimeException("Unsupported group registration level: " + level);
        }
    }

    /**
     * Parse post creation level
     *
     * @param level The level to parse
     * @return Equivalent post creation post level
     */
    private GroupPostsCreationLevel parse_post_creation_level(String level){
        switch (level){

            case "moderators":
                return GroupPostsCreationLevel.MODERATORS;

            case "members":
                return GroupPostsCreationLevel.MEMBERS;


            default:
                throw new RuntimeException("Unsupported group post creation level: " + level);
        }
    }
}
