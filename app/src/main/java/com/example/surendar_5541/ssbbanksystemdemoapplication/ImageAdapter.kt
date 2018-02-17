package com.example.surendar_5541.ssbbanksystemdemoapplication

import android.view.ViewGroup
import android.widget.BaseAdapter
import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.view.LayoutInflater

class ImageAdapter(private val mContext: Context) : BaseAdapter() {

    override fun getCount(): Int = mThumbIds.size

    override fun getItem(position: Int): Any? = null

    override fun getItemId(position: Int): Long = 0


   override fun getView(position:Int, convertView:View?, parent:ViewGroup):View {
        val grid:View
        val inflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        if (convertView == null)
        {
            grid = inflater.inflate(R.layout.grid_single, parent,false)
            val textView = grid.findViewById<View>(R.id.grid_text) as TextView
            val imageView = grid.findViewById<View>(R.id.grid_image) as ImageView
            textView.text = mTextIds[position]
            imageView.setImageResource(mThumbIds[position])
        }
        else
        {
            return convertView
        }

        return grid
    }

    private val mThumbIds = arrayOf(R.drawable.accounts_2,R.drawable.funds_transfer_2,R.drawable.bill_payments_1,R.drawable.cards_1,R.drawable.requests_1,R.drawable.complaint_1)
    private val mTextIds = arrayOf("My Accounts","Funds Transfer","Bill Payments","Cards","Requests","Complaints")
}