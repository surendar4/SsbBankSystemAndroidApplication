package com.example.surendar_5541.ssbbanksystemdemoapplication

import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.*

class AccountStatementAdapter(private val statements: MutableList<Statement>) : RecyclerView.Adapter<AccountStatementAdapter.ViewHolder>() {

    inner class ViewHolder(var layout: View) : RecyclerView.ViewHolder(layout) {
        var dateTextView: TextView = layout.findViewById<View>(R.id.date_text_view) as TextView
        var amountTextView: TextView = layout.findViewById<View>(R.id.amount_text_view) as TextView
        var referenceTextView: TextView = layout.findViewById<View>(R.id.ref_text_view) as TextView
        var balanceAmountTextView: TextView = layout.findViewById<View>(R.id.balance_after_transaction) as TextView
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent?.context)
        val v = inflater.inflate(R.layout.account_statement_transaction_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val statement = statements[position]
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
        holder.dateTextView.text = sdf.format(statement.date)
        holder.referenceTextView.text = statement.referenceNumber
        holder.amountTextView.text = statement.amount
        holder.balanceAmountTextView.text = statement.balance.toString()

        if (statement.amount[statement.amount.length - 2] == 'D') {     /* Adding TextColor according to Transaction Type Credit/Debit */
            holder.amountTextView.setTextColor(Color.RED)
        } else {
            holder.amountTextView.setTextColor(Color.parseColor("#4CAF50"))
        }

    }

    override fun getItemCount(): Int = statements.size
}

