package org.communiquons.android.comunic.client.data.models;

import org.communiquons.android.comunic.client.data.arrays.SurveyChoicesList;

import java.util.ArrayList;

/**
 * Base survey object
 *
 * @author Pierre HUBERT
 */
public class Survey {

    //Private fields
    private int id;
    private int userID;
    private int postID;
    private int create_time;
    private String question;
    private int user_choice;
    private SurveyChoicesList choices = new SurveyChoicesList();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public int getPostID() {
        return postID;
    }

    public void setPostID(int postID) {
        this.postID = postID;
    }

    public int getCreate_time() {
        return create_time;
    }

    public void setCreate_time(int create_time) {
        this.create_time = create_time;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public int getUser_choice() {
        return user_choice;
    }

    public boolean hasUserResponded(){
        return user_choice > 0;
    }

    public void setUser_choice(int user_choice) {
        this.user_choice = user_choice;
    }

    public SurveyChoicesList getChoices() {
        return choices;
    }

    public void addChoice(SurveyChoice choice){
        if(this.choices == null)
            this.choices = new SurveyChoicesList();
        choices.add(choice);
    }

    public void setChoices(SurveyChoicesList choices) {
        this.choices = choices;
    }
}
