package com.example.commonlibrary.utils;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import androidx.core.util.Pair;

public class MimeUtils {
    // 判断当前mime类型是哪个
    /*
    ------word-----
    application/msword 	                    doc
    application/x-dot                       dot
    -----txt-----
    text/plain 	                            txt/bas/c/h
    -----pdf-----
    application/pdf                         pdf
    -----excel-----
    application/vnd.ms-excel                xla/xlc/xlm/xls/xlt/xlw
    application/x-excel                     xll
    -----ppt-----
    application/vnd.ms-powerpoint           ppt/pps/pot
    -----压缩包-----
    application/x-compressed 	            tgz
    application/x-gzip 	                    gz
    application/x-tar 	                    tar
    application/zip 	                    zip
    -----可执行文件-----
    application/octet-stream 	            exe
    application/x-sh 	                    sh
    -----音频-----
    audio/mpeg 	                            mp3
    audio/x-aiff 	                        aiff/aif/aifc
    audio/x-mpegurl 	                    m3u
    audio/x-pn-realaudio 	                ram/ra
    audio/x-wav 	                        wav
    audio/midi                              mid
    audio/x-midi                            midi
    audio/webm                              weba
    -----视频------
    video/mpeg 	                            mpv2/mpg/mpeg/mpe/mpa/mp2
    video/quicktime 	                    mov/qt
    video/x-ms-asf 	                        asx/asf/asr
    video/x-msvideo 	                    avi
    video/mp4                               mp4
    video/webm                              webm
    -----图片-----
    image/bmp 	                            bmp // 位图
    image/gif 	                            gif // 动图
    image/jpeg 	                            jpeg/jpe/jpg
    image/svg+xml 	                        svg
    image/x-icon 	                        ico
    image/png                               png
    image/webp                              webp
    -----其他-----
    application/octet-stream 	            bin
    application/octet-stream 	            class
    application/x-javascript 	            js
    text/html 	                            html/htm/stm
    text/css 	                            css
    x-world/x-vrml 	                        xof/flr/xaf/wrz/wrl/vrml
    text/x-java-source,java                 java
     */

    public static String TYPE_IMAGE = "mime/image";
    public static String TYPE_VIDEO = "mime/video";
    public static String TYPE_AUDIO = "mime/audio";
    public static String TYPE_TEXT = "mime/text";
    public static String TYPE_PDF = "mime/pdf";
    public static String TYPE_WORD = "mime/word";
    public static String TYPE_PPT = "mime/ppt";
    public static String TYPE_OTHER = "mime/other";
    public static String TYPE_EXCEL = "mime/excel";
    public static String TYPE_COMPRESSION = "mime/compression";


    public static Pair<String, String[]> genMimeType(String type) {
        if (type == null || TextUtils.isEmpty(type)) {
            return null;
        }
        if (TextUtils.equals(type, TYPE_IMAGE)) {
            return genImageMimeType();
        } else if (TextUtils.equals(type, TYPE_VIDEO)) {
            return genVideoMimeType();
        } else if (TextUtils.equals(type, TYPE_AUDIO)) {
            return genAudioMimeType();
        } else if (TextUtils.equals(type, TYPE_TEXT)) {
            return genTextMimeType();
        } else if (TextUtils.equals(type, TYPE_PDF)) {
            return genPdfMimeType();
        } else if (TextUtils.equals(type, TYPE_WORD)) {
            return genWordMimeType();
        } else if (TextUtils.equals(type, TYPE_PPT)) {
            return genPPTMimeType();
        } else if (TextUtils.equals(type, TYPE_OTHER)) {
            return genOtherMimeType();
        } else if (TextUtils.equals(type, TYPE_EXCEL)) {
            return genExcelMimeType();
        } else if (TextUtils.equals(type, TYPE_COMPRESSION)) {
            return genCompressionMimeType();
        }
        return null;
    }



