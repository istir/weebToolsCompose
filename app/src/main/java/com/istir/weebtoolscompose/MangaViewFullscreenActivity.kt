package com.istir.weebtoolscompose

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.PopupMenu
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.istir.weebtoolscompose.databinding.ActivityMangaViewFullscreenBinding


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class MangaViewFullscreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMangaViewFullscreenBinding
    private lateinit var fullscreenContent: LinearLayout
    private lateinit var imageView: SubsamplingScaleImageView
    private val mangaViewModel: MangaViewModel by viewModels()
    private val hideHandler = Handler()
    private var showMenu = true

    @SuppressLint("InlinedApi")


    private val hidePart2Runnable = Runnable {
        // Delayed removal of status and navigation bar

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
            window.insetsController?.let {
                it.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    // Hide the nav bar and status bar
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN)
        }
    }
    private val showPart2Runnable = Runnable {
        // Delayed display of UI elements
        if (Build.VERSION.SDK_INT >= 30) {
//            Log.i("API", ">")
//    fullscreenContent.windowInsetsController?.show(WindowInsets.Type.)
//            window.insetsController.
            fullscreenContent.windowInsetsController?.show(WindowInsets.Type.systemBars())
//            fullscreenContent.windowInsetsController?.show(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
        } else {
//            fullscreenContent.systemUiVisibility = View.System_UI_FLAG_


            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        }
//        supportActionBar?.show()
//        fullscreenContentControls.visibility = View.VISIBLE

    }
    private var isFullscreen: Boolean = false

    private val hideRunnable = Runnable { hide() }

    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private val delayHideTouchListener = View.OnTouchListener { view, motionEvent ->
        when (motionEvent.action) {
            MotionEvent.ACTION_DOWN -> if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS)
            }
            MotionEvent.ACTION_UP -> view.performClick()
            else -> {
            }
        }
        false
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMangaViewFullscreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS) //TODO???
//        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT
        val attrib = window.attributes
        val h = resources.getDimensionPixelOffset(
            resources.getIdentifier(
                "status_bar_height",
                "dimen",
                "android"
            )
        )
        binding.floatingBar.setOnClickListener {
            // just here to stop hiding the UI
        }
        binding.dotButton.setOnClickListener {
//            showMenu = !showMenu
//            val context = ContextThemeWrapper(applicationContext, R.style.PopupMenu) as Context


            val popup = PopupMenu(this, binding.dotButton)
            popup.gravity = Gravity.END
            popup.menuInflater.inflate(R.menu.manga_viewer_menu, popup.menu)



            popup.show()


            popup.setOnMenuItemClickListener { menuItem: MenuItem ->
                // Respond to menu item click.
                true
            }
            popup.setOnDismissListener {
                // Respond to popup being dismissed.
            }

//            invalidateOptionsMenu()
//            menuInflater.inflate(R.menu.manga_viewer_menu)


//            Toast.makeText(
//                applicationContext,
//                "Dots",
//                Toast.LENGTH_SHORT
//            ).show()
        }
        if (h > 0) {

            binding.floatingBar.setPadding(0, h, 0, 0)
        }
//        Log.i("height", "h:${h}")
        attrib.layoutInDisplayCutoutMode =
            WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        WindowCompat.setDecorFitsSystemWindows(window, false)
//

//        window.setDecorFitsSystemWindows(false)
//        window.insetsController?.let {
//            it.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
//            it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
//        }
//        actionBar?.hide()
        supportActionBar?.hide()
        isFullscreen = true

        // Set up the user interaction to manually show or hide the system UI.
        fullscreenContent = binding.linearLayout
        fullscreenContent.setOnClickListener { toggle() }

        val mangaUri: Uri = Uri.parse(intent.getStringExtra("mangaUri"))
        val mangaName: String? = intent.getStringExtra("mangaName")
        if (mangaName != null) {
            binding.mangaNameText.setOnClickListener {
                binding.mangaNameText.maxLines = if (binding.mangaNameText.maxLines == 10) 1 else 10
            }
            binding.mangaNameText.text = mangaName
        }
        loadMangaImages(mangaUri = mangaUri)


//        var mangaImages by Delegates.observable(
//            initialValue = mangaViewModel.items,
//            onChange = { property: KProperty<*>, oldValue: SnapshotStateList<Bitmap>, newValue: SnapshotStateList<Bitmap> ->
//                Log.i("mangaImages", "${newValue}")
//            })
//        mangaViewModel.items.on


//    val imageBitmap = .asImageBitmap()

