package com.example.surendar_5541.ssbbanksystemdemoapplication

import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.*

class MiniStatementFragmentAdapter(private val statements: MutableList<Statement>) : RecyclerView.Adapter<MiniStatementFragmentAdapter.ViewHolder>() {

    inner class ViewHolder(var layout: View) : RecyclerView.ViewHolder(layout) {
        var dateTextView: TextView = layout.findViewById<View>(R.id.date_text_view) as TextView
        var amountTextView: TextView = layout.findViewById<View>(R.id.amount_text_view) as TextView
        var referenceTextView: TextView = layout.findViewById<View>(R.id.ref_text_view) as TextView
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent?.context)
        val v = inflater.inflate(R.layout.transaction_statement_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val statement = statements[position]
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
        holder.dateTextView.text = sdf.format(statement.date)
        holder.referenceTextView.text = statement.referenceNumber
        if(statement.amount[statement.amount.length-2] == 'D') {
            holder.amountTextView.text = statement.amount
            holder.amountTextView.setTextColor(Color.RED)
        }else {
            holder.amountTextView.text = statement.amount
            holder.amountTextView.setTextColor(Color.parseColor("#4CAF50"))
        }

        holder.dateTextView.setOnClickListener {

        }
    }

    override fun getItemCount(): Int = statements.size

}

