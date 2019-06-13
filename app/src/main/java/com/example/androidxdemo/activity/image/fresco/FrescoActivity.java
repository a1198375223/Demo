package com.example.androidxdemo.activity.image.fresco;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.androidxdemo.R;
import com.example.androidxdemo.base.image.FrescoWrapper;
import com.example.androidxdemo.base.image.ImageFactory;
import com.example.androidxdemo.utils.FrescoUtils;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.image.ImageInfo;

import java.util.Objects;

public class FrescoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fresco);
        final String url = "http://pic29.nipic.com/20130601/12122227_123051482000_2.jpg";
        final Uri uri = Uri.parse(url);
        final String small_url = "https://img3.duitang.com/uploads/blog/201403/04/20140304111500_FFkWB.jpeg";
        final Uri small_uri = Uri.parse(small_url);
        final SimpleDraweeView image = findViewById(R.id.image);
        final TextView in_bitmap_memory = findViewById(R.id.is_in_bitmap_memory);
        final TextView in_disk = findViewById(R.id.is_in_disk);
        final TextView in_memory = findViewById(R.id.is_in_memory);
        final TextView in_file = findViewById(R.id.is_in_file);
        final TextView file_path = findViewById(R.id.file_path);

        in_bitmap_memory.setText("in bitmap_memory=" + Fresco.getImagePipeline().isInBitmapMemoryCache(uri));
        in_disk.setText("in disk=" + Fresco.getImagePipeline().isInDiskCache(uri).getResult());
        in_memory.setText("in memory=" + FrescoUtils.isCachedInMemory(uri));
        in_file.setText("in file=" + FrescoUtils.isCachedInFile(uri));
        file_path.setText("in null");


        findViewById(R.id.load_image).setOnClickListener(v -> FrescoUtils.loadImage(image, ImageFactory.newHttpImage(url)
                .setLowImageUri(small_uri)
                .setCallBack(new FrescoWrapper(){
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void processWithInfo(ImageInfo info) {
                        in_bitmap_memory.setText("in bitmap_memory=" + Fresco.getImagePipeline().isInBitmapMemoryCache(uri));
                        in_disk.setText("in disk=" + Fresco.getImagePipeline().isInDiskCache(uri).getResult());
                        in_memory.setText("in memory=" + FrescoUtils.isCachedInMemory(uri));
                        in_file.setText("in file=" + FrescoUtils.isCachedInFile(uri));
                        if (FrescoUtils.isCachedInFile(uri)) {
                            file_path.setText(Objects.requireNonNull(FrescoUtils.getCacheFileFromFrescoDiskCache(uri)).getPath());
                        }
                    }
                }).build(), true, null));



    }
}
