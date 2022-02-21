package com.istir.weebtoolscompose

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anggrayudi.storage.file.baseName
import com.anggrayudi.storage.file.mimeType
import kotlinx.coroutines.*
import java.io.FileNotFoundException
import java.lang.Exception
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import kotlin.collections.ArrayList

class MangaViewModel : ViewModel() {


    lateinit var contentResolver: ContentResolver
    lateinit var mangaUri: Uri

    var pickedFolder by mutableStateOf("")


    //    val currentCouroutines: ArrayList<Deferred<Unit>> = ArrayList()
    val currentCouroutines: ArrayList<CoroutineScope> = ArrayList()
//    var currentCouroutines = mutableStateListOf<CoroutineScope>()
//        private set

    //    fun addCoroutine(coroutineScope: Deferred<Unit>) {
//        currentCouroutines.add(coroutineScope)
//    }
//
//    fun removeCoroutine(coroutineScope: Deferred<Unit>) {
//        currentCouroutines.remove(coroutineScope)
//    }
    fun addCoroutine(coroutineScope: CoroutineScope) {
        currentCouroutines.add(coroutineScope)
    }

    fun removeCoroutine(coroutineScope: CoroutineScope) {
        currentCouroutines.remove(coroutineScope)
    }

    var mangas = mutableStateListOf<Manga>()
        private set

    fun addManga(item: Manga) {
        mangas.add(item)
    }

    fun addMangas(items: List<Manga>) {
        mangas.addAll(items)
    }

    fun removeManga(item: Manga) {
        mangas.remove(item)
    }

    fun removeMangaAt(index: Int) {
        mangas.removeAt(index)
    }

    fun clearMangas() {
        mangas.clear()
    }

    var itemsLiveData = MutableLiveData<List<Bitmap>>()

    fun addLiveDataItem(item: Bitmap) {
        itemsLiveData.postValue(itemsLiveData.value?.plus(item) ?: listOf(item))
    }

    var items = mutableStateListOf<Bitmap>()
        private set

    fun addItem(item: Bitmap) {
        items.add(item)
    }

    fun clearItems() {
        items.clear()
    }

    fun initLiveData(contentResolver: ContentResolver, mangaUri: Uri) {
        this.contentResolver = contentResolver
        this.mangaUri = mangaUri
        clearItems()
        Log.i("START LIVE DATA", "")
        viewModelScope.launch {
//            this.cancel()
//            currentCouroutines.add(this)

            try {
                CoroutineScope(Dispatchers.IO).async rt@{
                    Log.i("START COURUTINE", "")
//                    addCoroutine(this)
//                    getZipSize()
                    unzipFile(this)
//                    removeCoroutine(this)
                    return@rt
                }.await()
//                addCoroutine(deferred)
//                deferred.await()
            } catch (e: CancellationException) {
                e.printStackTrace()
            }
//            currentCouroutines.remove(this)

        }
    }

    fun initDatabase(context: Context) {
        if (pickedFolder == "") return
        val dbHelper = DBHelper(context, null)
//        dbHelper.onUpgrade(dbHelper.writableDatabase, 1, 2)
        viewModelScope.launch {
            try {
                CoroutineScope(Dispatchers.IO).async rt@{
                    Log.i("START COURUTINE", "DATABASE")
                    addMangas(dbHelper.getExistingMangas())
                    val folder = DocumentFile.fromTreeUri(context, Uri.parse(pickedFolder))
                    Log.i("initDatabase", "folder:$folder, uri: $pickedFolder")
                    folder?.listFiles()?.let {

                        for (item in it) {
//                if(item.mimeType)
//                Log.i("MIME", "${item.mimeType}, ${item.name}")
                            if (item.mimeType == "application/x-cbz" || item.mimeType == "application/zip") {
                                folder.name?.let { it1 ->
                                    dbHelper.addManga(
                                        item.baseName!!,
                                        item.uri,
                                        0,
                                        123,
                                        !item.exists(),
                                        item.lastModified(),
                                        Uri.parse(pickedFolder),
                                        it1

                                    )
                                        ?.let { it1 -> addManga(it1) }
                                }
                            }
                        }

                        for (item in dbHelper.getExistingMangasInFolder(Uri.parse(pickedFolder))) {
                            Log.i("MANGA", item.toString())
//                            dbHelper.editManga(item.name, item.uri, "TESTINGTESTING")
                        }

//                        dbHelper.removeAllManga()

                        //====================
//            db.editManga(
//                "1.cbz",
//                Uri.parse("content://com.android.externalstorage.documents/tree/primary%3ADOujins/document/primary%3ADOujins%2F1.cbz"),
//                "POLICJAMIDOKUCZA"
//            )
//                                    for (item in it) {
//                                        db.addManga(
//                                            name = item.name!!,
//                                            uri = item.uri,
//                                            0,
//                                            123,
//                                            false
//                                        )
//                                    }

                    }
                    return@rt
                }.await()
            } catch (e: CancellationException) {
                e.printStackTrace()
            }
        }

    }

