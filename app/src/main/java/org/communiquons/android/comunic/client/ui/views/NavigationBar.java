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

/**
 * Application navigation bar
 *
 * @author Pierre HUBERT
 */
public class NavigationBar extends BaseFrameLayoutView {

    /**
     * Navigation bar items container
     */
    private LinearLayout mLinearLayout;

    /**
     * Popup mMenu used to inflate mMenu
     */
    private PopupMenu mPopupMenu;

    /**
     * Associated mMenu
     */
    private Menu mMenu;

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

        //Process mMenu
        mPopupMenu = new PopupMenu(getContext(), null);
        mMenu = mPopupMenu.getMenu();
        getActivity().getMenuInflater().inflate(R.menu.navigation_bar, mMenu);

        for(int i = 0; i < mMenu.size(); i++){

            //Inflate view
            NavigationBarItem itemView = new NavigationBarItem(getContext());
            mLinearLayout.addView(itemView,
                    new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1f));

            MenuItem item = mMenu.getItem(i);

            itemView.setIconDrawable(item.getIcon());
        }
    }
}
