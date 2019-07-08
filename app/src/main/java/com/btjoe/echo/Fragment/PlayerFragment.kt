package com.btjoe.echo.Fragment


import android.app.Activity
import android.content.Context
import android.hardware.*
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import com.btjoe.echo.CurrentSongHelper
import com.btjoe.echo.Database.EchoDatabase

import com.btjoe.echo.R
import com.btjoe.echo.Songs
import com.cleveroad.audiovisualization.AudioVisualization
import com.cleveroad.audiovisualization.DbmHandler
import com.cleveroad.audiovisualization.GLAudioVisualizationView
import kotlinx.android.synthetic.main.fragment_player.*
import java.util.*
import java.util.concurrent.TimeUnit

class PlayerFragment : Fragment() {

    object Statified{
        var myActivity : Activity? = null
        var mediaPlayer : MediaPlayer? = null

        var music_player_trackName : TextView? = null
        var music_player_artistName : TextView? = null
        var music_player_startTime : TextView? = null
        var music_player_endTime : TextView? = null
        var music_player_seekbar : SeekBar? = null
        var music_player_play_pause_button : ImageButton? = null
        var music_player_nextButton : ImageButton? = null
        var music_player_previousButton : ImageButton? = null
        var music_player_loopButton : ImageButton? = null
        var music_player_shuffleButton : ImageButton? = null

        var music_player_favouriteButton: ImageButton? = null
        var favoriteContent: EchoDatabase? = null

        var currentPosition : Int = 0
        var fetchSongs : ArrayList<Songs>? = null
        var currentSongHelper : CurrentSongHelper? = null

        var audioVisualizationView : AudioVisualization? = null
        var glView : GLAudioVisualizationView? = null

        var mSensorManager : SensorManager? = null
        var mSensorListener : SensorListener? = null

        var updateSongTime = object : Runnable{
            override fun run() {
                val getCurrent = mediaPlayer?.currentPosition
                music_player_startTime?.setText(String.format("%d:%d",
                        TimeUnit.MILLISECONDS.toMinutes(getCurrent?.toLong() as Long),
                        TimeUnit.MILLISECONDS.toSeconds(TimeUnit.MILLISECONDS.toMinutes(getCurrent.toLong() as Long))))
                music_player_seekbar?.setProgress(getCurrent?.toInt() as Int)
                Handler().postDelayed(this, 1000)
            }
        }
    }

    object Staticated {
        var MY_PREFS_SHUFFLE = "Shuffle feature"
        var MY_PREFS_LOOP = "Loop feature"

        fun onSongComplete(){
            if (Statified.currentSongHelper?.isShuffled as Boolean){
                playNext("playNextNormalShuffle")
                Statified.currentSongHelper?.isPlaying = true
            }else{
                if (Statified.currentSongHelper?.isLooped as Boolean){
                    Statified.currentSongHelper?.isPlaying = true

                    val nextSong = Statified.fetchSongs?.get(Statified.currentPosition)
                    Statified.currentSongHelper?.trackId = nextSong?.trackID as Long
                    Statified.currentSongHelper?.trackName = nextSong.trackName
                    Statified.currentSongHelper?.artistName = nextSong.artistName
                    Statified.currentSongHelper?.trackData = nextSong.trackData

                    updateTrackDetails(Statified.currentSongHelper?.trackName as String, Statified.currentSongHelper?.artistName as String)

                    Statified.mediaPlayer?.reset()
                    try {
                        Statified.mediaPlayer?.setDataSource(Statified.myActivity, Uri.parse(Statified.currentSongHelper?.trackData))
                        Statified.mediaPlayer?.prepare()
                        Statified.mediaPlayer?.start()
                        processInformation(Statified.mediaPlayer as MediaPlayer)
                    }catch (e : Exception){
                        e.printStackTrace()
                    }
                }else{
                    playNext("playNextNormal")
                    Statified.currentSongHelper?.isPlaying = true
                }
            }

            if (Statified.favoriteContent?.checkIfIDExists(Statified.currentSongHelper?.trackId?.toInt() as Int) as Boolean) {
                Statified.music_player_favouriteButton?.setBackgroundResource(R.drawable.favorite_on)
            } else {
                Statified.music_player_favouriteButton?.setBackgroundResource(R.drawable.favorite_off)
            }
        }