    fun init(contentResolver: ContentResolver, mangaUri: Uri) {
        this.contentResolver = contentResolver
        this.mangaUri = mangaUri
        clearItems()
        Log.i("START MODEL", "")
//        currentCouroutines.forEach { coroutine ->
//            try {
////                coroutine.cancel("???AXD")
////            coroutine.
////                removeCoroutine(coroutineScope = coroutine)
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }

        viewModelScope.launch {
//            this.cancel()
//            currentCouroutines.add(this)

            try {
                CoroutineScope(Dispatchers.IO).async rt@{
                    Log.i("START COURUTINE", "")
//                    addCoroutine(this)
//                    getZipSize()
                    unzipFile(this)
//                    removeCoroutine(this)
                    return@rt
                }.await()
//                addCoroutine(deferred)
//                deferred.await()
            } catch (e: CancellationException) {
                e.printStackTrace()
            }
//            currentCouroutines.remove(this)

        }
    }


    private fun getZipSize(): Int {
        var size: Int = 0
        Log.i("console.log", "START")
        try {

            val inputStream = this.contentResolver.openInputStream(this.mangaUri)
            var zipEntry: ZipEntry?

//            val readBuffer = ByteArray(4096)
            ZipInputStream(inputStream).use { zipInputStream ->
//                val po
                while (zipInputStream.nextEntry.also { zipEntry = it } != null) {
                    if (zipEntry?.isDirectory == true) continue

                    size += 1


                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        Log.i("console.log", "END $size")
        return size
    }

    private fun unzipFile(scope: CoroutineScope) {
        Log.i("UNZIP", "UNZIP_FILE")
        val currentUri = this.mangaUri


//        Log.i("isActive", scope.isActive.toString())
        try {

            val inputStream = this.contentResolver.openInputStream(this.mangaUri)
            var zipEntry: ZipEntry?

            val readBuffer = ByteArray(4096)
            ZipInputStream(inputStream).use { zipInputStream ->
//                val possibleParentDirectory = destinationDirectory.findFile(getNameWithoutExtension(file.name))
                //                .delete();
//                possibleParentDirectory?.delete()
//                val outFolder = destinationDirectory.createDirectory(getNameWithoutExtension(file.name))
//                while(zipInputStream.nextEntry)
//                do {
//                    zipEntry = zipInputStream.nextEntry
//                } while (zipEntry != null)
//                var iter = 0
                while (zipInputStream.nextEntry.also { zipEntry = it } != null) {
                    if (mangaUri != currentUri) break
//                    var bytes:ArrayList<Byte> = ArrayList()
//                    zipEntry.si

//                    var iter2 = 0

                    if (zipEntry?.isDirectory == true) continue
                    val bytes = zipInputStream.readBytes()
                    zipInputStream.closeEntry()
//                    val image: ImageView = ImageView(applicationContext)
                    try {
                        val bitmap: Bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

//                        Log.i("console.log", "$mangaUri, $currentUri")

                        addItem(bitmap)
                        addLiveDataItem(bitmap)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }


//                    nameArrayList.add(mangaUri.toString())
//                    _name.postValue(nameArrayList)
//                    bitmapsArrayList.add(bitmap)
//                    Log.i("bitmapsArrayList", bitmapsArrayList.toString())
//                    bitmapsLiveData.postValue(bitmapsArrayList)
//                    Log.i("bitmapsLiveData", bitmapsLiveData.toString())
//                    return@use bitmap
//                    Log.i("bitmap", "OK")
//                    return bitmap

//                    image.setImageBitmap(bitmap)
//                    linearLayout.addView(image)
//                    Log.i("BYTES", Arrays.toString(bytes))
//                    while (zipInputStream.read(readBuffer).also { readLen = it } != -1) {
////                        bytes.add(readBuffer)
////                        val test = rea
////                        iter += 1
//                    Log.i("ITER", "${iter.toString()}")
////                        Log.i("ITER2", iter2.toString())
//                    }
//                    iter2 += 1
//                    iter+=1
                }
//                while (zipInputStream.nextEntry.also { localFileHeader = it } != null) {
//                    val mimeType = if (getFileExtensionFromName(localFileHeader.fileName).length > 0) "image/" + getFileExtensionFromName(localFileHeader.fileName) else "image/png"
//                    val outFile = outFolder!!.createFile(mimeType, localFileHeader.fileName)
//                    fList.pushString(outFile!!.uri.toString())
//                    resolver.openOutputStream(outFile.uri).use { outputStream ->
//                        while (zipInputStream.read(readBuffer).also { readLen = it } != -1) {
//                            outputStream!!.write(readBuffer, 0, readLen)
//                        }
//                    }
//                }
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }

    }

}