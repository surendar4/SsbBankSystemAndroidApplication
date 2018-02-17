package com.example.surendar_5541.ssbbanksystemdemoapplication

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class MyAccountOptionsAdapter(private val accountOptions: MutableList<String>) : RecyclerView.Adapter<MyAccountOptionsAdapter.ViewHolder>(){
    private var onItemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    inner class ViewHolder(var layout: View) : RecyclerView.ViewHolder(layout) {
        var accountOptionTextView: TextView = layout.findViewById<View>(R.id.account_option_textView) as TextView

    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent?.context)
        val v = inflater.inflate(R.layout.my_account_fragment_list_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val accountOption = accountOptions[position]
        holder.accountOptionTextView.text = accountOption

        val listener = View.OnClickListener { onItemClickListener?.onItemClick(accountOption) }
        holder.accountOptionTextView.setOnClickListener(listener)
    }

    override fun getItemCount(): Int = accountOptions.size

}
