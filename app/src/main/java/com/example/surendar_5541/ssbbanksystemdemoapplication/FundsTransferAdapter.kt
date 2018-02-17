package com.example.surendar_5541.ssbbanksystemdemoapplication

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView


class FundsTransferAdapter(private val options: MutableList<String>) : RecyclerView.Adapter<FundsTransferAdapter.ViewHolder>() {

    private var onItemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    inner class ViewHolder(var layout: View) : RecyclerView.ViewHolder(layout) {
        var optionName: TextView = layout.findViewById<View>(R.id.option_text_view) as TextView
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent?.context)
        val v = inflater.inflate(R.layout.funds_tranfer_fragment_list_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val option = options[position]
        holder.optionName.text = option

        val listener = View.OnClickListener { onItemClickListener?.onItemClick(option) }
        holder.optionName.setOnClickListener(listener)
    }

    override fun getItemCount(): Int = options.size
}
