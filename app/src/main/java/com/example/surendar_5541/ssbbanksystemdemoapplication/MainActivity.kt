package com.example.surendar_5541.ssbbanksystemdemoapplication

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v4.view.ViewPager
import android.view.View
import android.widget.TextView


class MainActivity : FragmentActivity(), GetMainActivity {
    private var mAdapter: PagerAdapter? = null
    private lateinit var mPager: ViewPager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val registerTextView = findViewById<View>(R.id.register_textView) as TextView
        val loginTextView = findViewById<TextView>(R.id.login_textView) as TextView
        val registerBarView = findViewById<TextView>(R.id.register_View) as View
        val loginViewBarView = findViewById<TextView>(R.id.login_View) as View

        mAdapter = PagerAdapter(supportFragmentManager)
        mPager = findViewById<ViewPager>(R.id.pager) as ViewPager
        mPager?.adapter = mAdapter

        mPager?.setOnClickListener {
        }

        mPager?.setOnTouchListener(View.OnTouchListener { _, _ -> true })

        if (checkLoginStatus()) {
            switchLoginRegisterOptions(loginViewBarView, null, loginTextView, null)
            mPager?.currentItem = 0

        } else {
            switchLoginRegisterOptions(registerBarView, null, registerTextView, null)
            mPager?.currentItem = 1
        }

        loginTextView.setOnClickListener {
            switchLoginRegisterOptions(loginViewBarView, registerBarView, loginTextView, registerTextView)
            mPager?.currentItem = 0

        }

        registerTextView.setOnClickListener {
            switchLoginRegisterOptions(registerBarView, loginViewBarView, registerTextView, loginTextView)
            mPager?.currentItem = 1
        }

    }

    private fun switchLoginRegisterOptions(activeBar: View?, inActiveBar: View?, activeText: TextView?, inActiveText: TextView?) {
        activeBar?.setBackgroundColor(Color.parseColor("#FF6936C3"))
        inActiveBar?.setBackgroundColor(Color.TRANSPARENT)
        activeText?.setTypeface(null, Typeface.BOLD)
        inActiveText?.typeface = Typeface.DEFAULT
    }

    override fun checkLoginStatus(): Boolean {
        val loginPref = getSharedPreferences("loginStatus", Context.MODE_PRIVATE)
        val status = loginPref.getString("status", null)
        return status == "In"
    }

    private fun addFragmentToActivity(frameId: Int, fragment: android.support.v4.app.Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(frameId, fragment).addToBackStack(null).commit()
    }

    override fun processPayment(username: String) {
        val action = intent.action
        val type = intent.type
        intent.putExtra("username", username)
        if ("SSB_PAYMENT" == action && type != null) {
            if (type == "text/plain") {
                addFragmentToActivity(R.id.processPaymentFragment, ConfirmRequestedPaymentFragment())
            }
        }
    }

    override fun isPaymentRequested(): Boolean {
        val action = intent.action
        val type = intent.type
        return action == "SSB_PAYMENT" && type != null && type == "text/plain"
    }

    override fun onBackPressed() {
        if (isPaymentRequested()) {
            exitAlert()
        } else {
            super.onBackPressed()
        }
    }

    private fun exitAlert() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setMessage("Are you sure want to Abort Transaction")
        alertDialogBuilder.setPositiveButton("Abort", object : DialogInterface.OnClickListener {
            override fun onClick(arg0: DialogInterface, arg1: Int) {
                val responseIntent = Intent()
                responseIntent.putExtra("responseMessage", "Failed Due to user Aborted transaction")
                responseIntent.putExtra("orderNumber", intent.extras.getString("orderNumber"))
                responseIntent.putExtra("amount", intent.extras.getString("amount"))
                this@MainActivity.setResult(3, responseIntent)
                finish()
            }
        })

        alertDialogBuilder.setNegativeButton("Cancel", object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface, which: Int) {
                val registerText = findViewById<TextView>(R.id.register_textView) as TextView
                val loginText = findViewById<TextView>(R.id.login_textView) as TextView
                val registerBarView = findViewById<TextView>(R.id.register_View) as View
                val loginViewBar = findViewById<TextView>(R.id.login_View) as View

                if (checkLoginStatus()) {
                    loginText.setTypeface(null, Typeface.BOLD)
                    mPager?.currentItem = 0
                    loginViewBar.setBackgroundColor(Color.parseColor("#FF6936C3"))
                } else {
                    mPager?.currentItem = 1
                    registerText.setTypeface(null, Typeface.BOLD)
                    registerBarView.setBackgroundColor(Color.parseColor("#FF6936C3"))
                }
            }
        })
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

}
