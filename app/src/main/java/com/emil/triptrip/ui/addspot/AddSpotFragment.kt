package com.emil.triptrip.ui.addspot

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.emil.triptrip.R

class AddSpotFragment : Fragment() {

    companion object {
        fun newInstance() = AddSpotFragment()
    }

    private lateinit var viewModel: AddSpotViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.add_spot_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(AddSpotViewModel::class.java)
        // TODO: Use the ViewModel
    }

}