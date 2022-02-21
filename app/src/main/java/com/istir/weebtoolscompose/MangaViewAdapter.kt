package com.istir.weebtoolscompose

import android.graphics.Bitmap
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView


class MangaViewAdapter(
    private val onClickListener: View.OnClickListener,
//    private val mangaViewModel: MangaViewModel,
//    private val lifecycleOwner: LifecycleOwner
) :
    RecyclerView.Adapter<MangaViewAdapter.MangaViewHolder>() {

    inner class MangaViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        //???
    }

    var bitmaps: List<Bitmap> = arrayListOf()
//    var test: List<Int> = arrayListOf()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MangaViewHolder {
        Log.i("view created", "")
//        val lifecycleOwner = parent.context as LifecycleOwner
//        mangaViewModel.itemsLiveData.observe(lifecycleOwner, androidx.lifecycle.Observer {
////            Log.i("mangaImages", "${it.get(0)}")
////            bitmaps
//            for (bit in it) {
//                Log.i("mangaImages", "${bit}")
//            }
////            mangaImages = it
////            val state = recyclerView.layoutManager?.onSaveInstanceState()
////            mangaAdapter.bitmaps = it
////            recyclerView.layoutManager?.onRestoreInstanceState(state)
////            val onScrollChangeListener =
////            recyclerView.getpo
//
//
////            binding.maxPageCount.text = "${mangaAdapter.bitmaps.size}"
//
////            mangaAdapter.bitmaps.addAll(it)
////
////            imageView.setImage(ImageSource.bitmap(it.get(0)))
//        })


        val inflater = LayoutInflater.from(parent.context)
        return MangaViewHolder(
            inflater.inflate(R.layout.manga_page, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MangaViewHolder, position: Int) {
//        TODO("Not yet implemented")
        Log.i("onbind created", "")
        holder.setIsRecyclable(false) //TODO: this is temporary - app crashes without that
        holder.itemView.run {
            val imageView = findViewById<SubsamplingScaleImageView>(R.id.imageViewManga)
//            Log.i("BIND", "size: ${bitmaps.size}, position :${position}")
//            imageView.setOnClickListener {
//                Toast.makeText(context, "CLICK", Toast.LENGTH_SHORT).show()
//            }

            imageView.setOnClickListener(onClickListener)
//            liveData
//            val bmaps: List<Bitmap>? = mangaViewModel.itemsLiveData.value
//            bitmaps = mangaViewModel.itemsLiveData.value!!
//            if (bitmaps != null && bitmaps.size >= position) {
//                imageView.setImage(ImageSource.bitmap(bitmaps[position]))
//            }
            if (bitmaps.size >= position)
                if (bitmaps[position].isRecycled) {
                    Log.i("RECYCLED", "recycled bitmap at ${position}")
                } else {
                    imageView.setImage(ImageSource.bitmap(bitmaps[position]))
                }
        }
    }

    //get the size of color array
    override fun getItemCount(): Int = bitmaps.size

    //binding the screen with view
//    override fun onBindViewHolder(holder: PagerVH, position: Int) = holder.itemView.run {
//        val imageView = findViewById<SubsamplingScaleImageView>(R.id.imageViewManga)
//        imageView.setImage(ImageSource.bitmap(bitmaps[position]))
//
//    }

//    class MangaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
////        val imageView = itemView.findViewById<View>(R.id.imageVi)
//
//    }


}

//class PagerVH(itemView: View) : RecyclerView.ViewHolder(itemView)