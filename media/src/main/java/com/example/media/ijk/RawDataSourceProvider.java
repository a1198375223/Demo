package com.example.media.ijk;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.net.Uri;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import tv.danmaku.ijk.media.player.misc.IMediaDataSource;

public class RawDataSourceProvider implements IMediaDataSource {
    private static final String TAG = "RawDataSourceProvider@";
    private AssetFileDescriptor mDescriptor;
    private byte[] mMediaBytes;

    public RawDataSourceProvider(AssetFileDescriptor descriptor) {
        Log.d(TAG, "create");
        this.mDescriptor = descriptor;
    }

    @Override
    public int readAt(long position, byte[] buffer, int offset, int size) {
        Log.d(TAG, "readAt: position: " + position + " offset: " + offset + " size: " + size + " buffer: " + Arrays.toString(buffer));
        if (position + 1 >= mMediaBytes.length) {
            return -1;
        }

        int length;

        if (position + size < mMediaBytes.length) {
            length = size;
        } else {
            length = (int) (mMediaBytes.length - position);
            if (length > buffer.length) {
                length = buffer.length;
            }

            length--;
        }

        System.arraycopy(mMediaBytes, (int) position, buffer, offset, length);
        return length;
    }

    @Override
    public long getSize() throws IOException{
        Log.d(TAG, "getSize: ");
        long length = mDescriptor.getLength();
        if (mMediaBytes == null) {
            InputStream inputStream = mDescriptor.createInputStream();
            mMediaBytes = readBytes(inputStream);
        }
        return length;
    }

    @Override
    public void close() throws IOException{
        Log.d(TAG, "close: ");
        if (mDescriptor != null) {
            mDescriptor.close();
        }

        mDescriptor = null;
        mMediaBytes = null;
    }


    private byte[] readBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();

        int bufferSize = 1024;

        byte[] buffer = new byte[bufferSize];

        int len;

        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }

        return byteBuffer.toByteArray();
    }


    public static RawDataSourceProvider create(Context context, Uri uri) {
        Log.d(TAG, "static create.");
        try {
            AssetFileDescriptor fileDescriptor = context.getContentResolver().openAssetFileDescriptor(uri, "r");
            return new RawDataSourceProvider(fileDescriptor);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
