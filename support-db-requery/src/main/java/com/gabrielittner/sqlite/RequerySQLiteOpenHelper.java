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
import android.os.Build;
import android.support.annotation.RequiresApi;

import io.requery.android.database.DatabaseErrorHandler;
import io.requery.android.database.sqlite.SQLiteDatabase;
import io.requery.android.database.sqlite.SQLiteOpenHelper;

final class RequerySQLiteOpenHelper implements SupportSQLiteOpenHelper {
    private final OpenHelper mDelegate;

    RequerySQLiteOpenHelper(Context context, String name, int version, Callback callback) {
        mDelegate = createDelegate(context, name, version, callback);
    }

    private OpenHelper createDelegate(Context context, String name, int version, final Callback callback) {
        DatabaseErrorHandler errorHandler = new CallbackDatabaseErrorHandler(callback);
        return new OpenHelper(context, name, null, version, errorHandler) {
            @Override
            public void onCreate(SQLiteDatabase sqLiteDatabase) {
                mWrappedDb = new RequerySQLiteDatabase(sqLiteDatabase);
                callback.onCreate(mWrappedDb);
            }

            @Override
            public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
                callback.onUpgrade(getWrappedDb(sqLiteDatabase), oldVersion, newVersion);
            }

            @Override
            public void onConfigure(SQLiteDatabase db) {
                callback.onConfigure(getWrappedDb(db));
            }

            @Override
            public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                callback.onDowngrade(getWrappedDb(db), oldVersion, newVersion);
            }

            @Override
            public void onOpen(SQLiteDatabase db) {
                callback.onOpen(getWrappedDb(db));
            }
        };
    }

    @Override
    public String getDatabaseName() {
        return mDelegate.getDatabaseName();
    }

    @Override
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void setWriteAheadLoggingEnabled(boolean enabled) {
        mDelegate.setWriteAheadLoggingEnabled(enabled);
    }

    @Override
    public SupportSQLiteDatabase getWritableDatabase() {
        return mDelegate.getWritableSupportDatabase();
    }

    @Override
    public SupportSQLiteDatabase getReadableDatabase() {
        return mDelegate.getReadableSupportDatabase();
    }

    @Override
    public void close() {
        mDelegate.close();
    }

    private abstract static class OpenHelper extends SQLiteOpenHelper {

        RequerySQLiteDatabase mWrappedDb;

        OpenHelper(Context context, String name,
                   SQLiteDatabase.CursorFactory factory, int version,
                   DatabaseErrorHandler errorHandler) {
            super(context, name, factory, version, errorHandler);
        }

        SupportSQLiteDatabase getWritableSupportDatabase() {
            SQLiteDatabase db = super.getWritableDatabase();
            return getWrappedDb(db);
        }

        SupportSQLiteDatabase getReadableSupportDatabase() {
            SQLiteDatabase db = super.getReadableDatabase();
            return getWrappedDb(db);
        }

        RequerySQLiteDatabase getWrappedDb(SQLiteDatabase sqLiteDatabase) {
            if (mWrappedDb == null) {
                mWrappedDb = new RequerySQLiteDatabase(sqLiteDatabase);
            }
            return mWrappedDb;
        }

        @Override
        public synchronized void close() {
            super.close();
            mWrappedDb = null;
        }
    }

    private static final class CallbackDatabaseErrorHandler implements DatabaseErrorHandler {

        private final SupportSQLiteOpenHelper.Callback callback;

        CallbackDatabaseErrorHandler(SupportSQLiteOpenHelper.Callback callback) {
            this.callback = callback;
        }

        @Override
        public void onCorruption(SQLiteDatabase db) {
            callback.onCorruption(db);
        }
    }
}
