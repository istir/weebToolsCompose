package com.istir.weebtoolscompose

import android.content.ContentResolver
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntOffsetAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollDispatcher
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.istir.weebtoolscompose.ui.theme.WeebToolsComposeTheme
import kotlin.math.roundToInt
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Velocity
import androidx.core.view.WindowCompat
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.systemuicontroller.rememberSystemUiController

class MangaViewActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val mangaUri: Uri = Uri.parse(intent.getStringExtra("mangaUri"))
        val model: MangaViewModel by viewModels()
        val zoomedImagesModel: MangaViewZoomedImagesModel by viewModels()
//        Log.i("mangaUri", mangaUri.toString())
        setContent {
            val darkTheme = isSystemInDarkTheme()

            val DarkColors = darkColors(
                primary = Color(0xffffeb46),
                background = Color(0xff333333)
            )
            val LightColors = lightColors(
                primary = Color(0xfff98880),
                background = Color(0xffEEEEEE)
            )
            val systemUIController = rememberSystemUiController()
            systemUIController.isSystemBarsVisible = false // Status & Navigation bars
            ProvideWindowInsets {
                MaterialTheme(
                    colors = if (darkTheme) DarkColors else LightColors
                ) {
                    // A surface container using the 'background' color from the theme
                    Surface(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(),
                        color = MaterialTheme.colors.background
                    ) {
//                    Greeting2("Android")
                        MangaImages(
                            mangaViewModel = model,
                            choosenManga = mangaUri,
                            contentResolver = contentResolver,
                            zoomedImagesModel = zoomedImagesModel
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun MangaImages(
    mangaViewModel: MangaViewModel,
    choosenManga: Uri,
    contentResolver: ContentResolver,
    zoomedImagesModel: MangaViewZoomedImagesModel
) {

//    if (choosenManga == null) return

    mangaViewModel.init(contentResolver = contentResolver, mangaUri = choosenManga)

    MangaImages(bitmaps = mangaViewModel.items, zoomedImagesModel = zoomedImagesModel)

}


//@SuppressLint("CoroutineCreationDuringComposition")
//@OptIn(ExperimentalMaterialApi::class)
//@OptIn(ExperimentalSnapperApi::class)
@Composable
fun MangaImages(bitmaps: List<Bitmap?>?, zoomedImagesModel: MangaViewZoomedImagesModel) {
//    var offset by remember { mutableStateOf(0f) }
    if (bitmaps == null) return
//    val listState = rememberLazyListState()
//    val listState = LazyListState()
//
//    LaunchedEffect(!listState.isScrollInProgress) {
////        Log.i("...", "...")
////        if (!listState.isScrollInProgress) {
////        Log.i("firstVisibleOffset", listState.firstVisibleItemScrollOffset.toString())
////        Log.i("lastOffset", listState.layoutInfo.viewportEndOffset.toString())
////        Log.i("currentIndex", listState.firstVisibleItemIndex.toString())
////        Log.i("src", listState.interactionSource.toString())
//        Log.i("", "${listState.firstVisibleItemScrollOffset}")
//        val offset = listState.firstVisibleItemScrollOffset
//        val maxOffset = listState.layoutInfo.viewportEndOffset
//        val offsetToScrollToNext = maxOffset / 2
//        if (offset > offsetToScrollToNext) {
////Log.i("offset>")
////            Log.i("", listState.layoutInfo.totalItemsCount.toString())
//            val nextIndex =
//                if (listState.layoutInfo.totalItemsCount > listState.firstVisibleItemIndex + 1) listState.firstVisibleItemIndex + 1 else listState.layoutInfo.totalItemsCount
////            val nextIndex = listState.layoutInfo.totalItemsCount>listState.firstVisibleItemIndex+1?listState.firstVisibleItemIndex+1:
//            listState.animateScrollToItem(nextIndex) //check if exists!!!!
//        } else {
//            listState.animateScrollToItem(listState.firstVisibleItemIndex)
//        }
//        listState.firstVisibleItemIndex
////            Log.i("visible items info", listState.layoutInfo.visibleItemsInfo.toString())
//
////        }
//    }
//    coroutineScope.launch {
//
//    }
//listState.item


//    LazyRow(
//        state = listState,
//        modifier = Modifier
//            .fillMaxWidth(), flingBehavior = flingBehavior
//
//    ) {
////        Log.i("scolling?", listState.isScrollInProgress.toString())
//
//        items(bitmaps) { bitmap ->
//
//            if (bitmap != null) {
//                MangaImage(bitmap = bitmap)
//            }
//        }
//    }
//    val listState1 = rememberLazyListState()
////    val animationSpec:AnimationSpec<Float> =
////val animationSpec:AnimationSpec<Float> = tween(durationMillis = 300, easing = FastOutSlowInEasing)
////    val animationSpec: AnimationSpec<Float> =
////        tween(durationMillis = 300, easing = FastOutSlowInEasing)
//    val animationSpec: AnimationSpec<Float> =
////        keyframes {
////            durationMillis = 400
////            0.0f at 0 with LinearOutSlowInEasing
////            0.2f at 15 with FastOutSlowInEasing
////            0.4f at 75 with FastOutLinearInEasing
////            0.4f at 225
////        }
//        tween(
//            durationMillis = 300,
//
//            easing = CubicBezierEasing(0.7F, 0.005F, 0.34F, 1.005F)
//        )
//        spring(dampingRatio = Spring.DampingRatioHighBouncy, stiffness = Spring.StiffnessMedium, )

//    val decayAnimation: DecayAnimationSpec<Float> = splineBasedDecay(Density(3F))
//    val decayAnimation: DecayAnimationSpec<Float> = exponentialDecay(0.66F, 0.23f)
//    LazyRow(
//
////        state = listState,
////        state = LazyListState(),
//        modifier = Modifier
//            .fillMaxWidth()
//            .pointerInput(Unit) {
//                detectDragGestures { change, dragAmount ->
//                    Log.i("DRAGGING", "")
//                }
//            },
////        flingBehavior = rememberSnapperFlingBehavior(
////            lazyListState = listState1,
////            snapOffsetForItem = SnapOffsets.Start,
////            springAnimationSpec = animationSpec,
////            decayAnimationSpec = decayAnimation,
////            snapIndex = { _, startIndex, targetIndex ->
////                targetIndex.coerceIn(startIndex - 1, startIndex + 1)
////            }
////        )
//
//    ) {
////        Log.i("scolling?", listState.isScrollInProgress.toString())
//
//        items(bitmaps) { bitmap ->
//
//            if (bitmap != null) {
//                MangaImage(bitmap = bitmap)
//            }
//        }
//    }
    var isDragging by remember { mutableStateOf(false) }
    var startPosX by remember { mutableStateOf(0f) }
    var draggingTime by remember { mutableStateOf(0) }
    var lastScrollOffset by remember { mutableStateOf(0) }

//    val zoomedItems by remember { mutableStateOf() }
//    val zoomedItems = remember { mutableStateListOf<Int>() };
    val zoomedItems1 = ArrayList<Int>()
//    var isDraggingSmall by remember { mutableStateOf(false) }
    var velocity by remember { mutableStateOf(0) }
    var scrolling by remember { mutableStateOf(1) }
    var page by remember { mutableStateOf(0) }
    var scrollPosX by remember { mutableStateOf(0) }

//    val scrollState =
//        rememberScrollableState(consumeScrollDelta = { delta ->
//            offset += delta
//            delta
//        })
//    val scrollState = rememberScrollState()
//    val scope = rememberCoroutineScope()

//lazyScrollState.

    class OwnFlingBehavior : FlingBehavior {
//
//        private var page: Int = page
//
//        constructor(page: Int)


        override suspend fun ScrollScope.performFling(initialVelocity: Float): Float {
            Log.i("FLING", "")
//            scrolling += 1
            zoomedImagesModel.refreshScrolling()
//            zoomedImagesModel.refreshScrollingBack()
//            velocity = initialVelocity
//            if (scrolling >= 10) scrolling -= 10
            return 20F
        }


    }

    val flingBehavior = OwnFlingBehavior()

    val lazyScrollState = rememberLazyListState(0)

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
                Log.i("POSTFLING", "")
                return super.onPostFling(consumed, available)
            }

            override suspend fun onPreFling(available: Velocity): Velocity {
                Log.i("PREFLING", "")
                return super.onPreFling(available)
            }

            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                Log.i("PRESCROLL", "")
                return super.onPreScroll(available, source)
            }

            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                Log.i("POSTSCROLL", "")
                // we have no fling, so we're interested in the regular post scroll cycle
                // let's try to consume what's left if we need and return the amount consumed
//                val vertical = available.y
//                val weConsumed = onNewDelta(vertical)


                scrollPosX = consumed.x.roundToInt()
                Log.i("connection", "consumed:$consumed,available:$available,scrollPos:$scrollPosX")
                scrolling += 1
//                lazyScrollState.animateScrollBy(consumed.x)
                return Offset(x = available.x - 642f, y = 0f)
//                return Offset.Zero
            }
        }
    }


    val nestedScrollDispatcher = remember { NestedScrollDispatcher() }


    LaunchedEffect(scrolling, zoomedImagesModel.scrollingHack) {
//        val test = scrollState.scrollBy(off)
//        val test = scrollState.scrollTo(200)
//        zoomedImagesModel.stopScrollFromUpdating()

//        Log.i(
//            "launched",
//            "${lazyScrollState.interactionSource},${zoomedImagesModel.isDraggingSmall}"
//        )
//        lazyScrollState.scrollBy(-zoomedImagesModel.listOffset)
//        lazyScrollState.stopScroll()
//        nestedScrollDispatcher.dispatchPostScroll(
//            consumed = Offset.Zero,
//            available = Offset(200f, 0f),
//            source = NestedScrollSource.Drag
//        )

        val isCurrentIndexZoomed = zoomedImagesModel.contains(lazyScrollState.firstVisibleItemIndex)
//        Log.i(
//            "arr",
//            "${zoomedImagesModel.getItems()}, zoomed?: $isCurrentIndexZoomed,velocity:$velocity"
//        )
////        if (!isCurrentIndexZoomed) {
////        lazyScrollState.
//        Log.i(
//            "inter",
//            "firstVisibleScrollOffset: ${lazyScrollState.firstVisibleItemScrollOffset},page: ${zoomedImagesModel.page},currentIndex: ${lazyScrollState.firstVisibleItemIndex},  lastOffset: ${lazyScrollState.layoutInfo.viewportEndOffset}"
//
//        )
//        if (lazyScrollState.firstVisibleItemScrollOffset > lazyScrollState.layoutInfo.viewportEndOffset / 2) {
//            page += 1
//            Log.i("MOVE", "")
//        }
//        lastScrollOffset = lazyScrollState.firstVisibleItemScrollOffset


        if (velocity < -800 || (lazyScrollState.firstVisibleItemIndex >= zoomedImagesModel.page && (lazyScrollState.firstVisibleItemScrollOffset > lazyScrollState.layoutInfo.viewportEndOffset / 2.5))) {

            Log.i("velocity", "OK")
            if (zoomedImagesModel.page < lazyScrollState.layoutInfo.totalItemsCount && zoomedImagesModel.page <= (zoomedImagesModel.scrollStartPage)) {
                Log.i(
                    "page",
                    "++"
                )
                zoomedImagesModel.page += 1
            }
        } else if (velocity > 800 || (lazyScrollState.firstVisibleItemIndex < zoomedImagesModel.page && (lazyScrollState.firstVisibleItemScrollOffset < lazyScrollState.layoutInfo.viewportEndOffset / 2.5))) {
            Log.i("velocity", "OK")
            if (zoomedImagesModel.page >= 1 && zoomedImagesModel.page >= zoomedImagesModel.scrollStartPage) {
                Log.i("page", "--")
                zoomedImagesModel.page -= 1
            }

        }
//        zoomedImagesModel.page = page
        lazyScrollState.animateScrollToItem(zoomedImagesModel.page)
        Log.i("AFTER SCROLL", "${lazyScrollState.firstVisibleItemScrollOffset}")
//        zoomedImagesModel.letScrollUpdate()
//        } else {
//            lazyScrollState.dispatchRawDelta(0f)
//            lazyScrollState.scrollToItem(page)
//            //..
//        }
//        lastScrollOffset = lazyScrollState.firstVisibleItemScrollOffset
//        listState.animateScrollToItem(listState.firstVisibleItemIndex)


//        lazyScrollState.scrollBy(200F)
    }

    LaunchedEffect(zoomedImagesModel.updateLaunchedEffectBack) {
//    if(zoomedImagesModel.emergencyMeeting) {
////        lazyScrollState.scrollT
//    }
        Log.i("updateLaunchedBack", "")
        if (lazyScrollState.firstVisibleItemScrollOffset > 0) {
//            zoomedImagesModel.overboardOffset
            lazyScrollState.scrollBy(-zoomedImagesModel.overboardOffset)
        } else {
            zoomedImagesModel.draggingOverboard = false
        }
//        if (lazyScrollState.firstVisibleItemScrollOffset != zoomedImagesModel.currentListOffsetX)
//            lazyScrollState.scrollBy(-zoomedImagesModel.listOffset)
    }

    LaunchedEffect(zoomedImagesModel.timeChanged) {
        //v=m/s
        Log.i("timeChanged", "")
        velocity = if (zoomedImagesModel.timeChanged <= 0) 0 else
            (((zoomedImagesModel.flingDistance) / (zoomedImagesModel.timeChanged)) * 1000000000).toInt()
        Log.i("VELOCITY", "$velocity, timeChanged:${zoomedImagesModel.timeChanged}")

//        if (velocity > 1) {
//            Log.i("page", "++")
//            if (page < lazyScrollState.layoutInfo.totalItemsCount) {
//                page += 1
//            }
//        } else if (velocity < -1) {
//            if (page >= 1) {
//                Log.i("page", "--")
//                page -= 1
//            }
//
//        }
    }

    LaunchedEffect(zoomedImagesModel.listOffset, zoomedImagesModel.emergencyMeeting) {
        Log.i("amogus", "")
//        if(zoome)
//        if (Math.abs(zoomedImagesModel.currentImageOffsetX) <= Math.abs(zoomedImagesModel.maxImageOffsetX)&&zoomedImagesModel.draggingOverboard) {
        zoomedImagesModel.currentListOffsetX = lazyScrollState.firstVisibleItemScrollOffset
        lazyScrollState.scrollBy(-zoomedImagesModel.listOffset)
        Log.i("AFTER SCROLL2", "${lazyScrollState.firstVisibleItemScrollOffset}")
        zoomedImagesModel.refreshScrollIfNeeded()
//        }


    }
    /*LaunchedEffect(scrolling) {
//        val test = scrollState.scrollBy(off)
//        val test = scrollState.scrollTo(200)
        Log.i(
            "launched",
            "${lazyScrollState.interactionSource},$isDraggingSmall,${zoomedImagesModel.listOffset}"
        )
//        lazyScrollState.scrollBy(-zoomedImagesModel.listOffset)
//        lazyScrollState.stopScroll()
//        nestedScrollDispatcher.dispatchPostScroll(
//            consumed = Offset.Zero,
//            available = Offset(200f, 0f),
//            source = NestedScrollSource.Drag
//        )

        val isCurrentIndexZoomed = zoomedImagesModel.contains(lazyScrollState.firstVisibleItemIndex)
        Log.i("arr", "${zoomedImagesModel.getItems()}, zoomed?: $isCurrentIndexZoomed")
//        if (!isCurrentIndexZoomed) {
//        lazyScrollState.
        Log.i(
            "inter",
            "firstVisibleScrollOffset: ${lazyScrollState.firstVisibleItemScrollOffset},page: $page,currentIndex: ${lazyScrollState.firstVisibleItemIndex},  lastOffset: ${lazyScrollState.layoutInfo.viewportEndOffset}"

        )
//        if (lazyScrollState.firstVisibleItemScrollOffset > lazyScrollState.layoutInfo.viewportEndOffset / 2) {
//            page += 1
//            Log.i("MOVE", "")
//        }
//        lastScrollOffset = lazyScrollState.firstVisibleItemScrollOffset


        if (velocity > 1000 || (lazyScrollState.firstVisibleItemIndex >= page && (lazyScrollState.firstVisibleItemScrollOffset > lazyScrollState.layoutInfo.viewportEndOffset / 2.5))) {
            Log.i("page", "++")
            if (page < lazyScrollState.layoutInfo.totalItemsCount) {
                page += 1
            }
        } else if (velocity < -1000 || (lazyScrollState.firstVisibleItemIndex < page && (lazyScrollState.firstVisibleItemScrollOffset > lazyScrollState.layoutInfo.viewportEndOffset / 2.5))) {
            if (page >= 1) {
                Log.i("page", "--")
                page -= 1
            }

        }
        lazyScrollState.animateScrollToItem(page)

//        } else {
//            lazyScrollState.dispatchRawDelta(0f)
//            lazyScrollState.scrollToItem(page)
//            //..
//        }
//        lastScrollOffset = lazyScrollState.firstVisibleItemScrollOffset
//        listState.animateScrollToItem(listState.firstVisibleItemIndex)


//        lazyScrollState.scrollBy(200F)
    }
*/

//    val scrollState = rememberScrollState()

    val context = LocalContext.current
    LazyRow(

        state = lazyScrollState,
        userScrollEnabled = !zoomedImagesModel.isDraggingSmall, //this is supposed to be available in 1.2.0-alpha01 and higher, but it works with my 1.0.1?
//        verticalAlignment = Alignment.CenterVertically,
        flingBehavior = flingBehavior,
        modifier = Modifier
//            .pointerInput(Unit) {
//                detectDragGestures { change, dragAmount ->
//                    Toast
//                        .makeText(context, "DRAG INSIDE LAZYROW", Toast.LENGTH_SHORT)
//                        .show()
//                    Log.i("DRAG INSIDE LAZYROW", "")
//                }
//            }
//            .offset { IntOffset(scrollPosX, 0) }
//            .horizontalScroll(rememberScrollState())
//
//            .scrollable(
//                orientation = Orientation.Horizontal, state = scrollState
//            )
            .fillMaxWidth()
//            .scrollable(
//                flingBehavior = flingBehavior,
//                state = rememberScrollState(),
//                orientation = Orientation.Horizontal,
//                enabled = true
//            )
//            .draggable(
//                orientation = Orientation.Horizontal,
//                state = rememberDraggableState { delta ->
//                    val parentsConsumed = nestedScrollDispatcher.dispatchPreScroll(
//                        available = Offset(x = delta, y = delta),
//                        source = NestedScrollSource.Drag
//                    )
//                    // adjust what's available to us since might have consumed smth
//                    val adjustedAvailable = delta - parentsConsumed.y
//                    // we consume
////                    val weConsumed = onNewDelta(adjustedAvailable)
//
//                    // dispatch as a post scroll what's left after pre-scroll and our consumption
//                    val totalConsumed = Offset(x = 0f, y = adjustedAvailable) + parentsConsumed
//                    val left = adjustedAvailable - adjustedAvailable
//                    nestedScrollDispatcher.dispatchPostScroll(
//                        consumed = totalConsumed,
//                        available = Offset(x = 0f, y = left),
//                        source = NestedScrollSource.Drag
//                    )
//                }
//
//            )
//            .nestedScroll(nestedScrollConnection, nestedScrollDispatcher)
//            .offset { IntOffset(offset.roundToInt(), 0) }
            /*     .pointerInput(Unit) {
                     //                detectDragGestures { change,_ ->
                     //                    //if change is big then it's fast
                     //                    //---
                     //                    // iter = historical.length
                     //
                     //                val velocity = change.positionChange()
                     //                    Log.i("DRAGGING", "iter: ${}, positionChange: ${change.positionChange()},change:$change")
                     //                }

                     detectDragGestures({
                         Log.i("DRAG", "START")
                         isDragging = true
                         startPosX = it.x
                         draggingTime = 1
                     },
                         {
                             Log.i("DRAG", "END")
                             isDragging = false
                             startPosX = 0f
                             draggingTime = 0
                         },
                         {
                             Log.i("DRAG", "CANCEL")
                             isDragging = false
                             startPosX = 0f
                             draggingTime = 0
                         },
                         { change: PointerInputChange, dragAmount: Offset ->
                             val distance = startPosX - change.position.x

                             val time = draggingTime
                             val velocity = distance / time
                             //                        offset += dragAmount.x
                             Log.i("DRAG", "velocity: $velocity, dragging: $isDragging, change: $change")
                             //                        scrollState = rememberScrollState()
                             //                        scrollState.value += dragAmount.x.roundToInt()
                             //                        scrollState.scroll {  }
                             Log.i("velocity", "$velocity")
                             if (velocity > 10) {
                                 Log.i("PAGE", "$page")
                                 page += 1
                             }
                             //if velocity > X then change page to +=1?
                             //else change only after drag distance is more than half the screen
                             draggingTime += 1
                         })
                 }*/
            .background(Color.Red)
    ) {
//        Log.i("console.log", offset.toString())
//        for (image in bitmaps) {
//            if (image == null) continue
//            MangaImage(bitmap = image)
//        }
        items(bitmaps) { bitmap ->

            if (bitmap != null) {

                MangaImage(
                    bitmap = bitmap,
                    nestedScrollConnection,
                    nestedScrollDispatcher,
                    zoomedItems1,
                    bitmaps.indexOf(bitmap),
                    zoomedImagesModel = zoomedImagesModel
                )
            }
        }
    }
}

