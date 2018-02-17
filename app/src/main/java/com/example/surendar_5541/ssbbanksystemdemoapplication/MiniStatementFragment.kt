package com.example.surendar_5541.ssbbanksystemdemoapplication

import android.app.Fragment
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView


class MiniStatementFragment : Fragment() {
    private var getActivityListener:GetHomeActivity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            getActivityListener = activity as GetHomeActivity
        } catch (castException: ClassCastException) {
            /** The activity does not implement the listener. */
        }
    }
    private var mLayoutManager: RecyclerView.LayoutManager? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
         super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.min_statement_fragment,container,false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val db = DataBaseHandler(activity)
        val customerId = getActivityListener?.getCustomerId()

        val recyclerView = view.findViewById<View>(R.id.mini_statement_recycler_view) as RecyclerView
        recyclerView.setHasFixedSize(true)
        mLayoutManager = LinearLayoutManager(activity)
        recyclerView.layoutManager = mLayoutManager
        val miniStatements = db.getMiniStatement(db.getAccountNumber(customerId))
        val mAdapter = MiniStatementFragmentAdapter(miniStatements.asReversed())
        recyclerView.adapter = mAdapter

        val amountTextView = view.findViewById<View>(R.id.mini_amount_textView) as TextView
        amountTextView.setOnClickListener{ }

        val avlBalTextView = view.findViewById<View>(R.id.avl_bal_textView) as TextView
        avlBalTextView.text = String.format("%.2f",db.getAccountBalance(db.getAccountNumber(customerId)))
    }
}