package com.example.majika.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.majika.databinding.ListMenuItemBinding
import com.example.majika.model.MenuItem

class ListMenuAdapter : ListAdapter<MenuItem, ListMenuAdapter.MenuItemViewHolder>(DiffCallback) {

    class MenuItemViewHolder(private var binding: ListMenuItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(MenuItem: MenuItem) {
            binding.menuItem = MenuItem
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ListMenuAdapter.MenuItemViewHolder {
        return MenuItemViewHolder(ListMenuItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: ListMenuAdapter.MenuItemViewHolder, position: Int) {
        val menuItem = getItem(position)
        holder.bind(menuItem)
    }

    companion object DiffCallback : DiffUtil.ItemCallback<MenuItem>() {
        override fun areItemsTheSame(oldItem: MenuItem, newItem: MenuItem): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: MenuItem, newItem: MenuItem): Boolean {
            return oldItem.name == newItem.name
        }

    }

}