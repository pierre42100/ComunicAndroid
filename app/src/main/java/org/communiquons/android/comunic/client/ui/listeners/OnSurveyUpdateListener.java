package org.communiquons.android.comunic.client.ui.listeners;

import org.communiquons.android.comunic.client.data.models.Survey;

/**
 * Actions done when there is the need to perform an update on a survey
 *
 * @author Pierre HUBERT
 */
public interface OnSurveyUpdateListener {

    /**
     * This method is called when the user requests to cancel the response to a survey
     *
     * @param survey The target survey
     */
    void onCancelSurveyResponse(Survey survey);

    /**
     * This method is called when the user want to respond to survey
     *
     * @param survey The target survey
     * @param choiceID Selected choice by the user
     */
    void onRespondToSurvey(Survey survey, int choiceID);
}
