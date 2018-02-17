package com.example.surendar_5541.ssbbanksystemdemoapplication

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.support.v4.app.Fragment
import android.telephony.PhoneNumberFormattingTextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.inputmethod.InputMethodManager
import android.widget.*
import java.io.IOException
import java.lang.reflect.InvocationTargetException
import javax.crypto.BadPaddingException
import javax.crypto.IllegalBlockSizeException


class RegisterFragment : Fragment(), Animation.AnimationListener {
    private var getMainActivityListener: GetMainActivity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            getMainActivityListener = activity as GetMainActivity
        } catch (castException: ClassCastException) {
            /** The activity does not implement the listener. */
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.register_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val db = DataBaseHandler(activity)

        val enabledPinRadioButton = view?.findViewById<View>(R.id.pin_enable_radio) as RadioButton
        val disabledPinRadioButton = view?.findViewById<View>(R.id.pin_disable_radio) as RadioButton

        val pinEditText = view?.findViewById<View>(R.id.pin_edit_text) as EditText
        val confirmPinEditText = view?.findViewById<View>(R.id.confirm_pin_edit_text) as EditText
        val usernameEditText = view?.findViewById<View>(R.id.usernameSet_edit_text) as EditText
        val passwordEditText = view?.findViewById<View>(R.id.passwordSet_edit_text) as EditText
        val confirmPasswordEditText = view?.findViewById<View>(R.id.confirm_password_edit_text) as EditText
        val accountNumberEditText = view?.findViewById<View>(R.id.account_number_editText) as EditText
        val customerIdEditText = view?.findViewById<View>(R.id.customer_id_editText) as EditText
        val mobileNumberEditText = view?.findViewById<View>(R.id.mobile_number_editText) as EditText
        mobileNumberEditText.addTextChangedListener(PhoneNumberFormattingTextWatcher())

        val registerButton: Button = view?.findViewById<View>(R.id.register_button) as Button
        val easyPinLayout = view?.findViewById<View>(R.id.register_pin_layout) as LinearLayout

        enabledPinRadioButton.setOnClickListener {
            if (easyPinLayout.visibility == View.GONE) {
                easyPinLayout.visibility = View.VISIBLE
            }
        }

        disabledPinRadioButton.setOnClickListener {
            if (easyPinLayout.visibility == View.VISIBLE) {
                easyPinLayout.visibility = View.GONE
            }
        }

        usernameEditText.setOnFocusChangeListener { _, focus ->
            if (!focus) {
                val username = usernameEditText.text.toString()
                if (!db.isUsernameAvailable(username)) {
                    usernameEditText.error = "Username already registered"
                    return@setOnFocusChangeListener
                } else if (username.trim().length !in 4..14) {
                    usernameEditText.error = "please enter username between 4 to 14 characters only"
                    return@setOnFocusChangeListener
                } else {
                    val usernameArray = username.trim().toCharArray()
                    if (usernameArray.any { it.isWhitespace() }) {
                        usernameEditText.error = "In Username WhiteSpace is Not Allowed!!"
                        return@setOnFocusChangeListener
                    }
                }
            }
        }

        registerButton.setOnClickListener {

            try {
                val inputMethodManager = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager /* To Hide KeyBoard */
                inputMethodManager.hideSoftInputFromWindow(activity.currentFocus?.windowToken, 0)
            } catch (e: Exception) {

            }

            val accountNumber = accountNumberEditText.text.toString()
            val customerId = customerIdEditText.text.toString()
            val mobile = mobileNumberEditText.text.toString()
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()
            val confirmPassword = confirmPasswordEditText.text.toString()
            val mobileNumber = toStandardForm(mobile.toCharArray())

            if (accountNumber.isEmpty()) {
                accountNumberEditText.error = "Please Enter Account Number"
            }

            if (customerId.isEmpty()) {
                customerIdEditText.error = "Please Enter CustomerId"
            }

            if (mobileNumberEditText.text.isEmpty()) {
                mobileNumberEditText.error = "Please Enter Mobile Number"
            }
            if (username.isEmpty()) {
                usernameEditText.error = "Please Enter a Unique Username"
            }

            if (!isValidPassword(password)) {
                passwordEditText.error = "Please Enter a Password of length between 6-14 characters with 1 Lower case,1 Capital,1 Digit & 1 symbol"
            }

            if (confirmPassword.isEmpty()) {
                confirmPasswordEditText.error = "Please Re-Enter Password"
            }

            if (easyPinLayout.visibility == View.VISIBLE) {
                if (pinEditText.text.toString().isEmpty()) {
                    pinEditText.error = "Please Set 4 Digit PIN"
                }

                if (confirmPinEditText.text.toString().isEmpty()) {
                    confirmPinEditText.error = "Please Re-Enter 4 Digit PIN"
                }
            }

            if (password != confirmPassword) {
                passwordEditText.text.clear()
                confirmPasswordEditText.text.clear()
                Toast.makeText(activity, "Passwords are not Matching!! Try Again", Toast.LENGTH_LONG).show()
            }

            if (pinEditText.visibility == View.VISIBLE && pinEditText.text.toString().length != 4) {
                pinEditText.error = "PIN should be 4 digits"
            }

            if (pinEditText.visibility == View.VISIBLE && pinEditText.text.toString() != confirmPinEditText.text.toString()) {
                pinEditText.text.clear()
                confirmPinEditText.text.clear()
                Toast.makeText(activity, "Easy PIN's are not Matching!! Try Again", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if (!accountNumber.isEmpty() && !customerId.isEmpty() && !mobileNumber.toString().isEmpty() && isValidPassword(password)) {
                if (db.isUserAlreadyRegistered(customerId)) {
                    Toast.makeText(activity, "Failed!! User Already Registered Please Login", Toast.LENGTH_LONG).show()
                } else if (isValidCustomer(accountNumber, customerId, mobileNumber.toString())) { //Verifies is user data is Valid

                    if (!username.isEmpty() && !password.isEmpty() && !confirmPassword.isEmpty() && password == confirmPassword && db.isUsernameAvailable(username)) {
                        val pinStatus: Int
                        val pin: Int
                        if (easyPinLayout.visibility == View.VISIBLE) {
                            pinStatus = 1
                            pin = pinEditText.text.toString().toInt()
                        } else {
                            pinStatus = 0
                            pin = 0
                        }

                        try {
                            val ded = DataEncryptionAndDecryption()
                            val passKey = ded.getSecretKey(password)
                            val pinKey = ded.getSecretKey(pin.toString())
                            db.addSsbAccount(SsbAccount(customerId.trim(), username.trim(), ded.encryptData(password, passKey), pinStatus, ded.encryptData(pin.toString(), pinKey)))
                        } catch (e: IllegalBlockSizeException) {
                            //throw IllegalArgumentException("Invalid Login")
                            Toast.makeText(activity, "Please Enter a Password of length between 6-14 characters with 1 Lower case,1 Capital,1 Digit & 1 symbol", Toast.LENGTH_LONG).show()
                        } catch (e: UnknownError) {
                            Toast.makeText(activity, "Please Enter a Password of length between 6-14 characters with 1 Lower case,1 Capital,1 Digit & 1 symbol", Toast.LENGTH_LONG).show()
                        } catch (e: IOException) {
                            Toast.makeText(activity, "Please Enter a Password of length between 6-14 characters with 1 Lower case,1 Capital,1 Digit & 1 symbol", Toast.LENGTH_LONG).show()
                        } catch (e: BadPaddingException) {
                            Toast.makeText(activity, "Please Enter a Password of length between 6-14 characters with 1 Lower case,1 Capital,1 Digit & 1 symbol", Toast.LENGTH_LONG).show()
                        } catch (e: InvocationTargetException) {
                            Toast.makeText(activity, "Please Enter a Password of length between 6-14 characters with 1 Lower case,1 Capital,1 Digit & 1 symbol", Toast.LENGTH_LONG).show()
                        }
                        if (getMainActivityListener!!.isPaymentRequested()) {
                            getMainActivityListener?.processPayment(username)
                        } else {
                            val intent = Intent(activity, HomeActivity::class.java)
                            intent.putExtra("username", username)
                            startActivity(intent)
                            activity.finish()
                        }

                    } else {
                        return@setOnClickListener
                    }

                } else {
                    accountNumberEditText.text.clear()
                    mobileNumberEditText.text.clear()
                    customerIdEditText.text.clear()
                    Toast.makeText(activity, "Failed!!! Please Enter Valid Details", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun isValidCustomer(accountNumber: String, customerId: String, mobileNumber: String): Boolean {
        val db = DataBaseHandler(activity)
        val customerIdAndMobile = db.getMobileCustomerIdUsingAccountNumber(accountNumber)
        return (mobileNumber == customerIdAndMobile.first && customerId == customerIdAndMobile.second)
    }

    private fun isValidPassword(password: String): Boolean {
        val passwordArray = password.toCharArray()
        var isThereSpecialChar = false
        for (char in passwordArray) {
            if (!(char.isLetter() || char.isDigit())) {
                isThereSpecialChar = true
            }
        }
        return password.length in 6..18 && passwordArray.any { it.isDigit() } && passwordArray.any { it.isLowerCase() } && passwordArray.any { it.isUpperCase() } && isThereSpecialChar
    }

    private fun toStandardForm(mobileCharArray: CharArray): Number {
        var mobileNumber: Long = 0
        for (eachChar in mobileCharArray) {
            if (eachChar.toInt() in 48..57) {
                mobileNumber = (mobileNumber) * 10 + (eachChar.toInt() - 48)
            }
        }
        return mobileNumber
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)

        val enabledPinRadioButton: RadioButton? = view?.findViewById<View>(R.id.pin_enable_radio) as RadioButton
        val pinEditText: EditText? = view?.findViewById<View>(R.id.pin_edit_text) as EditText
        val confirmPinEditText: EditText? = view?.findViewById<View>(R.id.confirm_pin_edit_text) as EditText

        if (enabledPinRadioButton != null) {
            if (enabledPinRadioButton.isChecked) {
                pinEditText?.visibility = View.VISIBLE
                confirmPinEditText?.visibility = View.VISIBLE
            } else {
                pinEditText?.visibility = View.GONE
                confirmPinEditText?.visibility = View.GONE
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val enabledPinRadioButton = view?.findViewById<View>(R.id.pin_enable_radio) as RadioButton
        val easyPinLayout = view?.findViewById<View>(R.id.register_pin_layout) as LinearLayout

        if (enabledPinRadioButton.isChecked) {
            easyPinLayout.visibility = View.VISIBLE
        } else {
            easyPinLayout.visibility = View.GONE
        }
    }

    override fun onAnimationRepeat(p0: Animation?) {
    }

    override fun onAnimationEnd(p0: Animation?) {
        val disabledPinRadioButton = view?.findViewById<View>(R.id.pin_disable_radio) as RadioButton
        val easyPinLayout = view?.findViewById<View>(R.id.register_pin_layout) as LinearLayout

        if (disabledPinRadioButton.isChecked) {
            easyPinLayout.visibility = View.GONE
        }
    }

    override fun onAnimationStart(p0: Animation?) {
    }
}