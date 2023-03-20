package com.teamdefine.farmapp.authentication

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.teamdefine.farmapp.R
import com.teamdefine.farmapp.databinding.FragmentUserAuthenticationBinding

class UserAuthentication : Fragment() {

    lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var binding: FragmentUserAuthenticationBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var viewModel: UserAuthenticationViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserAuthenticationBinding.inflate(inflater, container, false)
        FirebaseApp.initializeApp(requireContext())
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)

        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
        firebaseAuth = FirebaseAuth.getInstance()

        binding.button.setOnClickListener { view: View? ->
            Toast.makeText(requireContext(), "Logging In", Toast.LENGTH_SHORT).show()
            signInWithGoogle()
        }

        return binding.root
    }

    private fun signInWithGoogle() {
        launcher.launch(googleSignInClient.signInIntent)
    }

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            Log.i("Test", "${result.resultCode}, ${Activity.RESULT_OK}")
            if (result.resultCode == Activity.RESULT_OK) {
                Log.i("Test", "Inside 1")
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                firebaseAuth.fetchSignInMethodsForEmail(task.result.email.toString())
                    .addOnCompleteListener { work ->
                        if (work.isSuccessful) {
                            Log.i("Test", "Inside 2")
                            val res = work.result?.signInMethods
                            if (res.isNullOrEmpty())
                                handleResults(task, true)
                            else
                                handleResults(task, false)
                        } else {
//                            binding.progressBar.visibility = View.GONE
                        }
                    }
            } else {
//                binding.progressBar.visibility = View.GONE
            }
        }

    private fun handleResults(task: Task<GoogleSignInAccount>, saveToDB: Boolean) {
        if (task.isSuccessful) {
            val account: GoogleSignInAccount? = task.result
            account?.let {
                updateUI(it, saveToDB)
            }
        } else {
//            binding.progressBar.visibility = View.GONE
        }
    }

    private fun updateUI(account: GoogleSignInAccount, saveToDB: Boolean) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                Log.i("Test", "Inside")
                if (saveToDB)
                    findNavController().navigate(UserAuthenticationDirections.actionUserAuthenticationToUserSelection())
                else {
                    val currentUser = firebaseAuth.currentUser?.uid
                    val user = FirebaseFirestore.getInstance().collection("Farmers")
                        .document(currentUser.toString())
                    user.get().addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val doc = task.result
                            if (doc != null) {
                                if (doc.exists()) {
                                    Log.d("TAG", "Document already exists.")
                                    findNavController().navigate(UserAuthenticationDirections.actionUserAuthenticationToFarmerHomeScreen())
                                } else {
                                    findNavController().navigate(UserAuthenticationDirections.actionUserAuthenticationToBuyerHomeScreen())
                                }
                            } else {
                                Log.d("TAG", "Error: ", task.exception)
                            }
                        }
                    }
                }
            } else {
//                toast("Some error occurred")
            }
        }
    }


}