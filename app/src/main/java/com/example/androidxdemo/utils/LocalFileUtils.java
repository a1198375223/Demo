package com.example.androidxdemo.utils;

import android.annotation.TargetApi;
import android.database.Cursor;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;

import androidx.core.util.Pair;


import com.example.androidxdemo.base.bean.MediaItem;
import com.example.commonlibrary.utils.AppUtils;
import com.example.commonlibrary.utils.MimeUtils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;


/**
 * 使用这个类来扫描手机中的不同文件和文件夹
 * 例如获取所有的视频, 获取所有的音频等等
 * 主要是通过ContentProvider来起作用的
 * mCursor = getContentResolver().query(
 *     UserDictionary.Words.CONTENT_URI,   // The content URI of the words table 表名称
 *     mProjection,                        // The columns to return for each row 列名称
 *     mSelectionClause                    // Selection criteria 选择的标准
 *     mSelectionArgs,                     // Selection criteria 选择的标准和上面的参数配合使用
 *     mSortOrder);                        // The sort order for the returned rows 整理的顺序(排列的顺序)
 *
 * 例如，要从用户字典中检索 _ID 为 4 的行，则可使用此内容 URI：
 * Uri singleUri = ContentUris.withAppendedId(UserDictionary.Words.CONTENT_URI,4);
 */
public class LocalFileUtils {
    private static final String TAG = "LocalFileUtils";

    public LocalFileUtils() {}


