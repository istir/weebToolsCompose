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
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream


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

    fun editManga(mangaToModify: Manga) {
//        removeManga(oldManga)
//        addManga(newManga)

//        var m = mangas.find { manga -> manga.id == mangaToModify.id }
//        m = mangaToModify
        Log.i("MANGA TO MODIFY", "${mangaToModify}")
//        if (mangaToModify.id != null) {
//            val index = mangas.indexOfFirst { it.id == mangaToModify.id }
//            if (index > 0)
//                mangas[index] = mangaToModify
//        } else {
        val index = mangas.indexOfFirst {
            Log.i("modifying", "it: ${it}, modify:${mangaToModify}")
            if (it.id != null) {
                it.id == mangaToModify.id
            } else {
                it.uri == mangaToModify.uri
            }
        }
        if (index > 0)
            mangas[index] = mangaToModify
//        }

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


    fun getMetadata(context: Context, mangas: ArrayList<Manga>) {
        val dbHelper = DBHelper(context, null)
        Log.i("METADATA?????????", "$mangas")
        viewModelScope.launch {
//            for (manga in mangas) {
            try {
                mangas.map {
                    Log.i("pages", "${it.pages}")
//                if (manga.pages == -1) {
                    async(Dispatchers.IO) {
                        val pageCount = getZipSize(context.contentResolver, it.uri)
                        val cover = getCoverImage(
                            context,
                            context.contentResolver,
                            it.uri,
                            it.id.toString()
                        )
                        Log.i("COVER", cover.toString())
                        dbHelper.editMangaCover(it.name, it.uri, cover)
                        dbHelper.editMangaPages(it.uri, pageCount)
//                    removeManga(it)
//                    val newManga = it
                        it.image = cover
                        it.pages = pageCount
//                    addManga(it)
//                    Log.i("PAGES", "forsenDespair ...")
                        editManga(it)
//                    removeMangaAt(2)
//                    Log.i("it", "${it.toString()}")
                    }
                }.awaitAll()
            } catch (e: Exception) {
                e.printStackTrace()
            }
//            Log.i("AWAITED", "ALL")
        }
//            this.cancel()
//            currentCouroutines.add(this)

//                try {
//                    CoroutineScope(Dispatchers.IO).async rt@{
//                        Log.i("START COURUTINE", "")
//
////                        val p = dbHelper.getMangaProgress(uri.uri)
////                        Log.i("progress", "${p}")
//
//
//
////                        unzipFile(this)
////                    removeCoroutine(this)
//                        return@rt
//                    }.await()
//                addCoroutine(deferred)
//                deferred.await()
//                } catch (e: CancellationException) {
//                    e.printStackTrace()
//                }

//            currentCouroutines.remove(this)


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
//                    Log.i("zipSize", "${getZipSize()}")
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
                    addMangas(dbHelper.getExistingMangasInFolder(Uri.parse(pickedFolder)))
//                    for (manga in dbHelper.getExistingMangas()) {
                    getMetadata(
                        context,
                        dbHelper.getExistingMangasInFolderWithoutPages(Uri.parse(pickedFolder))
                    )
//                    }

                    val folder = DocumentFile.fromTreeUri(context, Uri.parse(pickedFolder))
                    Log.i("initDatabase", "folder:$folder, uri: $pickedFolder")
                    folder?.listFiles()?.let {
                        val newlyAddedMangas = ArrayList<Manga>()
                        for (item in it) {
//                if(item.mimeType)
//                Log.i("MIME", "${item.mimeType}, ${item.name}")
                            if (item.mimeType == "application/x-cbz" || item.mimeType == "application/zip") {
                                folder.name?.let { it1 ->
                                    dbHelper.addManga(
                                        item.baseName!!,
                                        item.uri,
                                        0,
                                        -1,
                                        !item.exists(),
                                        item.lastModified(),
                                        Uri.parse(pickedFolder),
                                        it1, ""

                                    )
                                        ?.let { it1 ->
                                            addManga(it1)
                                            newlyAddedMangas.add(it1)
//                                            getMetadata(context, arrayListOf(it1))
                                        }
                                }
                            }
                        }
                        getMetadata(context, newlyAddedMangas)
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

    fun getCoverImage(
        context: Context,
        contentResolver: ContentResolver,
        uri: Uri,
        mangaName: String
    ): String {
//        var cover: String = ""

//        val cacheDirectory = DocumentFile.fromFile(context.cacheDir)
        val coverDirectory = context.getDir("cover", Context.MODE_PRIVATE)
        val outFile = File(coverDirectory, "$mangaName.jpg")
//        val cacheDirectory = DocumentFile.fromTreeUri(
//            context,
//            Uri.parse("content://com.android.externalstorage.documents/tree/primary%3ADOujins/document/primary%3ADOujins")
//        )
//        val foundCoverDir = cacheDirectory?.findFolder("cover")
//        var coverDirectory =
//            foundCoverDir ?: cacheDirectory?.createDirectory("cover")
//
//
////        val coverDirectory = cacheDirectory?.createDirectory("cover")
//        Log.i("coverDir", "$CD")
//        val outFile = coverDirectory!!.createFile("image/png", mangaName)

        try {
//            val outputStream = contentResolver.openOutputStream(coverDirectory!!.uri)
            val inputStream = contentResolver.openInputStream(uri)
            var zipEntry: ZipEntry?
            var readLen: Int
            val readBuffer = ByteArray(4096)
            ZipInputStream(inputStream).use { zipInputStream ->
//
                if (inputStream != null) {
//                    Log.i("available", "${inputStream.available()}")
                }
//
                while (zipInputStream.nextEntry.also { zipEntry = it } != null) {
                    if (zipEntry?.isDirectory == true) continue
                    if (zipEntry?.name?.endsWith("png") == true || zipEntry?.name?.endsWith("jpg") == true || zipEntry?.name?.endsWith(
                            "jpeg"
                        ) == true
                    ) {
//                        cover=zipEntry.
                        val bytes = zipInputStream.readBytes()
                        val bitmap: Bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
//                        bitmap.getScaledWidth(DisplayMetrics())
                        val w = bitmap.width
                        val h = bitmap.height
                        var newW = 0
                        var newH = 0
                        val maxSize = 512
                        if (w > h) {
                            newH = h * maxSize / w
                            newW = maxSize
                        } else {
                            newW = w * maxSize / h
                            newH = maxSize
                        }
//                        bitmap.getScaledWidth()
                        val out = FileOutputStream(outFile)
                        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, newW, newH, false)
                        //2048x1024
                        //512x(1024*512/2048)
                        //1024x2048
                        //(1024*512/2048)x512
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out)
                        out.flush()
                        out.close()
//960x764

//                        bitmap.compress(Bitmap.CompressFormat.JPEG,100,)
                        zipInputStream.closeEntry()
//contentResolver.openOutputStream(outFile.toURI())
//                        context.openfil
//                        contentResolver.openOutputStream(outFile!!.uri).use { oS ->
//                            oS!!.write(bytes)
////                            while (zipInputStream.read(readBuffer).also { readLen = it } != -1) {
////                                oS!!.write(readBuffer, 0, readLen)
////                            }
//                        }
                        break
                    }
//                    size += 1
//                    val bytes = zipInputStream.readBytes()

//                    val image: ImageView = ImageView(applicationContext)
////                    try {
//                        val bitmap: Bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
//
////                        Log.i("console.log", "$mangaUri, $currentUri")
//
//                        addItem(bitmap)
//                        addLiveDataItem(bitmap)
//
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
//        Log.i(
//            "OUTFILE",
//            "abs: ${outFile.absolutePath}, can: ${outFile.canonicalPath}, path: ${outFile.path}, uri: ${outFile.toUri()}"
//        )
        return outFile.path
//        return Uri.parse("")
    }

    private fun getZipSize(contentResolver: ContentResolver, uri: Uri): Int {
        var size: Int = 0
        Log.i("console.log", "START, $uri")
        try {
//            val cur = contentResolver.query(this.mangaUri, null, null, null, null)
//            val column_index: Int? = cur?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
//            Log.i("cur", "${column_index}")
//            val uri: URI = URI(URLEncoder.encode(this.mangaUri.toString(), "UTF-8"))
////            val uri1:Uri
//            val file = File(uri)
//            Log.i("FILE", "file: $file")

//        val zipFile:net.lingala.zip4j.ZipFile =
            val inputStream = contentResolver.openInputStream(uri)
//            inputStream.available()
            var zipEntry: ZipEntry?

//            val readBuffer = ByteArray(4096)
            ZipInputStream(inputStream).use { zipInputStream ->
//                val po
                if (inputStream != null) {
//                    Log.i("available", "${inputStream.available()}")
                }
//                while (zipInputStream.nextEntry != null) {
//                    size += 1
//                }
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
        Log.i("UNZIP", "UNZIP_FILE, ${this.mangaUri}")


        val currentUri = this.mangaUri

//        val file = File(currentUri.toString())
//        val zf = ZipFile(file)
//        Log.i("size", "${zf.size()}")
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