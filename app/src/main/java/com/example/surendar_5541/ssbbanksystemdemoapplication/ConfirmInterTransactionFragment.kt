package com.example.surendar_5541.ssbbanksystemdemoapplication

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import java.util.*

class ConfirmInterTransactionFragment : Fragment() {
    private var getActivityListener:GetHomeActivity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            getActivityListener = activity as GetHomeActivity
        } catch (castException: ClassCastException) {
            /** The activity does not implement the listener. */
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.confirm_inter_bank_transacation, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val db = DataBaseHandler(activity)
        val customerId = getActivityListener?.getCustomerId()
        val bundle: Bundle = this.arguments

        if (!bundle.isEmpty) {
            val accountNumber = bundle.getLong("accountNumber")
            val amount = bundle.getFloat("amount")
            val purpose = bundle.getString("purpose")
            val transactionMode = bundle.getString("transactionMode")
            val beneficiaries = db.getBeneficiaries(customerId)
            val name = beneficiaries.filter { it.accountNumber == accountNumber }.first().name

            val nameTextView = view.findViewById<View>(R.id.inter_name_text_view) as TextView
            val accountNumberTextView = view.findViewById<View>(R.id.inter_account_number_text_view) as TextView
            val ifscTextView = view.findViewById<View>(R.id.inter_ifsc_text_view) as TextView
            val amountTextView = view.findViewById<View>(R.id.inter_amount_text_view) as TextView
            val purposeTextView = view.findViewById<View>(R.id.inter_purpose_text_view) as TextView
            val editButtonView = view.findViewById<View>(R.id.inter_edit_button_view) as Button
            val confirmButtonView = view.findViewById<View>(R.id.inter_confirm_button_view) as Button

            nameTextView.text = name
            accountNumberTextView.text = accountNumber.toString()
            ifscTextView.text = beneficiaries.filter { it.accountNumber == accountNumber }.first().ifscCode
            amountTextView.text = amount.toString()
            purposeTextView.text = purpose

            editButtonView.setOnClickListener {
                activity.onBackPressed()
            }

            confirmButtonView.setOnClickListener {
                val now = Calendar.getInstance()
                val date = now.timeInMillis
                val mSeconds = date % 100000
                val res = db.addTransaction(Transaction(date, db.getAccountNumber(customerId), accountNumber, transactionMode, purpose, "OB17I$mSeconds@$name/$accountNumber", amount))

                if (res.lastPathSegment.toInt() > 0) {
                    val fromAccount = db.getAccountNumber(customerId)
                    val resUpdate = db.updateBalance(fromAccount, -amount)

                    if (resUpdate > 0) {
                        val fragment = PaymentReceiptFragment()
                        val bundleReceipt = Bundle()

                        bundleReceipt.putString("transactionStatus","Success")
                        bundleReceipt.putString("name",name)
                        bundleReceipt.putLong("accountNumber",accountNumber)
                        bundleReceipt.putString("transactionMode",transactionMode)
                        bundleReceipt.putString("referenceNumber",db.getTransactionWithUri(res).referenceNumber.substring(0,9))
                        bundleReceipt.putFloat("amount",amount)

                        fragment.arguments = bundleReceipt
                        getActivityListener?.paymentReceipt(R.id.fragment_to_replace_grid,fragment,"Payment Receipt") //Prints the receipt
                    }
                } else {
                    val fragment = PaymentReceiptFragment()
                    val bundleReceipt = Bundle()

                    bundleReceipt.putString("transactionStatus","failure")
                    bundleReceipt.putString("name",name)
                    bundleReceipt.putLong("accountNumber",accountNumber)
                    bundleReceipt.putString("transactionMode",transactionMode)
                    bundleReceipt.putFloat("amount",amount)

                    fragment.arguments = bundleReceipt
                   getActivityListener?.paymentReceipt(R.id.fragment_to_replace_grid,fragment,"Payment Receipt")
                }
            }
        }
    }
}