        fun updateTrackDetails(trackName : String, artistName : String){
            Statified.music_player_trackName?.setText(trackName)
            Statified.music_player_artistName?.setText(artistName)
        }

        fun processInformation(mediaPlayer: MediaPlayer){
            val startTime = mediaPlayer.currentPosition
            val finalTime = mediaPlayer.duration
            Statified.music_player_seekbar?.max = finalTime

            Statified.music_player_startTime?.setText(String.format("%d:%d",
                    TimeUnit.MILLISECONDS.toMinutes(startTime.toLong()),
                    TimeUnit.MILLISECONDS.toSeconds(startTime.toLong() -
                            TimeUnit.MILLISECONDS.toSeconds(TimeUnit.MILLISECONDS.toMinutes(startTime.toLong() )))))

            Statified.music_player_endTime?.setText(String.format("%d:%d",
                    TimeUnit.MILLISECONDS.toMinutes(finalTime.toLong()),
                    TimeUnit.MILLISECONDS.toSeconds(finalTime.toLong() ) -
                            TimeUnit.MILLISECONDS.toSeconds(TimeUnit.MILLISECONDS.toMinutes(finalTime.toLong() ))))

            Statified.music_player_seekbar?.setProgress(startTime)
            Handler().postDelayed(Statified.updateSongTime, 1000)
        }

        fun playNext(check : String){
            if (check.equals("playNextNormal", true)){
                Statified.currentPosition = Statified.currentPosition + 1
            }else if (check.equals("playNextNormalShuffle", true)){
                val randomObject = Random()
                val randomPosition = randomObject.nextInt(Statified.fetchSongs?.size?.plus(1) as Int)
                Statified.currentPosition = randomPosition
            }
            if (Statified.currentPosition == Statified.fetchSongs?.size){
                Statified.currentPosition = 0
            }

            Statified.currentSongHelper?.isLooped = false
            val nextSong = Statified.fetchSongs?.get(Statified.currentPosition)
            Statified.currentSongHelper?.trackId = nextSong?.trackID as Long
            Statified.currentSongHelper?.trackName = nextSong.trackName
            Statified.currentSongHelper?.artistName = nextSong.artistName
            Statified.currentSongHelper?.trackData = nextSong.trackData
            Statified.currentSongHelper?.currentPosition = Statified.currentPosition

            updateTrackDetails(Statified.currentSongHelper?.trackName as String, Statified.currentSongHelper?.artistName as String)

            Statified.mediaPlayer?.reset()
            try {
                Statified.mediaPlayer?.setDataSource(Statified.myActivity, Uri.parse(Statified.currentSongHelper?.trackData))
                Statified.mediaPlayer?.prepare()
                Statified.mediaPlayer?.start()
                processInformation(Statified.mediaPlayer as MediaPlayer)
            }catch (e : Exception){
                e.printStackTrace()
            }

            if (Statified.favoriteContent?.checkIfIDExists(Statified.currentSongHelper?.trackId?.toInt() as Int) as Boolean) {
                Statified.music_player_favouriteButton?.setBackgroundResource(R.drawable.favorite_on)
            } else {
                Statified.music_player_favouriteButton?.setBackgroundResource(R.drawable.favorite_off)
            }
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_player, container, false)

        Statified.music_player_trackName = view?.findViewById(R.id.music_player_trackName)
        Statified.music_player_artistName = view?.findViewById(R.id.music_player_artistName)
        Statified.music_player_startTime = view?.findViewById(R.id.music_player_startTime)
        Statified.music_player_endTime = view?.findViewById(R.id.music_player_endTime)
        Statified.music_player_seekbar = view?.findViewById(R.id.music_player_seekbar)
        Statified.music_player_play_pause_button = view?.findViewById(R.id.music_player_play_pause_button)
        Statified.music_player_nextButton = view?.findViewById(R.id.music_player_nextButton)
        Statified.music_player_previousButton = view?.findViewById(R.id.music_player_previousButton)
        Statified.music_player_loopButton = view?.findViewById(R.id.music_player_loopButton)
        Statified.music_player_shuffleButton = view?.findViewById(R.id.music_player_shuffleButton)
        Statified.glView = view?.findViewById(R.id.visualizer_view)
        Statified.music_player_favouriteButton = view?.findViewById(R.id.visualizer_favouriteIcon)
        Statified.music_player_favouriteButton?.alpha = 0.8f

        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Statified.mSensorManager = Statified.myActivity?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Statified.audioVisualizationView = Statified.glView as AudioVisualization
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        Statified.myActivity = context as Activity
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        Statified.myActivity = activity
    }

