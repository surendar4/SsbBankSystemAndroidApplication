package com.example.surendar_5541.ssbbanksystemdemoapplication

import android.content.Context
import android.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View

class RecyclerTouchListener(context: Context,clickListener: ClickListener) : RecyclerView.OnItemTouchListener {
    private val clickListener: ClickListener
    private val gestureDetector: GestureDetector

    init {
        this.clickListener = clickListener
        gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapUp(e: MotionEvent): Boolean = true

        })
    }

    override fun onInterceptTouchEvent(recyclerView: RecyclerView, e: MotionEvent): Boolean {
        val child = recyclerView.findChildViewUnder(e.x, e.y)

        if (child != null && gestureDetector.onTouchEvent(e)) {
            clickListener.onClick(child, recyclerView.getChildAdapterPosition(child))
        }

        return false
    }

    override fun onTouchEvent(recyclerView: RecyclerView, e: MotionEvent) {

    }

    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {

    }
}

interface ClickListener {
    fun onClick(view: View, position: Int)
}

