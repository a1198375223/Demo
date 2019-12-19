package com.example.androidxdemo.mianshi;

import androidx.recyclerview.widget.RecyclerView;

/**
 * 缓存(3级缓存)
 * 一级缓存; mAttachedScrap: 缓存在屏幕中正在显示的ViewHolder。 mChangedScrap: 缓存被跟新的viewHolder
 * 二级缓存: mCacheViews: 默认是2，缓存即将进入屏幕的ViewHolder
 * 三级缓存: RecyclerViewPool:根据viewType来对ViewHolder进行缓存
 */
public class RecyclerViewDetail {

}
