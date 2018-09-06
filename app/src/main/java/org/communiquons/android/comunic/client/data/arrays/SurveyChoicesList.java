package org.communiquons.android.comunic.client.data.arrays;

import org.communiquons.android.comunic.client.data.models.SurveyChoice;

import java.util.ArrayList;

/**
 * Handles a list of SurveyChoicesList
 *
 * @author Pierre HUBERT
 */
public class SurveyChoicesList extends ArrayList<SurveyChoice> {

    /**
     * Find a choice specified by its ID in the list
     *
     * @param id The ID of the choice to find
     * @return Matching choice
     */
    public SurveyChoice find(int id) {

        for(SurveyChoice choice : this)
            if(choice.getChoiceID() == id)
                return choice;

        throw new AssertionError();
    }
}
