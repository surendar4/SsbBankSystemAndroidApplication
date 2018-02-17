package com.example.surendar_5541.ssbbanksystemdemoapplication

import android.app.AlertDialog
import android.app.Fragment
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView.OnItemClickListener
import android.widget.GridView
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast

class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, GetHomeActivity {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = DataBaseHandler(this)

        title = "Home"
        setContentView(R.layout.activity_home)
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        val drawer = findViewById<View>(R.id.drawer_layout) as DrawerLayout  //Navigation Drawer
        val toggle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        val navigationView = findViewById<View>(R.id.nav_view) as NavigationView
        navigationView.setNavigationItemSelectedListener(this)
        navigationView.setCheckedItem(R.id.nav_home)

        val backIcon = findViewById<View>(R.id.back_icon) as ImageView
        backIcon.setOnClickListener {
            onBackPressed()
        }

        val nameBarTextView = findViewById<View>(R.id.name_bar_text_view) as TextView

        if (intent.hasExtra("username")) {
            val username = intent.extras.getString("username")
            setLoginStatus(username)
            val customerName = db.getCustomer(getCustomerId()).customerName.capitalize()
            nameBarTextView.text = customerName
        }

        val gridView = findViewById<View>(R.id.gridView) as GridView
        gridView.adapter = ImageAdapter(this)
        gridView.onItemClickListener = OnItemClickListener { _, _, position, _ ->
            when (position) {
                0 -> addFragmentToHome(R.id.fragment_to_replace_grid, MyAccountFragment(), "My Accounts")
                1 -> addFragmentToHome(R.id.fragment_to_replace_grid, FundsTransferFragment(), "Funds Transfer")
                else -> Toast.makeText(this@HomeActivity, "This feature not available now", Toast.LENGTH_SHORT).show()
            }
        }

        val logoutIcon = findViewById<View>(R.id.logout_icon) as ImageView
        logoutIcon.setOnClickListener {
            logoutAlert()
        }

    }

    override fun getCustomerId(): String {
        val db = DataBaseHandler(this)
        return db.getCustomerId(getSharedPreferences("loginStatus", Context.MODE_PRIVATE).getString("username", "null"))
    }

    private fun setLoginStatus(username: String) {
        val pref = getSharedPreferences("loginStatus", Context.MODE_PRIVATE)
        val toEdit = pref.edit()
        toEdit.putString("status", "In")
        toEdit.putString("username", username)
        toEdit.apply()
        toEdit.commit()
    }

    override fun setSubTitle(title: String) {
        this.title = title
        val navigationView = findViewById<View>(R.id.nav_view) as NavigationView
        when (title) {
            "Funds Transfer" -> navigationView.setCheckedItem(R.id.nav_funds_transfer)
            "My Accounts" -> navigationView.setCheckedItem(R.id.nav_my_accounts)
            "Change Password OR PIN" -> navigationView.setCheckedItem(R.id.nav_change_password_pin)
            "Home" -> navigationView.setCheckedItem(R.id.nav_home)
        }
    }

    override fun onBackPressed() {

        val drawer = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else if (fragmentManager.backStackEntryCount == 0) {
            exitAlert()
        } else {
            super.onBackPressed()
        }

        if (fragmentManager.backStackEntryCount == 0) {
            val home = "Home"
            val navigationView = findViewById<View>(R.id.nav_view) as NavigationView
            navigationView.setCheckedItem(R.id.nav_home)
            title = home
        }
    }

    private fun logoutAlert() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setMessage("Are you sure want to logout")
        alertDialogBuilder.setPositiveButton("Logout", object : DialogInterface.OnClickListener {
            override fun onClick(arg0: DialogInterface, arg1: Int) {
                val loginPref = getSharedPreferences("loginStatus", Context.MODE_PRIVATE)
                val edit = loginPref.edit()
                edit.clear()
                edit.apply()
                edit.commit()
                startActivity(Intent(this@HomeActivity, MainActivity::class.java))
                this@HomeActivity.finish()
            }
        })
        alertDialogBuilder.setNegativeButton("Cancel", object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface, which: Int) {
                // finish()
            }
        })
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun exitAlert() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setMessage("Are you sure want to Exit")
        alertDialogBuilder.setPositiveButton("Exit", object : DialogInterface.OnClickListener {
            override fun onClick(arg0: DialogInterface, arg1: Int) {
                this@HomeActivity.finish()
            }
        })
        alertDialogBuilder.setNegativeButton("Cancel", object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface, which: Int) {
                // finish()
            }
        })
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.home, menu)
        val db = DataBaseHandler(this)
        val bankCustomer = db.getCustomer(getCustomerId())
        val navNameTextView: TextView = findViewById<View>(R.id.nav_name_text_view) as TextView
        val emailTextView: TextView = findViewById<View>(R.id.nav_email_text_view) as TextView

        navNameTextView.text = bankCustomer.customerName
        emailTextView.text = bankCustomer.email

        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun addFragmentToHome(frameId: Int, fragment: Fragment, title: String) { /* Adds Fragment by Replace */
        setSubTitle(title)
        val ft = fragmentManager.beginTransaction()
        ft.replace(frameId, fragment).addToBackStack(title).commit()

    }

    override fun addAddFragmentToHome(frameId: Int, fragment: Fragment, title: String) {
        setSubTitle(title)
        val ft = fragmentManager.beginTransaction()
        ft.add(frameId, fragment).addToBackStack(title).commit()
    }

    override fun paymentReceipt(frameId: Int, fragment: Fragment, title: String) {
        setSubTitle(title)
        fragmentManager.popBackStack(null, android.app.FragmentManager.POP_BACK_STACK_INCLUSIVE)
        val ft = fragmentManager.beginTransaction()
        ft.add(frameId, fragment).addToBackStack(null).commit()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_my_accounts -> addFragmentToHome(R.id.fragment_to_replace_grid, MyAccountFragment(), "My Accounts")
            R.id.nav_funds_transfer -> addFragmentToHome(R.id.fragment_to_replace_grid, FundsTransferFragment(), "Funds Transfer")
            R.id.nav_change_password_pin -> addFragmentToHome(R.id.fragment_to_replace_grid, ChangePasswordAndPinFragment(), "Change Password OR PIN")
            R.id.nav_home -> {
                fragmentManager.popBackStack(null, android.app.FragmentManager.POP_BACK_STACK_INCLUSIVE)
                title = "Home"
            }
            else -> Toast.makeText(this@HomeActivity, "This feature not available now", Toast.LENGTH_SHORT).show()
        }

        val drawer = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onResume() {
        super.onResume()
        val navigationView = findViewById<View>(R.id.nav_view) as NavigationView
        navigationView.setCheckedItem(R.id.nav_home)
    }
}
