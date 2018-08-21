package org.communiquons.android.comunic.client.ui.utils;

import android.view.Menu;
import android.view.MenuItem;

/**
 * Menu utilities
 *
 * @author Pierre HUBERT
 */
public class MenuUtils {

    /**
     * Find and return the index of an identifier in a menu
     *
     * @param menu Target menu
     * @param identifier Queried identifier
     * @return Matching index
     */
    public static int MenuIdentifierToIndex(Menu menu, int identifier){
        MenuItem item = menu.findItem(identifier);

        for(int i = 0; i < menu.size(); i++)
            if(menu.getItem(i).equals(item))
                return i;

        throw new RuntimeException("Identifier not found in menu!");
    }
}
