package com.example.room.binding.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.DrawableRes;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.databinding.BindingAdapter;

import com.example.room.R;
import com.example.room.binding.model.Popularity;

public final class BindingAdapters {

    @BindingAdapter({"popularityIcon"})
    public static void popularityIcon(ImageView view, Popularity popularity) {
        int color = getAssociatedColor(popularity, view.getContext());

        ImageViewCompat.setImageTintList(view, ColorStateList.valueOf(color));

        view.setImageDrawable(getDrawablePopularity(popularity, view.getContext()));
    }


    @BindingAdapter(value = {"progressScaled", "max"}, requireAll = true)
    public static void setProgress(ProgressBar progress, int likes, int max) {
        progress.setProgress((likes * max / 5) % max);
    }


    @BindingAdapter({"progressTint"})
    public static void tintPopularity(ProgressBar progressBar, Popularity popularity) {
        int color = getAssociatedColor(popularity, progressBar.getContext());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            progressBar.setProgressTintList(ColorStateList.valueOf(color));
        }
    }

    @BindingAdapter("srcCompat")
    public static void srcCompat(ImageView imageView, @DrawableRes int drawableId) {
        imageView.setImageResource(drawableId);
    }


    @BindingAdapter({"hideIfZero"})
    public static void hindIfZero(View view, int number) {
        view.setVisibility(number == 0 ? View.GONE : View.VISIBLE);
    }


    private static int getAssociatedColor(Popularity popularity, Context context) {
        if (popularity == Popularity.NORMAL) {
            return ContextCompat.getColor(context, R.color.normal);
        } else if (popularity == Popularity.POPULAR) {
            return ContextCompat.getColor(context, R.color.popular);
        }
        return ContextCompat.getColor(context, R.color.star);
    }

    private static Drawable getDrawablePopularity(Popularity popularity, Context context) {
        if (popularity == Popularity.NORMAL) {
            return ContextCompat.getDrawable(context, R.drawable.ic_person_black_96dp);
        } else if (popularity == Popularity.POPULAR) {
            return ContextCompat.getDrawable(context, R.drawable.ic_whatshot_black_96dp);
        }
        return ContextCompat.getDrawable(context, R.drawable.ic_whatshot_black_96dp);
    }
}
