package org.communiquons.android.comunic.client.data.helpers;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.ArrayMap;

import org.communiquons.android.comunic.client.data.enums.KindSearchResult;
import org.communiquons.android.comunic.client.data.models.APIRequest;
import org.communiquons.android.comunic.client.data.models.APIResponse;
import org.communiquons.android.comunic.client.data.models.GroupInfo;
import org.communiquons.android.comunic.client.data.models.SearchResult;
import org.communiquons.android.comunic.client.data.models.SearchResultWithInfo;
import org.communiquons.android.comunic.client.data.models.UserInfo;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Search helper
 *
 * This helper is used to search users and groups
 *
 * @author Pierre HUBERT
 */
public class SearchHelper extends BaseHelper {

    public SearchHelper(Context context) {
        super(context);
    }

    /**
     * Perform a global search in the database
     *
     * @param query The query to search for
     * @return The list of results / null in case of failure
     */
    @Nullable
    public ArrayList<SearchResult> global(String query) {

        //Prepare request
        APIRequest request = new APIRequest(getContext(), "search/global");
        request.addString("query", query);

        //Execute request
        try {
            APIResponse response = request.exec();

            if(response.getResponse_code() != 200)
                return null;

            //Get JSON data
            JSONArray data = response.getJSONArray();
            if(data == null)
                return null;

            //Parse and return results
            ArrayList<SearchResult> results = new ArrayList<>();
            for(int i = 0; i < data.length(); i++)
                results.add(JSONObjectToSearchResult(data.getJSONObject(i)));
            return results;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }


    /**
     * Fill search result with information for users, groups...
     *
     * @param list The list of results to process
     * @return Information for groups, users / null in case of failure
     */
    @Nullable
    public ArrayList<SearchResultWithInfo> fillResults(@NonNull ArrayList<SearchResult> list){

        ArrayList<SearchResultWithInfo> listWithInfo = new ArrayList<>();

        //Extract the ids of the users and the groups to get
        ArrayList<Integer> usersID = new ArrayList<>();
        ArrayList<Integer> groupsID = new ArrayList<>();

        for(SearchResult result : list){
            if(result.getKind() == KindSearchResult.USER)
                usersID.add(result.getId());

            else if(result.getKind() == KindSearchResult.GROUP)
                groupsID.add(result.getId());

            SearchResultWithInfo resultWithInfo = new SearchResultWithInfo();
            result.copyTo(resultWithInfo);
            listWithInfo.add(resultWithInfo);
        }

        //Get information about users (if any)
        if(usersID.size() > 0){

            ArrayMap<Integer, UserInfo> usersInfo =  new GetUsersHelper(getContext())
                    .getMultiple(usersID);

            if(usersInfo == null)
                return null;

            //Process the list of result to apply information
            for(SearchResultWithInfo result : listWithInfo){

                //Skip non related entries
                if(result.getKind() != KindSearchResult.USER)
                    continue;

                result.setUserInfo(usersInfo.get(result.getId()));
            }

        }

        //Get information about groups (if any)
        if(groupsID.size() > 0){

            ArrayMap<Integer, GroupInfo> groupsInfo =  new GroupsHelper(getContext())
                    .getInfoMultiple(groupsID);

            if(groupsInfo == null)
                return null;

            //Process the list of result to apply information
            for(SearchResultWithInfo result : listWithInfo){

                //Skip non related entries
                if(result.getKind() != KindSearchResult.GROUP)
                    continue;

                result.setGroupInfo(groupsInfo.get(result.getId()));
            }

        }


        return listWithInfo;
    }

    /**
     * Turn a string into a search kind result
     *
     * @param string  The string to convert
     * @return Matching search result
     */
    private static KindSearchResult StringToKindSearchResult(String string){
        switch (string){

            case "user":
                return KindSearchResult.USER;

            case "group":
                return KindSearchResult.GROUP;

            default:
                return KindSearchResult.UNKNOWN;
        }
    }


    /**
     * Turn a JSONObject into a SearchResult object
     *
     * @param object The object to convert
     * @return Generated SearchResult object
     * @throws JSONException This exception is thrown in case of failure
     */
    private static SearchResult JSONObjectToSearchResult(JSONObject object) throws JSONException {
        SearchResult result = new SearchResult();

        result.setId(object.getInt("id"));
        result.setKind(StringToKindSearchResult(object.getString("kind")));

        return result;
    }
}
