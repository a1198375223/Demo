package com.example.androidxdemo.provider;

import android.annotation.TargetApi;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract;
import android.provider.DocumentsProvider;
import android.util.Log;
import android.webkit.MimeTypeMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;


import com.example.androidxdemo.R;
import com.example.commonlibrary.utils.AppUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * 一个自定义的文档提供起, 可以使用这个类来创建文档, 打开文档, 删除文档等等操作.
 */
@TargetApi(Build.VERSION_CODES.KITKAT)
public class MyDocumentProvider extends DocumentsProvider {
    private static final String TAG = "MyDocumentProvider";

    // flag类型
    // FLAG_SUPPORTS_DELETE     : 删除整个document  -> deleteDocument() 影响所有parent
    // FLAG_SUPPORTS_RENAME     : 重命名document    -> renameDocument()
    // FLAG_SUPPORTS_COPY       : 拷贝document      -> copyDocument()
    // FLAG_SUPPORTS_MOVE       : 移动document      -> moveDocument()
    // FLAG_SUPPORTS_REMOVE     : 删除document      -> removeDocument() 影响单个parent
    private static final String[] DEFAULT_ROOT_PROJECTION = new String[] {
            DocumentsContract.Root.COLUMN_ROOT_ID,          // 一个用指明root的第一无二的id(String)
            DocumentsContract.Root.COLUMN_SUMMARY,          // root的标题
            DocumentsContract.Root.COLUMN_FLAGS,            // 用来表明支持的操作,如果什么都不支持就写0
            DocumentsContract.Root.COLUMN_DOCUMENT_ID,      // 根目录的路径
            DocumentsContract.Root.COLUMN_TITLE,            // root的标题
            DocumentsContract.Root.COLUMN_ICON,             // 用来展示root的resId
            DocumentsContract.Root.COLUMN_AVAILABLE_BYTES   // 可用空间
    };

    private static final String[] DEFAULT_DOCUMENT_PROJECTION = new String[]{
            DocumentsContract.Document.COLUMN_DOCUMENT_ID,  // 一个用指明document的第一无二的id(String)
            DocumentsContract.Document.COLUMN_DISPLAY_NAME, // 用户可视的名称
            DocumentsContract.Document.COLUMN_FLAGS,        // 用来表明支持的操作,如果什么都不支持就写0
            DocumentsContract.Document.COLUMN_MIME_TYPE,    // document的mime类型
            DocumentsContract.Document.COLUMN_SIZE,         // document的size
            DocumentsContract.Document.COLUMN_LAST_MODIFIED // document的最后修改时间
    };

    private static final int MAX_SEARCH_RESULTS = 20;
    private static final int MAX_LAST_MODIFIED = 5;


    private File mBaseDir;
    private static final String ROOT = "root";

    /**
     * 直接返回true就好了
     */
    @Override
    public boolean onCreate() {
        Log.d(TAG, "onCreate: ");

        // 返回系统的文件路径
        mBaseDir = AppUtils.app().getFilesDir();


        return true;
    }


