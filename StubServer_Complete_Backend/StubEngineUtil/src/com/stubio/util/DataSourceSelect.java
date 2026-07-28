package com.stubio.util;

import java.util.LinkedList;

public class DataSourceSelect {


    private LinkedList<DataBase> database; // may be null / empty element
    private LinkedList<DataFile> file;   // the <vs:File> block

    public LinkedList<DataBase> getDatabase() {
        return database;
    }

    public void setDatabase(LinkedList<DataBase> database) {
        this.database = database;
    }

    public LinkedList<DataFile> getFile() {
        return file;
    }

    public void setFile(LinkedList<DataFile> file) {
        this.file = file;
    }
}
