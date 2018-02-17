package com.example.surendar_5541.ssbbanksystemdemoapplication

import android.app.Fragment
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteConstraintException
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*


class ConfirmAddBeneficiaryFragment : Fragment(), Animation.AnimationListener {
    var getActivityListener: GetHomeActivity? = null

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
        super.onActivityCreated(savedInstanceState)
        val bundle: Bundle = this.arguments
        val nameTextView = view.findViewById<View>(R.id.ben_name_text_view) as TextView
        val accountNumberTextView = view.findViewById<View>(R.id.ben_account_number_text_view) as TextView
        val ifscTextView = view.findViewById<View>(R.id.ben_ifsc_text_view) as TextView
        val bankTextView = view.findViewById<View>(R.id.ben_bank_text_view) as TextView
        val branchTextView = view.findViewById<View>(R.id.ben_branch_text_view) as TextView
        val editButton = view.findViewById<View>(R.id.edit_button_view) as Button
        val nickNameTextView = view.findViewById<View>(R.id.ben_nick_text_view) as TextView
        val progressBar = view.findViewById<View>(R.id.progressBar_ben) as ProgressBar
        val ifscDataLayout = view.findViewById<View>(R.id.ifsc_data_layout) as LinearLayout
        val confirmButton = view.findViewById<View>(R.id.confirm_button_view) as Button
        val fadeOutAnimation = AnimationUtils.loadAnimation(activity, R.anim.fade_out) //FadeOut Animation
        fadeOutAnimation.setAnimationListener(this)

        var bank: String? = null
        var branch: String? = null

        if (!bundle.isEmpty) {
            val name = bundle.getString("name")
            val accountNumber = bundle.getString("accountNumber")
            val ifsc = bundle.getString("ifsc")
            val nickName = bundle.getString("nickName")

            if (ifsc == "SSBN0004444") {
                bank = "SSB Bank"
                branch = "Guduvanchery"
            } else {
                // confirmButton.visibility = View.GONE
                confirmButton.animation = fadeOutAnimation

                ifscDataLayout.visibility = View.GONE
                progressBar.visibility = View.VISIBLE
                progressBar.isIndeterminate = true
                //  timeOut() //TimeOut For 15 secs
                val URL = "https://ifsc.firstatom.org/key/8ig6XeuP7J0J3E8Y6SOwSpA4C/ifsc/$ifsc" //Retrieving Bank Name and Branch using ifsc code through API
                HttpRequestGetIFScDetails(this, object : GetIfscData {
                    override fun getBankAndBranch(bankName: String?, branchName: String?) {
                        confirmButton.clearAnimation()
                        progressBar.visibility = View.GONE
                        if (bankName != null && branchName != null) {
                            confirmButton.visibility = View.VISIBLE
                            ifscDataLayout.visibility = View.VISIBLE
                            bankTextView.text = bankName
                            branchTextView.text = branchName
                            bank = bankName
                            branch = branchName
                        } else {
                            Toast.makeText(activity, "Invalid IFSC Code or Slow Network connection please try again", Toast.LENGTH_LONG).show()
                            activity.onBackPressed()
                        }
                    }
                }).execute(URL)
            }

            nameTextView.text = name
            accountNumberTextView.text = accountNumber
            ifscTextView.text = ifsc
            nickNameTextView.text = nickName

            editButton.setOnClickListener {
                activity.onBackPressed()
            }

            confirmButton.setOnClickListener {
                if (progressBar.visibility == View.VISIBLE) {
                    Toast.makeText(activity, "Loading Please wait...", Toast.LENGTH_SHORT).show()
                    confirmButton.animation = fadeOutAnimation
                    return@setOnClickListener
                } else {
                    try {
                        val customerId = getActivityListener?.getCustomerId()
                        val res = db.addBeneficiary(BeneficiaryAccount(customerId, nickName, name, accountNumber.toLong(), ifsc, bank, branch))

                        if (res.lastPathSegment.toInt() > 0) {
                            fragmentManager.popBackStack("Add Beneficiary", android.app.FragmentManager.POP_BACK_STACK_INCLUSIVE)
                        } else {
                            Toast.makeText(activity, "Either Account Number or nick name already registered!! Please check and try again", Toast.LENGTH_SHORT).show()
                            activity.onBackPressed()
                        }
                    } catch (e: SQLiteConstraintException) {
                        Toast.makeText(activity, "Either Account Number or nick name already registered!! Please check and try again", Toast.LENGTH_LONG).show()
                    }
                }
            }

        }
    }

    override fun onResume() {
        super.onResume()
        getActivityListener?.setSubTitle("Confirm Add Beneficiary")
    }

    /* fun timeOut() {
         val handler = Handler()
         handler.postDelayed({
             val progressBar: ProgressBar? = view.findViewById<View>(R.id.progressBar_ben) as ProgressBar
             if(progressBar != null) {
                 if (progressBar.visibility == View.VISIBLE) {
                     Toast.makeText(activity, "Invalid IFSC Code or Slow Network connection please try again", Toast.LENGTH_LONG).show()
                     activity?.onBackPressed()
                 }
             }
             /* 15 secs */
         }, 15000)
     } */

    override fun onAnimationStart(p0: Animation?) {
    }

    override fun onAnimationRepeat(p0: Animation?) {
    }

    override fun onAnimationEnd(p0: Animation?) {
    }
}

