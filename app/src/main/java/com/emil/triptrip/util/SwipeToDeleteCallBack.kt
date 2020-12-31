package com.emil.triptrip.util

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.emil.triptrip.R
import com.emil.triptrip.ui.notification.NotificationAdapter

abstract class SwipeToDeleteCallBack(
    private val context: Context,
    private val adapter: NotificationAdapter
): ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

    private val background = ColorDrawable()
    private val icon = ContextCompat.getDrawable(context, R.drawable.ic_baseline_delete_forever_24)


    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }


    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.adapterPosition
        adapter.notifyItemRemoved(position)
        adapter.deleteNotification(position)
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        val itemView = viewHolder.itemView
        val itemHeight = itemView.bottom - itemView.top

        // Draw Delete Background
        background.color = context.getColor(R.color.middle_blue)
        background.setBounds(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)
        background.draw(c)

        // Calculate position of delete icon
        val iconTop = itemView.top + (itemHeight - icon!!.intrinsicHeight) / 2
        val iconMargin = (itemHeight - icon!!.intrinsicHeight) / 2
        val iconLeft = itemView.right - iconMargin - icon!!.intrinsicWidth
        val iconRight = itemView.right - iconMargin
        val iconBottom = iconTop + icon!!.intrinsicHeight

        // Draw the delete icon
        icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
        icon.draw(c)


        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }
}