@Composable
fun MangaImage(
    bitmap: Bitmap,
    nestedScrollConnection: NestedScrollConnection,
    nestedScrollDispatcher: NestedScrollDispatcher,
    zoomedItems: ArrayList<Int>,
    indexOf: Int,
    zoomedImagesModel: MangaViewZoomedImagesModel
) {
    val imageBitmap = bitmap.asImageBitmap()
//  Image(bitmap)
    val imageBitmapDpHeight = with(LocalDensity.current) {
        imageBitmap.height.toDp()
    }
    val imageBitmapDpWidth = with(LocalDensity.current) {
        imageBitmap.width.toDp()
    }
    //szer / wys * display width
    val h = (imageBitmapDpWidth / imageBitmapDpHeight)
    val h1 = h * LocalConfiguration.current.screenHeightDp

    val zoomedScale = 2F
    val baseScale = 1F
    val (scale, setScale) = remember { mutableStateOf(1f) }
    val animatedScale = animateFloatAsState(targetValue = scale)
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    var animatedOffset =
        animateIntOffsetAsState(targetValue = IntOffset(offsetX.roundToInt(), offsetY.roundToInt()))
    Box(
        modifier = Modifier
            .background(Color.White)
            .fillMaxHeight()
//            .height(h1.dp)
//            .nestedScroll(nestedScrollConnection, nestedScrollDispatcher)
            .scale(animatedScale.value)
//            .graphicsLayer(
//                scaleX = scale,
//                scaleY = scale,
//
//                )

            .offset { animatedOffset.value }
//            .draggable(
//                orientation = Orientation.Horizontal,
//                state = rememberDraggableState { delta ->
//                    Log.i("DELTA", "$delta")
//                }
//            )
            .pointerInput(scale, Unit) {
                detectTapGestures(onDoubleTap = {

                    //                    scale = if (scale == 1F) 2F else 1F
                    if (scale == baseScale) {
                        offsetX = -(it.x - size.width / 2) / zoomedScale
                        offsetY = -(it.y - size.height / 2) / zoomedScale
                        //TODO: somehow make it so that new image is from top to bottom
//                        zoomedItems.add(indexOf)
                        zoomedImagesModel.isDraggingSmall = true
                        zoomedImagesModel.addItem(indexOf)
                        setScale(zoomedScale)
                    } else {
                        //                        setTapOffset(Offset(0f, 0f))
                        //                        setOffset(Offset(0f, 0f))
                        offsetX = 0f
                        offsetY = 0f
                        zoomedImagesModel.isDraggingSmall = false
//                        zoomedItems.remove(indexOf)
                        zoomedImagesModel.removeItem(indexOf)
                        setScale(baseScale)
                    }


                })
            }

            .pointerInput(Unit, scale) {


                //                detectTransformGestures(false) { centroid: Offset, pan: Offset, zoom: Float, rotation: Float ->
                ////                    if (scale == zoomedScale) { //only if zoomed
                //                    //TODO: save "tapped-in" offset and add (right and top) or subtract (left and bottom) centroid / size value
                //                    setOffset(
                //                        Offset(
                //                            offset.x + centroid.x / size.width,
                //                            offset.y + centroid.y / size.height
                //                        )
                //                    )
                //                    Log.i(
                //                        "offset",
                //                        "tapOffset: $tapOffset, centroid: $centroid, size: $size, offset: $offset"
                //                    )
                ////                        Log.i(
                ////                            "transform",
                ////                            "centroid: $centroid, offset $offset, pan:$pan, zoom:$zoom, rotation:$rotation"
                ////                        )
                //                }
                //                }

//  TODO: NESTED SCROLL!!!!

                detectDragGestures(
                    onDragStart = {
                        Log.i("START", "")
                        zoomedImagesModel.isScrolling = true
                        zoomedImagesModel.clearOffset()
                        zoomedImagesModel.stopScrollFromUpdating()
                        zoomedImagesModel.time = 0
                        zoomedImagesModel.timeChanged = 0
                        zoomedImagesModel.flingDistance = 0f
                        zoomedImagesModel.scrollStartPage = zoomedImagesModel.page
                    },

                    onDragCancel = { Log.i("CANCEL", "") },
                    onDragEnd = {
                        Log.i("END", "")
                        zoomedImagesModel.refreshScrolling()
                        zoomedImagesModel.letScrollUpdate()
                        zoomedImagesModel.isScrolling = false
                        zoomedImagesModel.timeChanged = zoomedImagesModel.time
                    },
                    onDrag = { change, dragAmount ->
//                        val tempOffX = offsetX + dragAmount.x
                        zoomedImagesModel.time += change.uptimeMillis
                        zoomedImagesModel.currentImageOffsetX = offsetX + (dragAmount.x)
                        zoomedImagesModel.flingDistance += dragAmount.x
                        val tempOffY = offsetY + dragAmount.y

//                        val maxOffX = (-(size.width / 2) / scale)
                        zoomedImagesModel.maxImageOffsetX = (-(size.width / 2) / scale)
//                        zoomedImagesModel.currentListOffsetX
                        //jeśli jestem poza to zapisuje flage
                        //jeśli jestem tu w ondrag i ta flaga jest prawdziwa to inkrementuje zmienną albo zapisuje aktualne dragAmount albo to i to
                        //w launchedeffect inkrementowanym cofam się o dragAmount aż będę offset 0
                        val maxOffY = (-(size.height / 2) / scale)
                        Log.i(
                            "IMAGE DRAG",
                            "${change.uptimeMillis}"
                        )

//TODO: somehow make it so shouldChangePagesZoomed controls if scrolls
                        if (scale > baseScale) {

//                        if (scale > baseScale && Math.abs(zoomedImagesModel.currentImageOffsetX) <= Math.abs(
//                                zoomedImagesModel.maxImageOffsetX
//                            )
//                        ) {
//                        if (scale > baseScale && (zoomedImagesModel.shouldChangePagesZoomed && Math.abs(
//                                zoomedImagesModel.currentImageOffsetX
//                            ) <= Math.abs(
//                                zoomedImagesModel.maxImageOffsetX
//                            ))
//                        ) {
//pre scroll
                            if (zoomedImagesModel.draggingOverboard) {
                                zoomedImagesModel.overboardOffset = dragAmount.x
                                zoomedImagesModel.refreshScrollingBack()

                            }

                            zoomedImagesModel.setOffset(0f)
                            Log.i("scale", "in scale")
//                        val parentConsumed = nestedScrollDispatcher.dispatchPreScroll(
//                            available = Offset(
//                                x = 0f,
//                                y = 0f
//                            ), source = NestedScrollSource.Drag
//                        )
//                        change.consumeAllChanges()
//                            val tempOffX = offsetX + dragAmount.x
//                            val tempOffY = offsetY + dragAmount.y

                            //

                            offsetX =
                                if (Math.abs(zoomedImagesModel.currentImageOffsetX) > Math.abs(
                                        zoomedImagesModel.maxImageOffsetX
                                    )
                                ) {
                                    if (zoomedImagesModel.currentImageOffsetX > 0) -zoomedImagesModel.maxImageOffsetX else zoomedImagesModel.maxImageOffsetX
                                } else {
                                    zoomedImagesModel.currentImageOffsetX
                                }
                            offsetY = if (Math.abs(tempOffY) > Math.abs(maxOffY)) {
                                if (tempOffY > 0) -maxOffY else maxOffY
                            } else {
                                tempOffY
                            }


//                        val offsetLeft = size.width - parentConsumed.x
//                        Log.i("scroll", "size:${size.width}")
//                        nestedScrollDispatcher.dispatchPostScroll(
//                            consumed = Offset(x = offsetLeft, y = 0f),
//                            available = Offset.Zero,
//                            source = NestedScrollSource.Drag
//                        )
// post scroll
                        } else {
                            Log.i("SCROLLING BIG LEFT IDK", "${dragAmount.x}")
                            zoomedImagesModel.setOffset(dragAmount.x)
                            zoomedImagesModel.draggingOverboard = true


//                        nestedScrollDispatcher.dispatchPostScroll(
//                            consumed = dragAmount,
//                            available = Offset(
//                                x = size.width.toFloat(),
//                                y = 0f
//                            ), //TODO: maybe change?
//                            source = NestedScrollSource.Drag
//                        )
                        }
                        //
                    })


            }

//            .fillMaxSize()
    ) {
//        Log.i("console.log", LocalConfiguration.current.screenLayout.)
        val imageBitmapDpHeight = with(LocalDensity.current) {
            imageBitmap.height.toDp()
        }
        val imageBitmapDpWidth = with(LocalDensity.current) {
            imageBitmap.width.toDp()
        }
        //szer / wys * display width

        val h = (imageBitmapDpWidth / imageBitmapDpHeight)
        val h1 = h * LocalConfiguration.current.screenHeightDp //maybe statusbar?
        Log.i(
            "SIZE",
            "bitmap: ${imageBitmapDpWidth}x${imageBitmapDpHeight}, screen: ${LocalConfiguration.current.screenWidthDp}x${LocalConfiguration.current.screenHeightDp}, h1: ${h1.dp}"
        )
        Image(
            bitmap = imageBitmap,
            contentDescription = "",
            Modifier
//                .fillMaxWidth(2f)
                .requiredHeight(h1.dp)
                .width(LocalConfiguration.current.screenWidthDp.dp)
////                .height(LocalConfiguration.current.screenHeightDp.dp)
//                .height(h1.dp)
//                .height(theDp)

//                .fillMaxSize()
                .background(Color.Magenta),
            contentScale = ContentScale.Fit


        )
    }

}


//
//@Preview(showBackground = true)
//@Composable
//fun DefaultPreview2() {
//    WeebToolsComposeTheme {
////       MangaImages
//    }
//}