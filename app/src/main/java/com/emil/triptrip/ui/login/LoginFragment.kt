package com.emil.triptrip.ui.login

import android.content.Intent
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
import com.emil.triptrip.MainActivity
import com.emil.triptrip.MainActivityViewModel
import com.emil.triptrip.R
import com.emil.triptrip.TripTripApplication
import com.emil.triptrip.database.User
import com.emil.triptrip.databinding.LoginFragmentBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*

class LoginFragment : Fragment() {

    private lateinit var viewModel: LoginViewModel
    private val GOOGLE_SIGN_IN = 9999
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = LoginFragmentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this

        val app = requireNotNull(activity).application
        val repository = (requireContext().applicationContext as TripTripApplication).repository
        val viewModelFactory = LoginViewModelFactory(app, repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(LoginViewModel::class.java)



        // for firebase auth
        auth = Firebase.auth

        // sign in
        binding.constraintLoginLogo.setOnClickListener {
            signIn()
        }

        // get data and navigation to main page
        viewModel.userData.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                Log.i("TAG", "navigation data is $it")
                viewModel.uploadUserDataToFirebase(it)
                viewModel.setUploadUserDoneAndNavToMyTrips()
            }
        })

        viewModel.uploadUserDataNavFlag.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToLoginSuccessDialogFragment())
                viewModel.setUploadUserDoneAndNavToMyTripsFinished()
            }
        })



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

                // transData to main activity drawer
                activity?.let {
                    ViewModelProvider(it).get(MainActivityViewModel::class.java).apply {
                        _user.value = userData
                    }
                }

                // for update to firebase data
                viewModel.userData.value = userData

            } else {
                Toast.makeText(requireContext(), "Authentication Failed.", Toast.LENGTH_SHORT).show()
            }
        }
    }



    // function to google sign in API
    private fun signIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        val mGoogleSignInClient = GoogleSignIn.getClient(requireContext(), gso)
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, GOOGLE_SIGN_IN)
    }

    // cancel toolbar in this page
    override fun onStart() {
        super.onStart()
        val activity = activity as MainActivity
        activity.toolbar.visibility = View.GONE
    }

    // un-cancel toolbar when leave page
    override fun onStop() {
        super.onStop()
        val activity = activity as MainActivity
        activity.toolbar.visibility = View.VISIBLE
    }
}