package com.teamdefine.farmapp.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.teamdefine.farmapp.databinding.FragmentSplashBinding

class SplashFragment : Fragment() {
    private lateinit var binding: FragmentSplashBinding
    private lateinit var firebaseAuth: FirebaseAuth //firebase auth
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSplashBinding.inflate(inflater, container, false)
        firebaseAuth = FirebaseAuth.getInstance() //getting instance

//        val loggedIn = HomePageFragment().checkUser(firebaseAuth)
//        if (loggedIn) {
//            Handler().postDelayed({
//                view?.post {
////                    findNavController().navigate(
////                        SplashScreenFragmentDirections.actionSplashScreenFragmentToHomePageFragment()
////                    )
//                }
//            }, 3000)
//        } else {
//            Handler().postDelayed({
//                view?.post {
////                    findNavController().navigate(SplashScreenFragmentDirections.actionSplashScreenFragmentToRegisterFragment())
//                }
//            }, 3000)
//        }

        return binding.root
    }
}