package com.teamdefine.farmapp.farmer.homescreen

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.teamdefine.farmapp.R

class FarmerHomeScreenAdapter(
    private val data: ArrayList<Map<String, Any>>,
    private val clickListeners: ClickListeners
) : RecyclerView.Adapter<FarmerHomeScreenAdapter.ViewHolder>() {

    interface ClickListeners {
        fun onMainItemClick(data: Map<String, Any>)
    }

    private val myData: MutableList<Map<String, Any>> = ArrayList()

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cropIv: TextView = itemView.findViewById(R.id.cropIv)
        val cropNameTv: TextView = itemView.findViewById(R.id.cropNameTv)
        val currentPrice: TextView = itemView.findViewById(R.id.currentPriceTv)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_farmer_crop, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = data[position]

        holder.cropNameTv.text = currentItem.get("itemName").toString()
        holder.currentPrice.text = currentItem.get("itemPrice").toString()

        holder.itemView.setOnClickListener {
            clickListeners.onMainItemClick(currentItem)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun addItem(newData: Map<String, Any>) {
        myData.add(newData)
        notifyDataSetChanged()
    }
}