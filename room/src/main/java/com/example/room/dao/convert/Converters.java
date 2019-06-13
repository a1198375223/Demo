package com.example.room.dao.convert;

import java.util.Date;

import androidx.room.TypeConverter;

// Room 提供内置支持基本数据类型及其封装类型。然而，有时候需要使用自定义类型，并将其值存储在数据库中单独的列里。
// 要添加自定义类型的支持，需要提供一个 TypeConverter 将自定义类转换为 Room 可以持久化的已知类型。
// 例如我们需要持久化存储Date类型的数据
// 编写之后, 需要在AppDatabase中添加@TypeConverter注解
public class Converters {
    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }
}
