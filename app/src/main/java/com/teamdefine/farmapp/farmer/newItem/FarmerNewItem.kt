package com.teamdefine.farmapp.farmer.newItem

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
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.teamdefine.farmapp.databinding.FragmentFarmerNewItemBinding
import java.util.*

class FarmerNewItem : Fragment() {

    private lateinit var viewModel: FarmerNewItemViewModel
    private lateinit var binding: FragmentFarmerNewItemBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var imageUrl: String
    var url: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFarmerNewItemBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(FarmerNewItemViewModel::class.java)
        initClickListeners()
    }

    private fun initClickListeners() {
        binding.picture.setOnFocusChangeListener { view, b ->
            val intent = Intent().setType("*/*").setAction(Intent.ACTION_GET_CONTENT)
            startActivityForResult(Intent.createChooser(intent, "Select a file"), 777)
        }
        binding.submitButton.setOnClickListener {
            saveItemToDb(url.toString())
        }
    }

    fun generateUUID(): String {
        return UUID.randomUUID().toString()
    }

    private fun saveItemToDb(fileUri: String) {
        val currentUser = auth.currentUser
        val uid = generateUUID()
        val database = FirebaseFirestore.getInstance()

        val crop: MutableMap<String, Any> = HashMap()
        val uri = fileUri

        currentUser?.let {
            crop["farmerId"] = currentUser.uid
            crop["itemId"] = uid
            crop["itemName"] = binding.inputCrop.text.toString()
            crop["itemPrice"] = binding.inputOfferPrice.text.toString()
            crop["image"] = uri


            database.collection("Crops").document(uid)
                .set(crop)//setting the data to be saved
                .addOnSuccessListener {
                    Log.i("helloabc", "success")
                    findNavController().navigate(FarmerNewItemDirections.actionFarmerNewItemToFarmerHomeScreen())
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
        Log.i("hello", "file name : $fileName")
        val storageRef = storage.reference.child("farmer/crops/$fileName")
        binding.inputInputPicture.setText(storageRef.toString())
        storageRef.putFile(fileUri)
            .addOnSuccessListener { taskSnapshot ->
                // File uploaded successfully
                Log.i("SaveFile", "Saved User")
                storageRef.downloadUrl.addOnSuccessListener {
                    url = it.toString()
                    Log.i("SaveFile", it.toString())
                }


            }
            .addOnFailureListener { exception ->
                // Handle any errors
            }
    }

}