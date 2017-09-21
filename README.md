# support-db-requery

A wrapper around [Requery's sqlite-android library](https://github.com/requery/sqlite-android)
that implements `SupportSQLite` interfaces from the support library. This allows you to ship your
own version of SQLite and use it with a shared platform independent API.

## Usage

The only public class in this library is `RequerySQLiteOpenHelperFactory` which creates and returns
an implementation of `SupportSQLiteOpenHelper`.


### Regular usage

Instead of extending `SQLiteOpenHelper` you have to extend `SupportSQLiteOpenHelper.Callback`:

```java
class DbCallback extends SupportSQLiteOpenHelper.Callback {

  public DbCallback(int version) {
    super(version);
  }

  @Override
  public void onCreate(SupportSQLiteDatabase db) {
    // create db
  }

  @Override
  public void onUpgrade(SupportSQLiteDatabase db, int oldVersion, int newVersion) {
    // upgrade db
  }
}

```

Use that to create an instance of `SupportSQLiteOpenHelper.Configuration` and pass it to the factory:

```java
SupportSQLiteOpenHelper.Configuration config = SupportSQLiteOpenHelper.Configuration.builder(context)
    .name("database-name.db")
    .callback(new DbCallback())
    .build();
SupportSQLiteOpenHelper helper = new RequerySQLiteOpenHelperFactory().create(config);
// use helper as usual
```


### Room

To use this with Room just pass an instance of `RequerySQLiteOpenHelperFactory` to it's database
builder:

```java
Room.databaseBuilder(context, YourDatabase.class, "database-name")
    .openHelperFactory(new RequerySQLiteOpenHelperFactory())
    .build();
```


### SQL Brite

Using this library requires version 3 of SQL Brite, which is currently only available as a snapshot
(`compile 'com.squareup.sqlbrite3:sqlbrite:3.0.0-SNAPSHOT'`). Create the helper as described in
[regular usage](#regular-usage) and pass it into `wrapDatabaseHelper`:

```java
SupportSQLiteOpenHelper helper = new RequerySQLiteOpenHelperFactory().create(config);
BriteDatabase db = sqlBrite.wrapDatabaseHelper(helper, Schedulers.io());
```


### SQLDelight

Currently not supported, keep an eye on: https://github.com/square/sqldelight/issues/566



## Download

Add a Gradle dependency:

```groovy
compile 'com.gabrielittner.sqlite:support-db-requery:0.1.0-SNAPSHOT'
```

Snapshots of the development version are available in [Sonatype's `snapshots` repository][snap].


## License

```
Copyright (C) 2016 The Android Open Source Project
Copyright 2017 Gabriel Ittner

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```



 [snap]: https://oss.sonatype.org/content/repositories/snapshots/
