package com.example.surendar_5541.ssbbanksystemdemoapplication

import android.app.Fragment
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*


class MyAccountFragment : Fragment() {
    private var getHomeActivityListener: GetHomeActivity? = null
    private var mLayoutManager: RecyclerView.LayoutManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            getHomeActivityListener = activity as HomeActivity
        }catch (castException: ClassCastException) {

        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.my_account_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val accountOptions = mutableListOf("Account Summary", "Mini Statement", "Account Statement")
        val recyclerView = view.findViewById<View>(R.id.account_recyclerView) as RecyclerView
        recyclerView.setHasFixedSize(true)
        mLayoutManager = LinearLayoutManager(activity)
        recyclerView.layoutManager = mLayoutManager
        val mAdapter = MyAccountOptionsAdapter(accountOptions)
        recyclerView.adapter = mAdapter

        mAdapter.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(item: String) {
                when (accountOptions.indexOf(item)) {
                    0 -> {
                        (activity as HomeActivity).addFragmentToHome(R.id.fragment_to_replace_grid, AccountSummaryFragment(), "Account Summary")
                    }
                    1 -> {
                        (activity as HomeActivity).addFragmentToHome(R.id.fragment_to_replace_grid, MiniStatementFragment(), "Mini Statement")
                    }
                    2 -> {
                        (activity as HomeActivity).addFragmentToHome(R.id.fragment_to_replace_grid, AccountStatementFragment(), "Account Statement")
                    }
                }
            }
        })

    }

    override fun onResume() {
        super.onResume()
        getHomeActivityListener?.setSubTitle("My Accounts")
    }
}
