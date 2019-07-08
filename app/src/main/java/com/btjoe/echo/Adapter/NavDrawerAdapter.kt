package com.btjoe.echo.Adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.btjoe.echo.Activity.HomeActivity
import com.btjoe.echo.Fragment.AboutUsFragment
import com.btjoe.echo.Fragment.FavouriteFragment
import com.btjoe.echo.Fragment.HomeFragment
import com.btjoe.echo.Fragment.SettingFragment
import com.btjoe.echo.R

class NavDrawerAdapter(_contentList: ArrayList<String>, _getImages: IntArray, _context: Context)
    : RecyclerView.Adapter<NavDrawerAdapter.NavViewHolder>(){

    var contentList : ArrayList<String>? = null
    var getImages : IntArray? = null
    var mcontext : Context? = null

    init {
        this.contentList = _contentList
        this.getImages = _getImages
        this.mcontext = _context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NavViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.custom_nav_drawer, parent, false)
        return NavViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return (contentList as ArrayList).size
    }

    override fun onBindViewHolder(holder: NavViewHolder, position: Int) {
        holder.iconGET?.setBackgroundResource(getImages?.get(position) as Int)
        holder.textGET?.text = contentList?.get(position)
        holder.contentHolder?.setOnClickListener({
            when (position) {
                0 -> {
                    val homeFragment = HomeFragment()
    
                    (mcontext as HomeActivity).supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.fragment, homeFragment)
                            .commit()
                }
                1 -> {
                    val favouriteFragment = FavouriteFragment()
    
                    (mcontext as HomeActivity).supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.fragment, favouriteFragment)
                            .commit()
                }
                2 -> {
                    val settingFragment = SettingFragment()
    
                    (mcontext as HomeActivity).supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.fragment, settingFragment)
                            .commit()
                }
                3 -> {
                    val aboutUsFragment = AboutUsFragment()
    
                    (mcontext as HomeActivity).supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.fragment, aboutUsFragment)
                            .commit()
                }
            }
            HomeActivity.Statified.drawerLayout?.closeDrawers()
        })
    }

    class NavViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        var iconGET: ImageView? = null
        var textGET: TextView? = null
        var contentHolder: RelativeLayout? = null

        init {
            iconGET = itemView?.findViewById(R.id.nav_icon)
            textGET = itemView?.findViewById(R.id.nav_text)
            contentHolder = itemView?.findViewById(R.id.nav_item_content_holder)
        }
    }

}