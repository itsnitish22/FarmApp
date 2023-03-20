package com.teamdefine.farmapp.buyer.homescreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.teamdefine.farmapp.R

class BuyerHomeScreen : Fragment() {

    companion object {
        fun newInstance() = BuyerHomeScreen()
    }

    private lateinit var viewModel: BuyerHomeScreenViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_buyer_home_screen, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(BuyerHomeScreenViewModel::class.java)
        // TODO: Use the ViewModel
    }

}