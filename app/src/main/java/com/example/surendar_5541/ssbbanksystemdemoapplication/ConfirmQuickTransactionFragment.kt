package com.example.surendar_5541.ssbbanksystemdemoapplication

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import java.util.*

class ConfirmQuickTransactionFragment : Fragment() {
    private var getActivityListener: GetHomeActivity? = null

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
        return inflater.inflate(R.layout.confirm_quick_transaction, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        val db = DataBaseHandler(activity)
        super.onActivityCreated(savedInstanceState)

        val bundle: Bundle = this.arguments

        if (!bundle.isEmpty) {
            val name = bundle.getString("name")
            val accountNumber = bundle.getString("accountNumber")
            val amount = bundle.getFloat("amount")
            val purpose = bundle.getString("purpose")
            val nameTextView = view.findViewById<View>(R.id.quick_name_text_view) as TextView
            val accountNumberTextView = view.findViewById<View>(R.id.quick_account_number_text_view) as TextView
            val amountTextView = view.findViewById<View>(R.id.quick_amount_text_view) as TextView
            val purposeTextView = view.findViewById<View>(R.id.quick_purpose_text_view) as TextView

            nameTextView.text = name
            accountNumberTextView.text = accountNumber
            amountTextView.text = amount.toString()
            purposeTextView.text = purpose

            val editButton = view.findViewById<View>(R.id.quick_edit_button_view) as Button
            val confirmButton = view.findViewById<View>(R.id.quick_confirm_button_view) as Button

            editButton.setOnClickListener {
                activity.onBackPressed()
            }

            confirmButton.setOnClickListener {
                val senderAcc = db.getAccountNumber(db.getCustomerId(activity.getSharedPreferences("loginStatus", 0).getString("username", "null")))

                if (!db.isAccountNumberExists(accountNumber.toLong())) {
                    showErrorMessageAndGoBack("Account Number does not exists please try again")
                } else if (senderAcc == accountNumber.toLong()) {
                    showErrorMessageAndGoBack("Account Number same as sender,please enter different account number")
                } else if (amount > db.getAccountBalance(senderAcc)) {
                    showErrorMessageAndGoBack("Account Balance is not sufficient to transfer")
                } else {
                    val now = Calendar.getInstance()
                    val date = now.timeInMillis
                    val mSeconds = date % 100000
                    val res = db.addTransaction(Transaction(date, senderAcc, accountNumber.toLong(), "Intra Bank", purpose, "IB17Q$mSeconds", amount))
                    val fragment = PaymentReceiptFragment()

                    val bundleReceipt = Bundle()
                    bundleReceipt.putString("name", name)
                    bundleReceipt.putLong("accountNumber", accountNumber.toLong())
                    bundleReceipt.putString("transactionMode", "Intra Bank")
                    bundleReceipt.putLong("accountNumber", accountNumber.toLong())
                    bundleReceipt.putFloat("amount", amount)

                    if (res.lastPathSegment.toInt() > 0) {
                        db.updateBalance(accountNumber.toLong(), amount)                      /* Updating balance after transaction */
                        val resUpdate = db.updateBalance(db.getAccountNumber(getActivityListener?.getCustomerId()), -amount)

                        if (resUpdate > 0) {
                            bundleReceipt.putString("transactionStatus", "Success")
                            val referenceNumber = db.getTransactionWithUri(res).referenceNumber.substring(0, 9)
                            bundleReceipt.putString("referenceNumber", referenceNumber)
                            fragment.arguments = bundleReceipt
                            getActivityListener?.paymentReceipt(R.id.fragment_to_replace_grid, fragment, "Payment Receipt")
                        }
                    } else {
                        bundleReceipt.putString("transactionStatus", "failure")
                        fragment.arguments = bundleReceipt
                        getActivityListener?.paymentReceipt(R.id.fragment_to_replace_grid, fragment, "Payment Receipt")
                    }
                }
            }
        }
    }

    private fun showErrorMessageAndGoBack(msg: String) {
        Toast.makeText(activity, msg, Toast.LENGTH_LONG).show()
        activity.onBackPressed()
    }

}