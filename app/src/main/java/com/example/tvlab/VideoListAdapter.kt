package com.example.tvlab

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class VideoListAdapter (private val context: Context, private val list: ArrayList<MainActivity.VideoList>)
    : RecyclerView.Adapter<VideoListAdapter.ViewHolder>() {
    private lateinit var listener: Listener

    interface Listener {
        fun onItemClick(embedded: String)
    }

    fun setListener(l: Listener) { listener = l }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val imgPicture: ImageView = v.findViewById(R.id.imgPicture)
        val tvVideoName: TextView = v.findViewById(R.id.tvVideoName)
        val tvInfo: TextView = v.findViewById(R.id.tvInfo)
        val clItem: ConstraintLayout = v.findViewById(R.id.clItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_video, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]

        holder.clItem.setOnClickListener { listener.onItemClick(item.videoID) }
        holder.tvVideoName.text = item.videoName
        holder.tvInfo.text = item.author
        setPicture(holder.imgPicture, item.picture)
    }

    override fun getItemCount(): Int = list.size

    private fun setPicture(view: ImageView, url: String) = Glide.with(context).load(url).into(view)
}