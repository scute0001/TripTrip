package com.emil.triptrip.ui.chatroom

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.emil.triptrip.TripTripApplication
import com.emil.triptrip.databinding.ChatRoomFragmentBinding

class ChatRoomFragment : Fragment() {

    private lateinit var viewModel: ChatRoomViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = ChatRoomFragmentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this

        val tripId = ChatRoomFragmentArgs.fromBundle(requireArguments()).tripId
        val app = requireNotNull(activity).application
        val repository = (requireContext().applicationContext as TripTripApplication).repository
        val viewModelFactory = ChatRoomViewModelFactory(app, repository, tripId)
        viewModel = ViewModelProvider(this, viewModelFactory).get(ChatRoomViewModel::class.java)

        //set data and create to xml
        binding.viewModel = viewModel

        // setup recyclerView adapter
        val adapter = ChatAdapter(viewModel)
        binding.recyclerMassage.adapter = adapter

        // check message and sent data to firebase
        viewModel.message.observe(viewLifecycleOwner, Observer { message ->
            if (message.message == "" || message.message == null) {
                Toast.makeText(requireContext(), "輸入的訊息是空的喔", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.sentMessageToFirebase(message)
            }
        })

        // massages live
        viewModel.liveMessages.observe(viewLifecycleOwner, Observer { messages ->
            if (messages.isNotEmpty()) {
                adapter.submitList(messages)
                binding.recyclerMassage.smoothScrollToPosition(messages.size)
                binding.textNoMessage.visibility = View.GONE
            } else {
                binding.textNoMessage.visibility = View.VISIBLE
            }
        })

        // sent massage
        binding.btnSentComment.setOnClickListener {
            viewModel.setMessage()
        }

        return binding.root
    }
}