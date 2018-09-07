package org.communiquons.android.comunic.client.ui.views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import org.communiquons.android.comunic.client.R;
import org.communiquons.android.comunic.client.data.arrays.SurveyChoicesList;
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
     * Adapter for select dropdown
     */
    private SurveyChoicesAdapter mSpinnerAdapter;

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

        mSpinnerAdapter = new SurveyChoicesAdapter(getContext());
        mResponsesSpinner.setAdapter(mSpinnerAdapter);
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

        mCancelButton.setEnabled(true);
        mResponseButton.setEnabled(true);

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
        mSpinnerAdapter.setList(mSurvey.getChoices());
    }

    @Override
    public void onClick(View v) {

        v.setEnabled(false);

        //Cancel response to survey
        if(v.equals(mCancelButton)){
            if(mOnSurveyUpdateListener != null)
                mOnSurveyUpdateListener.onCancelSurveyResponse(mSurvey);
        }

        //Respond to a survey
        if(v.equals(mResponseButton)){

            int choiceID = mSpinnerAdapter.getChoice(mResponsesSpinner.getSelectedItemPosition())
                    .getChoiceID();

            if(mOnSurveyUpdateListener != null)
                mOnSurveyUpdateListener.onRespondToSurvey(mSurvey, choiceID);

        }

    }


    /**
     * Adapter for the spinner
     *
     * @author Pierre HUBERT
     */
    private static class SurveyChoicesAdapter extends ArrayAdapter<CharSequence> {

        private SurveyChoicesList mList;

        SurveyChoicesAdapter(@NonNull Context context) {
            super(context, android.R.layout.simple_spinner_item);
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        }

        /**
         * Set a new list of choices
         *
         * @param list The list of choices
         */
        void setList(@NonNull SurveyChoicesList list){
            mList = list;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {

            if(mList == null)
                return 0;

            return mList.size();
        }

        @Nullable
        @Override
        public CharSequence getItem(int position) {
            return mList.get(position).getName();
        }

        /**
         * Get a choice from the list at a specified position
         *
         * @param pos The position of the item to select
         * @return The SurveyChoice
         */
        SurveyChoice getChoice(int pos) {
            return mList.get(pos);
        }
    }
}
