package org.communiquons.android.comunic.client.ui.fragments.groups;

import org.communiquons.android.comunic.client.R;
import org.communiquons.android.comunic.client.ui.activities.MainActivity;
import org.communiquons.android.comunic.client.ui.fragments.AbstractFragment;

import java.util.Objects;

/**
 * Base Group Fragment
 *
 * @author Pierre HUBERT
 */
abstract class AbstractGroupFragment extends AbstractFragment {

    @Override
    public void onResume() {
        super.onResume();

        MainActivity.SetNavbarSelectedOption(Objects.requireNonNull(getActivity()),
                R.id.action_personal_page);
    }
}
