package org.communiquons.android.comunic.client.ui.utils;

import android.graphics.drawable.Drawable;

import java.util.Objects;

/**
 * Drawable utilities
 *
 * @author Pierre HUBERT
 */
public class DrawableUtils {

    /**
     * Duplicate a {@link Drawable} object
     *
     *
     * @param source The drawable to duplicate
     * @return Generated drawable copy
     */
    public static Drawable DuplicateDrawable(Drawable source){
        return Objects.requireNonNull(source.getConstantState()).newDrawable().mutate();
    }

}
