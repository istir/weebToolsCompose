package com.istir.weebtoolscompose

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.documentfile.provider.DocumentFile
import com.anggrayudi.storage.SimpleStorageHelper
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.cutoutPadding
import com.google.accompanist.insets.statusBarsPadding
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.istir.weebtoolscompose.ui.theme.WeebToolsComposeTheme

class MainActivity : ComponentActivity() {
    private val storageHelper = SimpleStorageHelper(this)
    private var pickedFolder: Uri? = null //state later

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)


        pickedFolder = getFolderFromCache()


        setContent {
            val systemUIController = rememberSystemUiController()
            systemUIController.setSystemBarsColor(Color.Transparent)

            val darkTheme = isSystemInDarkTheme()

            val DarkColors = darkColors(
                primary = Color(0xffffeb46),
                background = Color(0xff333333)
            )
            val LightColors = lightColors(
                primary = Color(0xfff98880),
                background = Color(0xffEEEEEE)
            )
            ProvideWindowInsets {
                MaterialTheme(
                    colors = if (darkTheme) DarkColors else LightColors
                ) {
                    // A surface container using the 'background' color from the theme
                    Surface(
                        color = MaterialTheme.colors.background,
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth()
                    ) {
                        Box() {
                            val theDp = with(LocalDensity.current) {
                                LocalWindowInsets.current.systemBars.top.toDp()
                            }


                            if (pickedFolder != null) {
                                val folder =
                                    DocumentFile.fromTreeUri(applicationContext, pickedFolder!!)
                                folder?.listFiles()?.let {
                                    MangaList(
                                        documentFiles = it,

                                        )
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .height(theDp)
                                    .fillMaxWidth()
                                    .background(
                                        brush = Brush.verticalGradient(
                                            colors = listOf(
                                                Color(0x44000000),
                                                Color.Transparent
                                            )
                                        )
                                    )
                            )
                            ChooseFolderButton({ setupSimpleStorage() })
                        }


//                    if (this.choosenManga != null) {
//                        val context = LocalContext.current
//                        Log.i("before intent", "")
//                        val intent = Intent(context, MangaViewActivity::class.java)
//                        intent.putExtra("mangaUri", this.choosenManga.toString())
//                        context.startActivity(intent)
////                        context.start
//                        this.choosenManga = null
//                    }

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
fun MangaList(documentFiles: Array<DocumentFile>) {
//    val sortableFiles:Array<com.istir.weebtoolscompose.DocumentFile> = documentFiles as Array<com.istir.weebtoolscompose.DocumentFile>
//    var sortedFiles = sortableFiles.sort()
//
//    var sortedFiles:ArrayList<DocumentFile> = ArrayList()
//
//    for (documentFile in documentFiles) {
//
//    }
    documentFiles.sortByDescending { it.lastModified() }
    if (LocalWindowInsets.current.systemBars.top > 0) {
        Column(Modifier.verticalScroll(rememberScrollState())) {
            Log.i("STATUS BAR", "${LocalWindowInsets.current.systemBars.top}")

            val theDp = with(LocalDensity.current) {
                LocalWindowInsets.current.systemBars.top.toDp()
            }
            Box(
                modifier = Modifier
                    .height(theDp)
                    .fillMaxWidth()
            )
            for (documentFile in documentFiles) {
//        val documentFile = DocumentFile.fromSingleUri(context, uri)

                documentFile.name?.let {
                    MangaListItem(
                        uri = documentFile.uri,
                        name = it

                    )
                }

            }
        }
    }
}

@Composable
fun MangaListItem(uri: Uri, name: String) {
    val context1 = LocalContext.current
    Button(
        onClick = {
            val intent = Intent(context1, MangaViewFullscreenActivity::class.java)
            intent.putExtra("mangaUri", uri.toString())
            context1.startActivity(intent)
        },
        Modifier
            .fillMaxWidth()
            .padding(15.dp)

    ) {
        Text(text = name)
    }

}

@Composable
fun ChooseFolderButton(onClick: () -> Unit) {

    Button(onClick = onClick, modifier = Modifier.padding(0.dp, 200.dp, 0.dp, 0.dp)) {
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