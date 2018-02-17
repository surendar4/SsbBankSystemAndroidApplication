package com.example.surendar_5541.ssbbanksystemdemoapplication

import android.app.DatePickerDialog
import android.app.Fragment
import android.content.Context
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import java.text.SimpleDateFormat
import java.util.*


class AccountStatementFragment : Fragment() {
    private var getActivityListener: GetHomeActivity? = null
    private var mLayoutManager: RecyclerView.LayoutManager? = null
    private val c = Calendar.getInstance() // current Date
    private val mYear = c.get(Calendar.YEAR) // current year
    private val mMonth = c.get(Calendar.MONTH) // current month
    private val mDay = c.get(Calendar.DAY_OF_MONTH) // current day

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.account_statement_fragment, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            getActivityListener = activity as GetHomeActivity
        } catch (castException: ClassCastException) {
            /** The activity does not implement the listener. */
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val rootLayout = view.findViewById<View>(R.id.root_layout) as LinearLayout
        rootLayout.setOnClickListener { }

        val nameTextView = view.findViewById<View>(R.id.name_text_view) as TextView
        val accountNumberTextView = view.findViewById<View>(R.id.account_number_text_view) as TextView
        val toDateEditText = view.findViewById<View>(R.id.toDate) as EditText
        val fromDateEditText = view.findViewById<View>(R.id.fromDate) as EditText
        val viewTransactionsButton = view.findViewById<View>(R.id.view_transactions_button_view) as Button
        val fromDateIcon = view.findViewById<View>(R.id.from_date_icon) as ImageView
        val toDateIcon = view.findViewById<View>(R.id.to_date_icon) as ImageView

        val db = DataBaseHandler(activity)
        val customer = db.getCustomer(getActivityListener?.getCustomerId())
        val accountNumber = db.getAccountNumber(customer.customerId)

        nameTextView.text = customer.customerName
        accountNumberTextView.text = accountNumber.toString()

        fromDateEditText.setOnClickListener {
            /* Sets from Date */
            setFromDate()
        }

        fromDateIcon.setOnClickListener {
            /* When user clicks on FromDate Icon */
            setFromDate()
        }

        toDateEditText.setOnClickListener {
            setToDate()
        }

        toDateIcon.setOnClickListener {
            setToDate()
        }



        viewTransactionsButton.setOnClickListener {
            val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
            val fromDate: Long
            val toDate:Long

            if(fromDateEditText.text.isNotEmpty() && toDateEditText.text.isNotEmpty()) {
                fromDate = simpleDateFormat.parse(fromDateEditText.text.toString()).time
                toDate = simpleDateFormat.parse(toDateEditText.text.toString()).time

            } else {
                 fromDate = 0
                 toDate =0
            }

            hideKeyBoard()
            val noTransactionsTextView = view.findViewById<View>(R.id.no_transactions_found_textView) as TextView
            val dateAmountLayout = view.findViewById<View>(R.id.date_amount_layout) as LinearLayout
            val avlBalTextView = view.findViewById<View>(R.id.avl_bal_textView) as TextView
            val balanceOnTextView = view.findViewById<View>(R.id.avl_bal_on_textView) as TextView
            val dateTextView = view.findViewById<View>(R.id.date_text_view) as TextView

            val recyclerView = view.findViewById<View>(R.id.account_statement_statement_recycler_view) as RecyclerView /* Recycler View to show Transactions */
            recyclerView.setHasFixedSize(true)
            mLayoutManager = LinearLayoutManager(activity)
            recyclerView.layoutManager = mLayoutManager
            recyclerView.adapter = null

            if (fromDate != 0L || toDate != 0L) {
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)

                if (sdf.format(fromDate) > sdf.format(toDate)) {
                    setVisibility(View.GONE)
                    Toast.makeText(activity, "To Date should be greater than From Date", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                } else {
                    setVisibility(View.VISIBLE)
                    avlBalTextView.text = String.format("%.2f", db.getAccountBalance(accountNumber))
                    val balanceOn = db.balanceOn(accountNumber, fromDate).toString() + " INR "
                    balanceOnTextView.text = balanceOn
                    dateTextView.text = sdf.format(fromDate)

                    val statements = db.getTransactionsBetweenDates(accountNumber, fromDate, toDate)
                    if (statements.size > 0) {
                        dateAmountLayout.visibility = View.VISIBLE
                        noTransactionsTextView.visibility = View.GONE
                        val mAdapter = AccountStatementAdapter(statements)
                        recyclerView.adapter = mAdapter
                    } else {
                        dateAmountLayout.visibility = View.GONE
                        noTransactionsTextView.visibility = View.VISIBLE
                    }
                }
            } else {
                setVisibility(View.GONE)
                Toast.makeText(activity, "Please Select Dates", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setVisibility(visibility: Int) {
        val dateAmountLayout = view.findViewById<View>(R.id.date_amount_layout) as LinearLayout
        val avlBalLayout = view.findViewById<View>(R.id.avl_bal_layout) as LinearLayout
        val avlBalOnLayout = view.findViewById<View>(R.id.avl_bal_on_layout) as LinearLayout

        dateAmountLayout.visibility = visibility
        avlBalLayout.visibility = visibility
        avlBalOnLayout.visibility = visibility
    }

    private fun setFromDate() {
        val fromDateEditText = view.findViewById<View>(R.id.fromDate) as EditText

        val datePickerDialog = DatePickerDialog(activity, object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int) {
                val fromDate = "$dayOfMonth/${monthOfYear + 1}/$year"
                fromDateEditText.setText(fromDate)
            }
        }, mYear, mMonth, mDay)
        datePickerDialog.show()
    }

    private fun setToDate() {
        val toDateEditText = view.findViewById<View>(R.id.toDate) as EditText

        val datePickerDialog = DatePickerDialog(activity, object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int) {
                val toDate = "$dayOfMonth/${monthOfYear + 1}/$year"
                toDateEditText.setText(toDate)
            }
        }, mYear, mMonth, mDay)
        datePickerDialog.show()
    }

    private fun hideKeyBoard() {
        try {
            val inputMethodManager = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(activity.currentFocus?.windowToken, 0)
        } catch (e: Exception) {

        }

    }

    override fun onResume() {
        super.onResume()
        val db = DataBaseHandler(activity)
        val customer = db.getCustomer(getActivityListener?.getCustomerId())
        val accountNumber = db.getAccountNumber(customer.customerId)
        val toDateEditText = view.findViewById<View>(R.id.toDate) as EditText
        val fromDateEditText = view.findViewById<View>(R.id.fromDate) as EditText
        if (fromDateEditText.text.toString().isNotEmpty() && toDateEditText.text.isNotEmpty()) {
            val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
            val fromDate = simpleDateFormat.parse(fromDateEditText.text.toString()).time
            val toDate = simpleDateFormat.parse(toDateEditText.text.toString()).time

            hideKeyBoard()
            val noTransactionsTextView = view.findViewById<View>(R.id.no_transactions_found_textView) as TextView
            val dateAmountLayout = view.findViewById<View>(R.id.date_amount_layout) as LinearLayout
            val avlBalTextView = view.findViewById<View>(R.id.avl_bal_textView) as TextView
            val balanceOnTextView = view.findViewById<View>(R.id.avl_bal_on_textView) as TextView
            val dateTextView = view.findViewById<View>(R.id.date_text_view) as TextView

            val recyclerView = view.findViewById<View>(R.id.account_statement_statement_recycler_view) as RecyclerView /* Recycler View to show Transactions */
            recyclerView.setHasFixedSize(true)
            mLayoutManager = LinearLayoutManager(activity)
            recyclerView.layoutManager = mLayoutManager
            recyclerView.adapter = null

            if (fromDate != 0L || toDate != 0L) {
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)

                if (sdf.format(fromDate) > sdf.format(toDate)) {
                    setVisibility(View.GONE)
                    Toast.makeText(activity, "To Date should be greater than From Date", Toast.LENGTH_SHORT).show()
                    return
                } else {
                    setVisibility(View.VISIBLE)
                    avlBalTextView.text = String.format("%.2f", db.getAccountBalance(accountNumber))
                    val balanceOn = db.balanceOn(accountNumber, fromDate).toString() + " INR "
                    balanceOnTextView.text = balanceOn
                    dateTextView.text = sdf.format(fromDate)

                    val statements = db.getTransactionsBetweenDates(accountNumber, fromDate, toDate)
                    if (statements.size > 0) {
                        dateAmountLayout.visibility = View.VISIBLE
                        noTransactionsTextView.visibility = View.GONE
                        val mAdapter = AccountStatementAdapter(statements)
                        recyclerView.adapter = mAdapter
                    } else {
                        dateAmountLayout.visibility = View.GONE
                        noTransactionsTextView.visibility = View.VISIBLE
                    }
                }
            } else {
                setVisibility(View.GONE)
                Toast.makeText(activity, "Please Select Dates", Toast.LENGTH_LONG).show()
            }
        }
    }

}
