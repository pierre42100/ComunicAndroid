package org.communiquons.android.comunic.client.ui.fragments;

import android.app.AlertDialog;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.communiquons.android.comunic.client.R;
import org.communiquons.android.comunic.client.data.enums.NotifElemType;
import org.communiquons.android.comunic.client.data.helpers.GetUsersHelper;
import org.communiquons.android.comunic.client.data.helpers.NotificationsHelper;
import org.communiquons.android.comunic.client.data.arrays.NotifsList;
import org.communiquons.android.comunic.client.data.models.Notif;
import org.communiquons.android.comunic.client.ui.activities.MainActivity;
import org.communiquons.android.comunic.client.ui.adapters.NotificationsAdapter;
import org.communiquons.android.comunic.client.ui.listeners.onOpenUsersPageListener;
import org.communiquons.android.comunic.client.ui.listeners.onPostOpenListener;

/**
 * Notifications fragment
 *
 * @author Pierre HUBERT
 * Created by pierre on 4/1/18.
 */

public class NotificationsFragment extends Fragment implements View.OnCreateContextMenuListener,
        AdapterView.OnItemClickListener {

    /**
     * Notifications helper
     */
    private NotificationsHelper mNotificationsHelper;

    /**
     * Get users helper
     */
    private GetUsersHelper mUsersInfoHelper;

    /**
     * Notifications list
     */
    private NotifsList mNotificationsList;

    /**
     * Delete all the notifications button
     */
    private View mDeleteNotificationsBtn;

    /**
     * Notifications list view
     */
    private ListView mNotificationsListView;

    /**
     * Notifications adapter
     */
    private NotificationsAdapter mNotificationsAdapter;

    /**
     * Loading progress bar
     */
    private ProgressBar mLoadingProgress;

    /**
     * No notification notice
     */
    private TextView mNoNotifNotice;

    /**
     * User page opener
     */
    private onOpenUsersPageListener mUserPageOpener;

    /**
     * Post open listener
     */
    private onPostOpenListener mOpenPostListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Create notifications helper
        mNotificationsHelper = new NotificationsHelper(getActivity());

        //Create get users helper
        mUsersInfoHelper = new GetUsersHelper(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notifications, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Get loading progress view
        mLoadingProgress = view.findViewById(R.id.loading_progress);

        //Get the "no notification" notice
        mNoNotifNotice = view.findViewById(R.id.no_notification_notification_notice);
        mNoNotifNotice.setVisibility(View.GONE);

        //Delete all the notifications action
        mDeleteNotificationsBtn = view.findViewById(R.id.delete_all_notif_btn);
        mDeleteNotificationsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmNotificationsDeletion();
            }
        });

        //Get the notifications list view
        mNotificationsListView = view.findViewById(R.id.notificcation_list);
    }

    @Override
    public void onResume() {
        super.onResume();

        //Update the title of the application
        getActivity().setTitle(R.string.fragment_notifications_title);

        //Update the bottom navigation menu
        ((MainActivity) getActivity())
                .setSelectedNavigationItem(R.id.action_notifications);

        //Get user page opener
        mUserPageOpener = (onOpenUsersPageListener) getActivity();

        //Get post opener
        mOpenPostListener = (onPostOpenListener) getActivity();

        //Check if it is required to fetch the list of notifications
        if(mNotificationsList == null){

            //Get the list of notifications
            getListNotifications();

        }
        else
            //Display the list of notifications
            displayNotificationsList();
    }

    /**
     * Ask the user to confirm the deletion of all the notifications
     */
    private void confirmNotificationsDeletion(){

        //Create and display a confirmation dialog
        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.dialog_deleteallnotifs_title)
                .setMessage(R.string.dialog_deleteallnotifs_message)
                .setNegativeButton(R.string.dialog_deleteallnotifs_cancel, null)

                .setPositiveButton(R.string.dialog_deleteallnotifs_confirm,
                        new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        processAllNotificationsDeletion();
                    }
                })

                .show();

    }

    /**
     * Do the deletion of all the notifications
     */
    private void processAllNotificationsDeletion(){

        //Perform the operation in the background
        new AsyncTask<Void, Void, Boolean>(){

            @Override
            protected Boolean doInBackground(Void... params) {
                return mNotificationsHelper.deleteAllNotifs();
            }

            @Override
            protected void onPostExecute(Boolean result) {

                //Check if the activity has been destroyed
                if(getActivity() == null)
                    return;

                //Check for error
                if(!result) {
                    Toast.makeText(getActivity(), R.string.err_delete_all_notifs,
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                //Success
                Toast.makeText(getActivity(), R.string.success_delete_all_notifs,
                        Toast.LENGTH_SHORT).show();
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    /**
     * Get and save the list of notifications from the server
     */
    private void getListNotifications(){

        //Perform the task on a separate thread
        new AsyncTask<Void, Void, NotifsList>(){

            @Override
            protected NotifsList doInBackground(Void... params) {

                NotifsList list = mNotificationsHelper.getListUnread();

                //If we got the list of notifications, fetch users information
                if(list != null)
                    list.setUsersInfo(mUsersInfoHelper.getMultiple(list.getUsersID()));

                return list;

            }

            @Override
            protected void onPostExecute(@Nullable NotifsList notifs) {

                //Check if the activity has been destroyed
                if(getActivity() == null)
                    return;

                //Check if we could not get the list of notifications
                if(notifs == null){
                    Toast.makeText(getActivity(), R.string.err_get_list_notifs,
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                //Check if we could not get information about the users of the notifications
                if(notifs.getUsersInfo() == null){
                    Toast.makeText(getActivity(), R.string.err_get_users_info,
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                //Save the list of notifications and display it
                mNotificationsList = notifs;
                displayNotificationsList();
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    /**
     * Display the list of notifications
     */
    private void displayNotificationsList(){

        //Hide loading progress bar
        mLoadingProgress.setVisibility(View.GONE);

        //Create notification adapter
        mNotificationsAdapter = new NotificationsAdapter(getActivity(), mNotificationsList);
        mNotificationsListView.setAdapter(mNotificationsAdapter);

        //Set context menu creator
        mNotificationsListView.setOnCreateContextMenuListener(this);
        mNotificationsListView.setOnItemClickListener(this);

        //Check if there is not any notification to display the "no notification" notice
        updateNoNotifNotif();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        //Check the context menu is targeting the list view
        if(v != mNotificationsListView)
            return;

        //Inflate the menu
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.menu_notification_actions, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        //Fetch source item
        AdapterView.AdapterContextMenuInfo src
                = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        //Check if the action is to delete the notification
        if(item.getItemId() == R.id.action_delete){
            deleteNotification(src.position);
            return true;
        }

        return super.onContextItemSelected(item);
    }

    /**
     * Delete the notification located at a specified position
     *
     * @param pos The position of the notification to delete
     */
    private void deleteNotification(int pos){

        //Get the ID of the notification
        int notifID = mNotificationsList.get(pos).getId();
        
        //Delete the notification from the list
        mNotificationsList.remove(pos);
        mNotificationsAdapter.notifyDataSetChanged();

        //Check if the "no notification" notice has to be shown
        updateNoNotifNotif();

        //Delete the notification from the server
        new AsyncTask<Integer, Void, Boolean>(){

            @Override
            protected Boolean doInBackground(Integer... params) {
                return mNotificationsHelper.markSeen(params[0]);
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                if(getActivity() == null)
                    return;

                //Check for errors
                if(!aBoolean)
                    Toast.makeText(getActivity(), R.string.err_delete_notification,
                            Toast.LENGTH_SHORT).show();
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, notifID);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        //Perform notification action
        Notif notif = mNotificationsList.get(position);

        //Delete the notification
        deleteNotification(position);

        //For friendship request
        if(notif.getOn_elem_type() == NotifElemType.FRIEND_REQUEST){

            //Open user page
            mUserPageOpener.openUserPage(notif.getFrom_user_id());

        }

        //If the notification is targeting a post
        if(notif.getOn_elem_type() == NotifElemType.POST){

            //Open the post
            mOpenPostListener.onOpenPost(notif.getOn_elem_id());

        }
    }

    /**
     * Update the visibility status of the no notification status
     */
    private void updateNoNotifNotif(){
        mNoNotifNotice.setVisibility(mNotificationsList.size() == 0 ? View.VISIBLE : View.GONE);
    }
}
