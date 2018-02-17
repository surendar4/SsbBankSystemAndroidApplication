package com.example.surendar_5541.ssbbanksystemdemoapplication

import android.app.Fragment
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class PaymentReceiptFragment : Fragment() {
   private var getHomeActivityListener: GetHomeActivity? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)
       return inflater.inflate(R.layout.payment_receipt_fragment, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try{
            getHomeActivityListener = activity as GetHomeActivity
        }catch (castException: ClassCastException) {

        }
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val bundle = arguments
        if(!bundle.isEmpty) {
            val transactionStatusTextView = view.findViewById<View>(R.id.transaction_status_text_view) as TextView
            val nameTextView = view.findViewById<View>(R.id.ben_full_name_text_view) as TextView
            val accountNumberTextView = view.findViewById<View>(R.id.ben_account_number_text_view) as TextView
            val transactionModeTextView = view.findViewById<View>(R.id.transaction_type_text_view) as TextView
            val referenceNumberTextView = view.findViewById<View>(R.id.receipt_reference_text_view) as TextView
            val amountTextView = view.findViewById<View>(R.id.amount_text_view) as TextView

            nameTextView.text = bundle.getString("name")
            accountNumberTextView.text = bundle.getLong("accountNumber").toString()
            transactionModeTextView.text = bundle.getString("transactionMode")
            amountTextView.text = bundle.getFloat("amount").toString()
            val transactionStatus = bundle.getString("transactionStatus")
            transactionStatusTextView.text = transactionStatus

            if(transactionStatus == "Success") {
                transactionStatusTextView.setTextColor(Color.parseColor("#4CAF50"))
                val refLayout = view.findViewById<View>(R.id.ref_layout) as LinearLayout
                refLayout.visibility = View.VISIBLE
                referenceNumberTextView.text = bundle.getString("referenceNumber")

            } else {
                transactionStatusTextView.setTextColor(Color.RED)
                val refLayout = view.findViewById<View>(R.id.ref_layout) as LinearLayout
                refLayout.visibility = View.GONE
            }

            val homeButton = view.findViewById<View>(R.id.home_button) as Button
            homeButton.setOnClickListener {
                fragmentManager.popBackStack(null,android.app.FragmentManager.POP_BACK_STACK_INCLUSIVE)
            }
        }
    }


}