    // 包含png gif jpeg svg jpg webp bmp扩展名的图片
    private static Pair<String, String[]> genImageMimeType() {
        String selection = MediaStore.Files.FileColumns.MIME_TYPE + "=? or "
                + MediaStore.Files.FileColumns.MIME_TYPE + "=? or "
                + MediaStore.Files.FileColumns.MIME_TYPE + "=? or "
                + MediaStore.Files.FileColumns.MIME_TYPE + "=? or "
                + MediaStore.Files.FileColumns.MIME_TYPE + "=? or "
                + MediaStore.Files.FileColumns.MIME_TYPE + "=?";
        String[] selectionArgs = {
                MimeTypeMap.getSingleton().getMimeTypeFromExtension("png"),
                MimeTypeMap.getSingleton().getMimeTypeFromExtension("gif"),
                MimeTypeMap.getSingleton().getMimeTypeFromExtension("jpeg"),
                MimeTypeMap.getSingleton().getMimeTypeFromExtension("webp"),
                MimeTypeMap.getSingleton().getMimeTypeFromExtension("svg"),
                MimeTypeMap.getSingleton().getMimeTypeFromExtension("bmp"),
        };
        return new Pair<>(selection, selectionArgs);
    }

    // 包含mpg qt avi webm mp4 asf的所有视频
    private static Pair<String, String[]> genVideoMimeType() {
        String selection = MediaStore.Files.FileColumns.MIME_TYPE + "=? or "
                + MediaStore.Files.FileColumns.MIME_TYPE + "=? or "
                + MediaStore.Files.FileColumns.MIME_TYPE + "=? or "
                + MediaStore.Files.FileColumns.MIME_TYPE + "=? or "
                + MediaStore.Files.FileColumns.MIME_TYPE + "=? or "
                + MediaStore.Files.FileColumns.MIME_TYPE + "=?";
        String[] selectionArgs = {
                MimeTypeMap.getSingleton().getMimeTypeFromExtension("mpg"),
                MimeTypeMap.getSingleton().getMimeTypeFromExtension("qt"),
                MimeTypeMap.getSingleton().getMimeTypeFromExtension("avi"),
                MimeTypeMap.getSingleton().getMimeTypeFromExtension("webm"),
                MimeTypeMap.getSingleton().getMimeTypeFromExtension("mp4"),
                MimeTypeMap.getSingleton().getMimeTypeFromExtension("asf"),
        };
        return new Pair<>(selection, selectionArgs);
    }

    // 包含mp3 aiff/aif/aifc m3u ram/ra wav mid midi weba的音频
    private static Pair<String, String[]> genAudioMimeType() {
        String selection = MediaStore.Files.FileColumns.MIME_TYPE + "=? or "
                + MediaStore.Files.FileColumns.MIME_TYPE + "=? or "
                + MediaStore.Files.FileColumns.MIME_TYPE + "=? or "
                + MediaStore.Files.FileColumns.MIME_TYPE + "=? or "
                + MediaStore.Files.FileColumns.MIME_TYPE + "=? or "
                + MediaStore.Files.FileColumns.MIME_TYPE + "=? or "
                + MediaStore.Files.FileColumns.MIME_TYPE + "=? or "
                + MediaStore.Files.FileColumns.MIME_TYPE + "=?";
        String[] selectionArgs = {
                MimeTypeMap.getSingleton().getMimeTypeFromExtension("mp3"),
                MimeTypeMap.getSingleton().getMimeTypeFromExtension("aif"),
                MimeTypeMap.getSingleton().getMimeTypeFromExtension("m3u"),
                MimeTypeMap.getSingleton().getMimeTypeFromExtension("ram"),
                MimeTypeMap.getSingleton().getMimeTypeFromExtension("wav"),
                MimeTypeMap.getSingleton().getMimeTypeFromExtension("mid"),
                MimeTypeMap.getSingleton().getMimeTypeFromExtension("midi"),
                // weba
                "audio/webm",
        };
        return new Pair<>(selection, selectionArgs);
    }

