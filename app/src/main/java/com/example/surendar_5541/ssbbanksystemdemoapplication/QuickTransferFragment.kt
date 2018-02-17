package com.example.surendar_5541.ssbbanksystemdemoapplication

import android.app.Fragment
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView

class QuickTransferFragment : Fragment() {
    private var getActivityListener:GetHomeActivity? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.quick_transfer_fragment, container, false)
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
        val transferButton = view.findViewById<View>(R.id.quick_transfer_buttonView) as Button
        val nameEditText = view.findViewById<View>(R.id.quick_name_editText) as EditText
        val accountNumberEditText = view.findViewById<View>(R.id.quick_account_number_editText) as EditText
        val confirmAccountNumberEditText = view.findViewById<View>(R.id.quick_confirm_account_number_editText) as EditText
        val amountEditText = view.findViewById<View>(R.id.quick_amount_editText) as EditText
        val purposeSpinner = view.findViewById<View>(R.id.quick_purpose_spinner) as Spinner
        val fromAccountNumberTextView = view.findViewById<View>(R.id.quick_from_account_textView) as TextView
        val avlBalanceTextView = view.findViewById<View>(R.id.quick_available_balance_textView) as TextView

        val fromAccountNumber = db.getAccountNumber(getActivityListener?.getCustomerId())
        val avlBalance = db.getAccountBalance(fromAccountNumber).toString()
        avlBalanceTextView.text = avlBalance
        fromAccountNumberTextView.text = fromAccountNumber.toString()
        nameEditText.hint = "Beneficiary Name"
        accountNumberEditText.hint = "Account Number"
        confirmAccountNumberEditText.hint = "Account Number"
        amountEditText.hint = "Amount"

        transferButton.setOnClickListener {

            try {
                val inputMethodManager = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(activity.currentFocus?.windowToken, 0)
            } catch (e: Exception) {

            }

            val name = nameEditText.text.toString()
            val accountNumber = accountNumberEditText.text.toString()
            val confirmAccountNumber = confirmAccountNumberEditText.text.toString()
            val amount = amountEditText.text.toString()
            val purpose = purposeSpinner.selectedItem.toString()

            if(name.isEmpty()) {
                nameEditText.error = "Please Enter Name"
            } else {
                val nameArray = name.toCharArray()
                if (nameArray.any { it.isDigit() }) {
                    nameEditText.error = "Name should not contain digits"
                    return@setOnClickListener
                }
            }

            if(accountNumber.isEmpty() || accountNumber.length != 11) {
                accountNumberEditText.error = "Please enter valid account number"
            }

            if(confirmAccountNumber.isEmpty() || confirmAccountNumber.length != 11) {
                confirmAccountNumberEditText.error = "Please confirm above account Number"
            }

            if(amount.isEmpty() || amount.toFloat() <0) {
                amountEditText.error = "Please enter valid amount"
            } else if(amount.toFloat() > avlBalance.toFloat()) {
                amountEditText.error = "InSufficient Amount!! "
                return@setOnClickListener
            } else if(amount.toFloat() == 0.00f) {
                amountEditText.error = "Amount Should be GreaterThan Zero 0"
                return@setOnClickListener
            }

            if(accountNumber != confirmAccountNumber) {
                confirmAccountNumberEditText.error = "Account Number not Matching,Please enter same as Account Number"
                return@setOnClickListener
            }

            if(!name.isEmpty() && !accountNumber.isEmpty() && !confirmAccountNumber.isEmpty() && !amount.isEmpty() && !purpose.isEmpty() && accountNumber == confirmAccountNumber && accountNumber.length==11) {
                val bundle = Bundle()
                val fragment = ConfirmQuickTransactionFragment()

                bundle.putString("name",name)
                bundle.putString("accountNumber",accountNumber)
                bundle.putFloat("amount",amount.toFloat())
                bundle.putString("purpose",purpose)

                fragment.arguments = bundle
                getActivityListener?.addFragmentToHome(R.id.fragment_to_replace_grid,fragment,"Confirm Quick Transaction")
            }
        }
    }

    override fun onResume() {
        super.onResume()
        getActivityListener?.setSubTitle("Quick Transfer")
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)

        if(newConfig != null) {
            if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                val nameEditText = view.findViewById<View>(R.id.quick_name_editText) as EditText
                val accountNumberEditText = view.findViewById<View>(R.id.quick_account_number_editText) as EditText
                val confirmAccountNumberEditText = view.findViewById<View>(R.id.quick_confirm_account_number_editText) as EditText
                val amountEditText = view.findViewById<View>(R.id.quick_amount_editText) as EditText

                nameEditText.hint = "Beneficiary Name"
                accountNumberEditText.hint = "Beneficiary Account Number"
                confirmAccountNumberEditText.hint = "Confirm Beneficiary Account Number"
                amountEditText.hint = "Amount"
            } else if(newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                val nameEditText = view.findViewById<View>(R.id.quick_name_editText) as EditText
                val accountNumberEditText = view.findViewById<View>(R.id.quick_account_number_editText) as EditText
                val confirmAccountNumberEditText = view.findViewById<View>(R.id.quick_confirm_account_number_editText) as EditText
                val amountEditText = view.findViewById<View>(R.id.quick_amount_editText) as EditText

                nameEditText.hint = "Beneficiary Name"
                accountNumberEditText.hint = "Account Number"
                confirmAccountNumberEditText.hint = "Account Number"
                amountEditText.hint = "Amount"
            }
        }
    }
}