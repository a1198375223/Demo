package com.example.room.dao.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

// Entity注解表示这是一表
// Tips:
// 1. Entity中的每一个字段都会被持久化存储到数据库中, 如果不希望某个字段被持久化存储, 可以使用@Ignore注解来注解字段
// 2. 每个Entity必须定义至少一个主键(@PrimaryKey), 如果是复合主键使用@Entity(primaryKeys = {})来声明.
//    如果想要自增使用@PrimaryKey(autoGenerate = true)来实现
// 3. 一般情况都是直接使用类名直接作为表明, 如果需要改变可以使用@Entity(tableName = "xxx")来实现
// 4. 默认使用字段名称作为表的列名, 如果需要更换可以使用@ColumnInfo(name = "first_name")来实现
// 5. 如果需要添加索引可以使用@Entity(indices = {@Index(value = {"first_name", "last_name"}})来实现
//    如果需要声明一个独一无二的索引可以使用@Entity(indices = {@Index(value = {"first_name", "last_name"}, unique = true)})来实现
// 6. Entity之间没有对象引用, 可以使用外键来声明两个Entity之间的关系 --> @Entity(foreignKeys = @ForeignKey(entity = x.class, parentColumns = "uid", childColumns = "user_id"))
// 7. 使用@Embedded来声明一个组合字段
@Entity
public class User {

    public User() {}

    // 主键
    @PrimaryKey(autoGenerate = true)
    private int uid;

    // 更改列名, 可以不用. 默认是字段名称作为列名
    @ColumnInfo(name = "first_name")
    private String firstName;

    @ColumnInfo(name = "last_name")
    private String lastName;

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public String toString() {
        return "User{" +
                "uid=" + uid +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }
}
