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
import com.teamdefine.farmapp.R
import com.teamdefine.farmapp.databinding.FragmentFarmerHomeScreenBinding
import com.teamdefine.farmapp.databinding.FragmentFarmerRegisterBinding

class FarmerHomeScreen : Fragment() {

    private lateinit var viewModel: FarmerHomeScreenViewModel
    private lateinit var binding:FragmentFarmerHomeScreenBinding
    private lateinit var auth: FirebaseAuth

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
        val currentUserId=auth.currentUser?.uid

        viewModel = ViewModelProvider(this).get(FarmerHomeScreenViewModel::class.java)
        val crops=FirebaseFirestore.getInstance().collection("crops")
        crops.whereEqualTo("farmerId",currentUserId).get().addOnSuccessListener {
                res->
            for(i in res){
                Log.i("helloabc",i.data.toString())
            }
        }
        binding.addItem.setOnClickListener {
            findNavController().navigate(FarmerHomeScreenDirections.actionFarmerHomeScreenToFarmerNewItem())
        }
        // TODO: Use the ViewModel
    }

}