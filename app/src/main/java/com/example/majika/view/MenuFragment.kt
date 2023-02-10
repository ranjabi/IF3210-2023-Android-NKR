package com.example.majika.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.majika.R
import com.example.majika.adapter.ListMenuAdapter
import com.example.majika.databinding.FragmentMenuBinding
import com.example.majika.viewmodel.MenuViewModel

class MenuFragment : Fragment() {
    private var _binding: FragmentMenuBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MenuViewModel by viewModels()

    /*
     * inflate layout with data binding, sets its lifecycle owner to MenuFragment
     * to enable data binding to observe liveData
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMenuBinding.inflate(inflater)

        // allows data binding to observe liveData with this fragment lifecycle
        binding.lifecycleOwner = this

        // give binding access to menuViewModel
        binding.menuViewModel = viewModel
        binding.recyclerView.adapter = ListMenuAdapter()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding?.apply {
            menuFragment = this@MenuFragment
        }
    }

    fun goToNextScreen() {
        findNavController().navigate(R.id.action_menuFragment_to_cartFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}