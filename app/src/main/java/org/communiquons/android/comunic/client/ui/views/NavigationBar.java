package org.communiquons.android.comunic.client.ui.views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupMenu;

import org.communiquons.android.comunic.client.R;
import org.communiquons.android.comunic.client.ui.utils.MenuUtils;

import java.util.ArrayList;

/**
 * Application navigation bar
 *
 * @author Pierre HUBERT
 */
public class NavigationBar extends BaseFrameLayoutView implements NavigationBarItem.OnNavigationBarItemClickListener {

    /**
     * Navigation bar items container
     */
    private LinearLayout mLinearLayout;

    /**
     * Popup mMenu used to inflate mMenu
     */
    private PopupMenu mPopupMenu;

    /**
     * Associated menu
     */
    private Menu mMenu;

    /**
     * Navigation bar items
     */
    private ArrayList<NavigationBarItem> mItems = new ArrayList<>();

    /**
     * Navigation selected item listener
     */
    private OnNavigationItemSelectedListener mOnNavigationItemSelectedListener;

    public NavigationBar(@NonNull Context context) {
        super(context);
        init();
    }

    public NavigationBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NavigationBar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * Initialize view
     */
    private void init(){

        //Inflate main view
        View view = inflate(getContext(), R.layout.navigation_bar, this);
        mLinearLayout = view.findViewById(R.id.container);

        //Inflate menu
        mPopupMenu = new PopupMenu(getContext(), null);
        mMenu = mPopupMenu.getMenu();
        getActivity().getMenuInflater().inflate(R.menu.navigation_bar, mMenu);

        for(int i = 0; i < mMenu.size(); i++){

            //Inflate view
            NavigationBarItem itemView = new NavigationBarItem(getContext());
            mLinearLayout.addView(itemView,
                    new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1f));
            mItems.add(itemView);

            MenuItem item = mMenu.getItem(i);

            itemView.setIconDrawable(item.getIcon());
            itemView.setItemIndex(i);
            itemView.setOnNavigationBarItemClickListener(this);

        }
    }

    /**
     * Set the currently selected item by its index in the list
     *
     * @param index The index of the item to mark as selected
     */
    public void setIndexSelected(int index){

        //Process the list of items
        for(NavigationBarItem item : mItems)
            item.setSelected(index == item.getItemIndex());

    }

    /**
     * Set the currently selected item by its identifier in the list
     *
     * @param id The index of the item to mark as selected
     */
    public void setIdentifierSelected(int id){
        setIndexSelected(MenuUtils.MenuIdentifierToIndex(mMenu, id));
    }

    public void setOnNavigationItemSelectedListener(OnNavigationItemSelectedListener onNavigationItemSelectedListener) {
        this.mOnNavigationItemSelectedListener = onNavigationItemSelectedListener;
    }

    /**
     * Get the view associated to an index
     *
     * @param index The index of the item to get
     * @return Related view
     */
    public View getItemIndexView(int index){
        return mItems.get(index);
    }

    /**
     * Get the view associated to an identifier
     *
     * @param id The identifier of the item to get
     * @return Related view
     */
    public View getItemIdentifierView(int id){
        return getItemIndexView(MenuUtils.MenuIdentifierToIndex(mMenu, id));
    }


    @Override
    public void onItemClick(int index) {
        if(mOnNavigationItemSelectedListener == null)
            return;

        if(mOnNavigationItemSelectedListener.onNavigationItemSelected(mMenu.getItem(index)))
            setIndexSelected(index);
    }

    /**
     * Navigation item selected listener
     */
    public interface OnNavigationItemSelectedListener {

        /**
         * When an item is selected by the user
         *
         * @param item Selected MenuItem
         * @return True to keep the item selected (and deselect other ones), false else
         */
        boolean onNavigationItemSelected(MenuItem item);

    }
}
