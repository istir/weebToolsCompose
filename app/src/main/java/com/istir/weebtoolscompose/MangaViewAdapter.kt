package com.istir.weebtoolscompose

import android.graphics.Bitmap
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView


class MangaViewAdapter(private val onClickListener: View.OnClickListener) :
    RecyclerView.Adapter<MangaViewAdapter.MangaViewHolder>() {

    inner class MangaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        //???
    }

    var bitmaps: List<Bitmap> = arrayListOf()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MangaViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return MangaViewHolder(
            inflater.inflate(R.layout.manga_page, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MangaViewHolder, position: Int) {
//        TODO("Not yet implemented")
        holder.itemView.run {
            val imageView = findViewById<SubsamplingScaleImageView>(R.id.imageViewManga)
            Log.i("BIND", "size: ${bitmaps.size}, position :${position}")
//            imageView.setOnClickListener {
//                Toast.makeText(context, "CLICK", Toast.LENGTH_SHORT).show()
//            }
            imageView.setOnClickListener(onClickListener)
            if (bitmaps.size >= position)
                imageView.setImage(ImageSource.bitmap(bitmaps[position]))
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