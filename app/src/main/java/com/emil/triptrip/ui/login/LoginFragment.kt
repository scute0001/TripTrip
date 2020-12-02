package com.emil.triptrip.ui.login

import android.app.Activity
import android.content.Intent
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.emil.triptrip.MainActivityViewModel
import com.emil.triptrip.R
import com.emil.triptrip.database.User
import com.emil.triptrip.databinding.LoginFragmentBinding
import com.emil.triptrip.ui.tripdetail.TripDetailFragmentArgs
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.rpc.context.AttributeContext

class LoginFragment : Fragment() {

    private lateinit var viewModel: LoginViewModel
    private val GOOGLE_SIGN_IN = 9999
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = LoginFragmentBinding.inflate(inflater, container, false)

        auth = Firebase.auth

        binding.signInButton.apply {
            setSize(SignInButton.SIZE_WIDE)
        }


        // sign in
        binding.signInButton.setOnClickListener {
            signIn()
        }


        // navi to mytrip
        binding.button.setOnClickListener {
            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToMyTripsFragment())
        }



        return binding.root
    }

    // get google login result
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GOOGLE_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                Log.d("TAG", "firebaseAuthWithGoogle:" + account.getId());
                account?.idToken?.let { firebaseAuthWithGoogle(it) }

            } catch (e: ApiException) {
                Log.d("TAG", "Google sign in failed", e)
            }
        }

    }

    // after google logon success, To Firebase Authentication.
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener { task->
            if (task.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                Log.d("TAG", "signInWithCredential:success")
                val user = auth.currentUser
                val userData = User(
                    id = user?.uid,
                    name = user?.displayName,
                    photoUri = user?.photoUrl.toString(),
                    email = user?.email
                )
                UserManager.userToken = user?.uid
                UserManager._user.value = userData

                activity?.let {
                    ViewModelProvider(it).get(MainActivityViewModel::class.java).apply {
                        _user.value = userData
                    }
                }

                Log.d("TAG", "${user?.displayName}")
                Log.d("TAG", "${user?.photoUrl}")
                Log.d("TAG", "${user?.email}")
                Log.d("TAG", "${user?.phoneNumber}")
                Log.d("TAG", "${user?.uid}")
                Log.i("TAG","UserManager ${UserManager.user.value?.photoUri}")
            } else {
                Toast.makeText(requireContext(), "Authentication Failed.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun signIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        val mGoogleSignInClient = GoogleSignIn.getClient(requireContext(), gso)
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, GOOGLE_SIGN_IN)
    }

}