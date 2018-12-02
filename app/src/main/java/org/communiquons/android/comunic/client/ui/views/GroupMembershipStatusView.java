package org.communiquons.android.comunic.client.ui.views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

import org.communiquons.android.comunic.client.R;
import org.communiquons.android.comunic.client.data.enums.GroupRegistrationLevel;
import org.communiquons.android.comunic.client.data.models.GroupInfo;
import org.communiquons.android.comunic.client.ui.listeners.OnGroupMembershipUpdateListener;

/**
 * Group membership button
 *
 * @author Pierre HUBERT
 */
public class GroupMembershipStatusView extends BaseFrameLayoutView implements View.OnClickListener {

    /**
     * Views
     */
    private ConstraintLayout respondInvitationForm;
    private Button rejectButton;
    private Button acceptButton;
    private ConstraintLayout cancelRequestForm;
    private Button cancelButton;
    private ConstraintLayout requestForm;
    private Button joinButton;
    private ConstraintLayout memberForm;
    private Button leaveButton;
    private ConstraintLayout closeRegistrationForm;

    /**
     * Group information
     */
    private GroupInfo mInfo;

    /**
     * Listener
     */
    private OnGroupMembershipUpdateListener mOnGroupMembershipUpdateListener;


    public GroupMembershipStatusView(@NonNull Context context) {
        this(context, null);
    }

    public GroupMembershipStatusView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GroupMembershipStatusView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        View view = inflate(getContext(), R.layout.view_group_membership_status, this);
        respondInvitationForm = view.findViewById(R.id.respondInvitationForm);
        rejectButton = view.findViewById(R.id.rejectButton);
        acceptButton = view.findViewById(R.id.acceptButton);
        cancelRequestForm = view.findViewById(R.id.cancelRequestForm);
        cancelButton = view.findViewById(R.id.cancelButton);
        requestForm = view.findViewById(R.id.requestForm);
        joinButton = view.findViewById(R.id.joinButton);
        memberForm = view.findViewById(R.id.memberForm);
        leaveButton = view.findViewById(R.id.leaveButton);
        closeRegistrationForm = view.findViewById(R.id.closedRegistrationForm);

        //Show only one form
        showForm(respondInvitationForm);

        rejectButton.setOnClickListener(this);
        acceptButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
        joinButton.setOnClickListener(this);
        leaveButton.setOnClickListener(this);
    }

    /**
     * Set the group membership update listener
     *
     * @param onGroupMembershipUpdateListener The listener
     */
    public void setOnGroupMembershipUpdateListener(OnGroupMembershipUpdateListener onGroupMembershipUpdateListener) {
        this.mOnGroupMembershipUpdateListener = onGroupMembershipUpdateListener;
    }

    /**
     * Assign the view to a new group
     *
     * @param info Information about the group
     */
    public void setGroup(GroupInfo info) {
        mInfo = info;
        showButtons(true);

        switch (info.getMembershipLevel()) {
            case VISITOR:
                if(info.getRegistrationLevel() == GroupRegistrationLevel.CLOSED)
                    showForm(closeRegistrationForm);
                else
                    showForm(requestForm);
                return;

            case PENDING:
                showForm(cancelRequestForm);
                return;

            case INVITED:
                showForm(respondInvitationForm);
                return;

            default:
                showForm(memberForm);
        }
    }

    /**
     * Display the form contained in a specific view and hide the other ones
     *
     * @param v The container of the form to show
     */
    private void showForm(View v) {

        respondInvitationForm.setVisibility(v.equals(respondInvitationForm)
                ? View.VISIBLE : View.GONE);
        cancelRequestForm.setVisibility(v.equals(cancelRequestForm)
                ? View.VISIBLE : View.GONE);
        requestForm.setVisibility(v.equals(requestForm)
                ? View.VISIBLE : View.GONE);
        memberForm.setVisibility(v.equals(memberForm) ? View.VISIBLE : View.GONE);
        closeRegistrationForm.setVisibility(v.equals(closeRegistrationForm)
                ? View.VISIBLE : View.GONE);

    }

    /**
     * Update buttons visibility
     *
     * @param visible New visibility for the buttons
     */
    private void showButtons(boolean visible){
        rejectButton.setVisibility(visible ? View.VISIBLE : View.GONE);
        acceptButton.setVisibility(visible ? View.VISIBLE : View.GONE);
        cancelButton.setVisibility(visible ? View.VISIBLE : View.GONE);
        joinButton.setVisibility(visible ? View.VISIBLE : View.GONE);
        leaveButton.setVisibility(View.VISIBLE); //View cannot be hidden
    }

    @Override
    public void onClick(View v) {

        if(mOnGroupMembershipUpdateListener == null)
            return;

        showButtons(false);

        if(v.equals(rejectButton)){
            mOnGroupMembershipUpdateListener.onRespondInvitation(mInfo, false);
        }

        else if(v.equals(acceptButton)){
            mOnGroupMembershipUpdateListener.onRespondInvitation(mInfo, true);
        }

        else if(v.equals(cancelButton)){
            mOnGroupMembershipUpdateListener.onCancelRequest(mInfo);
        }

        else if (v.equals(joinButton)){
            mOnGroupMembershipUpdateListener.onSendRequest(mInfo);
        }

        else if(v.equals(leaveButton)){
            mOnGroupMembershipUpdateListener.onLeaveGroup(mInfo);
        }

    }
}
