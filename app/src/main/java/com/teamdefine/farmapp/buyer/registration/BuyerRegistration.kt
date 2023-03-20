package com.teamdefine.farmapp.buyer.registration

import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.teamdefine.farmapp.databinding.FragmentBuyerRegistrationBinding

class BuyerRegistration : Fragment() {

    private lateinit var binding: FragmentBuyerRegistrationBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var viewModel: BuyerRegistrationViewModel
    private var photoUrl: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBuyerRegistrationBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()
        uploadDoc()
        setupDataInViews()
        setupClickListeners()
        return binding.root
    }

    private fun setupClickListeners() {
        binding.idCard.setOnClickListener {
            uploadDoc()
        }
        binding.submitButton.setOnClickListener {
            photoUrl?.let {
                saveUserToDb(it)
            }
        }
    }

    private fun setupDataInViews() {
        binding.apply {
            inputName.setText(auth.currentUser?.displayName)
            inputEmail.setText(auth.currentUser?.email)
        }
    }

    private fun uploadDoc() {
        val intent = Intent().setType("*/*").setAction(Intent.ACTION_GET_CONTENT)
        startActivityForResult(Intent.createChooser(intent, "Select a file"), 777)
    }

    private fun saveUserToDb(docUri: String) {
        val currentUser = auth.currentUser
        val img1 = docUri
        val database = FirebaseFirestore.getInstance()

        val buyer: MutableMap<String, Any> = HashMap()
        currentUser?.let {
            buyer["name"] = currentUser.displayName.toString()
            buyer["email"] = currentUser.email.toString()
            buyer["phoneNumber"] = binding.inputPhone.text.toString()
            buyer["doc1"] = photoUrl.toString()

            database.collection("Buyers").document(currentUser.uid)
                .set(buyer) //setting the data to be saved
                .addOnSuccessListener {
                    Log.i("helloabc", "success")
                    findNavController().navigate(BuyerRegistrationDirections.actionBuyerRegistrationToBuyerHomeScreen())
                    //if the data is saved successfully, move the uer to login fragment

                }
                //otherwise toast that failed to save data
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Failed to save user data", Toast.LENGTH_SHORT)
                        .show()
                }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val fileUri = data?.data!!
        val storage = Firebase.storage
        val returnCursor: Cursor? =
            fileUri?.let { requireContext().contentResolver.query(it, null, null, null, null) }
        val nameIndex: Int = returnCursor!!.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        returnCursor.moveToFirst()
        val fileName = returnCursor.getString(nameIndex)
        binding.inputSoilReport.setText(fileName)
        Log.i("hello", "file name : $fileName")
        val storageRef = storage.reference.child("buyer/$fileName")
        storageRef.putFile(fileUri)
            .addOnSuccessListener { taskSnapshot ->
                // File uploaded successfully
                Log.i("SaveFile", "Saved User")
                storageRef.downloadUrl.addOnSuccessListener {
                    photoUrl = it.toString()
                    Log.i("SaveFile", it.toString())
                }


            }
            .addOnFailureListener { exception ->
                // Handle any errors
            }
    }

}