    override fun onResume() {
        super.onResume()
        Statified.audioVisualizationView?.onResume()
        Statified.mSensorManager?.registerListener(Statified.mSensorListener,
                Statified.mSensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        Statified.audioVisualizationView?.onPause()
        super.onPause()
    }

    override fun onDestroyView() {
        Statified.audioVisualizationView?.release()
        super.onDestroyView()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        Statified.favoriteContent = EchoDatabase(Statified.myActivity)

        Statified.currentSongHelper = CurrentSongHelper()
        Statified.currentSongHelper?.isPlaying = true
        Statified.currentSongHelper?.isLooped = false
        Statified.currentSongHelper?.isShuffled = false

        var trackId : Long = 0
        var trackName : String? = null
        var artistName : String? = null
        var trackData : String? = null
        try {
            trackId = arguments?.getInt("trackId")?.toLong() as Long
            trackName = arguments?.getString("trackName")
            artistName = arguments?.getString("artistName")
            trackData = arguments?.getString("trackData")
            Statified.currentPosition = arguments?.getInt("currentPosition")!!
            Statified.fetchSongs = arguments?.getParcelableArrayList("songList")

            Statified.currentSongHelper?.trackId = trackId
            Statified.currentSongHelper?.trackName = trackName
            Statified.currentSongHelper?.artistName = artistName
            Statified.currentSongHelper?.trackData = trackData
            Statified.currentSongHelper?.currentPosition = Statified.currentPosition

            Staticated.updateTrackDetails(Statified.currentSongHelper?.trackName as String, Statified.currentSongHelper?.artistName as String)

        }catch (e : Exception){
            e.printStackTrace()
        }

        var fromFavBottomBar = arguments?.get("FavBottomBar") as? String
        if (fromFavBottomBar != null){
            Statified.mediaPlayer = FavouriteFragment.Statified.mediaPlayer
        }else {
            Statified.mediaPlayer = MediaPlayer()
            Statified.mediaPlayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)
            try {
                Statified.mediaPlayer?.setDataSource(Statified.myActivity, Uri.parse(trackData))
                Statified.mediaPlayer?.prepare()
            }catch (e : Exception){
                e.printStackTrace()
            }
            Statified.mediaPlayer?.start()
        }

        Staticated.processInformation(Statified.mediaPlayer as MediaPlayer)

        if (Statified.currentSongHelper?.isPlaying as Boolean) {
            music_player_play_pause_button?.setBackgroundResource(R.drawable.pause_icon)
        } else {
            music_player_play_pause_button?.setBackgroundResource(R.drawable.play_icon)
        }

        Statified.mediaPlayer?.setOnCompletionListener {
            Staticated.onSongComplete()
        }

        clickHandlers()

        var visualizationHandler = DbmHandler.Factory.newVisualizerHandler(Statified.myActivity as Context, 0)
        Statified.audioVisualizationView?.linkTo(visualizationHandler)

        var prefsForShuffle = Statified.myActivity?.getSharedPreferences(Staticated.MY_PREFS_SHUFFLE, Context.MODE_PRIVATE)

        /*Here we extract the value of preferences and check if shuffle was ON or not*/
        var isShuffleAllowed = prefsForShuffle?.getBoolean("feaure", false)
        if (isShuffleAllowed as Boolean) {

            /*if shuffle was found activated, then we change the icon color and tun loop OFF*/
            Statified.currentSongHelper?.isShuffled = true
            Statified.currentSongHelper?.isLooped = false
            music_player_shuffleButton?.setBackgroundResource(R.drawable.shuffle_icon)
            music_player_loopButton?.setBackgroundResource(R.drawable.loop_white_icon)
        } else {

            /*Else default is set*/
            Statified.currentSongHelper?.isShuffled = false
            music_player_shuffleButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
        }

        /*Similar to the shuffle we check the value for loop activation*/
        var prefsForLoop = Statified.myActivity?.getSharedPreferences(Staticated.MY_PREFS_LOOP, Context.MODE_PRIVATE)

        /*Here we extract the value of preferences and check if loop was ON or not*/
        var isLoopAllowed = prefsForLoop?.getBoolean("feature", false)
        if (isLoopAllowed as Boolean) {

            /*If loop was activated we change the icon color and shuffle is turned OFF */
            Statified.currentSongHelper?.isShuffled = false
            Statified.currentSongHelper?.isLooped = true
            music_player_shuffleButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
            music_player_loopButton?.setBackgroundResource(R.drawable.loop_icon)
        } else {

            /*Else defaults are used*/
            music_player_shuffleButton?.setBackgroundResource(R.drawable.loop_white_icon)
            Statified.currentSongHelper?.isLooped = false
        }

        if (Statified.favoriteContent?.checkIfIDExists(Statified.currentSongHelper?.trackId?.toInt() as Int) as Boolean){
            Statified.music_player_favouriteButton?.setBackgroundResource(R.drawable.favorite_on)
        } else {
            Statified.music_player_favouriteButton?.setBackgroundResource(R.drawable.favorite_off)
        }

    }

