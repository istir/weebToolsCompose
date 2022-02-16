package com.istir.weebtoolscompose

import android.content.ContentResolver
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.core.*
import androidx.compose.animation.splineBasedDecay
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollDispatcher
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.istir.weebtoolscompose.ui.theme.WeebToolsComposeTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class MangaViewActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val mangaUri: Uri = Uri.parse(intent.getStringExtra("mangaUri"))
        val model: MangaViewModel by viewModels()

//        Log.i("mangaUri", mangaUri.toString())
        setContent {
            WeebToolsComposeTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
//                    Greeting2("Android")
                    MangaImages(
                        mangaViewModel = model,
                        choosenManga = mangaUri,
                        contentResolver = contentResolver
                    )
                }
            }
        }
    }
}


@Composable
fun MangaImages(
    mangaViewModel: MangaViewModel,
    choosenManga: Uri,
    contentResolver: ContentResolver
) {

//    if (choosenManga == null) return

    mangaViewModel.init(contentResolver = contentResolver, mangaUri = choosenManga)

    MangaImages(bitmaps = mangaViewModel.items)

}

fun LazyListState.disableScrolling(scope: CoroutineScope) {
    scope.launch {
        scroll(scrollPriority = androidx.compose.foundation.MutatePriority.PreventUserInput) {
            // Await indefinitely, blocking scrolls
            awaitCancellation()
        }
    }
}

fun LazyListState.reenableScrolling(scope: CoroutineScope) {
    scope.launch {
        scroll(scrollPriority = androidx.compose.foundation.MutatePriority.PreventUserInput) {
            // Do nothing, just cancel the previous indefinite "scroll"
        }
    }
}

