package com.example.surendar_5541.ssbbanksystemdemoapplication

import android.support.v4.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import java.io.IOException
import java.lang.reflect.InvocationTargetException
import javax.crypto.BadPaddingException
import javax.crypto.IllegalBlockSizeException

class ResetPasswordFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.reset_password_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val db = DataBaseHandler(activity)
        val accountNumberEditText = view?.findViewById<View>(R.id.account_number_editText) as EditText
        val customerIdEditText = view?.findViewById<View>(R.id.customer_id_editText) as EditText
        val usernameEditText = view?.findViewById<View>(R.id.username_edit_text) as EditText
        val newPasswordEditText = view?.findViewById<View>(R.id.new_passwordSet_edit_text) as EditText
        val confirmNewPasswordEditText = view?.findViewById<View>(R.id.new_confirm_password_edit_text) as EditText
        val resetPasswordButton = view?.findViewById<View>(R.id.reset_password_button) as Button

        resetPasswordButton.setOnClickListener {
            val accountNumber = accountNumberEditText.text.toString()
            val customerId = customerIdEditText.text.toString()
            val newPassword = newPasswordEditText.text.toString()
            val username = usernameEditText.text.toString()
            val newConfirmPassword = confirmNewPasswordEditText.text.toString()
            if(accountNumber.isEmpty()) {
                accountNumberEditText.error = "Please enter account Number"
            }

            if(customerId.isEmpty()) {
                customerIdEditText.error = "Please enter CustomerId"
            }

            if(username.isEmpty()) {
                usernameEditText.error = "Please Enter Username"
            }

            if(!isValidPassword(newPassword)) {
                newPasswordEditText.error = "Please Enter a Password of length between 6-14 characters with 1 Lower case,1 Capital,1 Digit & 1 symbol"
                return@setOnClickListener
            }

            if(newPassword != newConfirmPassword) {
                newPasswordEditText.text.clear()
                confirmNewPasswordEditText.text.clear()
                Toast.makeText(activity, "Passwords are not Matching!! Try Again", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if(!accountNumber.isEmpty() && !customerId.isEmpty() && !username.isEmpty() && !newPassword.isEmpty()) {
                val bankAccount = db.getBankAccount(accountNumber.toLong())
                val ssbAccount = db.getSsbAccount(username)
                if(bankAccount.customerId == ssbAccount.customerId) {
                    try {
                        val ded = DataEncryptionAndDecryption()
                        val passKey = ded.getSecretKey(newPassword)
                        val res = db.resetPassword(username, ded.encryptData(newPassword, passKey))
                        if (res > 0) {
                            Toast.makeText(activity, "Password updated Successfully Please login to continue", Toast.LENGTH_LONG).show()
                            activity.onBackPressed()
                        } else {
                            Toast.makeText(activity, "Internal Error Occurred !! please try again later", Toast.LENGTH_LONG).show()
                        }
                    } catch (e: IllegalBlockSizeException) {
                        //throw IllegalArgumentException("Invalid Login")
                        newPasswordEditText.error = "Please Enter a Password of length between 6-14 characters with 1 Lower case,1 Capital,1 Digit & 1 symbol"

                    } catch (e: UnknownError) {
                        newPasswordEditText.error = "Please Enter a Password of length between 6-14 characters with 1 Lower case,1 Capital,1 Digit & 1 symbol"
                    } catch (e: IOException) {
                        newPasswordEditText.error = "Please Enter a Password of length between 6-14 characters with 1 Lower case,1 Capital,1 Digit & 1 symbol"
                    } catch (e: BadPaddingException) {
                        newPasswordEditText.error = "Please Enter a Password of length between 6-14 characters with 1 Lower case,1 Capital,1 Digit & 1 symbol"
                    } catch (e: InvocationTargetException) {
                        newPasswordEditText.error = "Please Enter a Password of length between 6-14 characters with 1 Lower case,1 Capital,1 Digit & 1 symbol"
                    }
                }else {
                    Toast.makeText(activity,"Invalid !! please Check the details and try again",Toast.LENGTH_LONG).show()
                    newPasswordEditText.text.clear()
                    confirmNewPasswordEditText.text.clear()
                    customerIdEditText.text.clear()
                    accountNumberEditText.text.clear()
                    usernameEditText.text.clear()
                }
            }
        }
    }

    private fun isValidPassword(password: String): Boolean {
        val passwordArray = password.toCharArray()
        var isThereSpecialChar = false
        for(char in passwordArray) {
            if(!(char.isLetter() || char.isDigit())){
                isThereSpecialChar = true
            }
        }
        return password.length in 6..18 && passwordArray.any {it.isDigit()} && passwordArray.any { it.isLowerCase() } && passwordArray.any { it.isUpperCase() } && isThereSpecialChar
    }

}
