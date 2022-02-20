package com.istir.weebtoolscompose

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.net.Uri


class DBHelper(context: Context, factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {

    // below is the method for creating a database by a sqlite query
    override fun onCreate(db: SQLiteDatabase) {
        // below is a sqlite query, where column names
        // along with their data types is given
//        val query = ("CREATE TABLE " + TABLE_NAME + " ("
//                + ID_COL + " INTEGER PRIMARY KEY, " +
//                NAME_COL + " TEXT," +
//                URI_COL + " TEXT" + ")")
        val query =
            "CREATE TABLE $TABLE_NAME ($ID_COL INTEGER PRIMARY KEY, $NAME_COL TEXT, $URI_COL TEXT, $PROGRESS_COL INTEGER, $PAGES_COL INTEGER, $ISDELETED_COL INTEGER)"
        // we are calling sqlite
        // method for executing our query
        db.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase, p1: Int, p2: Int) {
        // this method is to check if table already exists
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME)
        onCreate(db)
    }

    fun addManga(name: String, uri: Uri, currentPosition: Int, pages: Int, deleted: Boolean) {
        val values = ContentValues()
        values.put(NAME_COL, name)
        values.put(URI_COL, uri.toString())
        values.put(PROGRESS_COL, currentPosition)
        values.put(PAGES_COL, pages)
        values.put(ISDELETED_COL, if (deleted) 1 else 0)
        val db = this.writableDatabase
        db.insert(TABLE_NAME, null, values)
        db.close()
    }

    //    // This method is for adding data in our database
//    fun addName(name: String, age: String) {
//
//        // below we are creating
//        // a content values variable
//        val values = ContentValues()
//
//        // we are inserting our values
//        // in the form of key-value pair
//        values.put(NAME_COl, name)
//        values.put(AGE_COL, age)
//
//        // here we are creating a
//        // writable variable of
//        // our database as we want to
//        // insert value in our database
//        val db = this.writableDatabase
//
//        // all values are inserted into database
//        db.insert(TABLE_NAME, null, values)
//
//        // at last we are
//        // closing our database
//        db.close()
//    }
    private fun createManga(cursor: Cursor): Manga {
        return Manga(
            name = cursor.getString(cursor.getColumnIndexOrThrow(NAME_COL)),
            uri = Uri.parse(
                cursor.getString(
                    cursor.getColumnIndexOrThrow(
                        URI_COL
                    )
                )
            ),
            currentPosition = cursor.getInt(
                cursor.getColumnIndexOrThrow(
                    PROGRESS_COL
                )
            ),
            pages = cursor.getInt(cursor.getColumnIndexOrThrow(PAGES_COL)),
            deleted = cursor.getInt(
                cursor.getColumnIndexOrThrow(
                    ISDELETED_COL
                )
            ) == 1
        )
    }

    fun getMangasWithUri(uri: Uri): ArrayList<Manga> {
        val mangas = ArrayList<Manga>()
        val db = this.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_NAME WHERE $ISDELETED_COL = 0 AND $URI_COL = $uri",
            null
        )
        if (cursor != null) {
            cursor.moveToFirst()
            do {
                mangas.add(createManga(cursor = cursor))
            } while (cursor.moveToNext())

        }
        return mangas
    }

    fun getMangasWithName(name: String): ArrayList<Manga> {
        val mangas = ArrayList<Manga>()
        val db = this.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_NAME WHERE $ISDELETED_COL = 0 AND $NAME_COL = $name",
            null
        )
        if (cursor != null) {
            cursor.moveToFirst()
            do {

                mangas.add(createManga(cursor = cursor))
            } while (cursor.moveToNext())

        }
        return mangas
    }

    fun getExistingMangas(): ArrayList<Manga> {
        val mangas = ArrayList<Manga>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM ${TABLE_NAME} WHERE ${ISDELETED_COL} = 0", null)
        if (cursor != null) {
            cursor.moveToFirst()
            do {

                mangas.add(createManga(cursor = cursor))
            } while (cursor.moveToNext())

//            mangas.add()
        }
        return mangas
//        db.query(table= TABLE_NAME,)
    }

    // below method is to get
    // all data from our database
//    fun getName(): Cursor? {
//
//        // here we are creating a readable
//        // variable of our database
//        // as we want to read value from it
//        val db = this.readableDatabase
//
//        // below code returns a cursor to
//        // read data from the database
//        return db.rawQuery("SELECT * FROM " + TABLE_NAME, null)
//
//    }

    //    companion object{
//        // here we have defined variables for our database
//
//        // below is variable for database name
//        private val DATABASE_NAME = "GEEKS_FOR_GEEKS"
//
//        // below is the variable for database version
//        private val DATABASE_VERSION = 1
//
//        // below is the variable for table name
//        val TABLE_NAME = "gfg_table"
//
//        // below is the variable for id column
//        val ID_COL = "id"
//
//        // below is the variable for name column
//        val NAME_COl = "name"
//
//        // below is the variable for age column
//        val AGE_COL = "age"
//    }
    companion object {
        // here we have defined variables for our database

        // below is variable for database name
        private val DATABASE_NAME = "weebToolsAndroid"

        // below is the variable for database version
        private val DATABASE_VERSION = 1

        // below is the variable for table name
        val TABLE_NAME = "Mangas"

        // below is the variable for id column
        val ID_COL = "id"

        // below is the variable for name column
        val NAME_COL = "name"

        val URI_COL = "uri"

        // below is the variable for age column
        val PROGRESS_COL = "progress"

        val PAGES_COL = "pages"

        val ISDELETED_COL = "isDeleted"
    }
}