package co.ghostnotes.trending.main

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import co.ghostnotes.trending.data.VideoData
import co.ghostnotes.trending.databinding.ListItemVideoBinding
import com.squareup.picasso.Picasso

internal class VideoDataAdapter(private val presenter: MainPresenter): RecyclerView.Adapter<VideoDataAdapter.ViewHolder>() {

    private var videoDataList: MutableList<VideoData>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListItemVideoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.presenter = presenter

        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = videoDataList?.size ?: 0

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val video = videoDataList!![position]
        holder.layout(video)
    }

    internal fun setVideoDataList(videoDataList: MutableList<VideoData>) {
        this.videoDataList = videoDataList

        notifyDataSetChanged()
    }

    internal fun addVideoDataList(videoDataList: MutableList<VideoData>) {
        if (this.videoDataList != null) {
            this.videoDataList!!.addAll(videoDataList)
        } else {
            this.videoDataList = videoDataList
        }

        notifyDataSetChanged()
    }

    internal class ViewHolder(private val binding: ListItemVideoBinding): RecyclerView.ViewHolder(binding.root) {
        fun layout(videoData: VideoData) {
            Picasso.with(binding.root.context).load(videoData.thumbnail).into(binding.thumbnail)
            binding.cardView.setOnClickListener { binding.presenter!!.startVideoData(videoData) }

            binding.videoData = videoData
        }
    }

}