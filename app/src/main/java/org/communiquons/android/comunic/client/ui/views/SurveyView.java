package org.communiquons.android.comunic.client.ui.views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.communiquons.android.comunic.client.R;
import org.communiquons.android.comunic.client.data.models.Survey;
import org.communiquons.android.comunic.client.data.models.SurveyChoice;
import org.communiquons.android.comunic.client.ui.listeners.OnSurveyUpdateListener;
import org.communiquons.android.comunic.client.ui.utils.UiUtils;

public class SurveyView extends BaseFrameLayoutView implements View.OnClickListener {

    /**
     * Current survey
     */
    private Survey mSurvey;

    /**
     * Update listener
     */
    private OnSurveyUpdateListener mOnSurveyUpdateListener;

    /**
     * Views
     */
    private TextView mQuestion;
    private LinearLayout mChoicesList;
    private ConstraintLayout mCancelSurveyResponseForm;
    private TextView mSelectedChoiceView;
    private Button mCancelButton;
    private ConstraintLayout mSendResponseForm;
    private Spinner mResponsesSpinner;
    private Button mResponseButton;


    public SurveyView(@NonNull Context context) {
        this(context, null);
    }

    public SurveyView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SurveyView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        View view = inflate(getContext(), R.layout.view_survey, this);
        mQuestion = view.findViewById(R.id.question);
        mCancelSurveyResponseForm = view.findViewById(R.id.cancelSurveyResponseForm);

        mChoicesList = view.findViewById(R.id.choicesTarget);
        mSelectedChoiceView = view.findViewById(R.id.selectedChoiceView);
        mCancelButton = view.findViewById(R.id.cancelButton);

        mSendResponseForm = view.findViewById(R.id.sendResponseForm);
        mResponsesSpinner = view.findViewById(R.id.responsesSpinner);
        mResponseButton = view.findViewById(R.id.respondButton);

        mCancelButton.setOnClickListener(this);
        mResponseButton.setOnClickListener(this);
    }

    /**
     * Set the survey updates listener
     *
     * @param onSurveyUpdateListener The new listener
     */
    public void setOnSurveyUpdateListener(@Nullable OnSurveyUpdateListener onSurveyUpdateListener) {
        this.mOnSurveyUpdateListener = onSurveyUpdateListener;
    }

    /**
     * Change the current survey
     *
     * @param survey The new survey to display
     */
    public void setSurvey(Survey survey){
        this.mSurvey = survey;

        mChoicesList.removeAllViews();

        mQuestion.setText(survey.getQuestion());

        //Process the list of choices
        for(SurveyChoice choice : survey.getChoices()){

            TextView textView = new TextView(getContext());
            textView.setText(UiUtils.getString(getContext(), R.string.survey_choice,
                    choice.getResponses(), choice.getName()));
            mChoicesList.addView(textView, new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        }

        mCancelSurveyResponseForm.setVisibility(survey.hasUserResponded() ?
                View.VISIBLE : View.GONE);
        mSendResponseForm.setVisibility(survey.hasUserResponded() ?
                View.GONE : View.VISIBLE);

        //Render the right part of the view
        if (survey.hasUserResponded()) {
            renderCancelResponseForm();
        } else {
            renderRespondForm();
        }
    }

    /**
     * Render cancel response form
     */
    private void renderCancelResponseForm(){

        //Display the choice of the user
        mSelectedChoiceView.setText(UiUtils.getString(getContext(),
                R.string.survey_your_choice,
                mSurvey.getChoices().find(mSurvey.getUser_choice()).getName()));


    }

    /**
     * Render respond form
     */
    private void renderRespondForm(){

    }

    @Override
    public void onClick(View v) {

        //Cancel response to survey
        if(v.equals(mCancelButton)){
            if(mOnSurveyUpdateListener != null)
                mOnSurveyUpdateListener.onCancelSurveyResponse(mSurvey);
        }

        //Respond to a survey
        if(v.equals(mResponseButton)){
            if(mOnSurveyUpdateListener != null)
                mOnSurveyUpdateListener.onRespondToSurvey(mSurvey, -1);
            //TODO : implement
        }

    }


}
