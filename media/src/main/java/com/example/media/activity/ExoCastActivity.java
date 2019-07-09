package com.example.media.activity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.commonlibrary.utils.Toasty;
import com.example.media.R;
import com.example.media.adapter.MediaQueueAdapter;
import com.example.media.adapter.RecyclerCallback;
import com.example.media.callback.PlaybackListenerWrapper;
import com.example.media.model.CastModel;
import com.example.media.presenter.GalileoPlayerManager;
import com.example.media.presenter.IPlayerCastManager;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ext.cast.MediaItem;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.gms.cast.CastMediaControlIntent;
import com.google.android.gms.cast.framework.CastButtonFactory;
import com.google.android.gms.cast.framework.CastContext;

import java.util.Collections;

public class ExoCastActivity extends AppCompatActivity {
    private static final String TAG = "ExoCastActivity";

    private final MediaItem.Builder mediaItemBuilder;

    private PlayerView localPlayerView;
    private PlayerControlView castControlView;
    private IPlayerCastManager playerManager;
    private RecyclerView mediaQueueList;
    private MediaQueueAdapter mediaQueueListAdapter;
    private CastContext castContext;

    public ExoCastActivity() {
        mediaItemBuilder = new MediaItem.Builder();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        castContext = CastContext.getSharedInstance(this);
        setContentView(R.layout.activity_exo_cast);
        localPlayerView = findViewById(R.id.local_player_view);
        castControlView = findViewById(R.id.cast_control_view);
        mediaQueueList = findViewById(R.id.sample_list);

        playerManager = new GalileoPlayerManager(this, this, castContext, castControlView, localPlayerView);
        playerManager.setListener(new PlaybackListenerWrapper() {
            @Override
            public void onQueuePositionChanged(int oldIndex, int currentItemIndex) {
                super.onQueuePositionChanged(oldIndex, currentItemIndex);
                if (oldIndex != C.INDEX_UNSET) {
                    mediaQueueListAdapter.notifyItemChanged(oldIndex);
                }
                if (currentItemIndex != C.INDEX_UNSET) {
                    mediaQueueListAdapter.notifyItemChanged(currentItemIndex);
                }
            }


            @Override
            public void onPlayerError(ExoPlaybackException error) {
                super.onPlayerError(error);
                Toasty.showError(getString(R.string.player_error_msg));
            }
        });

        mediaQueueList.setLayoutManager(new LinearLayoutManager(this));
        mediaQueueList.setHasFixedSize(true);
        mediaQueueListAdapter = new MediaQueueAdapter(playerManager);
        ItemTouchHelper helper = new ItemTouchHelper(new RecyclerCallback(mediaQueueListAdapter, playerManager));
        helper.attachToRecyclerView(mediaQueueList);
        mediaQueueList.setAdapter(mediaQueueListAdapter);

        findViewById(R.id.add_sample_button).setOnClickListener(view -> new AlertDialog.Builder(this)
                .setTitle(R.string.add_samples)
                .setView(buildSampleListView())
                .setPositiveButton(android.R.string.ok, null)
                .create()
                .show());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.cast_menu, menu);
        CastButtonFactory.setUpMediaRouteButton(this, menu, R.id.media_route_menu_item);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (castContext == null) {
            Log.e(TAG, "CastContext is destroyed.");
            return;
        }

        String applicationId = castContext.getCastOptions().getReceiverApplicationId();
        switch (applicationId) {
            case CastMediaControlIntent.DEFAULT_MEDIA_RECEIVER_APPLICATION_ID:
                Log.d(TAG, "onResume: CC1AD845");
                break;
            default:
                throw new IllegalStateException("Illegal receiver app id: " + applicationId);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (castContext == null) {
            Log.e(TAG, "CastContext is destroyed.");
            return;
        }

        mediaQueueListAdapter.notifyItemRangeRemoved(0, mediaQueueListAdapter.getItemCount());
        mediaQueueList.setAdapter(null);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        return super.dispatchKeyEvent(event) || playerManager.dispatchKeyEvent(event);
    }

    private View buildSampleListView() {
        View dialogList = getLayoutInflater().inflate(R.layout.layout_sample_list, null);
        ListView sampleList = dialogList.findViewById(R.id.sample_list);
        sampleList.setAdapter(new SampleListAdapter(this));
        sampleList.setOnItemClickListener((parent, view, position, id) -> {
            CastModel.Sample sample = CastModel.SAMPLES.get(position);
            mediaItemBuilder.clear()
                    .setMedia(sample.uri)
                    .setTitle(sample.name)
                    .setMimeType(sample.mimeType);
            if (sample.drmSchemeUuid != null) {
                mediaItemBuilder.setDrmSchemes(Collections.singletonList(new MediaItem.DrmScheme(sample.drmSchemeUuid,
                        new MediaItem.UriBundle(sample.licenseServerUri))));
            }

            playerManager.addItem(mediaItemBuilder.build());
            mediaQueueListAdapter.notifyItemInserted(playerManager.getMediaQueueSize() - 1);
        });
        return dialogList;
    }


    private static final class SampleListAdapter extends ArrayAdapter<CastModel.Sample> {

        public SampleListAdapter(Context context) {
            super(context, android.R.layout.simple_list_item_1, CastModel.SAMPLES);
        }
    }
}
