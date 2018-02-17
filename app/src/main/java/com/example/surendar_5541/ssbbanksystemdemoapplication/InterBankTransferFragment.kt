package com.example.surendar_5541.ssbbanksystemdemoapplication

import android.app.AlertDialog
import android.app.Fragment
import android.content.Context
import android.content.DialogInterface
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener


class InterBankTransferFragment : Fragment(), OnItemSelectedListener {
    private var getActivityListener: GetHomeActivity? = null
    private var selectedItem: String? = null

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
        return inflater.inflate(R.layout.inter_bank_transfer_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val db = DataBaseHandler(activity)
        val customerId = getActivityListener?.getCustomerId()
        val fromAccountNumber = db.getAccountNumber(customerId)
        val avlBalance = db.getAccountBalance(fromAccountNumber)
        val beneficiaries = db.getBeneficiaries(customerId)

        val fromAccountNumberTextView = view.findViewById<View>(R.id.from_account_textView) as TextView
        val avlBalanceTextView = view.findViewById<View>(R.id.available_balance_textView) as TextView
        fromAccountNumberTextView.text = fromAccountNumber.toString()
        avlBalanceTextView.text = avlBalance.toString()

        val spinnerView = view.findViewById<View>(R.id.select_beneficiary_spinner) as Spinner
        val beneficiariesNameArray = mutableListOf("Select")
        beneficiariesNameArray.addAll(beneficiaries.map { it.nickName })

        val adapter: ArrayAdapter<String> = ArrayAdapter(activity, R.layout.spinner_item, beneficiariesNameArray)
        spinnerView.adapter = adapter
        spinnerView.prompt = "Select Beneficiary"
        adapter.setDropDownViewResource(R.layout.spinner_item)
        spinnerView.onItemSelectedListener = this

        val transactionMode = view.findViewById<View>(R.id.transaction_mode_spinner) as Spinner
        val interTransferButton = view.findViewById<View>(R.id.inter_transfer_buttonView) as Button

        interTransferButton.setOnClickListener {

            try {
                val inputMethodManager = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(activity.currentFocus?.windowToken, 0)
            } catch (e: Exception) {

            }

            val amountEdit = view.findViewById<View>(R.id.inter_amount_EditView) as EditText
            val purposeEditText = view.findViewById<View>(R.id.inter_purpose_EditView) as EditText

            if (spinnerView.selectedItem == "Select") {
                Toast.makeText(activity, "Please Select Beneficiary", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            } else if (amountEdit.text.isEmpty()) {
                amountEdit.error = "Please Enter Amount"
                return@setOnClickListener
            } else if (amountEdit.text.toString().toFloat() <= 0.0f) {
                amountEdit.error = "Amount Should be greater than 0 (zero) "
                return@setOnClickListener
            } else if (purposeEditText.text.isEmpty()) {
                purposeEditText.error = "Please Enter Purpose"
                return@setOnClickListener
            } else if (amountEdit.text.toString().toFloat() > avlBalance) {
                Toast.makeText(activity, "Insufficient Amount!! Please Try Again", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            } else {
                val bundle = Bundle()
                bundle.putLong("accountNumber", beneficiaries.filter { it.nickName == spinnerView.selectedItem.toString() }.first().accountNumber.toLong())
                bundle.putFloat("amount", amountEdit.text.toString().toFloat())
                bundle.putString("purpose", purposeEditText.text.toString())
                bundle.putString("transactionMode", transactionMode.selectedItem.toString())

                val fragment = ConfirmInterTransactionFragment()
                fragment.arguments = bundle

                getActivityListener?.addAddFragmentToHome(R.id.fragment_to_replace_grid, fragment, "Confirm Transaction")
            }
        }

    }

    override fun onResume() {
        super.onResume()
        val spinnerView = view.findViewById<View>(R.id.select_beneficiary_spinner) as Spinner
        val beneficiariesNameArray = mutableListOf<String>()
        if(selectedItem == null) {
            beneficiariesNameArray.add("Select")
        } else if(selectedItem != null) {
            beneficiariesNameArray.add(selectedItem.toString())
        }
        getActivityListener?.setSubTitle("Inter Bank Transaction")
        val db = DataBaseHandler(activity)
        val customerId = getActivityListener?.getCustomerId()
        val beneficiaries = db.getBeneficiaries(customerId)
        beneficiariesNameArray.addAll(beneficiaries.map { it.nickName })

        val adapter: ArrayAdapter<String> = ArrayAdapter(activity, R.layout.spinner_item, beneficiariesNameArray)
        spinnerView.adapter = adapter


    }

    override fun onItemSelected(p0: AdapterView<*>?, itemView: View?, position: Int, itemId: Long) {
        val db = DataBaseHandler(activity)
        val beneficiariesNameArray = mutableListOf<String>("Select")
        val beneficiaries = db.getBeneficiaries(getActivityListener?.getCustomerId())
        beneficiariesNameArray.addAll(beneficiaries.map { it.nickName })
        val beneficiarySelected = beneficiariesNameArray[position]
        val accountNumberTextView = view.findViewById<View>(R.id.inter_ben_account_number_textView) as TextView
        val beneficiaryFullNameTextView = view.findViewById<View>(R.id.inter_ben_full_name_textView) as TextView
        val ifscTextView = view?.findViewById<View>(R.id.inter_ben_ifsc_textView) as TextView

        if (beneficiarySelected != "Select" && beneficiarySelected != " " && beneficiaries.size > 0) {
            accountNumberTextView.text = beneficiaries.filter { it.nickName == beneficiarySelected }.first().accountNumber.toString()
            ifscTextView.text = beneficiaries.filter { it.nickName == beneficiarySelected }.first().ifscCode
            beneficiaryFullNameTextView.text = beneficiaries.filter { it.nickName == beneficiarySelected }.first().name
        } else if (beneficiaries.size > 0) {
            Toast.makeText(activity, "Please Select Beneficiary", Toast.LENGTH_SHORT).show()
            accountNumberTextView.text = " "
            ifscTextView.text = " "
            beneficiaryFullNameTextView.text = " "
        } else {
            addBeneficiaryDialogue()
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        if (newConfig != null) {
            if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                val amountEdit = view.findViewById<View>(R.id.inter_amount_EditView) as EditText
                val purposeEditText = view.findViewById<View>(R.id.inter_purpose_EditView) as EditText
                amountEdit.hint = "Amount (INR)"
                purposeEditText.hint = "Purpose"

            }
        }
    }

    private fun addBeneficiaryDialogue() {
        val alertDialogBuilder = AlertDialog.Builder(activity)
        alertDialogBuilder.setMessage("It seems you have not yet added any Beneficiary! Want to Add ?")
        alertDialogBuilder.setPositiveButton("Yes", object : DialogInterface.OnClickListener {
            override fun onClick(arg0: DialogInterface, arg1: Int) {
                getActivityListener?.addFragmentToHome(R.id.fragment_to_replace_grid, AddBeneficiaryFragment(), "Add Beneficiary")
            }
        })

        alertDialogBuilder.setNegativeButton("Not now", object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface, which: Int) {
                // finish()
                activity.onBackPressed()
            }
        })
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }
}