//@SuppressLint("CoroutineCreationDuringComposition")
//@OptIn(ExperimentalMaterialApi::class)
//@OptIn(ExperimentalSnapperApi::class)
@Composable
fun MangaImages(bitmaps: List<Bitmap?>?) {
//    var offset by remember { mutableStateOf(0f) }
    if (bitmaps == null) return
    val listState = rememberLazyListState()
//    val listState = LazyListState()

    LaunchedEffect(!listState.isScrollInProgress) {
//        Log.i("...", "...")
//        if (!listState.isScrollInProgress) {
//        Log.i("firstVisibleOffset", listState.firstVisibleItemScrollOffset.toString())
//        Log.i("lastOffset", listState.layoutInfo.viewportEndOffset.toString())
//        Log.i("currentIndex", listState.firstVisibleItemIndex.toString())
//        Log.i("src", listState.interactionSource.toString())
        Log.i("", "${listState.firstVisibleItemScrollOffset}")
        val offset = listState.firstVisibleItemScrollOffset
        val maxOffset = listState.layoutInfo.viewportEndOffset
        val offsetToScrollToNext = maxOffset / 2
        if (offset > offsetToScrollToNext) {
//Log.i("offset>")
//            Log.i("", listState.layoutInfo.totalItemsCount.toString())
            val nextIndex =
                if (listState.layoutInfo.totalItemsCount > listState.firstVisibleItemIndex + 1) listState.firstVisibleItemIndex + 1 else listState.layoutInfo.totalItemsCount
//            val nextIndex = listState.layoutInfo.totalItemsCount>listState.firstVisibleItemIndex+1?listState.firstVisibleItemIndex+1:
            listState.animateScrollToItem(nextIndex) //check if exists!!!!
        } else {
            listState.animateScrollToItem(listState.firstVisibleItemIndex)
        }
        listState.firstVisibleItemIndex
//            Log.i("visible items info", listState.layoutInfo.visibleItemsInfo.toString())

//        }
    }
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
    val listState1 = rememberLazyListState()
//    val animationSpec:AnimationSpec<Float> =
//val animationSpec:AnimationSpec<Float> = tween(durationMillis = 300, easing = FastOutSlowInEasing)
//    val animationSpec: AnimationSpec<Float> =
//        tween(durationMillis = 300, easing = FastOutSlowInEasing)
    val animationSpec: AnimationSpec<Float> =
//        keyframes {
//            durationMillis = 400
//            0.0f at 0 with LinearOutSlowInEasing
//            0.2f at 15 with FastOutSlowInEasing
//            0.4f at 75 with FastOutLinearInEasing
//            0.4f at 225
//        }
        tween(
            durationMillis = 300,

            easing = CubicBezierEasing(0.7F, 0.005F, 0.34F, 1.005F)
        )
//        spring(dampingRatio = Spring.DampingRatioHighBouncy, stiffness = Spring.StiffnessMedium, )

    val decayAnimation: DecayAnimationSpec<Float> = splineBasedDecay(Density(3F))
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
    var velocity by remember { mutableStateOf(0f) }
    var scrolling by remember { mutableStateOf(1) }
    var page by remember { mutableStateOf(0) }
//    val scrollState =
//        rememberScrollableState(consumeScrollDelta = { delta ->
//            offset += delta
//            delta
//        })
    val scrollState = rememberScrollState()
//    val scope = rememberCoroutineScope()
    val lazyScrollState = rememberLazyListState(0)
//    lazyScrollState.per
//    lazyScrollState.disableScrolling(scope = scope)
    var test = lazyScrollState.layoutInfo.viewportEndOffset
    LaunchedEffect(scrolling) {
//        val test = scrollState.scrollBy(off)
//        val test = scrollState.scrollTo(200)
//        Log.i("launched", "effect, $test")
//        lazyScrollState.stopScroll()

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
//        lastScrollOffset = lazyScrollState.firstVisibleItemScrollOffset
//        listState.animateScrollToItem(listState.firstVisibleItemIndex)


//        lazyScrollState.scrollBy(200F)
    }
//lazyScrollState.

    class OwnFlingBehavior : FlingBehavior {

        private var page: Int = page

        constructor(page: Int)


        override suspend fun ScrollScope.performFling(initialVelocity: Float): Float {

            scrolling += 1
            velocity = initialVelocity
            if (scrolling >= 10) scrolling -= 10
            return 20F
        }


    }

    val flingBehavior = OwnFlingBehavior(page)


    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                // we have no fling, so we're interested in the regular post scroll cycle
                // let's try to consume what's left if we need and return the amount consumed
//                val vertical = available.y
//                val weConsumed = onNewDelta(vertical)
                Log.i("connection", "consumed:$consumed,available:$available")
                return Offset(x = available.x, y = 0f)

            }
        }
    }


    val nestedScrollDispatcher = remember { NestedScrollDispatcher() }


    LazyRow(

        state = lazyScrollState,
//        verticalAlignment = Alignment.CenterVertically,
        flingBehavior = flingBehavior,
        modifier = Modifier

//            .horizontalScroll(rememberScrollState())
//
//            .scrollable(
//                orientation = Orientation.Horizontal, state = scrollState
//            )
            .fillMaxWidth()
//            .nestedScroll(nestedScrollConnection)
//            .offset { IntOffset(offset.roundToInt(), 0) }
            /**.pointerInput(Unit) {
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
                MangaImage(bitmap = bitmap, nestedScrollConnection, nestedScrollDispatcher)
            }
        }
    }
}

@Composable
fun MangaImage(
    bitmap: Bitmap,
    nestedScrollConnection: NestedScrollConnection,
    nestedScrollDispatcher: NestedScrollDispatcher
) {
    val imageBitmap = bitmap.asImageBitmap()
//  Image(bitmap)
    val zoomedScale = 2F
    val baseScale = 1F
    val (scale, setScale) = remember { mutableStateOf(1f) }

    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }

    Box(
        modifier = Modifier
            .background(Color.White)
//            .nestedScroll(nestedScrollConnection, nestedScrollDispatcher)
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale,

                )

            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
            .pointerInput(scale, Unit) {
                detectTapGestures(onDoubleTap = {

                    //                    scale = if (scale == 1F) 2F else 1F
                    if (scale == baseScale) {
                        offsetX = -(it.x - size.width / 2) / zoomedScale
                        offsetY = -(it.y - size.height / 2) / zoomedScale
                        //TODO: somehow make it so that new image is from top to bottom
                        setScale(zoomedScale)
                    } else {
                        //                        setTapOffset(Offset(0f, 0f))
                        //                        setOffset(Offset(0f, 0f))
                        offsetX = 0f
                        offsetY = 0f
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

//                detectDragGestures { change, dragAmount ->
//                    Log.i("IMAGE DRAG", "")
//
//                    if (scale == zoomedScale) {
////pre scroll
//                        change.consumeAllChanges()
//                        val tempOffX = offsetX + dragAmount.x
//                        val tempOffY = offsetY + dragAmount.y
//                        val maxOffX = (-(size.width / 2) / scale)
//                        val maxOffY = (-(size.height / 2) / scale)
//                        //
//
//                        offsetX =
//                            if (Math.abs(tempOffX) > Math.abs(maxOffX)) {
//                                if (tempOffX > 0) -maxOffX else maxOffX
//                            } else {
//                                tempOffX
//                            }
//                        offsetY = if (Math.abs(tempOffY) > Math.abs(maxOffY)) {
//                            if (tempOffY > 0) -maxOffY else maxOffY
//                        } else {
//                            tempOffY
//                        }
//// post scroll
//                    } else {
//                        nestedScrollDispatcher.dispatchPostScroll(
//                            consumed = Offset(x = 0f, y = 0f),
//                            Offset(x = size.width.toFloat(), y = 0f), //TODO: maybe change?
//                            source = NestedScrollSource.Drag
//                        )
//                    }
//                    //
//                }


            }

//            .fillMaxSize()
    ) {
//        Log.i("console.log", LocalConfiguration.current.screenLayout.)

        Image(
            bitmap = imageBitmap,
            contentDescription = "",
            Modifier
                .width(LocalConfiguration.current.screenWidthDp.dp)
                .height(LocalConfiguration.current.screenHeightDp.dp)

//                .fillMaxSize(),

            ,
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