    /**
     * 组装生成一个在根目录下的游标,和sd卡的游标
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public Cursor queryRoots(String[] projection) {
        Log.d(TAG, "queryRoots: projection=" + Arrays.toString(projection));
        MatrixCursor result = new MatrixCursor(projection != null ? projection : DEFAULT_ROOT_PROJECTION){
            @Override
            public Bundle getExtras() {
                Bundle bundle = new Bundle();
                // DocumentsContract.EXTRA_LOADING : 让加载不一次性加载完,提高速度.
                // DocumentsContract.EXTRA_INFO :
                // DocumentsContract.EXTRA_ERROR :
                bundle.putBoolean(DocumentsContract.EXTRA_LOADING, true);
                return bundle;
            }
        };


        MatrixCursor.RowBuilder row = result.newRow();
        row.add(DocumentsContract.Root.COLUMN_ROOT_ID, ROOT);
        row.add(DocumentsContract.Root.COLUMN_DOCUMENT_ID, getDocIdForFile(mBaseDir));
        row.add(DocumentsContract.Root.COLUMN_TITLE, AppUtils.app().getString(R.string.app_name));
        row.add(DocumentsContract.Root.COLUMN_FLAGS, DocumentsContract.Root.FLAG_SUPPORTS_CREATE | DocumentsContract.Root.FLAG_SUPPORTS_RECENTS |
                DocumentsContract.Root.FLAG_LOCAL_ONLY | DocumentsContract.Root.FLAG_SUPPORTS_SEARCH);
        row.add(DocumentsContract.Root.COLUMN_ICON, R.mipmap.ic_launcher);
        row.add(DocumentsContract.Root.COLUMN_SUMMARY, AppUtils.app().getString(R.string.home));
        row.add(DocumentsContract.Root.COLUMN_AVAILABLE_BYTES, mBaseDir.getFreeSpace());
        //row.add(DocumentsContract.Root.COLUMN_MIME_TYPES, getChildMimeType(mBaseDir));
        return result;
    }


    // 查询最近修改文档
    @Override
    public Cursor queryRecentDocuments(String rootId, String[] projection) throws FileNotFoundException {
        Log.d(TAG, "queryRecentDocuments: rootId=" + rootId + " projection=" + Arrays.toString(projection));
        MatrixCursor result = new MatrixCursor(projection != null ? projection : DEFAULT_DOCUMENT_PROJECTION);
        File parent = getFileForDocId(rootId);
        PriorityQueue<File> lastModifiedFiles = new PriorityQueue<>(5, (o1, o2) -> Long.compare(o1.lastModified(), o2.lastModified()));

        LinkedList<File> pending = new LinkedList<>();
        pending.add(parent);

        while (!pending.isEmpty()) {
            File file = pending.removeFirst();
            if (file.isDirectory()) {
                Collections.addAll(pending, file.listFiles());
            } else {
                lastModifiedFiles.add(file);
            }
        }

        for (int i = 0; i < Math.min(MAX_LAST_MODIFIED + 1, lastModifiedFiles.size()); i++) {
            File file = lastModifiedFiles.remove();
            addFile(result, file);
        }
        return result;
    }


    @Override
    public Cursor querySearchDocuments(String rootId, String query, String[] projection) throws FileNotFoundException {
        Log.d(TAG, "queryRecentDocuments: rootId=" + rootId + " query=" + query + " projection=" + Arrays.toString(projection));
        MatrixCursor result = new MatrixCursor(projection != null ? projection : DEFAULT_DOCUMENT_PROJECTION);
        File parent = getFileForDocId(rootId);

        LinkedList<File> pending = new LinkedList<>();
        pending.add(parent);

        while (!pending.isEmpty() && result.getCount() < MAX_SEARCH_RESULTS) {
            File file = pending.removeFirst();
            if (file.isDirectory()) {
                Collections.addAll(pending, file.listFiles());
            } else {
                if (file.getName().toLowerCase().contains(query)) {
                    addFile(result, file);
                }
            }
        }
        return result;
    }

    /**
     * 返回的信息与queryChildDocuments传递的信息相同
     */
    @Override
    public Cursor queryDocument(String documentId, String[] projection) throws FileNotFoundException {
        Log.d(TAG, "queryDocument: documentId=" + documentId + " projection=" + Arrays.toString(projection));
        MatrixCursor result = new MatrixCursor(projection != null ? projection : DEFAULT_DOCUMENT_PROJECTION);
        addFile(result, getFileForDocId(documentId));
        return result;
    }


    /**
     * 将子文件添加到父文档中
     */
    @Override
    public Cursor queryChildDocuments(String parentDocumentId, String[] projection, String sortOrder) throws FileNotFoundException {
        Log.d(TAG, "queryChildDocuments: parentDocumentId=" + parentDocumentId + " projection=" + Arrays.toString(projection) + " sortOrder=" + sortOrder);
        MatrixCursor result = new MatrixCursor(projection != null ? projection : DEFAULT_DOCUMENT_PROJECTION);
        File parent = getFileForDocId(parentDocumentId);
        for (File file : parent.listFiles()) {
            if (!file.getName().startsWith(".")) {
                addFile(result, file);
            }
        }
        return result;
    }

    /**
     * 组装cursor
     */
    private void addFile(@NonNull MatrixCursor cursor, @NonNull File file) throws FileNotFoundException {
        Log.d(TAG, "addFile: ");

        int flags = 0;

        if (file.isDirectory()) {
            if (file.isDirectory() && file.canWrite()) {
                flags |= DocumentsContract.Document.FLAG_DIR_SUPPORTS_CREATE;
            }
        } else if (file.canWrite()) {
            flags |= DocumentsContract.Document.FLAG_SUPPORTS_WRITE;
            flags |= DocumentsContract.Document.FLAG_SUPPORTS_DELETE;
        }

        String mimeType = getTypeForFile(file);
        if (mimeType.startsWith("image/")) {
            flags |= DocumentsContract.Document.FLAG_SUPPORTS_THUMBNAIL;
        }

        MatrixCursor.RowBuilder row = cursor.newRow();
        row.add(DocumentsContract.Document.COLUMN_DOCUMENT_ID, file.getAbsolutePath());
        row.add(DocumentsContract.Document.COLUMN_DISPLAY_NAME, file.getName());
        row.add(DocumentsContract.Document.COLUMN_MIME_TYPE, mimeType);
        row.add(DocumentsContract.Document.COLUMN_FLAGS, flags);
        row.add(DocumentsContract.Document.COLUMN_SIZE, file.length());
        row.add(DocumentsContract.Document.COLUMN_LAST_MODIFIED, file.lastModified());
        row.add(DocumentsContract.Document.COLUMN_ICON, R.mipmap.ic_launcher);
    }


