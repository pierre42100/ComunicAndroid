package org.communiquons.android.comunic.client.ui.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.communiquons.android.comunic.client.R;
import org.communiquons.android.comunic.client.data.arrays.NotifsList;
import org.communiquons.android.comunic.client.data.models.GroupInfo;
import org.communiquons.android.comunic.client.data.models.Notif;
import org.communiquons.android.comunic.client.data.models.UserInfo;
import org.communiquons.android.comunic.client.data.utils.NotifsUtils;
import org.communiquons.android.comunic.client.data.utils.TimeUtils;
import org.communiquons.android.comunic.client.ui.views.WebUserAccountImage;

/**
 * Notifications list adapter
 *
 * @author Pierre HUBERT
 * Created by pierre on 4/10/18.
 */

public class NotificationsAdapter extends ArrayAdapter<Notif>{

    /**
     * Information about the users of the notifications
     */
    private ArrayMap<Integer, UserInfo> mUsersInfo;

    /**
     * Information about the groups related to the notifications
     */
    private ArrayMap<Integer, GroupInfo> mGroupsInfo;

    /**
     * Public adapter constructor
     *
     * @param context The context of the application
     * @param list The list of notifications
     */
    public NotificationsAdapter(Context context, NotifsList list){
        super(context, 0, list);

        //Save users and groups information
        mUsersInfo = list.getUsersInfo();
        mGroupsInfo = list.getGroupsInfo();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        //Inflate the view, if required
        if(convertView == null){
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.notification_item, parent, false);
        }

        //Get the notification
        Notif notif = getItem(position);
        assert notif != null;

        //Update the user account image
        WebUserAccountImage accountImage = convertView.findViewById(R.id.user_account_image);
        accountImage.setUser(mUsersInfo.get(notif.getFrom_user_id()));

        //Update the message of the notification
        TextView message = convertView.findViewById(R.id.notification_message);
        message.setText(NotifsUtils.getNotificationMessage(getContext(), notif,
                mUsersInfo, mGroupsInfo));

        //Update the date of the notification
        TextView date = convertView.findViewById(R.id.notification_date);
        date.setText(TimeUtils.TimeToString(getContext(),
                TimeUtils.time() - notif.getTime_create()));

        return convertView;
    }
}
