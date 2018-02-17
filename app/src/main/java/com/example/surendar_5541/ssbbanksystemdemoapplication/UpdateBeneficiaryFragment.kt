package com.example.surendar_5541.ssbbanksystemdemoapplication

import android.app.Fragment
import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class UpdateBeneficiaryFragment : Fragment() {
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
        return inflater.inflate(R.layout.add_beneficiary_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val db = DataBaseHandler(activity)
        val addBeneficiaryButton = view.findViewById<View>(R.id.add_beneficiary_button) as Button
        val update = "Update Beneficiary"
        addBeneficiaryButton.text = update

        val beneficiaryNameEditText = view.findViewById<View>(R.id.beneficiary_name_Edit_Text) as EditText
        val beneficiaryAccountNumberEditText = view.findViewById<View>(R.id.beneficiary_account_number_Edit_Text) as EditText
        val beneficiaryConfirmAccountNumberEditText = view.findViewById<View>(R.id.beneficiary_confirm_account_number_Edit_Text) as EditText
        val beneficiaryIfscCodeEditText = view.findViewById<View>(R.id.beneficiary_ifsc_Edit_Text) as EditText
        val beneficiaryNickNameEditText = view.findViewById<View>(R.id.beneficiary_nick_name_Edit_Text) as EditText
        val bundle = arguments
        var originalIfscCode: String? = null
        val benAccountNumber = bundle.getString("accountNumber")
        val beneficiary = db.getBeneficiaries(getActivityListener?.getCustomerId()).filter { it.accountNumber == benAccountNumber.toLong() }.first()

        if (!bundle.isEmpty) {
            beneficiaryNameEditText.text.append(beneficiary.name)
            beneficiaryAccountNumberEditText.text.append(beneficiary.accountNumber.toString())
            beneficiaryConfirmAccountNumberEditText.text.append(beneficiary.accountNumber.toString())
            beneficiaryIfscCodeEditText.text.append(beneficiary.ifscCode)
            beneficiaryNickNameEditText.text.append(beneficiary.nickName)
            originalIfscCode = beneficiary.ifscCode
        }

        addBeneficiaryButton.setOnClickListener {
            if (isNetworkAvailable()) {
                val name = beneficiaryNameEditText.text.toString()
                val accountNumber = beneficiaryAccountNumberEditText.text.toString()
                val confirmAccountNumber = beneficiaryConfirmAccountNumberEditText.text.toString()
                val ifscCode = beneficiaryIfscCodeEditText.text.toString()
                val nickName = beneficiaryNickNameEditText.text.toString()

                if (name.isEmpty()) {    //Validating basic data
                    beneficiaryNameEditText.error = "Please Enter Beneficiary Name"
                }

                if (nickName.isEmpty()) {
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

                if (arrayOf(name, accountNumber, confirmAccountNumber, ifscCode, nickName).all { it.isNotEmpty() }) {

                    if (accountNumber != confirmAccountNumber) {
                        beneficiaryConfirmAccountNumberEditText.error = " Account Number Not Matching"
                    } else {
                        val bankName: String?
                        val branchName: String?

                        if (originalIfscCode != ifscCode) {
                            bankName = null
                            branchName = null
                        } else {
                            bankName = beneficiary.bankName
                            branchName = beneficiary.branchName
                        }

                        val fragment = ConfirmUpdateBeneficiaryFragment()
                        val sendBundle = Bundle()

                        sendBundle.putString("originalAccountNumber", benAccountNumber)
                        sendBundle.putString("name", name)
                        sendBundle.putString("accountNumber", accountNumber)
                        sendBundle.putString("ifsc", ifscCode)
                        sendBundle.putString("bank", bankName)
                        sendBundle.putString("branch", branchName)
                        sendBundle.putString("nickName", nickName)
                        fragment.arguments = sendBundle

                        getActivityListener?.addFragmentToHome(R.id.fragment_to_replace_grid, fragment, "Confirm Update Beneficiary")
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
        getActivityListener?.setSubTitle("Update Beneficiary")
    }

}