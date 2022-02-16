package com.istir.weebtoolscompose

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.documentfile.provider.DocumentFile
import com.anggrayudi.storage.SimpleStorageHelper
import com.istir.weebtoolscompose.ui.theme.WeebToolsComposeTheme

class MainActivity : ComponentActivity() {
    private val storageHelper = SimpleStorageHelper(this)
    private var pickedFolder: Uri? = null //state later
    private var choosenManga by mutableStateOf<Uri?>(null)

    //    private var images: List<Bitmap?>? = null
    var images by mutableStateOf<List<Bitmap?>?>(null)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        pickedFolder = getFolderFromCache()


        setContent {
            WeebToolsComposeTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    if (pickedFolder != null) {
                        val folder = DocumentFile.fromTreeUri(applicationContext, pickedFolder!!)
                        folder?.listFiles()?.let {
                            MangaList(
                                documentFiles = it,
                                context = applicationContext
                            ) { uri: Uri ->
                                this.choosenManga = uri
                            }
                        }
                    }


                    ChooseFolderButton({ setupSimpleStorage() })
                    if (this.choosenManga != null) {
                        val context = LocalContext.current
                        val intent = Intent(context, MangaViewActivity::class.java)
                        intent.putExtra("mangaUri", this.choosenManga.toString())
                        context.startActivity(intent)
//                        this.choosenManga = null
                    }

                }
            }
        }
    }

    private fun addFolderToCache(folder: DocumentFile) {
        val sharedPreferences: SharedPreferences = this.getPreferences(Context.MODE_PRIVATE);
        val editor = sharedPreferences.edit()
        editor.putString(getString(R.string.pickedMangaFolder), folder.uri.toString())
        editor.apply()

    }

    private fun getFolderFromCache(): Uri? {
        val sharedPreferences: SharedPreferences = this.getPreferences(Context.MODE_PRIVATE);
        val defaultValue = resources.getString(R.string.pickedMangaFolder)
        val folderUri =
            sharedPreferences.getString(getString(R.string.pickedMangaFolder), defaultValue)
        Toast.makeText(baseContext, "cached: ${folderUri}", Toast.LENGTH_LONG).show()
//        val pref = baseContext.getSharedPreferences("@pickedMangaFolder", 0)
//        if (folderUri != null) {
//            Log.i("getFolderfromCache", folderUri)
//        }
        if (folderUri != "NULL") return Uri.parse(folderUri)
        return null;
    }

    private fun setupSimpleStorage() {
        storageHelper.onFolderSelected = { requestCode: Int, folder: DocumentFile ->
            Toast.makeText(baseContext, "Selected: ${folder.name}", Toast.LENGTH_LONG).show()
//            pickedFolder = folder
            this.pickedFolder = folder.uri
            addFolderToCache(folder)
//            populateList(folder)
        }

//        storageHelper.requestStorageAccess()
        storageHelper.openFolderPicker()
    }

}

//class DocumentFile(documentFile: androidx.documentfile.provider.DocumentFile) :
//    androidx.documentfile.provider.DocumentFile {
//    //    constructor() : this()
//    private var documentFile: DocumentFile = documentFile
//
//
//    override fun compareTo(other: androidx.documentfile.provider.DocumentFile): Int {
////        TODO("Not yet implemented")
//        return this.documentFile.lastModified().compareTo(other.lastModified())
//
//    }
//}

@Composable
fun MangaList(documentFiles: Array<DocumentFile>, context: Context, onClick: (uri: Uri) -> Unit) {
//    val sortableFiles:Array<com.istir.weebtoolscompose.DocumentFile> = documentFiles as Array<com.istir.weebtoolscompose.DocumentFile>
//    var sortedFiles = sortableFiles.sort()
//
//    var sortedFiles:ArrayList<DocumentFile> = ArrayList()
//
//    for (documentFile in documentFiles) {
//
//    }
    documentFiles.sortByDescending { it.lastModified() }
    Column(Modifier.verticalScroll(rememberScrollState())) {


        for (documentFile in documentFiles) {
//        val documentFile = DocumentFile.fromSingleUri(context, uri)

            documentFile.name?.let {
                MangaListItem(
                    uri = documentFile.uri,
                    name = it,
                    onClick = onClick
                )
            }

        }
    }
}

@Composable
fun MangaListItem(uri: Uri, name: String, onClick: (uri: Uri) -> Unit) {
    Button(onClick = { onClick(uri) }, Modifier.fillMaxWidth()) {
        Text(text = name)
    }

}

@Composable
fun ChooseFolderButton(onClick: () -> Unit) {

    Button(onClick = onClick) {
        Text(text = "Choose a folder")
    }
}

//
//@Preview(showBackground = true)
//@Composable
//fun DefaultPreview() {
//    WeebToolsComposeTheme {
//    }
//}