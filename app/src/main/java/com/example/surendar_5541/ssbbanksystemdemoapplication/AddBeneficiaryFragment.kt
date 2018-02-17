package com.example.surendar_5541.ssbbanksystemdemoapplication

import android.app.Fragment
import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

/* SSB Bank IFSC: SSBN0004444 */

class AddBeneficiaryFragment : Fragment() {

    private var getHomeActivity: GetHomeActivity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            getHomeActivity = activity as HomeActivity
        } catch (e: ClassCastException) {

        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.add_beneficiary_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val addBeneficiaryButton = view.findViewById<View>(R.id.add_beneficiary_button) as Button

        addBeneficiaryButton.setOnClickListener {
            //Executes when user clicks on the addBeneficiary Button
            hideKeyBoard() //Hides The KeyBoard after this click

            if (isNetworkAvailable()) {
                val beneficiaryNameEditText = view.findViewById<View>(R.id.beneficiary_name_Edit_Text) as EditText
                val beneficiaryAccountNumberEditText = view.findViewById<View>(R.id.beneficiary_account_number_Edit_Text) as EditText
                val beneficiaryConfirmAccountNumberEditText = view.findViewById<View>(R.id.beneficiary_confirm_account_number_Edit_Text) as EditText
                val beneficiaryIfscCodeEditText = view.findViewById<View>(R.id.beneficiary_ifsc_Edit_Text) as EditText
                val beneficiaryNickNameEditText = view.findViewById<View>(R.id.beneficiary_nick_name_Edit_Text) as EditText

                val name = beneficiaryNameEditText.text.toString()
                val accountNumber = beneficiaryAccountNumberEditText.text.toString()
                val confirmAccountNumber = beneficiaryConfirmAccountNumberEditText.text.toString()
                val ifscCode = beneficiaryIfscCodeEditText.text.toString()
                val beneficiaryNickName = beneficiaryNickNameEditText.text.toString()

                if (name.isEmpty()) {    //Validating basic data
                    beneficiaryNameEditText.error = "Please Enter Beneficiary Name"
                }

                if (beneficiaryNickName.isEmpty()) {
                    beneficiaryNickNameEditText.error = "Please Enter Beneficiary Nick Name"
                }

                if (accountNumber.isEmpty() || (accountNumber.length !in 7..18)) {
                    beneficiaryAccountNumberEditText.error = "Please Enter valid Beneficiary Account Number"
                }

                if (confirmAccountNumber.isEmpty() || (confirmAccountNumber.length !in 7..18)) {
                    beneficiaryConfirmAccountNumberEditText.error = "Please Enter valid Beneficiary Account Number Again"
                }

                if (ifscCode.isEmpty() || ifscCode.length != 11) {
                    beneficiaryIfscCodeEditText.error = "Please Enter Valid Beneficiary Bank IFSC Code"
                    return@setOnClickListener
                }

                if (name.trim().length !in 1..40) {
                    beneficiaryNameEditText.error = "Name should not be greater than 40 characters"
                    return@setOnClickListener
                } else {
                    val nameArray = name.toCharArray()
                    if (nameArray.any { it.isDigit() }) {
                        beneficiaryNameEditText.error = "Name should not contain digits"
                        return@setOnClickListener
                    }
                }

                if (beneficiaryNickName.trim().length !in 1..40) {
                    beneficiaryNameEditText.error = "Please should not be greater than 40 characters"
                    return@setOnClickListener
                } else {
                    val nameArray = beneficiaryNickName.toCharArray()
                    if (nameArray.any { it.isDigit() }) {
                        beneficiaryNickNameEditText.error = "Nick name should not contain digits"
                        return@setOnClickListener
                    }
                }

                if (arrayOf(name, accountNumber, confirmAccountNumber, ifscCode, beneficiaryNickName).all { it.isNotEmpty() }) {

                    if (accountNumber != confirmAccountNumber) {
                        Toast.makeText(activity, "Account numbers are not matching", Toast.LENGTH_LONG).show()
                        beneficiaryConfirmAccountNumberEditText.text.clear()
                    } else {
                        val fragment = ConfirmAddBeneficiaryFragment()
                        val bundle = Bundle()

                        bundle.putString("name", name.trim().capitalize())
                        bundle.putString("accountNumber", accountNumber)
                        bundle.putString("ifsc", ifscCode.trim().toUpperCase())
                        bundle.putString("nickName", beneficiaryNickName.trim())
                        fragment.arguments = bundle
                        getHomeActivity?.addFragmentToHome(R.id.fragment_to_replace_grid, fragment, "Confirm Add Beneficiary")
                    }
                }
            } else {
                Toast.makeText(activity, "NetWork Connection not Available!! Please Enable InterNet Connection", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

        }


    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected

    }

    override fun onResume() {
        super.onResume()
        (activity as HomeActivity).setSubTitle("Add Beneficiary")
    }

    private fun hideKeyBoard() {
        try {
            val inputMethodManager = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(activity.currentFocus?.windowToken, 0)
        } catch (e: Exception) {

        }
    }


}
