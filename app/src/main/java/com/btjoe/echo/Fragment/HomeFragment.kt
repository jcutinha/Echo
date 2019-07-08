package com.btjoe.echo.Fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
import com.btjoe.echo.Adapter.HomeAdapter

import com.btjoe.echo.R
import com.btjoe.echo.Songs

/**
 * A simple [Fragment] subclass.
 *
 */
class HomeFragment : Fragment() {

    var recyclerView : RecyclerView? = null
    var miniPlayer : RelativeLayout? = null
    var play_pause_button : ImageButton? = null
    var trackName : TextView? = null
    var songListScreen : RelativeLayout? = null
    var noSongScreen : RelativeLayout? = null
    var myActivity : Activity? = null
    var homeAdapter : HomeAdapter? = null
    var getSongList : ArrayList<Songs>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        recyclerView = view?.findViewById(R.id.homeRecyclerView)
        miniPlayer = view?.findViewById(R.id.miniPlayer)
        play_pause_button = view?.findViewById(R.id.play_pauseButton)
        trackName = view?.findViewById(R.id.miniPlayer_trackName)
        songListScreen = view?.findViewById(R.id.songListScreen)
        noSongScreen = view?.findViewById(R.id.noSongsScreen)

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        getSongList = getSongsFromPhone()
        homeAdapter = HomeAdapter(getSongList as ArrayList<Songs>, myActivity as Context)

        recyclerView?.layoutManager = LinearLayoutManager(myActivity)
        recyclerView?.itemAnimator = DefaultItemAnimator()
        recyclerView?.adapter = homeAdapter
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        myActivity = context as Activity
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        myActivity = activity
    }

    @SuppressLint("Recycle")
    fun getSongsFromPhone() : ArrayList<Songs> {
        val arrayList = ArrayList<Songs>()
        val contentResolver = myActivity?.contentResolver
        val songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val songCursor = contentResolver?.query(songUri, null, null, null, null)
        if (songCursor != null && songCursor.moveToFirst()){
            val trackId = songCursor.getColumnIndex(MediaStore.Audio.Media._ID)
            val trackName = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val artistName = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
            val trackData = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA)
            val dateAdded = songCursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED)

            while (songCursor.moveToNext()){
                val currentId = songCursor.getLong(trackId)
                val currentTrack = songCursor.getString(trackName)
                val currentArtist = songCursor.getString(artistName)
                val currentData = songCursor.getString(trackData)
                val currentDate = songCursor.getLong(dateAdded)

                arrayList.add(Songs(currentId, currentTrack, currentArtist, currentData, currentDate))
            }
        }
        return arrayList
    }

}
