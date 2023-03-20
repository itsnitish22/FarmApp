package com.teamdefine.farmapp.farmer.registration

import android.content.Intent
import android.database.Cursor
import android.net.Uri
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
import com.teamdefine.farmapp.databinding.FragmentFarmerRegisterBinding

class FarmerRegister : Fragment() {

    private lateinit var viewModel: FarmerRegisterViewModel
    private lateinit var binding: FragmentFarmerRegisterBinding
    private lateinit var auth: FirebaseAuth
    private var docUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = FragmentFarmerRegisterBinding.inflate(inflater, container, false).also {
        binding = it
        auth = FirebaseAuth.getInstance()
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.apply {
            soilReport.setOnClickListener {
                uploadDoc()
            }
            submitButton.setOnClickListener {
                saveUserToDb(docUri.toString())
            }
        }
    }

    private fun setupViews() {
        auth.currentUser?.let {
            binding.inputName.setText(it.displayName)
            binding.inputEmail.setText(it.email)
        }
    }


    private fun uploadDoc() {
        val intent = Intent().setType("*/*").setAction(Intent.ACTION_GET_CONTENT)
        startActivityForResult(Intent.createChooser(intent, "Select a file"), 777)

    }

    private fun saveUserToDb(docUri: String) {
        val currentUser = auth.currentUser
        val landSize = "landSize"
        val soilReport = docUri
        val database = FirebaseFirestore.getInstance()

        val farmer: MutableMap<String, Any> = HashMap()
        currentUser?.let {
            farmer["name"] = currentUser.displayName.toString()
            farmer["email"] = currentUser.email.toString()
            farmer["mobileNumber"] = binding.inputPhone.text.toString()
            farmer["soilReport"] = docUri

            database.collection("Farmers").document(currentUser.uid)
                .set(farmer) //setting the data to be saved
                .addOnSuccessListener {
                    Log.i("helloabc", "success")
                    findNavController().navigate(FarmerRegisterDirections.actionFarmerRegisterToFarmerHomeScreen())
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
        val storageRef = storage.reference.child("farmer/$fileName")
        storageRef.putFile(fileUri)
            .addOnSuccessListener { taskSnapshot ->
                // File uploaded successfully
                Log.i("SaveFile", "Saved User")
                storageRef.downloadUrl.addOnSuccessListener {
                    docUri = it
//                    saveUserToDb(it.toString())
                    Log.i("SaveFile", it.toString())
                }


            }
            .addOnFailureListener { exception ->
                // Handle any errors
            }
    }

}