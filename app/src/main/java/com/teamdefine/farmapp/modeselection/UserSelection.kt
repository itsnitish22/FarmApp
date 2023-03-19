package com.teamdefine.farmapp.modeselection

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.teamdefine.farmapp.databinding.FragmentUserSelectionBinding

class UserSelection : Fragment() {

    private lateinit var binding: FragmentUserSelectionBinding
    lateinit var radioButton: RadioButton
    private lateinit var viewModel: UserSelectionViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserSelectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initClickListeners()
    }

    private fun initClickListeners() {
        binding.apply {
            doneButton.setOnClickListener {
                val selectedButton: Int? = radioGroup.checkedRadioButtonId
                selectedButton?.let {
                    radioButton = root.findViewById<RadioButton>(selectedButton)
                    Toast.makeText(requireContext(), radioButton.text, Toast.LENGTH_SHORT).show()
                    if (radioButton == binding.farmer)
                        findNavController().navigate(UserSelectionDirections.actionUserSelectionToFarmerRegister())
                    else if (radioButton == binding.buyer)
                        findNavController().navigate(UserSelectionDirections.actionUserSelectionToBuyerRegistration())

                }
            }
        }
    }

}