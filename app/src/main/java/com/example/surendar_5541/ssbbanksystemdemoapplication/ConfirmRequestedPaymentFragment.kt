package com.example.surendar_5541.ssbbanksystemdemoapplication

import android.animation.ObjectAnimator
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import java.util.*


class ConfirmRequestedPaymentFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.confirm_requested_payment_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val db = DataBaseHandler(activity)
        val payNowButton = view?.findViewById<View>(R.id.pay_now_button_view) as Button
        val merchantNameTextView = view?.findViewById<View>(R.id.merchant_name_text_view) as TextView
        val orderNumberTextView = view?.findViewById<View>(R.id.order_number_text_view) as TextView
        val requestedAmountTextView = view?.findViewById<View>(R.id.amount_text_view) as TextView
        val fromAccountNumberTextView = view?.findViewById<View>(R.id.from_account_text_view) as TextView
        val currentBalanceTextView = view?.findViewById<View>(R.id.current_balance_text_view) as TextView
        val redirectWarningTextView = view?.findViewById<View>(R.id.redirect_message) as TextView

        val intent = activity.intent
        val accountNumber = db.getAccountNumber(db.getCustomerId(intent.extras.getString("username")))
        val bankAccount = db.getBankAccount(accountNumber)

        fromAccountNumberTextView.text = bankAccount.accountNumber.toString()
        currentBalanceTextView.text = bankAccount.balance.toString()
        var requestedAmount = "0.00"

        if (intent.hasExtra(Intent.EXTRA_TEXT)) {
            requestedAmount = intent.extras.getString("amount").toString()
            merchantNameTextView.text = intent.extras.getString("merchantName")
            orderNumberTextView.text = intent.extras.getString("orderNumber")
            requestedAmountTextView.text = requestedAmount
        }

        val responseIntent = Intent()
        if (requestedAmount.toFloat() == 0.00f || requestedAmount.toFloat() > bankAccount.balance) {
            responseIntent.putExtra("orderNumber", intent.extras.getString("orderNumber"))
            responseIntent.putExtra("amount", intent.extras.getString("amount"))
            intent.removeExtra("username")
            val redirectMessage: String
            val responseMessage: String
            val message: String

            if (requestedAmount.toFloat() == 0.00f) {
                redirectMessage = "Amount should be greater than Rs 0.00,You will be redirected Shopping application withing 10 seconds"
                message = "Amount is Rs.0.00 ,So This transaction is not allowed!!"
                responseMessage = "Amount Should be greaterThan 0.00"
            } else {
                message = "InSufficient Amount!! This Transaction Not Allowed!!"
                redirectMessage = "Insufficient Amount!!, You will be redirected Shopping application withing 10 seconds"
                responseMessage = "Failed Due To Insufficient Balance"
            }

            responseIntent.putExtra("responseMessage", responseMessage)
            (activity as MainActivity).setResult(3, responseIntent)
            redirectWarningTextView.text = redirectMessage
            alertMessageToGoBack(message)
            payNowButton.visibility = View.GONE
            timeOutToRedirect()

        }

        payNowButton.setOnClickListener {

            val merchantAccountNumber = activity.intent.extras.getLong("merchantAccountNumber")
            val now = Calendar.getInstance()
            val date = now.timeInMillis
            val mSeconds = date % 100000

            val res = db.addTransaction(Transaction(date, bankAccount.accountNumber, merchantAccountNumber, "By App", "Shopping", "SA17S$mSeconds", requestedAmount.toFloat()))
            db.updateBalance(bankAccount.accountNumber, 0 - requestedAmount.toFloat())
            db.updateBalance(merchantAccountNumber, requestedAmount.toFloat())
            responseIntent.putExtra("responseMessage", "Transaction Success")
            responseIntent.putExtra("customerAccountNumber", bankAccount.accountNumber)

            val referenceNumber = db.getTransactionWithUri(res).referenceNumber
            responseIntent.putExtra("referenceNumber", referenceNumber)
            responseIntent.putExtra("orderNumber", intent.extras.getString("orderNumber"))
            responseIntent.putExtra("amount", intent.extras.getString("amount"))
            (activity as MainActivity).setResult(2, responseIntent)
            intent.removeExtra("username")
            activity.finish()
        }
    }

    private fun alertMessageToGoBack(message: String) {
        val alertDialogBuilder = AlertDialog.Builder(activity)
        alertDialogBuilder.setMessage(message)
        alertDialogBuilder.setPositiveButton("Ok", object : DialogInterface.OnClickListener {
            override fun onClick(arg0: DialogInterface, arg1: Int) {
                // activity.finish()
            }
        })

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun timeOutToRedirect() {
        val progressBar = view?.findViewById<View>(R.id.redirect_progress_bar) as ProgressBar
        val progressBarTexView = view?.findViewById<View>(R.id.progress_bar_text_view) as TextView

        progressBar.visibility = View.VISIBLE
        progressBarTexView.visibility = View.VISIBLE

        val animation = ObjectAnimator.ofInt(progressBar, "progress", 0, 100)
        animation.duration = 10000
        animation.startDelay = 0
        animation.interpolator = LinearInterpolator()
        animation.start()

        val handler = Handler()
        handler.postDelayed({
            /* 10 secs */
            activity?.finish()
        }, 10000)
    }

    override fun onResume() {
        super.onResume()
        try {
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LOCKED
        } catch (e: Exception) {

        }
    }

    override fun onPause() {
        super.onPause()
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR
    }
}