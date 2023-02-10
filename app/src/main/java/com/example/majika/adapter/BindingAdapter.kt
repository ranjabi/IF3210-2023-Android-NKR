package com.example.majika.adapter

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.majika.model.MenuItem

@BindingAdapter("listData")
fun bindRecyclerView(recyclerView: RecyclerView, data: List<MenuItem>?) {
    val adapter = recyclerView.adapter as ListMenuAdapter
    adapter.submitList(data)
}