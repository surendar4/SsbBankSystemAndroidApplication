package com.example.surendar_5541.ssbbanksystemdemoapplication

import android.app.Fragment
import android.database.Cursor

interface OnItemClickListener {
    fun onItemClick(item: String)
}

interface GetHomeActivity {
    fun getCustomerId(): String
    fun addFragmentToHome(frameId: Int, fragment: Fragment, title: String)
    fun addAddFragmentToHome(frameId: Int, fragment: Fragment, title: String)
    fun paymentReceipt(frameId: Int, fragment: Fragment, title: String)
    fun setSubTitle(title: String)
}

interface GetIfscData {
    fun getBankAndBranch(bankName: String?,branchName: String?)
}

interface GetMainActivity {
    fun checkLoginStatus(): Boolean
    fun processPayment(username: String)
    fun isPaymentRequested(): Boolean
}

interface GetRegisteredBeneficiaries {
    fun getRegisteredBeneficiaries(): MutableList<BeneficiaryAccount>
}
