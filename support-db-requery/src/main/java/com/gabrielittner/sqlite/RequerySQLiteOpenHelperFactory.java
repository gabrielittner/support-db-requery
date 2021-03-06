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

import android.arch.persistence.db.SupportSQLiteOpenHelper;

/**
 * Implements {@link SupportSQLiteOpenHelper.Factory} using the SQLite implementation in the
 * framework.
 *
 * @deprecated use {@link io.requery.android.database.sqlite.RequerySQLiteOpenHelperFactory} instead
 */
@SuppressWarnings("unused")
@Deprecated
public final class RequerySQLiteOpenHelperFactory implements SupportSQLiteOpenHelper.Factory {

    private final io.requery.android.database.sqlite.RequerySQLiteOpenHelperFactory factory =
            new io.requery.android.database.sqlite.RequerySQLiteOpenHelperFactory();

    /**
     * @deprecated use {@link io.requery.android.database.sqlite.RequerySQLiteOpenHelperFactory} instead
     */
    @Deprecated
    @Override
    public SupportSQLiteOpenHelper create(SupportSQLiteOpenHelper.Configuration config) {
        return factory.create(config);
    }
}
