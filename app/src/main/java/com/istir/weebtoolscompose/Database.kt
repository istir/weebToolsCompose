package com.istir.weebtoolscompose

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.net.Uri
import android.util.Log


class DBHelper(context: Context, factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {

    // below is the method for creating a database by a sqlite query
    override fun onCreate(db: SQLiteDatabase) {
        // below is a sqlite query, where column names
        // along with their data types is given

        val query =
            "CREATE TABLE $TABLE_NAME ($ID_COL INTEGER PRIMARY KEY, $NAME_COL TEXT, $URI_COL TEXT, $PROGRESS_COL INTEGER, $PAGES_COL INTEGER, $ISDELETED_COL INTEGER, $MODIFIED_COL TEXT)"
        // we are calling sqlite
        // method for executing our query
        db.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase, p1: Int, p2: Int) {
        // this method is to check if table already exists
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME)
        onCreate(db)
    }

    fun checkIfMangaExists(
        name: String,
        uri: Uri
    ): Boolean {
        return checkIfMangaExists(name, uri, null)
    }

    fun checkIfMangaExists(
        name: String,
        uri: Uri,
        pages: Int?,
    ): Boolean {
//        val mangas = ArrayList<Manga>()
        val db = this.readableDatabase
        var query = "SELECT * FROM $TABLE_NAME WHERE $NAME_COL = '$name' AND $URI_COL = '$uri'"
        if (pages != null) {
            query += " AND $PAGES_COL = '$pages'"
        }
        val cursor = db.rawQuery(
            query,
            null
        )

        if (cursor != null) {
            cursor.moveToFirst()

            return try {
                cursor.getInt(cursor.getColumnIndexOrThrow(ID_COL))
                true
            } catch (e: Exception) {
                false
            }

//                mangas.add(createManga(cursor = cursor))


        }
        db.close()
        return false
    }

    fun addManga(manga: Manga): Manga? {
        return addManga(
            manga.name,
            manga.uri,
            manga.currentPosition,
            manga.pages,
            manga.deleted,
            manga.modifiedAt
        )
    }

    fun addManga(
        name: String,
        uri: Uri,
        currentPosition: Int,
        pages: Int,
        deleted: Boolean,
        modified: Long
    ): Manga? {
        if (checkIfMangaExists(name, uri)) return null
        val values = ContentValues()
        values.put(NAME_COL, name)
        values.put(URI_COL, uri.toString())
        values.put(PROGRESS_COL, currentPosition)
        values.put(PAGES_COL, pages)
        values.put(ISDELETED_COL, if (deleted) 1 else 0)
        values.put(MODIFIED_COL, modified.toString())
        val db = this.writableDatabase
        val id = db.insert(TABLE_NAME, null, values)
        db.close()
        Log.i("ID", "${id}")
        if (id > 0) {
            return Manga(id.toInt(), name, uri, currentPosition, pages, deleted, modified)
        }
//
        return null
    }

    fun editManga(id: Int, newModified: Long) {
        val db = this.writableDatabase
        try {
            db.execSQL("UPDATE $TABLE_NAME SET $MODIFIED_COL = $newModified WHERE $ID_COL=$id")
        } catch (e: Exception) {
            e.printStackTrace()
        }

        db.close()
    }

    fun editManga(originalName: String, originalUri: Uri, newModified: Long) {
//    if (checkIfMangaExists(originalName, originalUri)) {
        val db = this.writableDatabase
        try {
            db.execSQL("UPDATE $TABLE_NAME SET $MODIFIED_COL = $newModified WHERE $NAME_COL = '$originalName' AND $URI_COL = '$originalUri'")
        } catch (e: Exception) {
            e.printStackTrace()
        }

        db.close()
//    }
    }

    fun editManga(originalName: String, originalUri: Uri, newName: String, newUri: Uri) {
        if (checkIfMangaExists(originalName, originalUri)) {
            val db = this.writableDatabase
            db.execSQL("UPDATE $TABLE_NAME SET $NAME_COL = '$newName', $URI_COL = '$newUri' WHERE $NAME_COL = '$originalName' AND $URI_COL = '$originalUri'")
            db.close()
        }
    }

    fun editManga(originalName: String, originalUri: Uri, newUri: Uri) {
        if (checkIfMangaExists(originalName, originalUri)) {
            val db = this.writableDatabase
            db.execSQL("UPDATE $TABLE_NAME SET $URI_COL = '$newUri' WHERE $NAME_COL = '$originalName' AND $URI_COL = '$originalUri'")
            db.close()
        }
    }

    fun editManga(originalName: String, originalUri: Uri, newDeleted: Boolean) {
        if (checkIfMangaExists(originalName, originalUri)) {
            val db = this.writableDatabase
            db.execSQL("UPDATE $TABLE_NAME SET $ISDELETED_COL = ${if (newDeleted) 1 else 0} WHERE $NAME_COL = '$originalName' AND $URI_COL = '$originalUri'")
            db.close()
        }
    }

    fun editManga(originalName: String, originalUri: Uri, newName: String) {
        if (checkIfMangaExists(originalName, originalUri)) {
            val db = this.writableDatabase
            db.execSQL("UPDATE $TABLE_NAME SET $NAME_COL = '$newName' WHERE $NAME_COL = '$originalName' AND $URI_COL = '$originalUri'")
            db.close()
        }
    }


    fun editManga(originalName: String, originalUri: Uri, newManga: Manga) {
        if (checkIfMangaExists(originalName, originalUri)) {
            val db = this.writableDatabase
            db.execSQL("UPDATE $TABLE_NAME SET $NAME_COL = '${newManga.name}', $URI_COL = '${newManga.uri}', $PROGRESS_COL = ${newManga.currentPosition}, $PAGES_COL = ${newManga.pages}, $ISDELETED_COL = ${if (newManga.deleted) 1 else 0} WHERE $NAME_COL = '$originalName' AND $URI_COL = '$originalUri'")
            db.close()
        }
    }

    fun removeManga(name: String) {
        val db = this.readableDatabase
        try {
            db.delete(TABLE_NAME, "$NAME_COL = ?", arrayOf(name))
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            db.close()
        }
    }

    fun removeManga(uri: Uri) {
        val db = this.readableDatabase
        try {
            db.delete(TABLE_NAME, "$URI_COL = ?", arrayOf(uri.toString()))
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            db.close()
        }
    }

    fun removeAllManga() {
        val allMangas = getExistingMangas()
        for (manga in allMangas) {
            removeManga(manga.uri)
        }
    }
//    fun removeDuplicateManga() {
//        //I don't care to write anything more performant as this will be used only in development and only in a blue moon
//        val allMangas = getExistingMangas()
//        val filteredManga = ArrayList<List<String>>()
//        val filteredIds = ArrayList<ArrayList<Int>>()
//        for (manga in allMangas) {
//            val l = listOf(manga.name, manga.uri.toString())
////            val ids = listOf<Int>()
//
//                val oldIds = filteredIds.find {  }
//                oldIds.add(manga.)
//                filteredIds.set(manga.id!!,)
//
////            if (!filteredManga.contains(l)) {
////                filteredManga.add(l)
////            }
////           filteredManga.add
//        }
//        for (manga in filteredManga) {
//            Log.i("FILTERED", "${manga[0]} ${manga[1]} ${manga[2]}")
//        }
//    }

    private fun createManga(cursor: Cursor): Manga? {
        return try {
            Manga(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(ID_COL)),
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
                ) == 1,
                modifiedAt = cursor.getString(cursor.getColumnIndexOrThrow(MODIFIED_COL)).toLong()
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
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
                createManga(cursor = cursor)?.let { mangas.add(it) }
            } while (cursor.moveToNext())

        }
        db.close()
        return mangas
    }

    fun getMangasWithName(name: String): ArrayList<Manga> {
        val mangas = ArrayList<Manga>()
        val db = this.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_NAME WHERE $ISDELETED_COL = 0 AND $NAME_COL = '$name'",
            null
        )
        if (cursor != null) {
            cursor.moveToFirst()
            do {

                createManga(cursor = cursor)?.let { mangas.add(it) }
            } while (cursor.moveToNext())

        }
        db.close()
        return mangas
    }

    fun getExistingMangas(): ArrayList<Manga> {
        val mangas = ArrayList<Manga>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM ${TABLE_NAME} WHERE ${ISDELETED_COL} = 0", null)
        if (cursor != null) {
            cursor.moveToFirst()
            do {

                createManga(cursor = cursor)?.let { mangas.add(it) }
            } while (cursor.moveToNext())

//            mangas.add()
        }
        db.close()
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
        val MODIFIED_COL = "modified"
    }
}