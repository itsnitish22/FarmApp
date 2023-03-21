package com.teamdefine.farmapp.buyer.bidding

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.teamdefine.farmapp.databinding.FragmentBuyerBiddingBinding
import java.util.*

class BuyerBiddingFragment : Fragment() {
    private val viewModel: BuyerBiddingViewModel by viewModels()
    private lateinit var binding: FragmentBuyerBiddingBinding
    private val args: BuyerBiddingFragmentArgs by navArgs()
    private lateinit var database: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var currentUser: FirebaseUser


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBuyerBiddingBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()
        database = FirebaseFirestore.getInstance()
        db = FirebaseFirestore.getInstance()
        currentUser = auth.currentUser!!


        return binding.root
    }

    fun generateUUID(): String {
        return UUID.randomUUID().toString()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i("buyer id", currentUser?.uid.toString())
        var firstTime = false
        db.collection("Bidding").whereEqualTo("buyer_id", currentUser?.uid)
            .whereEqualTo("item_id", args.itemId).get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    Log.i("helloabc", "empty")
                    firstTime = true
                }
                bidding(firstTime)

            }

    }

    private fun bidding(firstTime: Boolean) {
        val bid: MutableMap<String, Any> = HashMap()

        if (firstTime) {
            var price: String = ""
            db.collection("Crops").whereEqualTo("itemId", args.itemId).get()
                .addOnSuccessListener { crop ->
                    Log.i("crop price", crop.documents[0].data.toString())
                    price = crop.documents[0].data?.get("itemPrice").toString()
                    Log.i("price", price.toString())
                    binding.priceTv.text = "₹" + price + "/क्विंटल"
                    val options: RequestOptions = RequestOptions()
                        .centerCrop()
                    binding.titleItemTv.text = crop.documents[0].data?.get("itemName").toString()
                    Glide.with(requireContext())
                        .load(crop.documents[0].data?.get("image").toString()).apply(options)
                        .into(binding.mainIv)
                    binding.submitButton.setOnClickListener {
                        val buyerBid = binding.inputNewPrice.text.toString()
                        currentUser?.let {
                            bid["buyer_id"] = currentUser.uid
                            bid["item_id"] = args.itemId
                            bid["farmer_bid"] = price
                            bid["buyer_bid"] = buyerBid
                            db.collection("Bidding").document(generateUUID()).set(bid)
                                .addOnSuccessListener {
                                    Log.i("helloabc", "bid created")
                                    findNavController().navigate(BuyerBiddingFragmentDirections.actionBuyerBiddingFragmentToBuyerHomeScreen())
                                }
                        }
                    }
                }

        } else {
            db.collection("Bidding").whereEqualTo("buyer_id", currentUser?.uid)
                .whereEqualTo("item_id", args.itemId)
                .get()
                .addOnSuccessListener { crop ->
                    Log.i("helloabcc3", crop.documents.size.toString())
                    binding.priceTv.text =
                        "₹" + crop.documents[0].data?.get("farmer_bid").toString() + "/क्विंटल"
                    val options: RequestOptions = RequestOptions()
                        .centerCrop()
                    binding.inputNewPrice.setText(
                        crop.documents[0].data?.get("buyer_bid").toString()
                    )

                    db.collection("Crops").whereEqualTo("itemId", args.itemId).get()
                        .addOnSuccessListener {
                            Log.i("itemname", it.documents[0].data?.get("itemName").toString())
                            binding.titleItemTv.text =
                                it.documents[0].data?.get("itemName").toString()
                            Glide.with(requireContext())
                                .load(it.documents[0].data?.get("image").toString()).apply(options)
                                .into(binding.mainIv)
                        }


                    binding.submitButton.setOnClickListener {
                        val buyerBid = binding.inputNewPrice.text.toString()
                        db.collection("Bidding").document(crop.documents[0].id)
                            .update("buyer_bid", buyerBid).addOnSuccessListener {
                                Log.i("helloabc", "bid updated")
                                findNavController().navigate(BuyerBiddingFragmentDirections.actionBuyerBiddingFragmentToBuyerHomeScreen())
                            }
                    }
                }
        }
    }

}
