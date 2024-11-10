package com.example.myapplication;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "category")
public class Category {
    @PrimaryKey(autoGenerate = true)
    private int ca_id;
    @ColumnInfo(name = "name")
    private String name;
    public int getCa_id() {
        return ca_id;
    }
    public String  getName() {
        return name;
    }

    public void setCa_id(int ca_id) {
        this.ca_id = ca_id;
    }

    public void setName(String name) {
        this.name = name;
    }

}