    // 包含c txt css js html class java的所有文件
    private static Pair<String, String[]> genTextMimeType() {
        String selection = MediaStore.Files.FileColumns.MIME_TYPE + "=? or "
                + MediaStore.Files.FileColumns.MIME_TYPE + "=? or "
                + MediaStore.Files.FileColumns.MIME_TYPE + "=? or "
                + MediaStore.Files.FileColumns.MIME_TYPE + "=? or "
                + MediaStore.Files.FileColumns.MIME_TYPE + "=?";
        String[] selectionArgs = {
                MimeTypeMap.getSingleton().getMimeTypeFromExtension("java"),
                MimeTypeMap.getSingleton().getMimeTypeFromExtension("txt"),
                MimeTypeMap.getSingleton().getMimeTypeFromExtension("css"),
                // js
                "application/javascript",
                MimeTypeMap.getSingleton().getMimeTypeFromExtension("html"),
        };
        return new Pair<>(selection, selectionArgs);
    }

    // 包含pdf所有文件
    private static Pair<String, String[]> genPdfMimeType() {
        String selection = MediaStore.Files.FileColumns.MIME_TYPE + "=?";
        String[] selectionArgs = {
                MimeTypeMap.getSingleton().getMimeTypeFromExtension("pdf"),
        };
        return new Pair<>(selection, selectionArgs);
    }

    // 包含excel的所有文件
    private static Pair<String, String[]> genExcelMimeType() {
        String selection = MediaStore.Files.FileColumns.MIME_TYPE + "=? or "
                + MediaStore.Files.FileColumns.MIME_TYPE + "=?";
        String[] selectionArgs = {
                MimeTypeMap.getSingleton().getMimeTypeFromExtension("xls" +
                        ""),
                // xlsx没作用
                MimeTypeMap.getSingleton().getMimeTypeFromExtension("xlsx"),
        };
        return new Pair<>(selection, selectionArgs);
    }

    // 包含word所有文件
    private static Pair<String, String[]> genWordMimeType() {
//        String selection = MediaStore.Files.FileColumns.MIME_TYPE + "=? or "
//                + MediaStore.Files.FileColumns.MIME_TYPE + "=?";
//        String[] selectionArgs = {
//                MimeTypeMap.getSingleton().getMimeTypeFromExtension("doc"),
//                // 没有正常检索
//                MimeTypeMap.getSingleton().getMimeTypeFromExtension("docx"),
//        };
        String selection = MediaStore.Files.FileColumns.DISPLAY_NAME + "=? or "
                + MediaStore.Files.FileColumns.DISPLAY_NAME + "=?";
        String[] selectionArgs = {
                "*.doc",
                "*.docx",
        };
        return new Pair<>(selection, selectionArgs);
    }

    // 包含ppt所有文件
    // 没有正常检索 能成功显示ppt 但是不能显示pptx
    private static Pair<String, String[]> genPPTMimeType() {
        String selection = MediaStore.Files.FileColumns.MIME_TYPE + "=? or "
                + MediaStore.Files.FileColumns.MIME_TYPE + "=?";
        String[] selectionArgs = {
                //MimeTypeMap.getSingleton().getMimeTypeFromExtension("pptx"),
                MimeTypeMap.getSingleton().getMimeTypeFromExtension("ppt"),
        };
        return new Pair<>(selection, selectionArgs);
    }

    // 包含tar zip gz的所有文件
    private static Pair<String, String[]> genCompressionMimeType() {
        String selection = MediaStore.Files.FileColumns.MIME_TYPE + "=? or "
                + MediaStore.Files.FileColumns.MIME_TYPE + "=?";
        String[] selectionArgs = {
                MimeTypeMap.getSingleton().getMimeTypeFromExtension("tar"),
                MimeTypeMap.getSingleton().getMimeTypeFromExtension("zip"),
        };
        return new Pair<>(selection, selectionArgs);
    }

    // 包含exe sh所有文件
    // 无法正常搜索
    private static Pair<String, String[]> genOtherMimeType() {
        String selection = MediaStore.Files.FileColumns.MIME_TYPE + "=? or "
                + MediaStore.Files.FileColumns.MIME_TYPE + "=?";
        String[] selectionArgs = {
                // exe
                "application/x-msdownload",
                // sh
                "application/x-sh",
        };
        return new Pair<>(selection, selectionArgs);
    }
}
