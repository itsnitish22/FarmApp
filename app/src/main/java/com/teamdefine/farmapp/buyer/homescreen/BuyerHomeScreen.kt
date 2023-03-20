package com.teamdefine.farmapp.buyer.homescreen

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.teamdefine.farmapp.databinding.FragmentBuyerHomeScreenBinding

class BuyerHomeScreen : Fragment() {
    private lateinit var viewModel: BuyerHomeScreenViewModel
    private lateinit var binding: com.teamdefine.farmapp.databinding.FragmentBuyerHomeScreenBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private var cropsData: ArrayList<Map<String, Any>> = arrayListOf()
    private lateinit var adapter2: BuyerHomeAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBuyerHomeScreenBinding.inflate(inflater, container, false)
        firebaseAuth = FirebaseAuth.getInstance()
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(BuyerHomeScreenViewModel::class.java)
        val db = FirebaseFirestore.getInstance()
        db.collection("Crops").get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    cropsData.add(document.data)
                    Log.i("helloabc", "${document.data}")
                }
                addDataToRecView(cropsData)


//                binding.next.setOnClickListener {
//                    findNavController().navigate(BuyerHomeScreenDirections.actionBuyerHomeScreenToBuyerBiddingFragment())
//                }
            }
            .addOnFailureListener { exception ->
                Log.i("helloabc", "Error getting documents: ", exception)
            }
    }

    private fun addDataToRecView(cropsData: ArrayList<Map<String, Any>>) {
        adapter2 =
            BuyerHomeAdapter(cropsData, object : BuyerHomeAdapter.ClickListeners {
                override fun onItemClick(data: Map<String, Any>) {
                    findNavController().navigate(
                        BuyerHomeScreenDirections.actionBuyerHomeScreenToBuyerBiddingFragment(
                            data.get("itemId") as String
                        )
                    )
                }
            })
        binding.recyclerView.adapter = adapter2
        binding.recyclerView.layoutManager = LinearLayoutManager(activity)

    }

}