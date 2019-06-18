package com.example.androidxdemo.activity.camera;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;

import com.example.androidxdemo.BuildConfig;
import com.example.androidxdemo.R;
import com.example.commonlibrary.utils.AppUtils;
import com.example.commonlibrary.utils.Toasty;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;


public class GalleryFragment extends Fragment {
    private static final String TAG = "GalleryFragment";
    public static final String KEY_ROOT_DIRECTORY = "root_folder";
    public static final List<String> EXTENSION_WHITELIST = Collections.singletonList("JPG");
    private ViewPager mediaViewPager;

    private File rootDirectory;
    private List<File> mediaList;
    private MediaPagerAdapter adapter;

    public GalleryFragment() {}


    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_gallery, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setRetainInstance(true);

        if (getArguments() != null) {
            rootDirectory = new File(getArguments().getString(KEY_ROOT_DIRECTORY));

            mediaList = new ArrayList<>();

            for (File file : rootDirectory.listFiles()) {
                if (EXTENSION_WHITELIST.contains(file.getName().substring(file.getName().lastIndexOf(".") + 1).toUpperCase())) {
                    mediaList.add(file);
                }
            }

            mediaViewPager = view.findViewById(R.id.photo_view_pager);
            mediaViewPager.setOffscreenPageLimit(2);
            adapter = new MediaPagerAdapter(getChildFragmentManager());
            mediaViewPager.setAdapter(adapter);


            view.findViewById(R.id.back_button).setOnClickListener(v -> {
//                if (getFragmentManager() != null) {
//                    getFragmentManager().popBackStack();
//                }
                requireActivity().onBackPressed();
            });

            // 分享
            view.findViewById(R.id.share_button).setOnClickListener(v -> {
                File file = mediaList.get(mediaViewPager.getCurrentItem());

                if (file != null) {
                    Intent intent = new Intent();
                    String mediaType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(file.getName().substring(file.getName().lastIndexOf(".") + 1));
                    Uri uri = FileProvider.getUriForFile(AppUtils.app(), BuildConfig.APPLICATION_ID + ".provider", file);
                    intent.putExtra(Intent.EXTRA_STREAM, uri);
                    intent.setType(mediaType);
                    intent.setAction(Intent.ACTION_SEND);
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(Intent.createChooser(intent, getString(R.string.share_hint)));
                }
            });

            // 删除
            view.findViewById(R.id.delete_button).setOnClickListener(v -> {
                new AlertDialog.Builder(requireContext(), android.R.style.Theme_Material_Dialog)
                        .setTitle(getString(R.string.delete_title))
                        .setMessage(getString(R.string.delete_dialog))
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                            File file = mediaList.get(mediaViewPager.getCurrentItem());
                            if (file != null) {
                                file.delete();
                                mediaList.remove(mediaViewPager.getCurrentItem());
                                Objects.requireNonNull(mediaViewPager.getAdapter()).notifyDataSetChanged();

                                if (mediaList.isEmpty()) {
                                    requireActivity().onBackPressed();
                                }
                            }
                        })
                        .setNegativeButton(android.R.string.no, (dialog, which) -> {
                            Toasty.showCustom("点击no");
                        })
                        .create()
                        .show();
            });
        }
    }

    class MediaPagerAdapter extends FragmentStatePagerAdapter {

        public MediaPagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return PhotoFragment.createPhotoFragment(mediaList.get(position));
        }

        @Override
        public int getCount() {
            return mediaList.size();
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            return POSITION_NONE;
        }
    }
}
