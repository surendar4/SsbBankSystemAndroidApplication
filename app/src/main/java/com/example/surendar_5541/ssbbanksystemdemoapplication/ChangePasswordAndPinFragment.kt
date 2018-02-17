package com.example.surendar_5541.ssbbanksystemdemoapplication

import android.app.Fragment
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.widget.*
import java.io.IOException
import java.lang.reflect.InvocationTargetException
import javax.crypto.BadPaddingException
import javax.crypto.IllegalBlockSizeException

class ChangePasswordAndPinFragment : Fragment(), Animation.AnimationListener {
    private var getHomeActivityListener: GetHomeActivity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            getHomeActivityListener = activity as GetHomeActivity
        } catch (castException: ClassCastException) {
            /** The activity does not implement the listener. */
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.change_password_pin_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val db = DataBaseHandler(activity)
        val username = activity.getSharedPreferences("loginStatus", Context.MODE_PRIVATE).getString("username", "null")
        val ssbAccount = db.getSsbAccount(username)

        val enabledPinRadioButton = view?.findViewById<View>(R.id.pin_enable_radio) as RadioButton
        val disabledPinRadioButton = view?.findViewById<View>(R.id.pin_disable_radio) as RadioButton
        val changePasswordRadioButton = view?.findViewById<View>(R.id.password_radio) as RadioButton
        val changeEasyPinRadioButton = view?.findViewById<View>(R.id.pin_radio) as RadioButton


        val pinEditText = view?.findViewById<View>(R.id.pin_edit_text) as EditText
        val confirmPinEditText = view?.findViewById<View>(R.id.confirm_pin_edit_text) as EditText
        val currentPasswordEditText = view?.findViewById<View>(R.id.current_password) as EditText
        val passwordEditText = view?.findViewById<View>(R.id.passwordSet_edit_text) as EditText
        val confirmPasswordEditText = view?.findViewById<View>(R.id.confirm_password_edit_text) as EditText

        val passwordLayout = view?.findViewById<View>(R.id.change_password_layout) as LinearLayout
        val easyPinLayout = view?.findViewById<View>(R.id.change_easyPinLayout) as LinearLayout
        val pinLayout = view?.findViewById<View>(R.id.pin_layout) as LinearLayout
        val pinSubLayout = view?.findViewById<View>(R.id.pin_sub_layout) as LinearLayout

        val changePasswordButton = view?.findViewById<View>(R.id.change_password_button) as Button
        val changePinButton = view?.findViewById<View>(R.id.change_pin_button) as Button

        if (changePasswordRadioButton.isChecked) {     /* Change Visibility of Layouts */
            changeLayoutsVisibility(passwordLayout, easyPinLayout)
        } else {
            changeLayoutsVisibility(easyPinLayout, passwordLayout)
        }

        changePasswordRadioButton.setOnClickListener {
            if (changePasswordRadioButton.isChecked) {
                changeLayoutsVisibility(passwordLayout, easyPinLayout)
            }
        }

        changeEasyPinRadioButton.setOnClickListener {
            if (changeEasyPinRadioButton.isChecked) {
                changeLayoutsVisibility(easyPinLayout, passwordLayout)
            }
        }

        if (ssbAccount.pinStatus == 1) {
            enabledPinRadioButton.isChecked = true
        } else {
            disabledPinRadioButton.isChecked = true
        }

        if (enabledPinRadioButton.isChecked) {
            pinLayout.visibility = View.VISIBLE
        }

        enabledPinRadioButton.setOnClickListener {
            if (pinSubLayout.visibility == View.GONE) {
                pinSubLayout.visibility = View.VISIBLE
            }

        }

        disabledPinRadioButton.setOnClickListener {

            if (pinSubLayout.visibility == View.VISIBLE) {
                pinSubLayout.visibility = View.GONE
            }

        }

        changePasswordButton.setOnClickListener {
            /* Verify and Update New Password */
            val currentPassword = currentPasswordEditText.text.toString()
            val newPassword = passwordEditText.text.toString()
            val confirmNewPassword = confirmPasswordEditText.text.toString()

            if (currentPassword.isEmpty()) {
                currentPasswordEditText.error = "Please Enter Current Password"
            } else if (!isValidPassword(currentPassword)) {
                currentPasswordEditText.error = "Please Enter Valid Password"
            }

            if (newPassword.isEmpty()) {
                passwordEditText.error = "Please Enter New Password"
            } else if (!isValidPassword(newPassword)) {
                passwordEditText.error = "Please Enter a Password of length between 6-14 characters with 1 Lower case,1 Capital,1 Digit & 1 symbol"
            }

            if (confirmNewPassword.isEmpty()) {
                confirmPasswordEditText.error = "Please Enter Above Password Again"
            } else if (newPassword != confirmNewPassword) {
                confirmPasswordEditText.error = "Passwords are not Matching!!! Try Again"
            }

            if (arrayOf(currentPassword, newPassword, confirmNewPassword).all { it.isNotEmpty() && isValidPassword(it) }) {
                try {
                    val ded = DataEncryptionAndDecryption()
                    val passKey = ded.getSecretKey(currentPassword)
                    val originalPassword = ded.decryptData(ssbAccount.password, passKey)

                    if (currentPassword == originalPassword) {
                        val newSecretKey = ded.getSecretKey(newPassword)
                        val newEncryptedPassword = ded.encryptData(newPassword, newSecretKey)
                        val res = db.resetPassword(username, newEncryptedPassword)

                        if (res > 0) {
                            Toast.makeText(activity, "Password Changed SuccessFully", Toast.LENGTH_LONG).show()
                            activity.onBackPressed()
                        } else {
                            Toast.makeText(activity, "Failed to Change Password, Error Occurred!! Try again", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        currentPasswordEditText.text.clear()
                        Toast.makeText(activity, "Failed to Change Password!!, Current Password is InCorrect", Toast.LENGTH_LONG).show()
                    }

                } catch (e: IllegalBlockSizeException) {
                    //throw IllegalArgumentException("Invalid Login")
                    Toast.makeText(activity, "Please Enter a Password of length between 6-14 characters with 1 Lower case,1 Capital,1 Digit & 1 symbol", Toast.LENGTH_LONG).show()
                } catch (e: UnknownError) {
                    Toast.makeText(activity, "Please Enter a Password of length between 6-14 characters with 1 Lower case,1 Capital,1 Digit & 1 symbol", Toast.LENGTH_LONG).show()
                } catch (e: IOException) {
                    Toast.makeText(activity, "Please Enter a Password of length between 6-14 characters with 1 Lower case,1 Capital,1 Digit & 1 symbol", Toast.LENGTH_LONG).show()
                } catch (e: BadPaddingException) {
                    Toast.makeText(activity, "Failed to Change Password!!, Current Password is InCorrect", Toast.LENGTH_LONG).show()
                } catch (e: InvocationTargetException) {
                    Toast.makeText(activity, "Please Enter a Password of length between 6-14 characters with 1 Lower case,1 Capital,1 Digit & 1 symbol", Toast.LENGTH_LONG).show()
                }
            } else {
                return@setOnClickListener
            }
        }

        changePinButton.setOnClickListener {
            /*Update New PIN And PIN Status */
            if (enabledPinRadioButton.isChecked) {
                val newPin = pinEditText.text.toString()
                val confirmNewPin = confirmPinEditText.text.toString()

                if (newPin.isEmpty()) {
                    pinEditText.error = "Please Enter a 4 Digit PIN"
                } else if (newPin.length < 4) {
                    pinEditText.error = "Please Enter a Valid 4 Digit PIN"
                }

                if (confirmNewPin.isEmpty()) {
                    confirmPinEditText.error = "Please Re-Enter Above 4 Digit PIN"
                }

                if (newPin != confirmNewPin) {
                    Toast.makeText(activity, "PIN'S are Not Matching!! Please Try Again", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                if (newPin == confirmNewPin && newPin.length == 4) {
                    val ded = DataEncryptionAndDecryption()
                    val secreteKey = ded.getSecretKey(newPin)
                    val encryptedPin = ded.encryptData(newPin, secreteKey)
                    val updateResponse = db.updateEasyPin(username, encryptedPin, 1)

                    if (updateResponse > 0) {
                        Toast.makeText(activity, "EasyPin Changed SuccessFully", Toast.LENGTH_LONG).show()
                        activity.onBackPressed()
                    } else {
                        Toast.makeText(activity, "Failed To Update PIN,Please Try Again", Toast.LENGTH_LONG).show()
                    }
                } else {
                    return@setOnClickListener
                }
            } else if (disabledPinRadioButton.isChecked) {
                val pin = ByteArray(0)
                val updateResponse = db.updateEasyPin(username, pin, 0)

                if (updateResponse > 0) {
                    Toast.makeText(activity, "EasyPin Changed SuccessFully", Toast.LENGTH_LONG).show()
                    activity.onBackPressed()
                } else {
                    Toast.makeText(activity, "Failed To Update PIN,Please Try Again", Toast.LENGTH_LONG).show()
                }
            }
        }

        val changePasswordPinLinearLayout = view.findViewById<View>(R.id.change_password_pin_linear_layout) as LinearLayout
        changePasswordPinLinearLayout.setOnClickListener {}
    }

    override fun onAnimationRepeat(p0: Animation?) {
    }

    override fun onAnimationEnd(p0: Animation?) {
        val pinSubLayout = view?.findViewById<View>(R.id.pin_sub_layout) as LinearLayout
        val disabledPinRadioButton = view?.findViewById<View>(R.id.pin_disable_radio) as RadioButton

        if (disabledPinRadioButton.isChecked) {
            pinSubLayout.visibility = View.GONE
        }
    }

    override fun onAnimationStart(p0: Animation?) {
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

    override fun onResume() {
        super.onResume()
        getHomeActivityListener?.setSubTitle("Change Password OR PIN")
    }

    private fun changeLayoutsVisibility(activeLayout: LinearLayout, inActiveLayout: LinearLayout) {
        activeLayout.visibility = View.VISIBLE
        inActiveLayout.visibility = View.GONE
    }

}