package com.example.storyapp.ui.adapters

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.storyapp.DetailStoryActivity
import com.example.storyapp.data.responses.ListStoryItem
import com.example.storyapp.databinding.StoryItemsBinding

class StoryListAdapter() :
    PagingDataAdapter<ListStoryItem, StoryListAdapter.ListViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = StoryItemsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) {
            holder.bind(data)
        }
    }

    class ListViewHolder(var binding: StoryItemsBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(dataStory: ListStoryItem) {
            binding.storyAuthor.text = dataStory.name
            binding.storyDesc.text = dataStory.description
            Glide.with(itemView.context)
                .load(dataStory.photoUrl)
                .into(binding.storyImg)

            itemView.setOnClickListener {
                val intentToDetail = Intent(itemView.context, DetailStoryActivity::class.java)
                intentToDetail.putExtra(DetailStoryActivity.ID, dataStory.id)
                intentToDetail.putExtra(DetailStoryActivity.EXTRA_STORY_ITEMS, dataStory)

                val optionsCompat: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        itemView.context as Activity,
                        Pair(binding.storyImg, "detail_image"),
                        Pair(binding.storyAuthor, "detail_author")
                    )
                itemView.context.startActivity(intentToDetail, optionsCompat.toBundle())
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: ListStoryItem,
                newItem: ListStoryItem,
            ): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}