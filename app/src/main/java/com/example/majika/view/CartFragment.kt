package com.example.majika.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.majika.R
import com.example.majika.databinding.FragmentCartBinding
import com.example.majika.model.CartApplication
import com.example.majika.model.Fnb
import com.example.majika.viewmodel.CartViewModel
import com.example.majika.viewmodel.CartViewModelFactory

class CartFragment : Fragment() {
    private val viewModel: CartViewModel by activityViewModels {
        CartViewModelFactory(
            (activity?.application as CartApplication).database.fnbDao()
        )
    }
    lateinit var firstFnb: Fnb

    private fun addNewFnb() {
        viewModel.addNewFnb("New Fnb", "2000", "5")
    }
    private fun addFnbQuantity(fnb: Fnb) {
        viewModel.addFnbQuantity(fnb)
    }
    private fun resetFnb() {
        viewModel.resetFnb()
    }
    private fun removeFnbQuantity(fnb: Fnb) {
        viewModel.removeFnbQuantity(fnb)
    }
    private fun bindForFirstFnb (fnb: Fnb) {
        binding.apply {
            addFnbQty.setOnClickListener { addFnbQuantity(fnb) }
            removeFnbQty.setOnClickListener { removeFnbQuantity(fnb) }
            resetFnbBtn.setOnClickListener{ resetFnb() }
        }
    }
    private fun isTableEmpty (tableRowLength: Int): Boolean {
        return (tableRowLength == 0)
    }
    private fun bindForTableRow (tableRowLength: Int) {
        binding.apply {
            addFnbQty.isEnabled = !isTableEmpty(tableRowLength)
            removeFnbQty.isEnabled = !isTableEmpty(tableRowLength)
        }
    }

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.addToCart.setOnClickListener { addNewFnb() }
        // still dummy only operate first data
        viewModel.retrieveFirstFnb().observe(this.viewLifecycleOwner) { selectedFnb ->
            if(selectedFnb != null) {
                firstFnb = selectedFnb
                bindForFirstFnb(firstFnb)
            }
        }
        // still dummy to check whether the fnb table is empty or not
        viewModel.getTableRowLength().observe(this.viewLifecycleOwner) { selectedCount ->
            val tableRowLength = selectedCount
            bindForTableRow(tableRowLength)
        }
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