package com.example.surendar_5541.ssbbanksystemdemoapplication

import android.app.Fragment
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*

class FundsTransferFragment : Fragment() {
    private var getHomeActivityListener: GetHomeActivity? = null
    private var mLayoutManager: RecyclerView.LayoutManager? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.funds_transfer_fragment, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            getHomeActivityListener = activity as HomeActivity
        } catch (classCast: ClassCastException) {

        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val recyclerView = view.findViewById<View>(R.id.recycler_view) as RecyclerView
        recyclerView.setHasFixedSize(true)
        mLayoutManager = LinearLayoutManager(activity)
        recyclerView.layoutManager = mLayoutManager
        val OPTION_THROUGH_IMPS_NEFT = "Transfer Through IMPS,NEFT"
        val OPTION_QUICK_TRANSFER = "Quick Transfer"
        val OPTION_BENEFICIARIES = "View/Modify/Delete Beneficiaries"
        val options = mutableListOf(OPTION_THROUGH_IMPS_NEFT, OPTION_QUICK_TRANSFER, OPTION_BENEFICIARIES)
        val mAdapter = FundsTransferAdapter(options)
        recyclerView.adapter = mAdapter

        mAdapter.setOnItemClickListener(object : OnItemClickListener {
          override  fun onItemClick(item: String) {
              when (item) {
                  OPTION_THROUGH_IMPS_NEFT -> { (activity as HomeActivity).addFragmentToHome(R.id.fragment_to_replace_grid,InterBankTransferFragment(),"Inter Bank Transfer")
                  }

                  OPTION_QUICK_TRANSFER -> { (activity as HomeActivity).addFragmentToHome(R.id.fragment_to_replace_grid,QuickTransferFragment(),"Quick Transfer")
                  }

                  OPTION_BENEFICIARIES -> (activity as HomeActivity).addFragmentToHome(R.id.fragment_to_replace_grid, ViewBeneficiariesFragment(),"View Beneficiaries")
              }
          }
        })

    }

    override fun onResume() {
        super.onResume()
        getHomeActivityListener?.setSubTitle("Funds Transfer")
    }
}