package com.emil.triptrip.ui.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.DialogFragment
import com.emil.triptrip.R
import com.emil.triptrip.database.User
import com.emil.triptrip.ui.modify.ModifyTripViewModel


class ModifyUsersDialog(val userList: List<User>, val viewModel: ModifyTripViewModel) : DialogFragment() {

    override fun onStart() {
        super.onStart()
        dialog?.setCanceledOnTouchOutside(false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {

            val currentList = viewModel._currentUsersData.value as MutableList
            val arrayList = userList.map { it?.email }.toTypedArray()
            val selectedItems = ArrayList<Int>() // Where we track the selected items
            val builder = AlertDialog.Builder(it)

            // Set the dialog title
            builder.setTitle(R.string.add_attend_user)
                // Specify the list array, the items to be selected by default (null for none),
                // and the listener through which to receive callbacks when items are selected
                .setMultiChoiceItems(arrayList, null,
                    DialogInterface.OnMultiChoiceClickListener { dialog, which, isChecked ->
                        if (isChecked) {
                            // If the user checked the item, add it to the selected items
                            selectedItems.add(which)
                        } else if (selectedItems.contains(which)) {
                            // Else, if the item is already in the array, remove it
                            selectedItems.remove(Integer.valueOf(which))
                        }
                    })
                // Set the action buttons
                .setPositiveButton(
                    R.string.ok,
                    DialogInterface.OnClickListener { dialog, id ->
                        // User clicked OK, so save the selectedItems results somewhere
                        // or return them to the component that opened the dialog
                        val selectUsers = mutableListOf<User>()
                        for (item in selectedItems){
                            selectUsers.add(userList[item])
                        }
                        Log.d("DATADATA", "$selectUsers")
                        currentList.addAll(selectUsers)

                        viewModel._currentUsersData.value = currentList.toSet().toList()
                    })
                .setNegativeButton(R.string.cancel,
                    DialogInterface.OnClickListener { dialog, id ->
                    })

            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}