package com.btjoe.echo.Activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import com.btjoe.echo.Adapter.NavDrawerAdapter
import com.btjoe.echo.Fragment.HomeFragment
import com.btjoe.echo.Fragment.PlayerFragment
import com.btjoe.echo.R

class HomeActivity : AppCompatActivity(){

    @SuppressLint("StaticFieldLeak")
    object Statified{
        var drawerLayout : DrawerLayout? = null
    }
    var navDrawerIconList : ArrayList<String> = arrayListOf()
    var navDrawerIconImage = intArrayOf(R.drawable.navigation_allsongs, R.drawable.navigation_favorites,
            R.drawable.navigation_settings, R.drawable.navigation_aboutus)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        Statified.drawerLayout = findViewById(R.id.drawer_layout)

        val toggle = ActionBarDrawerToggle(
                this@HomeActivity, Statified.drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        Statified.drawerLayout?.addDrawerListener(toggle)
        toggle.syncState()

        val homeFragment = HomeFragment()
        this.supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment, homeFragment, "HomeFragment")
                .commit()

        navDrawerIconList.add("All Songs")
        navDrawerIconList.add("Favourites")
        navDrawerIconList.add("Settings")
        navDrawerIconList.add("About Us")

        val navDrawerAdapter = NavDrawerAdapter(navDrawerIconList, navDrawerIconImage, this)
        navDrawerAdapter.notifyDataSetChanged()

        val navRecyclerView = findViewById<RecyclerView>(R.id.nav_recyclerView)
        navRecyclerView.layoutManager = LinearLayoutManager(this)
        navRecyclerView.itemAnimator = DefaultItemAnimator()
        navRecyclerView.adapter = navDrawerAdapter
        navRecyclerView.setHasFixedSize(true)

    }

    override fun onStart() {
        super.onStart()
    }

}

