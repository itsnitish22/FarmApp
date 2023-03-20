package com.teamdefine.farmapp.farmer.homescreen

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.teamdefine.farmapp.databinding.FragmentFarmerHomeScreenBinding

class FarmerHomeScreen : Fragment() {

    private lateinit var viewModel: FarmerHomeScreenViewModel
    private lateinit var binding: FragmentFarmerHomeScreenBinding
    private lateinit var auth: FirebaseAuth
    private var cropsData:ArrayList<Any> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFarmerHomeScreenBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val currentUserId = auth.currentUser?.uid

        viewModel = ViewModelProvider(this).get(FarmerHomeScreenViewModel::class.java)
        val db = FirebaseFirestore.getInstance()
        db.collection("Crops").whereEqualTo("farmerId",currentUserId).get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    cropsData.add(document.data)
                    Log.i("helloabc", "${document.data}")
                }
            }
            .addOnFailureListener { exception ->
                Log.i("helloabc", "Error getting documents: ", exception)
            }
        binding.addItem.setOnClickListener {
            findNavController().navigate(FarmerHomeScreenDirections.actionFarmerHomeScreenToFarmerNewItem())
        }
        // TODO: Use the ViewModel
    }

}