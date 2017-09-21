/*
 * Copyright (C) 2016 The Android Open Source Project
 * Copyright 2017 Gabriel Ittner
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gabrielittner.sqlite;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.db.SupportSQLiteOpenHelper;
import android.content.Context;
import android.support.annotation.RequiresApi;

import io.requery.android.database.DatabaseErrorHandler;
import io.requery.android.database.sqlite.SQLiteDatabase;
import io.requery.android.database.sqlite.SQLiteOpenHelper;

class RequerySQLiteOpenHelper implements SupportSQLiteOpenHelper {
    private final RequerySQLiteOpenHelper.OpenHelper mDelegate;

    RequerySQLiteOpenHelper(Context context, String name, Callback callback) {
        this.mDelegate = this.createDelegate(context, name, callback);
    }

    private RequerySQLiteOpenHelper.OpenHelper createDelegate(Context context, String name, Callback callback) {
        RequerySQLiteDatabase[] dbRef = new RequerySQLiteDatabase[1];
        return new RequerySQLiteOpenHelper.OpenHelper(context, name, dbRef, callback);
    }

    public String getDatabaseName() {
        return this.mDelegate.getDatabaseName();
    }

    @RequiresApi(
            api = 16
    )
    public void setWriteAheadLoggingEnabled(boolean enabled) {
        this.mDelegate.setWriteAheadLoggingEnabled(enabled);
    }

    public SupportSQLiteDatabase getWritableDatabase() {
        return this.mDelegate.getWritableSupportDatabase();
    }

    public SupportSQLiteDatabase getReadableDatabase() {
        return this.mDelegate.getReadableSupportDatabase();
    }

    public void close() {
        this.mDelegate.close();
    }

    static class OpenHelper extends SQLiteOpenHelper {
        final RequerySQLiteDatabase[] mDbRef;
        final Callback mCallback;

        OpenHelper(Context context, String name, final RequerySQLiteDatabase[] dbRef, final Callback callback) {
            super(context, name, (SQLiteDatabase.CursorFactory)null, callback.version, new DatabaseErrorHandler() {
                public void onCorruption(SQLiteDatabase dbObj) {
                    RequerySQLiteDatabase db = dbRef[0];
                    if(db != null) {
                        callback.onCorruption(db);
                    }

                }
            });
            this.mCallback = callback;
            this.mDbRef = dbRef;
        }

        SupportSQLiteDatabase getWritableSupportDatabase() {
            SQLiteDatabase db = super.getWritableDatabase();
            return this.getWrappedDb(db);
        }

        SupportSQLiteDatabase getReadableSupportDatabase() {
            SQLiteDatabase db = super.getReadableDatabase();
            return this.getWrappedDb(db);
        }

        RequerySQLiteDatabase getWrappedDb(SQLiteDatabase sqLiteDatabase) {
            RequerySQLiteDatabase dbRef = this.mDbRef[0];
            if(dbRef == null) {
                dbRef = new RequerySQLiteDatabase(sqLiteDatabase);
                this.mDbRef[0] = dbRef;
            }

            return this.mDbRef[0];
        }

        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            this.mCallback.onCreate(this.getWrappedDb(sqLiteDatabase));
        }

        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
            this.mCallback.onUpgrade(this.getWrappedDb(sqLiteDatabase), oldVersion, newVersion);
        }

        public void onConfigure(SQLiteDatabase db) {
            this.mCallback.onConfigure(this.getWrappedDb(db));
        }

        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            this.mCallback.onDowngrade(this.getWrappedDb(db), oldVersion, newVersion);
        }

        public void onOpen(SQLiteDatabase db) {
            this.mCallback.onOpen(this.getWrappedDb(db));
        }

        public synchronized void close() {
            super.close();
            this.mDbRef[0] = null;
        }
    }
}
