package com.example.surendar_5541.ssbbanksystemdemoapplication

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.*
import java.io.IOException
import java.lang.reflect.InvocationTargetException
import javax.crypto.BadPaddingException
import javax.crypto.IllegalBlockSizeException

class LoginFragment : Fragment(), Animation.AnimationListener {

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
        return inflater.inflate(R.layout.login_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if (savedInstanceState != null) {
            val pinEditText: EditText = view?.findViewById<View>(R.id.pin_editText) as EditText
            pinEditText.append(savedInstanceState.getString("pin"))
        }
        val loginOptionPin = 1
        val loginOptionPassword = 2
        val db = DataBaseHandler(activity)
        val usernameFromPref = activity.getSharedPreferences("loginStatus", Context.MODE_PRIVATE).getString("username", "empty")

        val loginPinTextView = view?.findViewById<View>(R.id.easyPin_textView) as TextView
        val loginPasswordTextView = view?.findViewById<View>(R.id.password_textView) as TextView
        val loginPinLayout = view?.findViewById<View>(R.id.login_pin_customer_name_layout) as LinearLayout
        val loginButton = view?.findViewById<View>(R.id.login_button) as Button
        val forgotTextView = view?.findViewById<View>(R.id.forgot_password_textView) as TextView


        if (getMainActivityListener!!.checkLoginStatus()) {
            val ssbAccount: SsbAccount = db.getSsbAccount(usernameFromPref)

            if (ssbAccount.pinStatus == 1) {
                switchLoginOption(loginPinTextView, loginPasswordTextView, loginOptionPin)

                val customerNameTextView = view?.findViewById<View>(R.id.customer_name_text_view) as TextView
                customerNameTextView.text = db.getCustomer(db.getCustomerId(usernameFromPref)).customerName.capitalize()
            } else {
                switchLoginOption(loginPasswordTextView, loginPinTextView, loginOptionPassword)
            }
        } else {
            switchLoginOption(loginPasswordTextView, loginPinTextView, loginOptionPassword)
        }

        loginPinTextView.setOnClickListener {
            if (getMainActivityListener!!.checkLoginStatus()) {
                val ssbAccount: SsbAccount = db.getSsbAccount(usernameFromPref)

                if (ssbAccount.pinStatus == 1) {
                    switchLoginOption(loginPinTextView, loginPasswordTextView, loginOptionPin)

                    val customerNameTextView = view?.findViewById<View>(R.id.customer_name_text_view) as TextView
                    customerNameTextView.text = db.getCustomer(db.getCustomerId(usernameFromPref)).customerName.capitalize()
                } else {
                    Toast.makeText(activity, "Sorry!! you have not yet enabled EasyPin", Toast.LENGTH_SHORT).show()
                    switchLoginOption(loginPasswordTextView, loginPinTextView, loginOptionPassword)
                }
            } else {
                Toast.makeText(activity, "Sorry!! Your not allowed to login with EasyPin", Toast.LENGTH_SHORT).show()
                switchLoginOption(loginPasswordTextView, loginPinTextView, loginOptionPassword)
            }
        }

        loginPasswordTextView.setOnClickListener {
            switchLoginOption(loginPasswordTextView, loginPinTextView, loginOptionPassword)
        }

        loginButton.setOnClickListener {

            try {
                val inputMethodManager = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(activity.currentFocus?.windowToken, 0)
            } catch (e: Exception) {

            }

            if (loginPinLayout.visibility == View.VISIBLE) {
                val pinEditText = view?.findViewById<View>(R.id.pin_editText) as EditText
                val pin = pinEditText.text.toString()

                if (pin.isEmpty() || pin.length != 4) {
                    pinEditText.error = "Please Enter Valid 4 Digit PIN"
                    return@setOnClickListener
                } else {
                    verifyPinOrPassword(usernameFromPref, usernameFromPref, pin, " ")
                }

            } else {
                val usernameEditText = view?.findViewById<View>(R.id.username_edit_text) as EditText
                val passwordEditText = view?.findViewById<View>(R.id.password_edit_text) as EditText
                val username = usernameEditText.text.toString()
                val password = passwordEditText.text.toString()

                if (username.isEmpty()) {
                    usernameEditText.error = "Please Enter Username"
                    return@setOnClickListener
                }

                if (password.isEmpty()) {
                    passwordEditText.error = "Please Enter Password"
                    return@setOnClickListener
                }

                if (!username.isEmpty() && !password.isEmpty()) {
                    verifyPinOrPassword(usernameFromPref, username, "0", password)
                }
            }
        }

        forgotTextView.setOnClickListener {
            val fragment = ResetPasswordFragment()
            val ft = fragmentManager.beginTransaction()
            ft.replace(R.id.reset_fragment, fragment).addToBackStack(null).commit()
        }

    }