    // 获取所有的Video文件
    public static Observable<List<MediaItem>> getAllLocalVideo() {
        return Observable.create(new ObservableOnSubscribe<List<MediaItem>>() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void subscribe(ObservableEmitter<List<MediaItem>> observableEmitter) {
                String[] projection = {
                        MediaStore.Video.Media.DATA,
                        MediaStore.Video.Media.DISPLAY_NAME,
                        MediaStore.Video.Media.DURATION,
                        MediaStore.Video.Media.SIZE,
                        MediaStore.Video.Media.MIME_TYPE,
                        MediaStore.Video.Media.TITLE,
                        MediaStore.Video.Media.ARTIST,
                        MediaStore.Video.Media.RESOLUTION, // 解析度
                        MediaStore.Video.Media.DESCRIPTION,
                        MediaStore.Video.Media.CATEGORY,
                        MediaStore.Video.Media.LANGUAGE,
                        MediaStore.Video.Media.DATE_TAKEN,
                        MediaStore.Video.Media.WIDTH,
                        MediaStore.Video.Media.HEIGHT
                };

                String selection = null;
                String[] selectionArgs = null;

                Cursor cursor = AppUtils.app().getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                        projection,
                        selection,
                        selectionArgs,
                        MediaStore.Video.Media.DEFAULT_SORT_ORDER);

                if (cursor == null) {
                    observableEmitter.onError(new Throwable("Failed search video"));
                    return;
                }

                String[] columns = cursor.getColumnNames();
                for (String column : columns) {
                    Log.d(TAG, "getAllLocalVideo: column name=" + column);
                }

                List<MediaItem> list = new ArrayList<>();
                int dataIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
                int durationIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION);
                int nameIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME);
                int mimeTypeIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE);
                int sizeIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE);
                int titleIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE);
                int descriptionIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DESCRIPTION);
                int widthIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.WIDTH);
                int heightIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.HEIGHT);
                int artistIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.ARTIST);
                int resolutionIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.RESOLUTION);
                int dateTakenIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN);
                int categoryIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.CATEGORY);

                while (cursor.moveToNext()) {
//                    Log.d(TAG, "subscribe: data=" + cursor.getString(dataIndex)
//                            + "\ndateTaken=" + cursor.getString(dateTakenIndex)
//                            + "\nname=" + cursor.getString(nameIndex)
//                            + "\nartist=" + cursor.getString(artistIndex)
//                            + "\nmime=" + cursor.getString(mimeTypeIndex)
//                            + "\nsize=" + cursor.getLong(sizeIndex)
//                            + "\ntitle=" + cursor.getString(titleIndex)
//                            + "\ndescription=" + cursor.getString(descriptionIndex)
//                            + "\nresolution=" + cursor.getLong(resolutionIndex)
//                            + "\nwidth=" + cursor.getString(widthIndex)
//                            + "\nheight=" + cursor.getString(heightIndex)
//                            + "\ncategory=" + cursor.getString(categoryIndex));
                    /*data=/storage/emulated/0/apowersoft/record/ScreenRecord_20190326_141430.mp4
                    dateTaken=1553580882000
                    name=ScreenRecord_20190326_141430.mp4
                    artist=<unknown>
                    mime=video/mp4
                    size=4477477
                    title=ScreenRecord_20190326_141430
                    description=null
                    resolution=0
                    width=null
                    height=null
                    category=null*/
                    String path = cursor.getString(dataIndex);

                    MediaItem item = new MediaItem();
                    item.setPath(path); // 设置视频路径
                    item.setDuration(cursor.getInt(durationIndex)); // 设置视频时长
                    item.setTitle(cursor.getString(nameIndex)); // 设置文件名称
                    item.setSize(cursor.getLong(sizeIndex)); // 设置文件大小
                    item.setMimeType(cursor.getString(mimeTypeIndex)); // 设置mime类型
                    item.setType(MediaItem.VIDEO_ITEM); // 设置MediaItem的类型
                    item.setFlag(MediaItem.STATUS_NORMAL); // 设置状态
                    item.setSelected(false);
                    item.setTimeStamps(System.currentTimeMillis());
                    item.setProgress(0);
                    item.setCoverUrl(path); // 设置封面图路径 可以使用VideoUtils.getVideoCover(path)来设置封面图
                    list.add(item);
                }
                cursor.close();

                observableEmitter.onNext(list);
                observableEmitter.onComplete();
            }
        });
    }


    // 获取所有图片文件
    public static Observable<List<MediaItem>> getAllImage() {
        return Observable.create(new ObservableOnSubscribe<List<MediaItem>>() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void subscribe(ObservableEmitter<List<MediaItem>> observableEmitter) {
                String[] projection = {
                        MediaStore.Images.Media.DATA,
                        MediaStore.Images.Media.SIZE,
                        MediaStore.Images.Media.MIME_TYPE,
                        MediaStore.Images.Media.DATE_TAKEN,
                        MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                        MediaStore.Images.Media.MINI_THUMB_MAGIC,
                        MediaStore.Images.Media.TITLE,
                        MediaStore.Images.Media.DESCRIPTION,
                        MediaStore.Images.Media.ORIENTATION,
                        MediaStore.Images.Media.WIDTH,
                        MediaStore.Images.Media.HEIGHT,
                };

                String selection = null;
                String[] selectionArgs = null;

                Cursor cursor = AppUtils.app().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        projection,
                        selection,
                        selectionArgs,
                        MediaStore.Images.Media.DEFAULT_SORT_ORDER);

                if (cursor == null) {
                    observableEmitter.onError(new Throwable("Failed search image"));
                    return;
                }

                String[] columns = cursor.getColumnNames();
                for (String column : columns) {
                    Log.d(TAG, "getAllImage: column name=" + column);
                }


                List<MediaItem> list = new ArrayList<>();
                int dataIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                int sizeIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE);
                int mimeTypeIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE);
                int dateTakenIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN);
                int nameIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
                int thumbIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.MINI_THUMB_MAGIC);
                int titleIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.TITLE);
                int descriptionIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DESCRIPTION);
                int orientationIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.ORIENTATION);
                int widthIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.WIDTH);
                int heightIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.HEIGHT);


                while (cursor.moveToNext()) {
//                    Log.d(TAG, "subscribe: data=" + cursor.getString(dataIndex)
//                            + "\ndateTaken=" + cursor.getString(dateTakenIndex)
//                            + "\nname=" + cursor.getString(nameIndex)
//                            + "\nthumb=" + cursor.getString(thumbIndex)
//                            + "\nmime=" + cursor.getString(mimeTypeIndex)
//                            + "\nsize=" + cursor.getLong(sizeIndex)
//                            + "\ntitle=" + cursor.getString(titleIndex)
//                            + "\ndescription=" + cursor.getString(descriptionIndex)
//                            + "\norientation=" + cursor.getLong(orientationIndex)
//                            + "\nwidth=" + cursor.getString(widthIndex)
//                            + "\nheight=" + cursor.getString(heightIndex));
                    /*data=/storage/emulated/0/tencent/MicroMsg/WeiXin/wx_camera_1551712490389.jpg
                    dateTaken=1551712490000
                    name=WeiXin
                    thumb=-8338229882799915179
                    mime=image/jpeg
                    size=573939
                    title=wx_camera_1551712490389
                    description=null
                    orientation=0
                    width=1080
                    height=1920*/
                    String path = cursor.getString(dataIndex);

                    MediaItem item = new MediaItem();
                    item.setPath(path); // 设置图片路径
                    item.setTimeStamps(cursor.getLong(dateTakenIndex)); // 设置拍照时间
                    item.setTitle(cursor.getString(nameIndex)); // 设置文件名称
                    item.setSize(cursor.getLong(sizeIndex)); // 设置文件大小
                    item.setMimeType(cursor.getString(mimeTypeIndex)); // 设置mime类型
                    item.setType(MediaItem.IMAGE_ITEM); // 设置MediaItem的类型
                    item.setFlag(MediaItem.STATUS_NORMAL); // 设置状态
                    item.setSelected(false);
                    item.setProgress(0);
                    item.setCoverUrl(path); // 设置封面图
                    list.add(item);
                }
                cursor.close();
                Log.d(TAG, "subscribe: ");
                observableEmitter.onNext(list);
                observableEmitter.onComplete();
            }
        });
    }

    // 获取所有音频文件
    public static Observable<List<MediaItem>> getAllAudio() {
        return Observable.create(new ObservableOnSubscribe<List<MediaItem>>() {
            @Override
            public void subscribe(ObservableEmitter<List<MediaItem>> observableEmitter) {
                String[] projection = {
                        MediaStore.Audio.Media.DATA,
                        MediaStore.Audio.Media.SIZE,
                        MediaStore.Audio.Media.MIME_TYPE,
                        MediaStore.Audio.Media.ALBUM,
                        MediaStore.Audio.Media.ARTIST,
                        MediaStore.Audio.Media.DISPLAY_NAME,
                        MediaStore.Audio.Media.TITLE,
                        MediaStore.Audio.Media.DURATION,
                        MediaStore.Audio.Media.COMPOSER,
                };

                String selection = null;
                String[] selectionArgs = null;

                Cursor cursor = AppUtils.app().getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        projection,
                        selection,
                        selectionArgs,
                        MediaStore.Audio.Media.DEFAULT_SORT_ORDER);

                if (cursor == null) {
                    observableEmitter.onError(new Throwable("Failed search audio"));
                    return;
                }

//                String[] columns = cursor.getColumnNames();
//                for (String column : columns) {
//                    Log.d(TAG, "getAllAudio: column name=" + column);
//                }

                List<MediaItem> list = new ArrayList<>();
                int dataIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
                int albumIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM);
                int nameIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME);
                int artistIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST);
                int mimeTypeIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.MIME_TYPE);
                int sizeIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE);
                int titleIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE);
                int composerIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.COMPOSER);
                int durationIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION);


                while (cursor.moveToNext()) {
//                    Log.d(TAG, "subscribe: data=" + cursor.getString(dataIndex)
//                            + "\nalbum=" + cursor.getString(albumIndex)
//                            + "\nname=" + cursor.getString(nameIndex)
//                            + "\nartist=" + cursor.getString(artistIndex)
//                            + "\nmime=" + cursor.getString(mimeTypeIndex)
//                            + "\nsize=" + cursor.getLong(sizeIndex)
//                            + "\ntitle=" + cursor.getString(titleIndex)
//                            + "\ncomposer=" + cursor.getString(composerIndex)
//                            + "\nduration=" + cursor.getLong(durationIndex));
                    /*
                    data=/storage/emulated/0/com.corntree.mud.unknown/Res/sound/begin_game.mp3
                    album=sound
                    name=begin_game.mp3
                    artist=<unknown>
                    mime=audio/mpeg
                    size=186099
                    title=begin_game
                    composer=null
                    duration=7711*/
                    String path = cursor.getString(dataIndex);

                    MediaItem item = new MediaItem();
                    item.setPath(path); // 设置图片路径
                    item.setTimeStamps(System.currentTimeMillis()); // 设置时间
                    item.setTitle(cursor.getString(nameIndex)); // 设置文件名称
                    item.setSize(cursor.getLong(sizeIndex)); // 设置文件大小
                    item.setDuration(cursor.getLong(durationIndex)); // 设置音频时长
                    item.setMimeType(cursor.getString(mimeTypeIndex)); // 设置mime类型
                    item.setType(MediaItem.AUDIO_ITEM); // 设置MediaItem的类型
                    item.setFlag(MediaItem.STATUS_NORMAL); // 设置状态
                    item.setSelected(false);
                    item.setProgress(0);
                    list.add(item);
                }
                cursor.close();
                Log.d(TAG, "subscribe: ");
                observableEmitter.onNext(list);
                observableEmitter.onComplete();
            }
        });
    }


    // 获取可播放的文件
    public static Observable<List<MediaItem>> getPlayList() {
        return Observable.create(new ObservableOnSubscribe<List<MediaItem>>() {
            @Override
            public void subscribe(ObservableEmitter<List<MediaItem>> observableEmitter) {
                String[] projection = {
                        MediaStore.Audio.Playlists.DATA,
                        MediaStore.Audio.Playlists.NAME,
                };

                String selection = null;
                String[] selectionArgs = null;

                Cursor cursor = AppUtils.app().getContentResolver().query(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
                        projection,
                        selection,
                        selectionArgs,
                        MediaStore.Audio.Playlists.DEFAULT_SORT_ORDER);

                if (cursor == null) {
                    observableEmitter.onError(new Throwable("Failed search play list"));
                    return;
                }

//                String[] columns = cursor.getColumnNames();
//                for (String column : columns) {
//                    Log.d(TAG, "getPlayList: column name=" + column);
//                }

                List<MediaItem> list = new ArrayList<>();
                int dataIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Playlists.DATA);
                int nameIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Playlists.NAME);


                while (cursor.moveToNext()) {
//                    Log.d(TAG, "subscribe: data=" + cursor.getString(dataIndex)
//                            + "\nname=" + cursor.getString(nameIndex));
                    /*data=/storage/emulated/0/Browser/视频/.1b22fccb4e86f2475ab022705270827b/1b22fccb4e86f2475ab022705270827b.m3u8
                    name=1b22fccb4e86f2475ab022705270827b*/
                    String path = cursor.getString(dataIndex);

                    MediaItem item = new MediaItem();
                    item.setPath(path); // 设置图片路径
                    item.setTimeStamps(System.currentTimeMillis()); // 设置时间
                    item.setTitle(cursor.getString(nameIndex)); // 设置文件名称
                    item.setType(MediaItem.AUDIO_ITEM); // 设置MediaItem的类型
                    item.setFlag(MediaItem.STATUS_NORMAL); // 设置状态
                    item.setSelected(false);
                    item.setProgress(0);
                    list.add(item);
                }
                cursor.close();
                Log.d(TAG, "subscribe: ");
                observableEmitter.onNext(list);
                observableEmitter.onComplete();
            }
        });
    }

    // 获取pdf image video audio html c txt pdf等文件
    public static Observable<List<MediaItem>> getFileByType(final String type) {
        return Observable.create(new ObservableOnSubscribe<List<MediaItem>>() {
            @Override
            public void subscribe(ObservableEmitter<List<MediaItem>> observableEmitter) {
                String[] projection = {
                        MediaStore.Files.FileColumns.DATA,
                        MediaStore.Files.FileColumns.SIZE,
                        MediaStore.Files.FileColumns.PARENT,
                        MediaStore.Files.FileColumns.MIME_TYPE,
                        MediaStore.Files.FileColumns.TITLE,
                        MediaStore.Files.FileColumns.DISPLAY_NAME,
                        MediaStore.Files.FileColumns.MEDIA_TYPE
                };
                Pair<String, String[]> pair;

                if ((pair = MimeUtils.genMimeType(type)) == null) {
                    observableEmitter.onError(new Throwable("Failed search file type=" + type));
                    return;
                }

                String selection = pair.first;
                String[] selectionArgs = pair.second;


                Cursor cursor = AppUtils.app().getContentResolver().query(MediaStore.Files.getContentUri("external"),
                        projection,
                        selection,
                        selectionArgs,
                        null);

                if (cursor == null) {
                    observableEmitter.onError(new Throwable("Failed search file type=" + type));
                    return;
                }

//                String[] columns = cursor.getColumnNames();
//                for (String column : columns) {
//                    Log.d(TAG, "getFileByType: column name=" + column);
//                }

                List<MediaItem> list = new ArrayList<>();
                int dataIndex = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA);
                int nameIndex = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME);
                int parentIndex = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.PARENT);
                int sizeIndex = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE);
                int mimeIndex = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MIME_TYPE);
                int titleIndex = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.TITLE);
                int mediaTypeIndex = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MEDIA_TYPE);


                while (cursor.moveToNext()) {
                    Log.d(TAG, "subscribe: data=" + cursor.getString(dataIndex)
                            + "\nname=" + cursor.getString(nameIndex)
                            + "\nparent=" + cursor.getString(parentIndex)
                            + "\nsize=" + cursor.getString(sizeIndex)
                            + "\nmime=" + cursor.getString(mimeIndex)
                            + "\ntitle=" + cursor.getString(titleIndex)
                            + "\nmediaType=" + cursor.getString(mediaTypeIndex));
                    String path = cursor.getString(dataIndex);

                    MediaItem item = new MediaItem();
                    item.setPath(path); // 设置图片路径
                    item.setCoverUrl(path);
                    item.setTimeStamps(System.currentTimeMillis()); // 设置时间
                    item.setTitle(cursor.getString(nameIndex)); // 设置文件名称
                    item.setType(MediaItem.AUDIO_ITEM); // 设置MediaItem的类型
                    item.setFlag(MediaItem.STATUS_NORMAL); // 设置状态
                    item.setMimeType(cursor.getString(mimeIndex));
                    item.setSelected(false);
                    item.setProgress(0);
                    list.add(item);
                }
                cursor.close();
                Log.d(TAG, "subscribe: ");
                observableEmitter.onNext(list);
                observableEmitter.onComplete();
            }
        });
    }
}
