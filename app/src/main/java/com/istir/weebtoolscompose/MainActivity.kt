package com.istir.weebtoolscompose

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
//import android.graphics.Color
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.*
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
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
import com.google.android.material.appbar.CollapsingToolbarLayout
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
        mangaViewModel.pickedFolder = getFolderFromCache(mangaViewModel).toString()
//        val db = DBHelper(applicationContext, null)


        setContent {
            LaunchedEffect(mangaViewModel.pickedFolder) {
                mangaViewModel.clearMangas()
                Log.i("CLEAR", "??")
                mangaViewModel.initDatabase(applicationContext)

            }
            val systemUIController = rememberSystemUiController()


            ProvideWindowInsets() {
                WeebToolsComposeTheme(isSystemInDarkTheme()) {
                    val decayAnimationSpec = rememberSplineBasedDecay<Float>()
                    val scrollBehavior = remember(decayAnimationSpec) {
//                        val d =  decayAnimationSpec(f:Float) {
//
//                    }
//                        val decayAnimationSpec = DecayAnimationSpec(f:Float) {
//
//                        }
                        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(decayAnimationSpec)
//                        CollapsingToolbarLayout(applicationContext,)
//                        TopAppBarDefaults.pinnedScrollBehavior(true)
                    }
//val topAppBarColors = TopAppBarColors.containerColor(scrollFraction = 0f)
//                  val topAppBarColors = object :TopAppBarColors {
//
//                  }
                    val c = TopAppBarDefaults.mediumTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        scrolledContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        navigationIconContentColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.primary,
                        actionIconContentColor = MaterialTheme.colorScheme.primary,

                        )

                    systemUIController.setSystemBarsColor(MaterialTheme.colorScheme.primaryContainer)
                    val scrollBehavior1 = remember { TopAppBarDefaults.pinnedScrollBehavior() }
                    val scrollBehavior2 = remember { TopAppBarDefaults.enterAlwaysScrollBehavior() }
                    Scaffold(
                        modifier = Modifier
                            .nestedScroll(scrollBehavior2.nestedScrollConnection)
                            .statusBarsPadding(),
                        topBar = {
                            MediumTopAppBar(
                                title = { Text("weebTools") },
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
//colors = TopAppBarColors.,
                                colors = c,
                                scrollBehavior = scrollBehavior2,

//                                modifier = Modifier
//                                    .padding(0.dp)
//                                    .padding(0.dp, 100.dp, 0.dp, 0.dp)

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

    private fun getFolderFromCache(mangaViewModel: MangaViewModel): Uri? {
        val sharedPreferences: SharedPreferences = this.getPreferences(Context.MODE_PRIVATE);
        val defaultValue = resources.getString(R.string.pickedMangaFolder)
        val folderUri =
            sharedPreferences.getString(getString(R.string.pickedMangaFolder), defaultValue)
        Toast.makeText(baseContext, "cached: ${folderUri}", Toast.LENGTH_LONG).show()
//        val pref = baseContext.getSharedPreferences("@pickedMangaFolder", 0)
//        if (folderUri != null) {
//            Log.i("getFolderfromCache", folderUri)
//        }
//        if (folderUri != "NULL") return Uri.parse(folderUri)
//        return setupSimpleStorage(mangaViewModel = mangaViewModel)
        if (folderUri == "NULL") {
            setupSimpleStorage(mangaViewModel)
//            Log.i("PICKED", "${mangaViewModel.pickedFolder}")
            return Uri.parse(mangaViewModel.pickedFolder)
        } else {
            return Uri.parse(folderUri)
        }
//        return null;
    }

    private fun setupSimpleStorage(mangaViewModel: MangaViewModel) {
//        var selected: Uri? = null

        storageHelper.onFolderSelected = { requestCode: Int, folder: DocumentFile ->
            Toast.makeText(baseContext, "Selected: ${folder.name}", Toast.LENGTH_LONG).show()
//            pickedFolder = folder
//            this.pickedFolder = folder.uri
            mangaViewModel.clearMangas()

            mangaViewModel.pickedFolder = folder.uri.toString()
//            mangaViewModel.initDatabase(applicationContext)
            addFolderToCache(folder)
//            populateList(folder)
//            selected = folder.uri


        }

//        storageHelper.requestStorageAccess()
        storageHelper.openFolderPicker()
//        return getFolderFromCache(mangaViewModel)
//        return selected
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MangaList(mangaViewModel: MangaViewModel, innerPadding: PaddingValues) {
    val sorted = mangaViewModel.mangas.sortedByDescending { it.modifiedAt }
//    if (LocalWindowInsets.current.systemBars.top > 0) {


    LazyVerticalGrid(
        contentPadding = innerPadding,
//        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),

        cells = GridCells.Fixed(2),
        modifier = Modifier.padding(8.dp, 0.dp)
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

    Box(Modifier.padding(0.dp, 4.dp)) {
        ElevatedCard(modifier = Modifier
            .clickable {
                val intent = Intent(context1, MangaViewFullscreenActivity::class.java)
                intent.putExtra("mangaUri", manga.uri.toString())
                intent.putExtra("mangaName", manga.name)
                context1.startActivity(intent)
            }
            .height(300.dp)
        ) {
//            if (manga.pages == -1) {
////                Box(
////                    Modifier
////                        .fillMaxSize()
////                        .background(Color.Transparent)) {
////                CircularProgressIndicator(
//////                    Modifier.fillMaxSize()
////                )
////                }
//            }

            Column(verticalArrangement = Arrangement.SpaceBetween) {
//                Box(Modifier.wei)
                MangaThumbnail(image = manga.image)
//                Spacer(Modifier.size(5.dp))
                MangaDescription(manga = manga)
            }
        }
    }
}

@Composable
fun ShimmerAnimation(transition: InfiniteTransition) {
    val a by transition.animateColor(
        initialValue = MaterialTheme.colorScheme.primary,
        targetValue = MaterialTheme.colorScheme.primaryContainer,
        animationSpec = infiniteRepeatable(
            tween(durationMillis = 1000, easing = FastOutSlowInEasing),
            RepeatMode.Reverse
        )
    )


    Box(
        modifier = Modifier
            .fillMaxSize()
//                .size(250.dp)
            .background(a)
    ) {
        CircularProgressIndicator(
            Modifier.align(Alignment.Center),
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}


@Composable
fun ColumnScope.MangaDescription(manga: Manga) {
    Box(Modifier.padding(8.dp)) {
        Column() {
            Text(text = manga.name)
            val text =
                if (manga.pages != -1) "${manga.currentPosition}/${manga.pages}" else "Loading..."
            Text(text = text)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColumnScope.MangaThumbnail(image: String) {
    ElevatedCard(
        Modifier
            .weight(1f)
            .padding(8.dp)
    ) {
        if (image != null && image != "") {
//        Log.i("MANGAIMAGE", "${image}")
            var bitmap: ImageBitmap? = null
            try {
                bitmap = BitmapFactory.decodeFile(image).asImageBitmap()

            } catch (e: Exception) {
                e.printStackTrace()
            }
            if (bitmap != null) {
                Image(
                    bitmap = bitmap,
                    contentDescription = "cover",
                    contentScale = ContentScale.Crop,
//                modifier=Modifier.size
                    modifier = Modifier.fillMaxHeight()
                )

            }
        } else {
            ShimmerAnimation(
                rememberInfiniteTransition()
            )
        }
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