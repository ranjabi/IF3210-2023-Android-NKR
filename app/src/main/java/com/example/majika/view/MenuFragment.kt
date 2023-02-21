package com.example.majika.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ConcatAdapter
import com.example.majika.adapter.*
import com.example.majika.databinding.FragmentMenuBinding
import com.example.majika.model.CartApplication
import com.example.majika.model.Datasource
import com.example.majika.viewmodel.CartViewModel
import com.example.majika.viewmodel.CartViewModelFactory
import com.example.majika.viewmodel.MenuViewModel

class MenuFragment: Fragment() {
    private var _binding: FragmentMenuBinding? = null
    private val binding get() = _binding!!

    private val menuViewModel: MenuViewModel by viewModels()
    private val cartViewModel: CartViewModel by activityViewModels {
        CartViewModelFactory(
            (activity?.application as CartApplication).database.fnbDao()
        )
    }

    lateinit var adapter: ConcatAdapter
    lateinit var foodSectionHeaderAdapter: SectionHeaderAdapter
    lateinit var foodItemAdapter: ListMenuAdapter
    lateinit var drinkSectionHeaderAdapter: SectionHeaderAdapter
    lateinit var drinkItemAdapter: ListMenuAdapter

    /*
     * inflate layout with data binding, sets its lifecycle owner to MenuFragment
     * to enable data binding to observe liveData
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMenuBinding.inflate(inflater)

        // allows data binding to observe liveData with this fragment lifecycle
        binding.lifecycleOwner = this

        // give binding access to menuViewModel
        binding.menuViewModel = menuViewModel
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(query: String): Boolean {
                menuViewModel.filterData(query)
                return true
            }
        })

        foodSectionHeaderAdapter = SectionHeaderAdapter(Datasource .getFoodTitle())
        foodItemAdapter = ListMenuAdapter(MenuItemIncreaseListener { name, price ->
            cartViewModel.addNewFnb(name, price)
            menuViewModel._filteredFoodItem.value?.find { it.name == name }?.quantity = menuViewModel._filteredFoodItem.value?.find { it.name == name }?.quantity?.plus(
                1
            )

            Log.d("MenuFragment", "${name} increase clicked")
            Toast.makeText(context, "${name} increase clicked", Toast.LENGTH_LONG).show()
        }, MenuItemDecreaseListener { name ->
            cartViewModel.removeFnbQuantityByName(name)
            menuViewModel._filteredFoodItem.value?.find { it.name == name }?.quantity = menuViewModel._filteredFoodItem.value?.find { it.name == name }?.quantity?.minus(
                1
            )

            Log.d("MenuFragment", "${name} decrease clicked")
            Toast.makeText(context, "${name} decrease clicked", Toast.LENGTH_LONG).show()
        }, MenuItemDBGetter{
            name ->
            Log.d("MenuFragment", "${name} is null: ${cartViewModel.allFnbs.value?.find{it.fnbName == name}} from menuItemDBGetter")
            cartViewModel.allFnbs.value?.find{it.fnbName == name}?.fnbQuantity ?: -1
        })

        drinkSectionHeaderAdapter = SectionHeaderAdapter(Datasource.getDrinkTitle())
        drinkItemAdapter = ListMenuAdapter(MenuItemIncreaseListener { name, price ->
            cartViewModel.addNewFnb(name, price)
            menuViewModel._filteredDrinkItem.value?.find { it.name == name }?.quantity = menuViewModel._filteredDrinkItem.value?.find { it.name == name }?.quantity?.plus(
                1
            )

            Log.d("MenuFragment", "${name} increase clicked")
            Toast.makeText(context, "${name} increase clicked", Toast.LENGTH_LONG).show()
        }, MenuItemDecreaseListener { name ->
            cartViewModel.removeFnbQuantityByName(name)
            menuViewModel._filteredDrinkItem.value?.find { it.name == name }?.quantity = menuViewModel._filteredDrinkItem.value?.find { it.name == name }?.quantity?.minus(
                1
            )

            Log.d("MenuFragment", "${name} decrease clicked")
            Toast.makeText(context, "${name} decrease clicked", Toast.LENGTH_LONG).show()
        }, MenuItemDBGetter{
                name ->
            cartViewModel.allFnbs.value?.find{it.fnbName == name}?.fnbQuantity ?: -1
        })

        adapter = ConcatAdapter(foodSectionHeaderAdapter, foodItemAdapter,drinkSectionHeaderAdapter, drinkItemAdapter)
        binding.recyclerView.adapter = adapter

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding?.apply {
            menuFragment = this@MenuFragment
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}