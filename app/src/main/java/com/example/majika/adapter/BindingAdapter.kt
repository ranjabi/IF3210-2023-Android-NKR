package com.example.majika.adapter

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.majika.model.BranchItem
import com.example.majika.model.MenuItem

//@BindingAdapter("listBranchData")
//fun bindRecyclerViewWithBranch(recyclerView: RecyclerView, data: List<BranchItem>?) {
////    data?.let {
////       val adapter = recyclerView.adapter[0] as BranchAdapter
////    adapter.submitList(data)
////    }
//}
@BindingAdapter("listFoodData")
fun bindRecyclerViewWithFood(recyclerView: RecyclerView, data: List<MenuItem>?) {
    val adapter = recyclerView.adapter as ConcatAdapter
    val foodAdapter = adapter.adapters[1] as ListMenuAdapter
    foodAdapter.submitList(data)
}

@BindingAdapter("listDrinkData")
fun bindRecyclerViewWithDrink(recyclerView: RecyclerView, data: List<MenuItem>?) {
    val adapter = recyclerView.adapter as ConcatAdapter
    val drinkAdapter = adapter.adapters[3] as ListMenuAdapter
    drinkAdapter.submitList(data)
}