    // 打开文档
    @Override
    public ParcelFileDescriptor openDocument(String documentId, String mode, @Nullable CancellationSignal signal) throws FileNotFoundException {
        Log.d(TAG, "openDocument: documentId=" + documentId + " mode=" + mode + " signal=" + (signal != null ? signal.toString() : null));
        File file = new File(documentId);
        boolean isWrite = mode.indexOf('w') != -1;
        if (isWrite) {
            try {
                return ParcelFileDescriptor.open(file, ParcelFileDescriptor.parseMode(mode), new Handler(Looper.getMainLooper()), new ParcelFileDescriptor.OnCloseListener() {
                    @Override
                    public void onClose(IOException e) {
                        Log.d(TAG, "onClose: file close");
                    }
                });
            } catch (IOException e) {
                throw new FileNotFoundException("Failed to open document with id " + documentId + " and mode " + mode);
            }
        } else {
            return ParcelFileDescriptor.open(file, ParcelFileDescriptor.parseMode(mode));
        }
    }

    // 创建文档
    @Override
    public String createDocument(String parentDocumentId, String mimeType, String displayName) throws FileNotFoundException {
        Log.d(TAG, "createDocument: parentDocumentId=" + parentDocumentId + " mimeType=" + mimeType + " displayName=" + displayName);
        File parent = getFileForDocId(parentDocumentId);
        File file = new File(parent.getPath(), displayName);
        try {
            file.createNewFile();
            file.setWritable(true);
            file.setReadable(true);
        } catch (IOException e) {
            throw new FileNotFoundException("Failed to create document with name " + displayName + " and documentId " + parentDocumentId);
        }
        return getDocIdForFile(file);
    }

    // 删除文档
    @Override
    public void deleteDocument(String documentId) throws FileNotFoundException {
        Log.d(TAG, "deleteDocument: documentId=" + documentId);
        File file = getFileForDocId(documentId);
        if (file.delete()) {
            Log.i(TAG, "deleteDocument: deleted file with id=" + documentId);
        } else {
            throw new FileNotFoundException("Failed to delete document with id " + documentId);
        }
    }

    /**
     * 显示缩略图
     */
    @Override
    public AssetFileDescriptor openDocumentThumbnail(String documentId, Point sizeHint, CancellationSignal signal) throws FileNotFoundException {
        Log.d(TAG, "openDocumentThumbnail: documentId=" + documentId + " sizeHint=" + sizeHint.toString() + " signal=" + signal.toString());
        File file = getFileForDocId(documentId);
        ParcelFileDescriptor pfd = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
        return new AssetFileDescriptor(pfd, 0, AssetFileDescriptor.UNKNOWN_LENGTH);
    }



    /**
     * 判断是否是子文档
     */
    @Override
    public boolean isChildDocument(String parentDocumentId, String documentId) {
        Log.d(TAG, "isChildDocument: parentDocumentId=" + parentDocumentId + " documentId=" + documentId);
        //return documentId.startsWith(parentDocumentId);
        return super.isChildDocument(parentDocumentId, documentId);
    }


    /**
     * 获取文档的类型
     */
    @Override
    public String getDocumentType(String documentId) throws FileNotFoundException {
        Log.d(TAG, "getDocumentType: documentId=" + documentId);
        File file = getFileForDocId(documentId);
        return getTypeForFile(file);
    }


    // 处理file的路径
    private String getDocIdForFile(@NonNull File file) {
        String path = file.getAbsolutePath();
        String rootPath = mBaseDir.getAbsolutePath();
        if (rootPath.equals(path)) {
            path = "";
        } else if (rootPath.endsWith("/")) {
            path = path.substring(rootPath.length());
        } else {
            path = path.substring(rootPath.length() + 1);
        }
        return "root" + ':' + path;
    }


    private String getTypeForFile(@NonNull File file) {
        if (file.isDirectory()) {
            return DocumentsContract.Document.MIME_TYPE_DIR;
        } else {
            return getTypeForName(file.getName());
        }
    }

    private String getTypeForName(@NonNull String name) {
        int lastDot = name.lastIndexOf('.');
        if (lastDot > 0) {
            String extensioon = name.substring(lastDot + 1);
            String mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extensioon);
            if (mime != null) {
                return mime;
            }
        }
        return "application/octet-stream";
    }


    private String getChildMimeType(@NonNull File parent) {
        Set<String> mimeTypes = new HashSet<>();
        mimeTypes.add("image/*");
        mimeTypes.add("text/*");
        mimeTypes.add("application/vnd.openxmlformat-officedocument.wordprocessingml.document");
        StringBuilder mimeTypesString = new StringBuilder();
        for (String mimeType : mimeTypes) {
            mimeTypesString.append(mimeType).append('\n');
        }
        return mimeTypesString.toString();
    }

    // 获取file通过fileId
    private File getFileForDocId(@NonNull String docId) throws FileNotFoundException {
        File target = mBaseDir;
        if (docId.equals(ROOT)) {
            return target;
        }
        final int splitIndex = docId.indexOf(':', 1);
        if (splitIndex < 0) {
            throw new FileNotFoundException("Missing root for " + docId);
        } else {
            final String path = docId.substring(splitIndex + 1);
            target = new File(target, path);
            if (!target.exists()) {
                throw new FileNotFoundException("Missing file for " + docId + " at " + target);
            }
            return target;
        }
    }
}

