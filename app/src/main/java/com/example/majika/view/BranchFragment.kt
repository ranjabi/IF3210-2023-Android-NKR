package com.example.majika.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.majika.adapter.BranchAdapter
import com.example.majika.databinding.FragmentBranchBinding
import com.example.majika.viewmodel.BranchViewModel

class BranchFragment : Fragment() {
    private var _binding: FragmentBranchBinding? = null
    private val binding get() = _binding!!

    private val branchViewModel: BranchViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBranchBinding.inflate(inflater)

        // fill here
        binding.lifecycleOwner = this
        binding.branchViewModel = branchViewModel
        val branchAdapter = branchViewModel.branchItem.value?.let { BranchAdapter(it) }
        binding.branchRecyclerView.adapter = branchAdapter
        binding.branchRecyclerView.setHasFixedSize(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding?.apply {
            branchFragment = this@BranchFragment
        }

//        binding.mapsBtn.setOnClickListener {
//            // Creates an Intent that will load a map of a point
//            // 43.6692262, -72.3088865, "116 Frost Park"
//            // 61.32927710000001, -149.5778193, "12022 Town Park Circle"
//            val lat = 61.32927710000001
//            val lng = -149.5778193
//            val rawAddr = "12022 Town Park Circle"
//            val addr = rawAddr.replace(' ', '+')
//            val gmmIntentUri = Uri.parse("geo:0,0?q=$lat,$lng($addr)")
//            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
//            mapIntent.setPackage("com.google.android.apps.maps")
//            startActivity(mapIntent)
//        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}