//        fullscreenContentControls = binding.fullscreenContentControls

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
//        binding.dummyButton.setOnTouchListener(delayHideTouchListener)
    }

//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.manga_viewer_menu, menu)
//        val item: MenuItem? = menu?.findItem(R.id.mangaViewerMenuSettings)
//        item?.setVisible(showMenu)
//        return true
//    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100)
    }

    private fun loadMangaImages(mangaUri: Uri) {
//        mangaViewModel.init(contentResolver = contentResolver, mangaUri = mangaUri)
        mangaViewModel.initLiveData(contentResolver = contentResolver, mangaUri = mangaUri)
        var mangaImages = listOf<Bitmap>()
        val onClickListener = View.OnClickListener { toggle() }
        val mangaAdapter = MangaViewAdapter(onClickListener)

        binding.viewPager.adapter = mangaAdapter
//        mangaAdapter.test = listOf(1, 2, 3)
        val recyclerView = binding.viewPager.getChildAt(0) as RecyclerView
        var prevItem = 0
        val dbHelper = DBHelper(applicationContext, null)
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                val positionView =
                    (recyclerView.layoutManager as LinearLayoutManager?)!!.findFirstVisibleItemPosition()
                if (positionView != prevItem && positionView >= 0) {
//                    Log.i("Scroll", "${positionView}")
                    binding.currentPageCount.text = "${positionView + 1}"

                    dbHelper.editMangaProgress(mangaViewModel.mangaUri, positionView + 1)
                    prevItem = positionView
                }
//                    recyclerView.
                super.onScrollStateChanged(recyclerView, newState)
            }
        })

        mangaViewModel.itemsLiveData.observe(this, androidx.lifecycle.Observer {
//            Log.i("mangaImages", "${it.get(0)}")
//            mangaImages = it
            val state = recyclerView.layoutManager?.onSaveInstanceState()
            mangaAdapter.bitmaps = it
            recyclerView.layoutManager?.onRestoreInstanceState(state)
//            val onScrollChangeListener =
//            recyclerView.getpo


//            binding.maxPageCount.text = "${mangaAdapter.bitmaps.size}"

//            mangaAdapter.bitmaps.addAll(it)
//
//            imageView.setImage(ImageSource.bitmap(it.get(0)))
        })
    }

    private fun toggle() {
//        Log.i("TOGGLE", "isFullscreen: ${isFullscreen}")
        binding.mangaNameText.maxLines = 1

        if (isFullscreen) {
            hide()
        } else {
            show()
        }
    }

    private fun hide() {
        // Hide UI first
//        supportActionBar?.hide()
//        fullscreenContentControls.visibility = View.GONE
        isFullscreen = false
        val slideUp = AnimationUtils.loadAnimation(applicationContext, R.anim.slide_up)

        binding.floatingBar.visibility = View.INVISIBLE
        binding.floatingBar.startAnimation(slideUp)
        // Schedule a runnable to remove the status and navigation bar after a delay
        hideHandler.removeCallbacks(showPart2Runnable)
        hideHandler.post(hidePart2Runnable)
    }

    private fun show() {
        // Show the system bar

        val slideDown = AnimationUtils.loadAnimation(applicationContext, R.anim.slide_down)

        binding.floatingBar.visibility = View.VISIBLE
        binding.floatingBar.startAnimation(slideDown)
//post with delay and another one without delay (delay is status bar, without is animation)
        isFullscreen = true

        // Schedule a runnable to display UI elements after a delay
        hideHandler.removeCallbacks(hidePart2Runnable)
        hideHandler.postDelayed(showPart2Runnable, 115.toLong())
    }

    /**
     * Schedules a call to hide() in [delayMillis], canceling any
     * previously scheduled calls.
     */
    private fun delayedHide(delayMillis: Int) {
        hideHandler.removeCallbacks(hideRunnable)
        hideHandler.postDelayed(hideRunnable, delayMillis.toLong())
    }

    companion object {
        /**
         * Whether or not the system UI should be auto-hidden after
         * [AUTO_HIDE_DELAY_MILLIS] milliseconds.
         */
        private const val AUTO_HIDE = false

        /**
         * If [AUTO_HIDE] is set, the number of milliseconds to wait after
         * user interaction before hiding the system UI.
         */
        private const val AUTO_HIDE_DELAY_MILLIS = 3000

        /**
         * Some older devices needs a small delay between UI widget updates
         * and a change of the status and navigation bar.
         */
        private const val UI_ANIMATION_DELAY = 0
    }
}