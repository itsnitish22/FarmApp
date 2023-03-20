package com.teamdefine.farmapp.farmer.bidding

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.teamdefine.farmapp.R

class FarmerBiddingAdapter(private val bidList: ArrayList<String>) :
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
        db.collection("Bidding").document(currentItem).get().addOnSuccessListener {
            holder.bidAmount.text = it.get("buyer_bid").toString()
            db.collection("Buyers").document(it.get("buyer_id").toString()).get()
                .addOnSuccessListener {
                    holder.nameOfBuyer.text = it.get("name").toString()
                }
        }
    }

    override fun getItemCount(): Int {
        return 2
    }
}