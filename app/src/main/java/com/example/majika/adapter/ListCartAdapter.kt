package com.example.majika.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.majika.R
import com.example.majika.model.Fnb

class ListCartAdapter : ListAdapter<Fnb, ListCartAdapter.CartViewHolder>(CartComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        return CartViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val fnb = getItem(position)
        holder.bind(fnb.fnbName, fnb.fnbPrice, fnb.fnbQuantity)
    }

    class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cartName: TextView = itemView.findViewById(R.id.cartName)
        private val cartPrice: TextView = itemView.findViewById(R.id.cartPrice)
        private val cartQuantity: TextView = itemView.findViewById(R.id.cartQuantity)

        fun bind(name: String?, price: Int?, quantity: Int?) {
            cartName.text = name
            cartPrice.text = price.toString()
            cartQuantity.text = quantity.toString()
        }

        companion object {
            fun create(parent: ViewGroup): CartViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.list_cart_item, parent, false)
                return CartViewHolder(view)
            }
        }
    }

    class CartComparator : DiffUtil.ItemCallback<Fnb>() {
        override fun areItemsTheSame(oldItem: Fnb, newItem: Fnb): Boolean {
            return oldItem.fnbName == newItem.fnbName
        }

        override fun areContentsTheSame(oldItem: Fnb, newItem: Fnb): Boolean {
            return oldItem.fnbQuantity == newItem.fnbQuantity
        }
    }
}