package com.teamdefine.farmapp.farmer.homescreen

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.teamdefine.farmapp.databinding.FragmentFarmerHomeScreenBinding

class FarmerHomeScreen : Fragment() {

    private lateinit var viewModel: FarmerHomeScreenViewModel
    private lateinit var binding: FragmentFarmerHomeScreenBinding
    private lateinit var auth: FirebaseAuth
    private var cropsData: ArrayList<Map<String, Any>> = arrayListOf()
    private lateinit var db: FirebaseFirestore
    private var adapter: RecyclerView.Adapter<FarmerHomeScreenAdapter.ViewHolder>? = null
    private lateinit var adapter2: FarmerHomeScreenAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFarmerHomeScreenBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        viewModel = ViewModelProvider(this).get(FarmerHomeScreenViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getUserData()
        binding.addItem.setOnClickListener {
            findNavController().navigate(FarmerHomeScreenDirections.actionFarmerHomeScreenToFarmerNewItem())
        }
    }

    private fun getUserData() {
        db.collection("Crops").whereEqualTo("farmerId", auth.currentUser?.uid).get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    cropsData.add(document.data)
                    Log.i("helloabc", "${document.data}")
                }
                setDataInRecView()
            }
            .addOnFailureListener { exception ->
                Log.i("helloabc", "Error getting documents: ", exception)
            }
    }

    private fun setDataInRecView() {
        adapter2 =
            FarmerHomeScreenAdapter(cropsData, object : FarmerHomeScreenAdapter.ClickListeners {
                override fun onMainItemClick(data: Map<String, Any>) {
                    findNavController().navigate(
                        FarmerHomeScreenDirections.actionFarmerHomeScreenToFarmerBiddingFragment(
                            data.get("itemId") as String
                        )
                    )
                }

            })
        binding.recyclerView.adapter = adapter2
        binding.recyclerView.layoutManager = LinearLayoutManager(activity)
    }
}