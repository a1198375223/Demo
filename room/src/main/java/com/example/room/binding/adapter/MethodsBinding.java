package com.example.room.binding.adapter;

import android.widget.ImageView;

import androidx.databinding.BindingMethod;
import androidx.databinding.BindingMethods;

/**
 * This is equivalent to:
 * ```
 *
 *   @BindingAdapter("app:srcCompat")
 *   @JvmStatic fun srcCompat(view: ImageView, @DrawableRes drawableId: Int) {
 *       view.setImageResource(drawable)
 *   }
 * ```
 */
@BindingMethods({
        @BindingMethod(type = ImageView.class, attribute = "app:srcCompat", method = "setImageResource")
})
public class MethodsBinding {}
