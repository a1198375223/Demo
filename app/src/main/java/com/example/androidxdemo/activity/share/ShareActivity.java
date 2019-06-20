package com.example.androidxdemo.activity.share;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.MimeTypeMap;
import android.widget.EditText;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.ShareActionProvider;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.view.MenuItemCompat;

import com.example.androidxdemo.BuildConfig;
import com.example.androidxdemo.R;
import com.example.commonlibrary.utils.AppUtils;
import com.example.commonlibrary.utils.Toasty;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class ShareActivity extends Activity {
    private static final String TAG = "ShareActivity";
    public static final String ROOT_PHOTO_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/hupu/games/image/hupuImage/";
    public static final String IMAGE_CACHE_DIR = "images";
    public static final String IMAGE_FILE = "image.png";

    private final String[] REQUEST_PERMISSIONS = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.NFC
    };
    private final int REQUEST_CODE = 10;
    private final int REQUIRE_PICK_CODE = 12;

    // 多图分享的个数
    private int size = 3;

    private ShareActionProvider mShareActionProvider;


    private Uri[] fileUris = new Uri[10];
    private FileUriCallback fileUriCallback;


    private EditText mShareText;

    private SharingShortcutsManager mSharingShortcutsManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        mShareText = findViewById(R.id.share_text);

        mSharingShortcutsManager = new SharingShortcutsManager();
        mSharingShortcutsManager.pushDirectShareTargets(this);

        findViewById(R.id.simple_share).setOnClickListener(view -> {
            // 简单的分享
            Intent sendIntent = new Intent();
            // 设置分享的action
            sendIntent.setAction(Intent.ACTION_SEND);
            // 设置分享的东西的类型 是一个MimeType
            sendIntent.setType("text/plain");
            // 通过对Intent设置EXTRA来分享需要分享的内容
            sendIntent.putExtra(Intent.EXTRA_TEXT, "将要被分享的文本");
            // 开启分享
            startActivity(sendIntent);
        });

        findViewById(R.id.simple_photo_share).setOnClickListener(view -> {
            // 分享之前先检查权限
            if (checkPermissions()) {
                return;
            }
            Log.d(TAG, "share image root dir=" + ROOT_PHOTO_PATH);
            File dir = new File(ROOT_PHOTO_PATH);
            if (!dir.exists()) {
                Toasty.showError("文件夹不存在!!");
                return;
            }
            File[] photos = dir.listFiles();
            if (photos == null || photos.length <= 0) {
                Toasty.showError("没有图片哦～");
                return;
            }
            // 随机获取一张图片进行分享
            Random random = new Random();
            File randomFile = photos[random.nextInt(photos.length)];
            Log.d(TAG, "select random file=" + randomFile.getPath());
            Uri uri = FileProvider.getUriForFile(AppUtils.app(), BuildConfig.APPLICATION_ID + ".provider", randomFile);
            Log.d(TAG, "final uri=" + uri.toString());
            String mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(randomFile.getName().substring(randomFile.getName().lastIndexOf(".") + 1));
            Log.d(TAG, "mime type=" + mime);
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.setType(mime);
            sendIntent.putExtra(Intent.EXTRA_STREAM, uri);
            setShareIntent(sendIntent);
            startActivity(Intent.createChooser(sendIntent, "分享图片的标题！！！"));
        });

        findViewById(R.id.simple_share_mult_photo).setOnClickListener(view -> {
            // 分享之前先检查权限
            if (checkPermissions()) {
                return;
            }
            Log.d(TAG, "share image root dir=" + ROOT_PHOTO_PATH);
            File dir = new File(ROOT_PHOTO_PATH);
            if (!dir.exists()) {
                Toasty.showError("文件夹不存在!!");
                return;
            }
            File[] photos = dir.listFiles();
            if (photos == null || photos.length <= 0) {
                Toasty.showError("没有图片哦～");
                return;
            }
            List<File> fileList = Arrays.asList(photos);
            List<File> realFileList = new ArrayList<>(fileList);
            // 分享多张图片
            ArrayList<Uri> imageUris = new ArrayList<>();
            Random random = new Random();
            if (size > realFileList.size()) {
                size = realFileList.size();
            }
            for (int i = 0; i < size; i++) {
                int number = random.nextInt(realFileList.size());
                Log.d(TAG, "share multi photo random number=" + number);
                Uri uri = FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID + ".provider", realFileList.get(number));
                realFileList.remove(number);
                imageUris.add(uri);
            }
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
            sendIntent.setType("image/*");
            sendIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris);
            startActivity(Intent.createChooser(sendIntent, "分享多图图片的标题！！！"));
        });


        findViewById(R.id.pick_then_share).setOnClickListener(view -> {
            // 分享之前先检查权限
            if (checkPermissions()) {
                return;
            }
            Log.d(TAG, "share image root dir=" + ROOT_PHOTO_PATH);
            File dir = new File(ROOT_PHOTO_PATH);
            if (!dir.exists()) {
                Toasty.showError("文件夹不存在!!");
                return;
            }
            File[] photos = dir.listFiles();
            if (photos == null || photos.length <= 0) {
                Toasty.showError("没有图片哦～");
                return;
            }

            Intent pickIntent = new Intent(Intent.ACTION_PICK);
            pickIntent.setType("image/*");
            startActivityForResult(pickIntent, REQUIRE_PICK_CODE);
        });

        // 使用nfc进行分享
        findViewById(R.id.nfc_share).setOnClickListener(view -> {
            Intent nfcIntent = new Intent(ShareActivity.this, NFCActivity.class);
            startActivity(nfcIntent);
        });


        // 直接共享文本
        findViewById(R.id.direct_share).setOnClickListener(view -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, mShareText.getText().toString());
            shareIntent.putExtra(Intent.EXTRA_TITLE, "发送消息的标题");
            ClipData thumbnail = getClipDataThumbnail();
            if (thumbnail != null) {
                Log.d(TAG, "the thumbnail not null");
                shareIntent.setClipData(thumbnail);
                shareIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
            startActivity(Intent.createChooser(shareIntent, null));
        });
    }


    private ClipData getClipDataThumbnail() {
        Uri contentUri = saveImageThumbnail();
        return ClipData.newUri(getContentResolver(), null, contentUri);
    }

    private Uri saveImageThumbnail() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        Log.d(TAG, "cache dir=" + getCacheDir());
        File cachePath = new File(getCacheDir(), IMAGE_CACHE_DIR);
        cachePath.mkdirs();

        try {
            FileOutputStream fos = new FileOutputStream(cachePath + "/" + IMAGE_FILE);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        File imagePath = new File(getCacheDir(), IMAGE_CACHE_DIR);
        File newFile = new File(imagePath, IMAGE_FILE);
        return FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", newFile);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUIRE_PICK_CODE) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                Uri uri = data.getData();
                Intent shareIntent = new Intent();
                shareIntent.setType("image/*");
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                startActivity(shareIntent);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 获取菜单
        getMenuInflater().inflate(R.menu.share_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.menu_item_share);

        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        return true;
    }

    // 设置简单的分享action, 算是一个记录分享的行为吧
    private void setShareIntent(Intent shareIntent) {
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE) {
            Log.d(TAG, "onRequestPermissionsResult: 权限获取成功！！！");
        }
    }

    private boolean checkHasPermissions() {
        for (String permission : REQUEST_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }


    private boolean checkPermissions() {
        if (!checkHasPermissions()) {
            ActivityCompat.requestPermissions(this, REQUEST_PERMISSIONS, REQUEST_CODE);
            return true;
        }
        return false;
    }


    private class FileUriCallback implements NfcAdapter.CreateBeamUrisCallback{
        public FileUriCallback() {}

        @Override
        public Uri[] createBeamUris(NfcEvent event) {
            return fileUris;
        }
    }
}
