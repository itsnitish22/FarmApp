package com.teamdefine.farmapp.farmer.bidding

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.teamdefine.farmapp.R

class FarmerBiddingAdapter(private val bidList: ArrayList<Map<String, Any>>) :
    RecyclerView.Adapter<FarmerBiddingAdapter.ViewHolder>() {
    private lateinit var db: FirebaseFirestore

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val bidAmount: TextView = itemView.findViewById(R.id.bidAmount)
        val nameOfBuyer: TextView = itemView.findViewById(R.id.nameOfBuyer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        db = FirebaseFirestore.getInstance()
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_farmer_bid, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = bidList[position]
        Log.i("cur_item", currentItem.toString())
        db.collection("Bidding").document(currentItem.get("bid_id").toString()).get()
            .addOnSuccessListener {
                Log.i("buyBid", it.get("buyer_bid").toString())
                holder.bidAmount.text = "₹" + it.get("buyer_bid").toString() + "/क्विंटल"
                db.collection("Buyers").document(it.get("buyer_id").toString()).get()
                    .addOnSuccessListener {
                        holder.nameOfBuyer.text = it.get("name").toString()
                    }
            }
    }

    override fun getItemCount(): Int {
        return bidList.size
    }
}