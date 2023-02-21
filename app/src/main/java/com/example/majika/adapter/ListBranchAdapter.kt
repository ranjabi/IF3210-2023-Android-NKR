package com.example.majika.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.majika.R
import com.example.majika.model.BranchItem

class ListBranchAdapter (
    private val dataset: ArrayList<BranchItem>
) : RecyclerView.Adapter<ListBranchAdapter.BranchViewHolder>() {
    class BranchViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val branchName: TextView = view.findViewById(R.id.branch_name_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BranchViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_branch_item, parent, false)
        return BranchViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: BranchViewHolder, position: Int) {
        val data = dataset[position]
        holder.branchName.text = data.name
    }

    override fun getItemCount(): Int {
        return dataset.size
    }
}
