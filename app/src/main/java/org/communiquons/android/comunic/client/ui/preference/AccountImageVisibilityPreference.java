package org.communiquons.android.comunic.client.ui.preference;

import android.content.Context;
import android.support.v7.preference.ListPreference;
import android.util.AttributeSet;

import org.communiquons.android.comunic.client.data.enums.AccountImageVisibility;

/**
 * Account image visibility preference
 */
public class AccountImageVisibilityPreference extends ListPreference {

    public AccountImageVisibilityPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public AccountImageVisibilityPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public AccountImageVisibilityPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AccountImageVisibilityPreference(Context context) {
        super(context);
    }

    /**
     * Set new selected value
     *
     * @param visibility Visibility level
     */
    public void setValue(AccountImageVisibility visibility) {
        setValue(AccountImageVisibilityToString(visibility));
    }

    /**
     * Get the currently selected visibility
     *
     * @return Currently selected visibility level
     */
    public AccountImageVisibility getVisibility() {
        return StringToAccountImageVisibility(getValue());
    }

    /**
     * Turn an AccountImageVisibility entry into a string
     *
     * @param visibility The visibility to convert
     * @return Generated string
     */
    private static String AccountImageVisibilityToString(AccountImageVisibility visibility) {
        switch (visibility) {
            case OPEN:
                return "open";

            case PUBLIC:
                return "public";

            case FRIENDS:
                return "friends";

            default:
                throw new AssertionError();
        }
    }

    /**
     * Turn a string into an account image visibility level
     *
     * @param string The string to convert
     * @return Matching account visibility level
     */
    public static AccountImageVisibility StringToAccountImageVisibility(String string) {
        switch (string) {
            case "open":
                return AccountImageVisibility.OPEN;

            case "public":
                return AccountImageVisibility.PUBLIC;

            case "friends":
                return AccountImageVisibility.FRIENDS;

            default:
                throw new AssertionError();
        }
    }
}
