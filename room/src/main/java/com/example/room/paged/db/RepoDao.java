package com.example.room.paged.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.room.paged.model.Repo;

import java.util.List;

@Dao
public interface RepoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Long[] insert(List<Repo> repos);


    @Query("select * from repos " +
            "where name like :queryString or description like :queryString " +
            "order by stars desc, name asc")
    LiveData<List<Repo>> reposAll(String queryString);
}
