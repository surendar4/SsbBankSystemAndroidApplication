package com.example.surendar_5541.ssbbanksystemdemoapplication

import android.app.Fragment
import android.content.CursorLoader
import android.content.Loader
import android.database.Cursor
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.app.LoaderManager
import android.content.Context
import android.os.AsyncTask
import android.provider.ContactsContract
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView

class ViewBeneficiariesFragment : Fragment(), LoaderManager.LoaderCallbacks<Cursor>{
    private var mLayoutManager: RecyclerView.LayoutManager? = null
    private var getActivityListener:GetHomeActivity? = null
    val BENEFICIARIES_LOADER = 0
   // var simpleCursorAdapter: SimpleCursorAdapter? = null
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
        return inflater.inflate(R.layout.view_beneficiaries_fragment, container, false)
    }

    override  fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val addBeneficiaryFabButton = view?.findViewById<View>(R.id.fab_add_beneficiary) as FloatingActionButton

        addBeneficiaryFabButton.setOnClickListener {
            addBeneficiaryFabButton.hide()
            getActivityListener?.addFragmentToHome(R.id.fragment_to_replace_grid, AddBeneficiaryFragment(), "Add Beneficiary")
        }

        val relativeLayout = view.findViewById<View>(R.id.relative_layout_beneficiaries) as RelativeLayout
        relativeLayout.setOnClickListener { }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

       /* mAdapter.setOnItemClickListener(object: OnItemClickListener {
            override fun onItemClick(item: String) {

                Toast.makeText(activity,"ben ac: $item",Toast.LENGTH_SHORT).show()
            }

        }) */

        /*val viewBenListView  = view.findViewById<View>(R.id.view_beneficiaries_list_view) as ListView
        val from = arrayOf(cursor?.getString(0),cursor?.getString(1))
        val to = intArrayOf(R.id.beneficiary_name_text_view,R.id.ben_account_number_text_view)
        simpleCursorAdapter = SimpleCursorAdapter(activity,R.layout.view_beneficiaries_list_item,cursor,from,to,0)
        viewBenListView.adapter = simpleCursorAdapter */

    }

    override fun onResume() {
        super.onResume()
        /*  if(loaderManager.getLoader<Cursor>(BENEFICIARIES_LOADER) != null) {
            loaderManager.restartLoader(BENEFICIARIES_LOADER, null, this)
        } */
        loaderManager.initLoader(BENEFICIARIES_LOADER,null,this)
       /* val db = DataBaseHandler(activity)
        val noBeneficiariesFoundTextView = view.findViewById<View>(R.id.beneficiaries_not_found_TextView) as TextView
        val recyclerView = view.findViewById<View>(R.id.beneficiaries_recycler_view) as RecyclerView /* Setting up Recycler View */
        recyclerView.setHasFixedSize(true)
        mLayoutManager = LinearLayoutManager(activity)
        recyclerView.layoutManager = mLayoutManager

        val beneficiaries = mutableListOf<Pair<String, Number>>()
        val registeredBeneficiaries = db.getBeneficiaries(getActivityListener?.getCustomerId())
        for (beneficiary in registeredBeneficiaries) {
            beneficiaries.add(Pair(beneficiary.nickName, beneficiary.accountNumber))
        }

        if (beneficiaries.size == 0) {
            noBeneficiariesFoundTextView.visibility = View.VISIBLE
        } else {
            noBeneficiariesFoundTextView.visibility = View.GONE
        }

        val mAdapter = ViewBeneficiariesAdapter(beneficiaries)
        recyclerView.adapter = mAdapter
        mAdapter.notifyDataSetChanged()

        recyclerView.addOnItemTouchListener(RecyclerTouchListener(activity, object : ClickListener {
            override fun onClick(view: View, position: Int) { //Catching clicks
                val bundle = Bundle()
                val fragment = ViewBeneficiaryFragment()
                fragment.arguments = bundle
                // Toast.makeText(activity," $view & ${beneficiaries.size}",Toast.LENGTH_SHORT).show()
                if (beneficiaries.size > 0) {
                    bundle.putString("accountNumber", beneficiaries[position].second.toString()) // Starts View Beneficiary Fragment
                    getActivityListener?.addFragmentToHome(R.id.fragment_to_replace_grid, fragment, "View Beneficiary")
                }
            }

        })) */
    }

    override fun onCreateLoader(id: Int, bundle: Bundle?): Loader<Cursor> {
        val cp = MyContentProvider()
        val uri = cp.CONTENT_SSB_BENEFICIAFIES_URI
        val projection = arrayOf(DataBaseHandler.CUSTOMER_ID, DataBaseHandler.BENEFICIARY_NICK_NAME, DataBaseHandler.BENEFICIARY_NAME, DataBaseHandler.BENEFICIARY_ACCOUNT_NUMBER, DataBaseHandler.BENEFICIARY_IFSC, DataBaseHandler.BENEFICIARY_BANK, DataBaseHandler.BENEFICIARY_BRANCH_NAME)
        val selection = DataBaseHandler.CUSTOMER_ID + "= ?"
        val selectionArgs = arrayOf(getActivityListener?.getCustomerId())
        return CursorLoader(activity,uri,projection,selection,selectionArgs,null)
    }

    override fun onLoaderReset(loader: Loader<Cursor>?) {
          loader?.reset()
    }

    override fun onLoadFinished(cursorLoader: Loader<Cursor>?, cursor: Cursor?) {
        val noBeneficiariesFoundTextView = view.findViewById<View>(R.id.beneficiaries_not_found_TextView) as TextView
        val beneficiaries = mutableListOf<Pair<String, Number>>()
        // val registeredBeneficiaries = db.getBeneficiaries(getActivityListener?.getCustomerId())
        val registeredBeneficiaries = getBeneficiariesFromCursor(cursor)
        for (beneficiary in registeredBeneficiaries) {
            beneficiaries.add(Pair(beneficiary.nickName, beneficiary.accountNumber))
        }

        if (beneficiaries.size == 0) {
            noBeneficiariesFoundTextView.visibility = View.VISIBLE
        } else {
            noBeneficiariesFoundTextView.visibility = View.GONE
        }
        val mAdapter = ViewBeneficiariesAdapter(beneficiaries)
        val recyclerView = view.findViewById<View>(R.id.beneficiaries_recycler_view) as RecyclerView /* Setting up Recycler View */
        recyclerView.setHasFixedSize(true)
        mLayoutManager = LinearLayoutManager(activity)
        recyclerView.layoutManager = mLayoutManager
        recyclerView.adapter = mAdapter
        mAdapter.notifyDataSetChanged()

        recyclerView.addOnItemTouchListener(RecyclerTouchListener(activity, object : ClickListener {
            override fun onClick(view: View, position: Int) { //Catching clicks
                val bundle = Bundle()
                val fragment = ViewBeneficiaryFragment()
                fragment.arguments = bundle
                // Toast.makeText(activity," $view & ${beneficiaries.size}",Toast.LENGTH_SHORT).show()
                if (beneficiaries.size > 0) {
                    bundle.putString("accountNumber", beneficiaries[position].second.toString()) // Starts View Beneficiary Fragment
                    getActivityListener?.addFragmentToHome(R.id.fragment_to_replace_grid, fragment, "View Beneficiary")
                }
            }

        }))


    }

    private fun getBeneficiariesFromCursor(cursor: Cursor?): MutableList<BeneficiaryAccount> {
        val beneficiaries = mutableListOf<BeneficiaryAccount>()
        if(cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    beneficiaries.add(BeneficiaryAccount(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getLong(3), cursor.getString(4), cursor.getString(5), cursor.getString(6)))
                } while (cursor.moveToNext())
            }
        }
        return beneficiaries
    }

}
/*
class getRegisteredBeneficiaries(context: Context, listener: GetRegisteredBeneficiaries) : AsyncTask<String, Void, Cursor>(){
    val db = DataBaseHandler(context)
    val listener: GetRegisteredBeneficiaries?
    val context: Context?
    init {
        this.listener = listener
        this.context = context
    }

    override fun doInBackground(vararg customerId: String): Cursor {
        val cursor = onCreateLoader()
    }

    override fun onPostExecute(result: Cursor?) {
        super.onPostExecute(result)
    }

     fun onCreateLoader(customerId: String): Loader<Cursor> {
        val cp = MyContentProvider()
        val uri = cp.CONTENT_SSB_BENEFICIAFIES_URI
        val projection = arrayOf(DataBaseHandler.CUSTOMER_ID, DataBaseHandler.BENEFICIARY_NICK_NAME, DataBaseHandler.BENEFICIARY_NAME, DataBaseHandler.BENEFICIARY_ACCOUNT_NUMBER, DataBaseHandler.BENEFICIARY_IFSC, DataBaseHandler.BENEFICIARY_BANK, DataBaseHandler.BENEFICIARY_BRANCH_NAME)
        val selection = DataBaseHandler.CUSTOMER_ID + "= ?"
        val selectionArgs = arrayOf()
        return CursorLoader(context,uri,projection,selection,selectionArgs,null)
    }

} */