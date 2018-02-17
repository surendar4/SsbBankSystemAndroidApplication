package com.example.surendar_5541.ssbbanksystemdemoapplication

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast

class AccountSummaryFragment : Fragment() {
    private var getActivityListener: GetHomeActivity? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.account_summary_fragment, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            getActivityListener = activity as GetHomeActivity
        } catch (castException: ClassCastException) {
            /** The activity does not implement the listener. */
        }

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val db = DataBaseHandler(activity)
        try {
            val customerId = getActivityListener?.getCustomerId()
            val accountNumber = db.getAccountNumber(customerId)
            val bankAccount = db.getBankAccount(accountNumber)
            val nameTextView = view.findViewById<View>(R.id.account_summary_name_text_view) as TextView
            val accountNumberTextView = view.findViewById<View>(R.id.account_number_text_view) as TextView
            val availableBalanceTextView = view.findViewById<View>(R.id.avl_bal_text_view) as TextView
            val accountTypeTextView = view.findViewById<View>(R.id.account_type_text_view) as TextView
            val availableBalance = String.format("%.2f", bankAccount.balance) + " INR"

            accountNumberTextView.text = bankAccount.accountNumber.toString()
            availableBalanceTextView.text = availableBalance
            nameTextView.text = db.getCustomer(customerId).customerName
            accountTypeTextView.text = bankAccount.accountType
        } catch (e: IllegalArgumentException) {
            Toast.makeText(activity, "Something went wrong", Toast.LENGTH_SHORT).show()
        }
    }
}