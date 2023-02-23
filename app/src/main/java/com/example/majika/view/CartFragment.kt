package com.example.majika.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.majika.R
import com.example.majika.adapter.ListCartAdapter
import com.example.majika.databinding.FragmentCartBinding
import com.example.majika.model.MajikaApplication
import com.example.majika.viewmodel.CartViewModel
import com.example.majika.viewmodel.CartViewModelFactory

class CartFragment : Fragment() {
    private val viewModel: CartViewModel by activityViewModels {
        CartViewModelFactory(
            (activity?.application as MajikaApplication).repository
        )
    }
    private fun isTableEmpty (tableRowLength: Int): Boolean {
        return (tableRowLength == 0)
    }

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCartBinding.inflate(inflater, container, false)

        val recyclerView = binding.cartRecyclerView
        val adapter = ListCartAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(activity)

        viewModel.allFnbs.observe(viewLifecycleOwner, Observer {
                fnbs -> fnbs?.let {
            adapter.submitList(it)
            binding.totalPrice.text = "Rp" + fnbs.sumOf { fnb -> fnb.fnbPrice * fnb.fnbQuantity }.toString()
                }
        })

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        binding.addToCart.setOnClickListener { addNewFnb() }
//        // still dummy only operate first data
//        viewModel.retrieveFirstFnb().observe(this.viewLifecycleOwner) { selectedFnb ->
//            if(selectedFnb != null) {
//                firstFnb = selectedFnb
//                bindForFirstFnb(firstFnb)
//            }
//        }
//        // still dummy to check whether the fnb table is empty or not
//        viewModel.getTableRowLength().observe(this.viewLifecycleOwner) { selectedCount ->
//            val tableRowLength = selectedCount
//            bindForTableRow(tableRowLength)
//        }
        _binding?.apply {
            cartFragment = this@CartFragment
        }
    }

    fun goToNextScreen() {
        findNavController().navigate(R.id.action_cartFragment_to_branchFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}