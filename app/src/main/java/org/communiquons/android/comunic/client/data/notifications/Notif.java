package org.communiquons.android.comunic.client.data.notifications;

/**
 * Notification object
 *
 * @author Pierre HUBERT
 * Created by pierre on 4/9/18.
 */

public class Notif {

    //Private fields
    private int id;
    private int time_create;
    private boolean seen;
    private int from_user_id;
    private int dest_user_id;
    private int on_elem_id;
    private NotifElemType on_elem_type;
    private NotificationTypes type;
    private NotificationVisibility visibility;
    private int from_container_id;
    private NotifElemType from_container_type;

    //Get and set notification ID
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    //Get and set notification creation time
    public int getTime_create() {
        return time_create;
    }

    public void setTime_create(int time_create) {
        this.time_create = time_create;
    }


    //Get and set seen state of the notification
    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }


    //Get and set source user ID
    public int getFrom_user_id() {
        return from_user_id;
    }

    public void setFrom_user_id(int from_user_id) {
        this.from_user_id = from_user_id;
    }


    //Get and set destination user id
    public int getDest_user_id() {
        return dest_user_id;
    }

    public void setDest_user_id(int dest_user_id) {
        this.dest_user_id = dest_user_id;
    }


    //Get and set on element id
    public int getOn_elem_id() {
        return on_elem_id;
    }

    public void setOn_elem_id(int on_elem_id) {
        this.on_elem_id = on_elem_id;
    }


    //Get and set on elem type
    public NotifElemType getOn_elem_type() {
        return on_elem_type;
    }

    public void setOn_elem_type(NotifElemType on_elem_type) {
        this.on_elem_type = on_elem_type;
    }


    //Get and set notification type
    public NotificationTypes getType() {
        return type;
    }

    public void setType(NotificationTypes type) {
        this.type = type;
    }


    //Get and set notification visibility
    public NotificationVisibility getVisibility() {
        return visibility;
    }

    public void setVisibility(NotificationVisibility visibility) {
        this.visibility = visibility;
    }


    //Get and set source container id
    public int getFrom_container_id() {
        return from_container_id;
    }

    public void setFrom_container_id(int from_container_id) {
        this.from_container_id = from_container_id;
    }


    //Get and set the type of the source container
    public NotifElemType getFrom_container_type() {
        return from_container_type;
    }

    public void setFrom_container_type(NotifElemType from_container_type) {
        this.from_container_type = from_container_type;
    }
}
