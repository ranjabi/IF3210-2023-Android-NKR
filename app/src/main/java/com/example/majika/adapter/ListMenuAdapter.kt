package com.example.majika.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.majika.R
import com.example.majika.databinding.ListMenuItemBinding
import com.example.majika.model.MenuItem
import java.text.NumberFormat
import java.util.*

class ListMenuAdapter(val increaseClickListener: MenuItemIncreaseListener, val decreaseClickListener: MenuItemDecreaseListener, val menuItemDBGetter: MenuItemDBGetter) : ListAdapter<MenuItem, ListMenuAdapter.MenuItemViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ListMenuAdapter.MenuItemViewHolder {
        return MenuItemViewHolder(ListMenuItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: ListMenuAdapter.MenuItemViewHolder, position: Int) {
        val menuItem = getItem(position)
        holder.itemView.findViewById<ImageView>(R.id.imageButton2).setOnClickListener {
            increaseClickListener.onIncreaseClick(menuItem)
        }
        holder.itemView.findViewById<ImageView>(R.id.imageButton4).setOnClickListener {
            decreaseClickListener.onDecreaseClick(menuItem)
        }
        holder.bind(menuItem, increaseClickListener, decreaseClickListener, menuItemDBGetter)
    }

    class MenuItemViewHolder(private var binding: ListMenuItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(
            MenuItem: MenuItem,
            increaseClickListener: MenuItemIncreaseListener,
            decreaseClickListener: MenuItemDecreaseListener,
            menuItemDB: MenuItemDBGetter
        ) {
            binding.menuItem = MenuItem
            binding.increaseClickListener = increaseClickListener
            binding.decreaseClickListener = decreaseClickListener
            binding.menuItemDB = menuItemDB
            binding.quantity.text = MenuItem.quantity.toString()
            binding.soldText.text = MenuItem.sold + "terjual"

            val localeID = Locale("in", "ID")
            val formatRupiah = NumberFormat.getCurrencyInstance(localeID)
            val priceText: String = formatRupiah.format(MenuItem.price.toInt()).toString()

            binding.price.text = priceText.substring(0, priceText.length - 3);
            binding.executePendingBindings()
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<MenuItem>() {
        override fun areItemsTheSame(oldItem: MenuItem, newItem: MenuItem): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: MenuItem, newItem: MenuItem): Boolean {
            return oldItem.quantity == newItem.quantity
        }

    }

}

class MenuItemIncreaseListener(val increaseclickListener: ((name: String, price: String) -> Unit?)? = null)  {
    fun onIncreaseClick(menuItem: MenuItem) = increaseclickListener?.invoke(menuItem.name, menuItem.price)
}

class MenuItemDecreaseListener(val decreaseclickListener: ((name: String) -> Unit?)? = null)  {
    fun onDecreaseClick(menuItem: MenuItem) = decreaseclickListener?.invoke(menuItem.name)
}

class MenuItemDBGetter(val menuItemDBGetter: (name: String) -> Int) {
    fun getMenuItem(name: String) = menuItemDBGetter.invoke(name).toString()
}