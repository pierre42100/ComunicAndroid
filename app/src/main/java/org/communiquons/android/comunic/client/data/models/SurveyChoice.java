package org.communiquons.android.comunic.client.data.models;

/**
 * Survey choice base model
 *
 * @author Pierre HUBERT
 */
public class SurveyChoice {

    //Private field
    private int choiceID;
    private String name;
    private int responses;


    public int getChoiceID() {
        return choiceID;
    }

    public void setChoiceID(int choiceID) {
        this.choiceID = choiceID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getResponses() {
        return responses;
    }

    public void addOneResponse(){
        responses++;
    }

    public void removeOneResponse(){
        responses--;
    }

    public void setResponses(int responses) {
        this.responses = responses;
    }
}
