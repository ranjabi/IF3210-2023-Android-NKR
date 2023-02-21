package com.example.majika.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.majika.R
import com.example.majika.databinding.ListBranchItemBinding
import com.example.majika.databinding.ListMenuItemBinding
import com.example.majika.model.BranchItem
import com.example.majika.model.MenuItem
import com.example.majika.view.BranchFragment

class BranchAdapter(
    private val branches: List<BranchItem>
) : RecyclerView.Adapter<BranchAdapter.BranchViewHolder>() {
    class BranchViewHolder(private var binding: ListBranchItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(
            BranchItem: BranchItem
        ) {
            binding.branchItem = BranchItem
            binding.branchNameText.text = BranchItem.name
//            binding.branchNameText.text = branchItem.name
//            Log.d("COKS","Branch Name: ${branchItem.name}")
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BranchViewHolder {
        return BranchViewHolder(
            ListBranchItemBinding.inflate(
                LayoutInflater.from(
                    parent.context
                )
            )
        )
    }

    override fun onBindViewHolder(holder: BranchViewHolder, position: Int) {
        val branchItem = branches[position]
        holder.itemView.findViewById<TextView>(R.id.branchNameText).text = branchItem.name
        holder.bind(branchItem)
    }

    override fun getItemCount(): Int {
        return branches.size
    }
}
