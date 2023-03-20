package com.teamdefine.farmapp.farmer.bidding

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.teamdefine.farmapp.databinding.FragmentFarmerBiddingBinding

class FarmerBiddingFragment : Fragment() {
    private val viewModel: FarmerBiddingViewModel by viewModels()
    private lateinit var binding: FragmentFarmerBiddingBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private val args: FarmerBiddingFragmentArgs by navArgs()
    private var adapter: RecyclerView.Adapter<FarmerBiddingAdapter.ViewHolder>? = null //adapter
    private var bidList: ArrayList<String> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = FragmentFarmerBiddingBinding.inflate(inflater, container, false).also {
        binding = it
        db = FirebaseFirestore.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val cropId = args.itemId
        db.collection("Crops").document(cropId).get().addOnSuccessListener { it ->
            val options: RequestOptions = RequestOptions()
                .centerCrop()
            Glide.with(requireContext()).load(it.get("image").toString()).apply(options)
                .into(binding.cropIv)

            binding.cropNameTv.text = it.get("itemName").toString()
            binding.priceOfCrop.text = it.get("itemPrice").toString() + "/क्विंटल"

            val farmerId = it.get("farmerId")
            db.collection("Farmers").document(farmerId.toString()).get().addOnSuccessListener {
                binding.farmerNameTv.text = it.get("name").toString()
                db.collection("Bidding").whereEqualTo("item_id", it.get("itemId")).get()
                    .addOnSuccessListener {
                        for (i in it) {
                            Log.i("test", i.data.toString())
                            bidList.add(i.data.toString())
                        }
                    }
            }

        }
        initViews()
    }

    private fun initViews() {
        adapter = FarmerBiddingAdapter(bidList)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(activity)
    }
}