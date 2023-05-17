package com.example.storyapp.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.storyapp.data.responses.ListStoryItem
import com.example.storyapp.databinding.StoryItemsBinding

class StoryListAdapter(private var listStory: List<ListStoryItem>) :
    RecyclerView.Adapter<StoryListAdapter.ListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = StoryItemsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun getItemCount(): Int = listStory.size

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.binding.apply {
            Glide.with(storyImg).load(listStory[position].photoUrl)
                .into(storyImg)
            storyAuthor.text = listStory[position].name
            storyDesc.text = listStory[position].description
        }
    }

    class ListViewHolder(var binding: StoryItemsBinding) : RecyclerView.ViewHolder(binding.root) {
    }
}