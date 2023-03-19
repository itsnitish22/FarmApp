package com.teamdefine.farmapp.farmer.registration

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
import com.teamdefine.farmapp.databinding.FragmentFarmerRegisterBinding

class FarmerRegister : Fragment() {

    private lateinit var viewModel: FarmerRegisterViewModel
    private lateinit var binding: FragmentFarmerRegisterBinding
    private lateinit var auth: FirebaseAuth


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFarmerRegisterBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()
        uploadDoc()
//        saveUserToDb()
        return binding.root
    }

    private fun uploadDoc() {
        binding.button2.setOnClickListener {
            val intent = Intent().setType("*/*").setAction(Intent.ACTION_GET_CONTENT)
            startActivityForResult(Intent.createChooser(intent, "Select a file"), 777)
        }
    }

    private fun saveUserToDb(docUri: String) {
        val currentUser = auth.currentUser
        val img1 = "first"
        val img2 = "second"
        val img3 = "third"
        val landSize = "landSize"
        val soilReport = docUri
        val database = FirebaseFirestore.getInstance()

        val farmer: MutableMap<String, Any> = HashMap()
        currentUser?.let {
            farmer["name"] = currentUser.displayName.toString()
            farmer["email"] = currentUser.email.toString()
            farmer["landSize"] = landSize
            farmer["soilReport"] = soilReport
            farmer["doc1"] = img1
            farmer["doc2"] = img2
            farmer["doc3"] = img3

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
        Log.i("hello", "file name : $fileName")
        val storageRef = storage.reference.child("farmer/$fileName")
        storageRef.putFile(fileUri)
            .addOnSuccessListener { taskSnapshot ->
                // File uploaded successfully
                Log.i("SaveFile", "Saved User")
                storageRef.downloadUrl.addOnSuccessListener {
                    saveUserToDb(it.toString())
                    Log.i("SaveFile", it.toString())
                }


            }
            .addOnFailureListener { exception ->
                // Handle any errors
            }
    }

}