    private fun switchLoginOption(activeTextView: TextView, inActiveTextView: TextView?, loginOption: Int) {
        val loginPinLayout = view?.findViewById<View>(R.id.login_pin_customer_name_layout) as LinearLayout
        val loginPasswordLayout = view?.findViewById<View>(R.id.login_password_customer_name_layout) as LinearLayout
        val zoomInFadeInAnimation = AnimationUtils.loadAnimation(context, R.anim.zoom_fade_in) //Animations
        val zoomOutFadeOutAnimation = AnimationUtils.loadAnimation(context, R.anim.zoom_fade_out)

        zoomInFadeInAnimation.setAnimationListener(this)
        zoomOutFadeOutAnimation.setAnimationListener(this)

        inActiveTextView?.setTextColor(Color.DKGRAY)
        inActiveTextView?.typeface = Typeface.DEFAULT

        activeTextView.setTypeface(null, Typeface.BOLD)
        activeTextView.startAnimation(zoomInFadeInAnimation)
        inActiveTextView?.startAnimation(zoomOutFadeOutAnimation)

        when (loginOption) {
            1 -> {
                loginPasswordLayout.visibility = View.GONE
                loginPinLayout.visibility = View.VISIBLE

            }
            2 -> {
                loginPinLayout.visibility = View.GONE
                loginPasswordLayout.visibility = View.VISIBLE
            }
        }
    }

    private fun verifyPinOrPassword(originalUsername: String, username: String, pin: String, password: String) {
        val db = DataBaseHandler(activity)
        val ded = DataEncryptionAndDecryption()
        var auth = false

        val usernameEditText = view?.findViewById<View>(R.id.username_edit_text) as EditText
        val passwordEditText = view?.findViewById<View>(R.id.password_edit_text) as EditText

        try {
            if (pin == "0") {
                val key = ded.getSecretKey(password)
                if (password != ded.decryptData(db.getPassword(username), key) || db.getPassword(username).isEmpty()) {
                    usernameEditText.text.clear()
                    passwordEditText.text.clear()
                    Toast.makeText(activity, "Invalid Login! Either Username or Password is Incorrect, please try again", Toast.LENGTH_LONG).show()
                } else {
                    auth = true
                }
            } else {
                val ssbAccount = db.getSsbAccount(originalUsername)
                val pinKey = ded.getSecretKey(pin)
                val originalPin = ded.decryptData(ssbAccount.pin, pinKey)

                if (pin == originalPin) {
                    auth = true
                } else {
                    val pinEditText = view?.findViewById<View>(R.id.pin_editText) as EditText
                    pinEditText.text.clear()
                    Toast.makeText(activity, "Invalid Login! InCorrect PIN you have entered", Toast.LENGTH_LONG).show()
                }
            }

            if (auth) {
                if (getMainActivityListener!!.isPaymentRequested()) {
                    getMainActivityListener?.processPayment(username)
                } else {
                    val intent = Intent(activity, HomeActivity::class.java)
                    intent.putExtra("username", username)
                    startActivity(intent)
                    activity.finish()
                }
            }

        } catch (e: IllegalBlockSizeException) {
            Toast.makeText(activity, "Invalid Login! Either Username or Password is Incorrect, please try again", Toast.LENGTH_LONG).show()

        } catch (e: UnknownError) {
            Toast.makeText(activity, "Invalid Login! Either Username or Password is Incorrect, please try again", Toast.LENGTH_LONG).show()
        } catch (e: IOException) {
            Toast.makeText(activity, "Invalid Login! Either Username or Password is Incorrect, please try again", Toast.LENGTH_LONG).show()
        } catch (e: BadPaddingException) {
            Toast.makeText(activity, "Invalid Login! Either Username or Password is Incorrect, please try again", Toast.LENGTH_LONG).show()
        } catch (e: InvocationTargetException) {
            Toast.makeText(activity, "Invalid Login! Either Username or Password is Incorrect, please try again", Toast.LENGTH_LONG).show()
        }

    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        val pinEditText: EditText? = view?.findViewById<View>(R.id.pin_editText) as EditText
        if (pinEditText?.text != null) {
            outState?.putString("pin", pinEditText.text.toString())
        }
    }

    override fun onAnimationStart(p0: Animation?) {
    }

    override fun onAnimationEnd(p0: Animation?) {
    }

    override fun onAnimationRepeat(p0: Animation?) {
    }

}