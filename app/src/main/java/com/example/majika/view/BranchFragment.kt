package com.example.majika.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.majika.R
import com.example.majika.adapter.ListBranchAdapter
import com.example.majika.databinding.FragmentBranchBinding
import com.example.majika.model.BranchItem
import com.example.majika.network.MajikaApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import retrofit2.Retrofit

class BranchFragment : Fragment() {
    private var _binding: FragmentBranchBinding? = null
    private val binding get() = _binding!!

    lateinit var recyclerView: RecyclerView
    lateinit var branchList: ArrayList<BranchItem>
    lateinit var branchAdapter: ListBranchAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBranchBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding?.apply {
            branchFragment = this@BranchFragment
        }
        branchList = arrayListOf<BranchItem>()
        lifecycleScope.launch {
            val operation = GlobalScope.async(Dispatchers.Default) {
                val branchDataData = MajikaApi.retrofitService.getAllBranch()
                if(branchDataData != null) {
                    val branchData = branchDataData!!.body()!!.data
                    for (item in branchData) {
                        branchList.add(
                            BranchItem(
                                item.name,
                                item.popular_food,
                                item.address,
                                item.contact_person,
                                item.phone_number,
                                item.longitude,
                                item.latitude,
                            )
                        )
                    }
                    branchList.sortBy {it.name}
                }
            }
            operation.await()
            val layoutManager = LinearLayoutManager(context)
            recyclerView = view.findViewById(R.id.branch_recycler_view)
            recyclerView.layoutManager = layoutManager
            recyclerView.setHasFixedSize(true)
            branchAdapter = ListBranchAdapter(branchList)
            recyclerView.adapter = branchAdapter
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