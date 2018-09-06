package org.communiquons.android.comunic.client.data.helpers;

import android.content.Context;

import org.communiquons.android.comunic.client.data.models.APIRequest;
import org.communiquons.android.comunic.client.data.models.APIResponse;
import org.communiquons.android.comunic.client.data.models.Survey;
import org.communiquons.android.comunic.client.data.models.SurveyChoice;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

/**
 * Survey helper
 *
 * @author Pierre HUBERT
 */
public class SurveyHelper extends BaseHelper {

    public SurveyHelper(Context context) {
        super(context);
    }

    /**
     * Cancel the response to a survey
     *
     * @param postID The ID of the target post
     * @return TRUE in case of success / FALSE else
     */
    public boolean cancelResponse(int postID){
        APIRequest request = new APIRequest(getContext(), "surveys/cancel_response");
        request.addInt("postID", postID);

        try {
            return new APIRequestHelper().exec(request).getResponse_code() == 200;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Turn an API JSON object into a Survey
     *
     * @param object The object to parse
     * @return Generated Survey
     * @throws JSONException In case of failure
     */
    public static Survey APIToSurvey(JSONObject object) throws JSONException {
        Survey survey = new Survey();
        survey.setId(object.getInt("ID"));
        survey.setUserID(object.getInt("userID"));
        survey.setPostID(object.getInt("postID"));
        survey.setCreate_time(object.getInt("creation_time"));
        survey.setQuestion(object.getString("question"));
        survey.setUser_choice(object.getInt("user_choice"));

        JSONObject choicesObject = object.getJSONObject("choices");
        for (Iterator<String> it = choicesObject.keys(); it.hasNext(); ) {
            String key = it.next();
            survey.addChoice(APIToSurveyChoice(choicesObject.getJSONObject(key)));
        }

        return survey;
    }


    /**
     * Turn an API JSON object into a SurveyChoice object
     *
     * @param object The object to convert
     * @return Generated SurveyChoice object
     * @throws JSONException in case of failure
     */
    private static SurveyChoice APIToSurveyChoice(JSONObject object) throws JSONException {
        SurveyChoice surveyChoice = new SurveyChoice();
        surveyChoice.setChoiceID(object.getInt("choiceID"));
        surveyChoice.setName(object.getString("name"));
        surveyChoice.setResponses(object.getInt("responses"));
        return surveyChoice;
    }
}
