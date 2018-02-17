package com.example.surendar_5541.ssbbanksystemdemoapplication

import android.app.AlertDialog
import android.app.Fragment
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

class ViewBeneficiaryFragment : Fragment() {
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
        return inflater.inflate(R.layout.confirm_add_beneficiary_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        val db = DataBaseHandler(activity)
        val customerId = getActivityListener?.getCustomerId()
        super.onActivityCreated(savedInstanceState)
        val confirmButton = view.findViewById<View>(R.id.confirm_button_view) as Button
        val del = "Delete"
        confirmButton.text = del
        val bundle: Bundle = arguments

        if (!bundle.isEmpty) {
            val benAccountNumber = bundle.getString("accountNumber")
            val beneficiaries = db.getBeneficiaries(customerId)
            val bank: String?
            val branch: String?
            val beneficiary = beneficiaries.filter { it.accountNumber == benAccountNumber.toLong() }.first()

            if (beneficiary.ifscCode == "SSBN0004444") {
                bank = "SSB"
                branch = "Estancia Guduvanchery"
            } else {
                bank = beneficiary.bankName
                branch = beneficiary.branchName
            }

            val nameTextView = view.findViewById<View>(R.id.ben_name_text_view) as TextView
            val accountNumberTextView = view.findViewById<View>(R.id.ben_account_number_text_view) as TextView
            val ifscTextView = view.findViewById<View>(R.id.ben_ifsc_text_view) as TextView
            val bankTextView = view.findViewById<View>(R.id.ben_bank_text_view) as TextView
            val branchTextView = view.findViewById<View>(R.id.ben_branch_text_view) as TextView
            val beneficiaryNickNameTextView = view.findViewById<View>(R.id.ben_nick_text_view) as TextView
            val editButton = view.findViewById<View>(R.id.edit_button_view) as Button

            nameTextView.text = beneficiary.name
            accountNumberTextView.text = benAccountNumber
            ifscTextView.text = beneficiary.ifscCode
            bankTextView.text = bank
            branchTextView.text = branch
            beneficiaryNickNameTextView.text = beneficiary.nickName

            editButton.setOnClickListener {
                val fragment = UpdateBeneficiaryFragment()
                val bundleToSend = Bundle()

                bundleToSend.putString("name", beneficiary.name)
                bundleToSend.putString("accountNumber", beneficiary.accountNumber.toString())
                bundleToSend.putString("ifsc", beneficiary.ifscCode)
                bundleToSend.putString("nickName", beneficiary.nickName)
                fragment.arguments = bundleToSend
                getActivityListener?.addFragmentToHome(R.id.fragment_to_replace_grid, fragment, "Update Beneficiary")
            }

            confirmButton.setOnClickListener {
                val alertDialogBuilder = AlertDialog.Builder(activity)
                alertDialogBuilder.setMessage("Are you sure want to Delete")
                alertDialogBuilder.setPositiveButton("Delete", object : DialogInterface.OnClickListener {
                    override fun onClick(arg0: DialogInterface, arg1: Int) {
                        db.deleteBeneficiary(customerId, benAccountNumber.toLong())
                        fragmentManager.popBackStack("View Beneficiary",android.app.FragmentManager.POP_BACK_STACK_INCLUSIVE)
                    }
                })
                alertDialogBuilder.setNegativeButton("Cancel", object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface, which: Int) {
                    }
                })
                val alertDialog = alertDialogBuilder.create()
                alertDialog.show()
            }

        } else {
            Toast.makeText(activity, "Internal Error Occurred,Please Try Later", Toast.LENGTH_LONG).show()
        }
    }

    override fun onResume() {
        super.onResume()
        getActivityListener?.setSubTitle("View Beneficiary")
    }

}