    fun clickHandlers() {
        music_player_play_pause_button?.setOnClickListener({
            if (Statified.mediaPlayer?.isPlaying as Boolean) {
                Statified.mediaPlayer?.pause()
                Statified.currentSongHelper?.isPlaying = false
                music_player_play_pause_button?.setBackgroundResource(R.drawable.play_icon)
            } else {
                Statified.mediaPlayer?.start()
                Statified.currentSongHelper?.isPlaying = true
                music_player_play_pause_button?.setBackgroundResource(R.drawable.pause_icon)
            }
        })

        music_player_nextButton?.setOnClickListener({
            Statified.currentSongHelper?.isPlaying = true
            if (Statified.currentSongHelper?.isShuffled as Boolean) {
                Staticated.playNext("playNextNormalShuffle")
            } else {
                Staticated.playNext("playNextNormal")
            }
        })

        music_player_previousButton?.setOnClickListener({
            Statified.currentSongHelper?.isPlaying = true
            if (Statified.currentSongHelper?.isLooped as Boolean) {
                music_player_loopButton?.setBackgroundResource(R.drawable.loop_white_icon)
            }
            playPrevious()
        })

        music_player_loopButton?.setOnClickListener({
            var editorShuffle = Statified.myActivity?.getSharedPreferences(Staticated.MY_PREFS_SHUFFLE, Context.MODE_PRIVATE)?.edit()
            var editorLoop = Statified.myActivity?.getSharedPreferences(Staticated.MY_PREFS_LOOP, Context.MODE_PRIVATE)?.edit()

            if (Statified.currentSongHelper?.isLooped as Boolean) {
                Statified.currentSongHelper?.isLooped = false
                music_player_loopButton?.setBackgroundResource(R.drawable.loop_white_icon)

                editorLoop?.putBoolean("feature", false)
                editorLoop?.apply()
            } else {
                Statified.currentSongHelper?.isLooped = true
                Statified.currentSongHelper?.isShuffled = false
                music_player_loopButton?.setBackgroundResource(R.drawable.loop_icon)
                music_player_shuffleButton?.setBackgroundResource(R.drawable.shuffle_white_icon)

                editorShuffle?.putBoolean("feature", false)
                editorShuffle?.apply()

                editorLoop?.putBoolean("feature", true)
                editorLoop?.apply()
            }
        })

        music_player_shuffleButton?.setOnClickListener({
            var editorShuffle = Statified.myActivity?.getSharedPreferences(Staticated.MY_PREFS_SHUFFLE, Context.MODE_PRIVATE)?.edit()
            var editorLoop = Statified.myActivity?.getSharedPreferences(Staticated.MY_PREFS_LOOP, Context.MODE_PRIVATE)?.edit()

            if (Statified.currentSongHelper?.isShuffled as Boolean) {
                Statified.currentSongHelper?.isShuffled = false
                music_player_shuffleButton?.setBackgroundResource(R.drawable.shuffle_white_icon)

                editorShuffle?.putBoolean("feature", false)
                editorShuffle?.apply()
            } else {
                Statified.currentSongHelper?.isLooped = false
                Statified.currentSongHelper?.isShuffled = true
                music_player_loopButton?.setBackgroundResource(R.drawable.loop_white_icon)
                music_player_shuffleButton?.setBackgroundResource(R.drawable.shuffle_icon)

                editorShuffle?.putBoolean("feature", true)
                editorShuffle?.apply()

                editorLoop?.putBoolean("feature", false)
                editorLoop?.apply()
            }
        })

        Statified.music_player_favouriteButton?.setOnClickListener({
            if (Statified.favoriteContent?.checkIfIDExists(Statified.currentSongHelper?.trackId?.toInt() as Int) as Boolean) {
                Statified.music_player_favouriteButton?.setBackgroundResource(R.drawable.favorite_off)
                Statified.favoriteContent?.deleteFavourite(Statified.currentSongHelper?.trackId?.toInt() as Int)

                /*Toast is prompt message at the bottom of screen indicating that an action has been performed*/
                Toast.makeText(Statified.myActivity, "Removed from Favorites", Toast.LENGTH_SHORT).show()
            } else {
                Statified.music_player_favouriteButton?.setBackgroundResource(R.drawable.favorite_on)
                Statified.favoriteContent?.storeAsFavourite(Statified.currentSongHelper?.trackId?.toInt(), Statified.currentSongHelper?.artistName,
                        Statified.currentSongHelper?.trackName, Statified.currentSongHelper?.trackData)
                Toast.makeText(Statified.myActivity, "Added to Favorites", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun playPrevious(){
        Statified.currentPosition -= 1
        if (Statified.currentPosition == -1){
            Statified.currentPosition = 0
        }

        if (Statified.currentSongHelper?.isPlaying as Boolean){
            music_player_play_pause_button?.setBackgroundResource(R.drawable.play_icon)
        }else{
            music_player_play_pause_button?.setBackgroundResource(R.drawable.pause_icon)
        }

        Statified.currentSongHelper?.isLooped = false
        val nextSong = Statified.fetchSongs?.get(Statified.currentPosition)
        Statified.currentSongHelper?.trackId = nextSong?.trackID as Long
        Statified.currentSongHelper?.trackName = nextSong.trackName
        Statified.currentSongHelper?.artistName = nextSong.artistName
        Statified.currentSongHelper?.trackData = nextSong.trackData
        Statified.currentSongHelper?.currentPosition = Statified.currentPosition

        Staticated.updateTrackDetails(Statified.currentSongHelper?.trackName as String, Statified.currentSongHelper?.artistName as String)

        Statified.mediaPlayer?.reset()
        try {
            Statified.mediaPlayer?.setDataSource(Statified.myActivity, Uri.parse(Statified.currentSongHelper?.trackData))
            Statified.mediaPlayer?.prepare()
            Statified.mediaPlayer?.start()
            Staticated.processInformation(Statified.mediaPlayer as MediaPlayer)
        }catch (e : Exception){
            e.printStackTrace()
        }

        if (Statified.favoriteContent?.checkIfIDExists(Statified.currentSongHelper?.trackId?.toInt() as Int) as Boolean) {
            Statified.music_player_favouriteButton?.setBackgroundResource(R.drawable.favorite_on)
        } else {
            Statified.music_player_favouriteButton?.setBackgroundResource(R.drawable.favorite_off)
        }
    }

    fun bindShakeListener(){
        Statified.mSensorListener = object: SensorEventListener, SensorListener {
            override fun onSensorChanged(sensor: Int, values: FloatArray?) {
            }

            override fun onAccuracyChanged(sensor: Int, accuracy: Int) {
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
            }

            override fun onSensorChanged(event: SensorEvent) {
                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]
            }

        }
    }

}

private fun SensorManager?.registerListener(mSensorListener: SensorListener?, defaultSensor: Sensor?, sensoR_DELAY_NORMAL: Int) {

}

