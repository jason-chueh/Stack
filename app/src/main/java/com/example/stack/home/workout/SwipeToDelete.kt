package com.example.stack.home.workout

import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.PorterDuffXfermode
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.stack.R


abstract class SwipeToDeleteCallback internal constructor(private var mContext: Context) :
    ItemTouchHelper.Callback() {
    private val mClearPaint: Paint = Paint()
    private val mBackground: ColorDrawable = ColorDrawable()
    private val backgroundColor: Int = Color.parseColor("#006874")
    private val deleteDrawable: Drawable?


    init {
        mClearPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        deleteDrawable = ContextCompat.getDrawable(mContext, R.drawable.trash)
    }

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        return makeMovementFlags( ItemTouchHelper.UP or ItemTouchHelper.DOWN, ItemTouchHelper.LEFT)
    }

//    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
//
//    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        viewHolder1: RecyclerView.ViewHolder
    ): Boolean {
        return false
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
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        val itemView = viewHolder.itemView
        val itemHeight = itemView.height
        val isCancelled = dX == 0f && !isCurrentlyActive
        if (isCancelled) {
            clearCanvas(
                c,
                itemView.right + dX,
                itemView.top.toFloat(),
                itemView.right.toFloat(),
                itemView.bottom.toFloat()
            )
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            return
        }
        mBackground.color = backgroundColor
        mBackground.setBounds(
            itemView.right + dX.toInt(),
            itemView.top,
            itemView.right,
            itemView.bottom
        )
        mBackground.draw(c)

        val deleteIconTop = itemView.top + (itemHeight - 24.dpToPx()) / 2 // Vertically centered
        val deleteIconMargin = 24.dpToPx() // Right margin of 24dp
        val deleteIconRight = itemView.right - deleteIconMargin
        val deleteIconLeft = deleteIconRight - 24.dpToPx() // Adjust the width

        val deleteIconBottom = deleteIconTop + 24.dpToPx() // Set the height
        deleteDrawable!!.setBounds(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom)

        val color = ContextCompat.getColor(mContext, R.color.onPrimary)
        deleteDrawable.colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN)

        deleteDrawable.draw(c)
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    // Extension function to convert dp to pixels
    private fun Int.dpToPx(): Int {
        return (this * Resources.getSystem().displayMetrics.density).toInt()
    }

    private fun clearCanvas(c: Canvas, left: Float, top: Float, right: Float, bottom: Float) {
        c.drawRect(left, top, right, bottom, mClearPaint)
    }

    override fun getSwipeThreshold( viewHolder: RecyclerView.ViewHolder): Float {
        return 0.7f
    }
}