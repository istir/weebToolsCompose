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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import com.istir.weebtoolscompose.ui.theme.WeebToolsComposeTheme
import dev.chrisbanes.snapper.ExperimentalSnapperApi
import kotlin.math.roundToInt

class MangaViewActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val mangaUri: Uri = Uri.parse(intent.getStringExtra("mangaUri"))
        val model: MangaViewModel by viewModels()

        Log.i("mangaUri", mangaUri.toString())
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

private val VerticalScrollConsumer = object : NestedScrollConnection {
    override fun onPreScroll(available: Offset, source: NestedScrollSource) = available.copy(x = 0f)
    override suspend fun onPreFling(available: Velocity) = available.copy(x = 0f)
}

private val HorizontalScrollConsumer = object : NestedScrollConnection {
    override fun onPreScroll(available: Offset, source: NestedScrollSource) = available.copy(y = 0f)
    override suspend fun onPreFling(available: Velocity) = available.copy(y = 0f)
}

fun Modifier.disabledVerticalPointerInputScroll(disabled: Boolean = true) =
    if (disabled) this.nestedScroll(VerticalScrollConsumer) else this

fun Modifier.disabledHorizontalPointerInputScroll(disabled: Boolean = true) =
    if (disabled) this.nestedScroll(HorizontalScrollConsumer) else this

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

    class OwnFlingBehavior : FlingBehavior {
        override suspend fun ScrollScope.performFling(initialVelocity: Float): Float {
            Log.i("FLING", initialVelocity.toString())
//            if (Math.abs(initialVelocity) > 1000)
//                return 1F
//            return 0F
//            if (initialVelocity > 1000) {
//                val nextIndex =
//                    if (listState.layoutInfo.totalItemsCount > listState.firstVisibleItemIndex + 1) listState.firstVisibleItemIndex + 1 else listState.layoutInfo.totalItemsCount
//                listState.animateScrollToItem(nextIndex)
//            }
//            if (initialVelocity < 1000) {
//                return 20F
//            }
            return 20F
        }


    }

    val flingBehavior = OwnFlingBehavior()

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
    var offset by remember { mutableStateOf(0f) }
    val scrollState =
        rememberScrollableState(consumeScrollDelta = { delta ->
            offset += delta
            delta
        })
    val lazyScrollState = rememberLazyListState()
    LaunchedEffect(key1 = offset, key2 = lazyScrollState.isScrollInProgress, block = {
//        val test = scrollState.scrollBy(off)
//        val test = scrollState.scrollTo(200)
//        Log.i("launched", "effect, $test")
//        lazyScrollState.stopScroll()

        lazyScrollState.scrollBy(200F)
    })


    LazyRow(

//        state = lazyScrollState,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .disabledVerticalPointerInputScroll()
            .disabledVerticalPointerInputScroll()
//            .horizontalScroll(rememberScrollState())
//
//            .scrollable(
//                orientation = Orientation.Horizontal, state = scrollState
//            )
            .fillMaxWidth()
//            .offset { IntOffset(offset.roundToInt(), 0) }
            .pointerInput(Unit) {
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
                        //if velocity > X then change page to +=1?
                        //else change only after drag distance is more than half the screen
                        draggingTime += 1
                    })
            }
            .background(Color.Red)
    ) {
//        Log.i("console.log", offset.toString())
//        for (image in bitmaps) {
//            if (image == null) continue
//            MangaImage(bitmap = image)
//        }
        items(bitmaps) { bitmap ->

            if (bitmap != null) {
                MangaImage(bitmap = bitmap)
            }
        }
    }
}

@Composable
fun MangaImage(bitmap: Bitmap) {
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
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale,

                )
        /**
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


        detectDragGestures { change, dragAmount ->
        Log.i("IMAGE DRAG", "")
        if (scale == zoomedScale) {

        change.consumeAllChanges()
        val tempOffX = offsetX + dragAmount.x
        val tempOffY = offsetY + dragAmount.y
        val maxOffX = (-(size.width / 2) / scale)
        val maxOffY = (-(size.height / 2) / scale)
        //

        offsetX =
        if (Math.abs(tempOffX) > Math.abs(maxOffX)) {
        if (tempOffX > 0) -maxOffX else maxOffX
        } else {
        tempOffX
        }
        offsetY = if (Math.abs(tempOffY) > Math.abs(maxOffY)) {
        if (tempOffY > 0) -maxOffY else maxOffY
        } else {
        tempOffY
        }

        }
        //
        }
        //                }


        }
         */
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