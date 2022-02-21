package com.istir.weebtoolscompose

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.DatabaseUtils
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteQueryBuilder
import android.net.Uri
import android.util.Log
import kotlin.text.Regex.Companion.escape
import kotlin.text.Regex.Companion.escapeReplacement


class DBHelper(context: Context, factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {

    // below is the method for creating a database by a sqlite query
    override fun onCreate(db: SQLiteDatabase) {
        // below is a sqlite query, where column names
        // along with their data types is given

        val query =
            "CREATE TABLE $TABLE_NAME ($ID_COL INTEGER PRIMARY KEY, $NAME_COL TEXT, $URI_COL TEXT, $PROGRESS_COL INTEGER, $PAGES_COL INTEGER, $ISDELETED_COL INTEGER, $MODIFIED_COL TEXT, $FOLDERNAME_COL TEXT, $FOLDERURI_COL TEXT)"
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

    fun escapeQuotes(string: String): String {
        var replaced = string.replace("\'", "\'\'")
        replaced = replaced.replace(
            "\"",
            "\"\""
        )
        return replaced
    }

    fun checkIfMangaExists(
        name: String,
        uri: Uri,
        pages: Int?,
    ): Boolean {
//        val mangas = ArrayList<Manga>()
        val db = this.readableDatabase
        val columns = arrayOf(ID_COL)
        val whereClause = "$NAME_COL = ? AND $URI_COL = ?"
        val whereArgs = arrayOf(
            name, uri.toString()
        )

        val cursor = db.query(TABLE_NAME, columns, whereClause, whereArgs, null, null, null)

        Log.i("checkIfMangaExists", "${cursor}")
        if (cursor != null) {
            Log.i("checkIfMangaExists", "not null")
            cursor.moveToFirst()

            return try {
                val a = cursor.getInt(cursor.getColumnIndexOrThrow(ID_COL))
                Log.i(
                    "checkIfMangaExists",
                    "return try, ${a}"
                )

                true
            } catch (e: Exception) {
                Log.i(
                    "checkIfMangaExists",
                    "exception $e"
                )
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
            manga.modifiedAt,
            manga.folderUri,
            manga.folderName
        )
    }

    fun addManga(
        name: String,
        uri: Uri,
        currentPosition: Int,
        pages: Int,
        deleted: Boolean,
        modified: Long,
        folderUri: Uri,
        folderName: String
    ): Manga? {
        Log.i("addManga", "START")
        val check = checkIfMangaExists(name, uri)
        Log.i("addManga", "check: $check")
        if (check) return null
        Log.i("addManga", "NOT NULL")
        val values = ContentValues()
        values.put(NAME_COL, name)
        values.put(URI_COL, uri.toString())
        values.put(PROGRESS_COL, currentPosition)
        values.put(PAGES_COL, pages)
        values.put(ISDELETED_COL, if (deleted) 1 else 0)
        values.put(MODIFIED_COL, modified.toString())
        values.put(FOLDERNAME_COL, folderName)
        values.put(FOLDERURI_COL, folderUri.toString())
        val db = this.writableDatabase
        val id = db.insert(TABLE_NAME, null, values)
        db.close()
        Log.i("ID", "${id}")
        if (id > 0) {
            return Manga(
                id.toInt(),
                name,
                uri,
                currentPosition,
                pages,
                deleted,
                modified,
                folderUri,
                folderName
            )
        }
//
        return null
    }

    fun editMangaModified(id: Int, newModified: Long) {
        val db = this.writableDatabase
        val whereClause = "$ID_COL = ?"
        val whereArgs = arrayOf(
            id.toString()
        )
        val values = ContentValues()
        values.put(MODIFIED_COL, newModified.toString())
        updateManga(values, whereClause, whereArgs)
//        try {
//            db.execSQL("UPDATE $TABLE_NAME SET $MODIFIED_COL = $newModified WHERE $ID_COL=$id")
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }

        db.close()
    }

    fun editMangaModified(originalName: String, originalUri: Uri, newModified: Long) {
//    if (checkIfMangaExists(originalName, originalUri)) {
        val values = ContentValues()

        values.put(MODIFIED_COL, newModified.toString())
        updateMangaByUriAndName(values, originalUri, originalName)
//    }
    }

    fun editMangaNameAndUri(originalName: String, originalUri: Uri, newName: String, newUri: Uri) {
        if (checkIfMangaExists(originalName, originalUri)) {
            val values = ContentValues()
            values.put(URI_COL, newUri.toString())
            values.put(NAME_COL, newName)
            updateMangaByUriAndName(values, originalUri, originalName)
        }
    }

    fun editMangaUri(originalName: String, originalUri: Uri, newUri: Uri) {
        if (checkIfMangaExists(originalName, originalUri)) {
//            val db = this.writableDatabase
//            db.execSQL(
//                "UPDATE $TABLE_NAME SET $URI_COL = '$newUri' WHERE $NAME_COL = '${
//                    escapeQuotes(
//                        originalName
//                    )
//                }' AND $URI_COL = '$originalUri'"
//            )
//            db.close()
            val values = ContentValues()
            values.put(URI_COL, newUri.toString())
            updateMangaByUriAndName(values, originalUri, originalName)
        }
    }

    fun editMangaDeleted(originalName: String, originalUri: Uri, newDeleted: Boolean) {
        if (checkIfMangaExists(originalName, originalUri)) {
//            val db = this.writableDatabase
//            db.execSQL(
//                "UPDATE $TABLE_NAME SET $ISDELETED_COL = ${if (newDeleted) 1 else 0} WHERE $NAME_COL = '${
//                    escapeQuotes(
//                        originalName
//                    )
//                }' AND $URI_COL = '$originalUri'"
//            )
            val values = ContentValues()
            values.put(ISDELETED_COL, if (newDeleted) 1 else 0)
            updateMangaByUriAndName(values, originalUri, originalName)
//            db.close()
        }
    }

    fun editMangaName(originalName: String, originalUri: Uri, newName: String) {

        if (checkIfMangaExists(originalName, originalUri)) {
//            val db = this.writableDatabase

            val values = ContentValues()
            values.put(NAME_COL, newName)
            updateMangaByUriAndName(values, originalUri, originalName)
//            db.update(TABLE_NAME, values, whereClause, whereArgs)
//            db.close()
        }
    }

    fun editMangaPages(originalUri: Uri, newPages: Int) {
        val values = ContentValues()
        values.put(PAGES_COL, newPages)
        val whereClause = "$URI_COL = ?"
        val whereArgs = arrayOf(
            originalUri.toString()
        )
        updateManga(values, whereClause, whereArgs)
    }

    fun editMangaProgress(originalUri: Uri, newProgress: Int) {
        val values = ContentValues()
        values.put(PROGRESS_COL, newProgress)
        val whereClause = "$URI_COL = ?"
        val whereArgs = arrayOf(
            originalUri.toString()
        )
        updateManga(values, whereClause, whereArgs)
    }

    fun editManga(originalName: String, originalUri: Uri, newManga: Manga) {
        if (checkIfMangaExists(originalName, originalUri)) {

//            val columns = null
//            val whereClause = "$NAME_COL = ? AND $URI_COL = ?"
//            val whereArgs = arrayOf(
//                originalName, originalUri.toString()
//            )
//            val orderBy = "$MODIFIED_COL DESC"

//            val values
            val values = ContentValues()
            values.put(NAME_COL, newManga.name)
            values.put(URI_COL, newManga.uri.toString())
            values.put(PROGRESS_COL, newManga.currentPosition)
            values.put(PAGES_COL, newManga.pages)
            values.put(ISDELETED_COL, if (newManga.deleted) 1 else 0)
            values.put(MODIFIED_COL, newManga.modifiedAt.toString())
//            val cursor = db.update(TABLE_NAME, columns, whereClause, whereArgs, null, null, orderBy)
            updateMangaByUriAndName(values, originalUri, originalName)
//            db.update(TABLE_NAME, values, whereClause, whereArgs)


//            db.execSQL(
//                "UPDATE $TABLE_NAME SET $NAME_COL = '${escapeQuotes(newManga.name)}', $URI_COL = '${newManga.uri}', $PROGRESS_COL = ${newManga.currentPosition}, $PAGES_COL = ${newManga.pages}, $ISDELETED_COL = ${if (newManga.deleted) 1 else 0}, $MODIFIED_COL = ${newManga.modifiedAt} WHERE $NAME_COL = '${
//                    escapeQuotes(
//                        originalName
//                    )
//                }' AND $URI_COL = '$originalUri'"
//            )

        }
    }

    fun updateMangaByUriAndName(newValues: ContentValues, uri: Uri, name: String) {
        val whereClause = "$NAME_COL = ? AND $URI_COL = ?"
        val whereArgs = arrayOf(
            name, uri.toString()
        )
        updateManga(newValues, whereClause, whereArgs)
    }

    fun updateManga(newValues: ContentValues, whereClause: String, whereArgs: Array<String>) {

        val db = this.writableDatabase
        db.update(TABLE_NAME, newValues, whereClause, whereArgs)
        db.close()

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
                modifiedAt = cursor.getString(cursor.getColumnIndexOrThrow(MODIFIED_COL)).toLong(),
                folderName = cursor.getString(cursor.getColumnIndexOrThrow(FOLDERNAME_COL)),
                folderUri = Uri.parse(cursor.getString(cursor.getColumnIndexOrThrow(FOLDERURI_COL)))
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun getMangasWithUri(uri: Uri): ArrayList<Manga> {
        val mangas = ArrayList<Manga>()
        val db = this.readableDatabase

        val columns = null
        val whereClause = "$ISDELETED_COL = 0 AND $URI_COL = ?"
        val whereArgs = arrayOf(
            uri.toString()
        )
        val orderBy = "$MODIFIED_COL DESC"
        val cursor = db.query(TABLE_NAME, columns, whereClause, whereArgs, null, null, orderBy)


//        val cursor = db.rawQuery(
//            "SELECT * FROM $TABLE_NAME WHERE $ISDELETED_COL = 0 AND $URI_COL = $uri",
//            null
//        )
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

        val columns = null
        val whereClause = "$ISDELETED_COL = 0 AND $NAME_COL = ?"
        val whereArgs = arrayOf(
            name
        )
        val orderBy = "$MODIFIED_COL DESC"
        val cursor = db.query(TABLE_NAME, columns, whereClause, whereArgs, null, null, orderBy)


        if (cursor != null) {
            cursor.moveToFirst()
            do {

                createManga(cursor = cursor)?.let { mangas.add(it) }
            } while (cursor.moveToNext())

        }
        db.close()
        return mangas
    }

    fun getExistingMangasInFolder(folderName: String): ArrayList<Manga> {
        val mangas = ArrayList<Manga>()
        val db = this.readableDatabase
        val columns = null
        val whereClause = "$ISDELETED_COL = 0 AND $FOLDERNAME_COL = ?"
        val whereArgs = arrayOf(folderName)
        val orderBy = "$MODIFIED_COL DESC"
        val cursor = db.query(TABLE_NAME, columns, whereClause, whereArgs, null, null, orderBy)

        if (cursor != null) {
            cursor.moveToFirst()
            do {

                createManga(cursor = cursor)?.let { mangas.add(it) }
            } while (cursor.moveToNext())

//            mangas.add()
        }
        db.close()
        return mangas
    }

    fun getExistingMangasInFolder(folderUri: Uri): ArrayList<Manga> {
        val mangas = ArrayList<Manga>()
        val db = this.readableDatabase
        val columns = null
        val whereClause = "$ISDELETED_COL = 0 AND $FOLDERURI_COL = ?"
        val whereArgs = arrayOf(folderUri.toString())
        val orderBy = "$MODIFIED_COL DESC"
        val cursor = db.query(TABLE_NAME, columns, whereClause, whereArgs, null, null, orderBy)

        if (cursor != null) {
            cursor.moveToFirst()
            do {

                createManga(cursor = cursor)?.let { mangas.add(it) }
            } while (cursor.moveToNext())

//            mangas.add()
        }
        db.close()
        return mangas
    }

    fun getExistingMangas(): ArrayList<Manga> {
        val mangas = ArrayList<Manga>()
        val db = this.readableDatabase
        val columns = null
        val whereClause = "$ISDELETED_COL = 0 "
        val orderBy = "$MODIFIED_COL DESC"
        val cursor = db.query(TABLE_NAME, columns, whereClause, null, null, null, orderBy)

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
        val FOLDERNAME_COL = "folderName"
        val FOLDERURI_COL = "folderUri"
    }
}