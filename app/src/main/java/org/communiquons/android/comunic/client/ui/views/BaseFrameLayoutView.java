package org.communiquons.android.comunic.client.ui.views;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * Base extensible FrameLayoutView
 *
 * @author Pierre HUBERT
 */
abstract class BaseFrameLayoutView extends FrameLayout {

    public BaseFrameLayoutView(@NonNull Context context) {
        super(context);
    }

    public BaseFrameLayoutView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseFrameLayoutView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public BaseFrameLayoutView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    /**
     * Get hosting activity
     *
     * @return Hosting activity, if found, null else
     */
    protected Activity getActivity(){
        Context context = getContext();

        while(context instanceof ContextWrapper){
            if(context instanceof Activity)
                return (Activity)context;

            context = ((ContextWrapper)context).getBaseContext();
        }

        return null;
    }
}
