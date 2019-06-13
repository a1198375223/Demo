package com.example.androidxdemo.base.bean;


/**
 * 实体类, 用来保存图片视频等文件的信息
 */
public class MediaItem {
    public static int VIDEO_ITEM = 1;
    public static int IMAGE_ITEM = 2;
    public static int AUDIO_ITEM = 3;

    public static int STATUS_NORMAL = 10;
    public static int STATUS_TIMEOUT = 11;
    public static int STATUS_ERROR = 12;

    //----------video相关-------------
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
    category=null
    duration=7711*/
    //----------image相关-------------
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
    //----------audio相关-------------
    /*data=/storage/emulated/0/com.corntree.mud.unknown/Res/sound/begin_game.mp3
    album=sound
    name=begin_game.mp3
    artist=<unknown>
    mime=audio/mpeg
    size=186099
    title=begin_game
    composer=null
    duration=7711*/
    //----------playlist相关-------------
    /*data=/storage/emulated/0/Browser/视频/.1b22fccb4e86f2475ab022705270827b/1b22fccb4e86f2475ab022705270827b.m3u8
    name=1b22fccb4e86f2475ab022705270827b*/


    //------------common-------------
    // audio一般应name
    // image一般用title  name对应上级文件夹名称
    // video一般用name
    private String coverUrl;        // 封面或者是图片
    private String title;           // 标题 一般带有后缀
    private String name;            // 名称 不带后缀
    private String path;            // 文件路径
    private boolean isSelected;     // 是否被选中
    private long size;              // 文件的大小
    private long duration;          // 时长
    private int type;               // 文件类型
    private int progress;           // 上传的进度
    private long timeStamps;        // 时间戳
    private int flag;               // 上传状态 (正常 出错 超时)
    private String thumbPath;       // 缩略图的路径
    private String mimeType;        // 文件的mime类型
    private int dateTaken;          // 视频或拍照的时间
    private String description;     // 描述
    private int width;              // 宽度
    private int height;             // 高度
    private int orientation;        // 图片的方向 0-横向
    private String artist;          // 作家
    private String album;           // 专辑




    public MediaItem() {}

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public long getTimeStamps() {
        return timeStamps;
    }

    public void setTimeStamps(long timeStamps) {
        this.timeStamps = timeStamps;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public String getThumbPath() {
        return thumbPath;
    }

    public void setThumbPath(String thumbPath) {
        this.thumbPath = thumbPath;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public int getDateTaken() {
        return dateTaken;
    }

    public void setDateTaken(int dateTaken) {
        this.dateTaken = dateTaken;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getOrientation() {
        return orientation;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }
}
