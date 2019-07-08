package com.btjoe.echo.Adapter

import android.content.Context
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.btjoe.echo.Fragment.PlayerFragment
import com.btjoe.echo.R
import com.btjoe.echo.Songs

class FavouriteAdapter(_songDetails: ArrayList<Songs>, _context: Context) : RecyclerView.Adapter<FavouriteAdapter.MyViewHolder>() {

    var songDetails: ArrayList<Songs>? = null
    var mcontext: Context? = null

    init {
        this.songDetails = _songDetails
        this.mcontext = _context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder{
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.custom_home_adapter, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        if (songDetails == null){
            return 0
        }else{
            return (songDetails as ArrayList<Songs>).size
        }
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val songObject = songDetails?.get(position)
        holder.trackName?.text = songObject?.trackName
        holder.artistName?.text = songObject?.artistName
        holder.contentHolder?.setOnClickListener({
            val playerFragment = PlayerFragment()

            var args = Bundle()
            args.putInt("trackId", songObject?.trackID?.toInt() as Int)
            args.putString("trackName", songObject?.trackName)
            args.putString("artistName", songObject?.artistName)
            args.putString("trackData", songObject?.trackData)
            args.putInt("trackPosition", position)
            args.putParcelableArrayList("songDetails", songDetails)
            playerFragment.arguments = args

            (mcontext as FragmentActivity).supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fragment, playerFragment)
                    .commit()
        })
    }

    class MyViewHolder(view : View) : RecyclerView.ViewHolder(view){
        var trackName : TextView? = null
        var artistName : TextView? = null
        var contentHolder : LinearLayout? = null

        init {
            trackName = view.findViewById(R.id.trackName)
            artistName = view.findViewById(R.id.artistName)
            contentHolder = view.findViewById<LinearLayout>(R.id.homeContent)

        }
    }


}