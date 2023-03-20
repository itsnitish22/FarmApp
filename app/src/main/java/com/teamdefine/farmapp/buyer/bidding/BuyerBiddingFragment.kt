package com.teamdefine.farmapp.buyer.bidding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.teamdefine.farmapp.R
import com.teamdefine.farmapp.databinding.FragmentBuyerBiddingBinding

class BuyerBiddingFragment : Fragment() {
    private val viewModel: BuyerBiddingViewModel by viewModels()
    private lateinit var binding: FragmentBuyerBiddingBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_buyer_bidding, container, false)
    }
}