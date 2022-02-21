package com.istir.weebtoolscompose

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.documentfile.provider.DocumentFile
import com.anggrayudi.storage.SimpleStorageHelper
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.statusBarsPadding
//import com.google.accompanist.insets.LocalWindowInsets
//import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.istir.weebtoolscompose.ui.theme.WeebToolsComposeTheme
import java.io.File


class MainActivity : ComponentActivity() {
    private val storageHelper = SimpleStorageHelper(this)
//    private var pickedFolder: Uri? = null //state later

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val mangaViewModel: MangaViewModel by viewModels()
        //=============
//        val cover = mangaViewModel.getCoverImage(
//            applicationContext.cacheDir,
//            contentResolver,
//            Uri.parse("content://com.android.externalstorage.documents/tree/primary%3ADOujins/document/primary%3ADOujins%2FChapter%200%20(2).cbz"),
//            9.toString()
//        )
//        Log.i("cover", "$cover")
//        pickedFolder = getFolderFromCache()
        mangaViewModel.pickedFolder = getFolderFromCache().toString()
//        val db = DBHelper(applicationContext, null)


        setContent {
            LaunchedEffect(mangaViewModel.pickedFolder) {
                mangaViewModel.clearMangas()
                Log.i("CLEAR", "??")
                mangaViewModel.initDatabase(applicationContext)

            }
//            val systemUIController = rememberSystemUiController()
//            systemUIController.setSystemBarsColor(Color.Transparent)

            ProvideWindowInsets() {
                WeebToolsComposeTheme(isSystemInDarkTheme()) {
                    val decayAnimationSpec = rememberSplineBasedDecay<Float>()
                    val scrollBehavior = remember(decayAnimationSpec) {
                        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(decayAnimationSpec)
                    }



                    Scaffold(
                        modifier = Modifier
                            .nestedScroll(scrollBehavior.nestedScrollConnection)
                            .statusBarsPadding(),
                        topBar = {
                            MediumTopAppBar(
                                title = { Text("Medium TopAppBar") },
                                navigationIcon = {
                                    IconButton(onClick = { /* doSomething() */ }) {
                                        Icon(
                                            imageVector = Icons.Filled.Menu,
                                            contentDescription = "Localized description"
                                        )
                                    }
                                },
                                //set background color so it doenst change idk
                                actions = {
                                    IconButton(onClick = { /* doSomething() */ }) {
                                        Icon(
                                            imageVector = Icons.Filled.Favorite,
                                            contentDescription = "Localized description"
                                        )
                                    }
                                },
                                scrollBehavior = scrollBehavior,


                                )
                        }, content = { innerPadding ->

//                                items()
                            MangaList(
                                mangaViewModel = mangaViewModel,
                                innerPadding = innerPadding
                            )

//                                MediumTopAppBar (title = { Text(text = "weebTools") })

                        })

//                                MangaList(mangaViewModel = mangaViewModel)
//
//
//                                ChooseFolderButton({ setupSimpleStorage(mangaViewModel) })
                }


            }
        }
    }

    private fun addFolderToCache(folder: DocumentFile) {
        Log.i("SAVE?", "")
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

    private fun setupSimpleStorage(mangaViewModel: MangaViewModel) {
        storageHelper.onFolderSelected = { requestCode: Int, folder: DocumentFile ->
            Toast.makeText(baseContext, "Selected: ${folder.name}", Toast.LENGTH_LONG).show()
//            pickedFolder = folder
//            this.pickedFolder = folder.uri
            mangaViewModel.clearMangas()

            mangaViewModel.pickedFolder = folder.uri.toString()
//            mangaViewModel.initDatabase(applicationContext)
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

//@ExperimentalMaterial3Api
//fun exitUntilCollapsedScrollBehavior(
//    decayAnimationSpec: DecayAnimationSpec<Float?>?,
//    canScroll: (() -> Boolean)? = { true }
//): TopAppBarScrollBehavior {
//
//}

@Composable
fun MangaList(mangaViewModel: MangaViewModel, innerPadding: PaddingValues) {
    val sorted = mangaViewModel.mangas.sortedByDescending { it.modifiedAt }
//    if (LocalWindowInsets.current.systemBars.top > 0) {
    LazyColumn(
        contentPadding = innerPadding,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(sorted.size) {
            MangaListItem(manga = sorted[it])
        }
        /*
//            Log.i("STATUS BAR", "${LocalWindowInsets.current.systemBars.top}")

//            val theDp = with(LocalDensity.current) {
//                LocalWindowInsets.current.systemBars.top.toDp()
//            }
        Box(
            modifier = Modifier
//                    .height(theDp)
                .fillMaxWidth()
        )
        for (manga in sorted) {
//        val documentFile = DocumentFile.fromSingleUri(context, uri)

//                documentFile.name?.let {
//                if (manga.pages == -1) {
//                    Log.i(
//                        "PAGES", "${manga}"
//                    )
//                    MangaListItemLoading(
//                        manga = manga
//                    )
//                } else {
            MangaListItem(
                manga = manga
            )
//                }

//                }

        }
    */
//        }
    }
}

//
//@Composable
//fun MangaListItemLoading(manga: Manga) {
//    val context1 = LocalContext.current
//    CircularProgressIndicator(
//
//    )
//    if (manga.image != "") {
//        val bitmap = BitmapFactory.decodeFile(manga.image).asImageBitmap()
//        Image(bitmap = bitmap, contentDescription = "cover")
//    }
//
//    Button(
//        onClick = {
//            val intent = Intent(context1, MangaViewFullscreenActivity::class.java)
//            intent.putExtra("mangaUri", manga.uri.toString())
//            intent.putExtra("mangaName", manga.name)
//            context1.startActivity(intent)
//        },
//        Modifier
//            .fillMaxWidth()
//            .padding(15.dp)
//            .background(Color.Blue)
////            .background(if (manga.pages == -1) Color.Red else Color.Blue)
//
//    ) {
//        Text(text = manga.name)
//    }
//}
//@ExperimentalMaterial3
//@OptIn(ExperimentalMaterialApi::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MangaListItem(manga: Manga) {
    val context1 = LocalContext.current


    ElevatedCard(modifier = Modifier.clickable {
        val intent = Intent(context1, MangaViewFullscreenActivity::class.java)
        intent.putExtra("mangaUri", manga.uri.toString())
        intent.putExtra("mangaName", manga.name)
        context1.startActivity(intent)
    }) {
        if (manga.pages == -1) {
            CircularProgressIndicator(
            )
        }

        if (manga.image != null && manga.image != "") {
            Log.i("MANGAIMAGE", "${manga.image}")
            var bitmap: ImageBitmap? = null
            try {
                bitmap = BitmapFactory.decodeFile(manga.image).asImageBitmap()

            } catch (e: Exception) {
                e.printStackTrace()
            }
            if (bitmap != null) {
                Image(bitmap = bitmap, contentDescription = "cover")
            }
        }

        Text(text = manga.name)
    }

}

@Composable
fun MangaListItem(uri: Uri, name: String) {
    val context1 = LocalContext.current
    Button(
        onClick = {
            val intent = Intent(context1, MangaViewFullscreenActivity::class.java)
            intent.putExtra("mangaUri", uri.toString())
            intent.putExtra("mangaName", name)
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