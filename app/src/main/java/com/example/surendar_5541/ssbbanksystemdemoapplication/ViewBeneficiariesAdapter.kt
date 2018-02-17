package com.example.surendar_5541.ssbbanksystemdemoapplication

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class ViewBeneficiariesAdapter(private val beneficiaries: MutableList<Pair<String,Number>>) : RecyclerView.Adapter<ViewBeneficiariesAdapter.ViewHolder>(){

   // private var onItemClickListener: OnItemClickListener? = null

    /*fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }
 */
    var vhCount: Int = 0
    inner class ViewHolder(var layout: View) : RecyclerView.ViewHolder(layout) {
        var beneficiaryNameTextView: TextView = layout.findViewById<View>(R.id.beneficiary_name_text_view) as TextView
        var beneficiaryAccountNumberTextView: TextView = layout.findViewById<View>(R.id.beneficiary_account_number_text_view) as TextView
       // var beneficiaryLayout: LinearLayout = layout.findViewById<View>(R.id.beneficiary_item_layout) as LinearLayout
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent?.context)
        val v = inflater.inflate(R.layout.view_beneficiaries_list_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val beneficiary = beneficiaries[position]
        holder.beneficiaryNameTextView.text = beneficiary.first
        holder.beneficiaryAccountNumberTextView.text = beneficiary.second.toString()
       // val accountNumber = beneficiary.second.toString()

       /* val listener = View.OnClickListener { onItemClickListener?.onItemClick(accountNumber) }
        holder.beneficiaryNameTextView.setOnClickListener(listener) */
        /*holder.beneficiaryAccountNumberTextView.setOnClickListener(listener)
        holder.beneficiaryLayout.setOnClickListener(listener) */
    }

    override fun getItemCount(): Int = beneficiaries.size

}
