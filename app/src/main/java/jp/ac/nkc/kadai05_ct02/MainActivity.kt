package jp.ac.nkc.kadai05_ct02

import android.icu.util.DateInterval
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.CompletableFuture

class MainActivity : AppCompatActivity() {

    private lateinit var soundPool: SoundPool
    private var soundResId = 0

    var usrMin = 0L
    var usrSec = 0L
    var timer = MyCountDownTimer(30 * 1000, 100)

    inner class MyCountDownTimer(millisInFuture: Long, countDownInterval: Long):CountDownTimer(millisInFuture,countDownInterval){
        var isRunning = false

        override fun onTick(millisUntilFinished: Long) {
            val min = millisUntilFinished / 1000L / 60L
            val sec = millisUntilFinished / 1000L % 60L
            timerText.text = "%1d:%2$02d".format(min,sec)
        }

        override fun onFinish() {
            timerText.text = "0:00"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button.setOnClickListener {
            timer.cancel()
            timer.isRunning = false
            playStop.setImageResource(R.drawable.ic_play_arrow_black_24dp)

            if((minText.text.toString().toInt() < 0 || minText.text.toString().toInt() >= 100) || (secText.text.toString().toInt() < 0 || secText.text.toString().toInt() >= 60)){
                timerText.text = "?:??"
            }else{
                usrMin = minText.text.toString().toLong()
                usrSec = secText.text.toString().toLong()

                timerText.text = "%1d:%2$02d".format(usrMin,usrSec)

                timer = MyCountDownTimer((usrMin * 60000) + (usrSec * 1000), 100)
            }
        }
//playStopを押したときになぜ timer = MyCount~ が動くのかを聞く
        playStop.setOnClickListener {
            timer.isRunning = when(timer.isRunning){
                true -> {
                    timer.cancel()
                    playStop.setImageResource(R.drawable.ic_play_arrow_black_24dp)
                    timerText.text = "%1d:%2$02d".format(usrMin,usrSec)
                    false
                }
                false -> {
                    timer.start()
                    playStop.setImageResource(R.drawable.ic_stop_black_24dp)
                    true
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        soundPool = if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP){
            @Suppress("DEPRECATION")
            SoundPool(2, AudioManager.STREAM_ALARM, 0)
        }else{
            val audioAttributes = AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ALARM).build()
            SoundPool.Builder().setMaxStreams(1).setAudioAttributes(audioAttributes).build()
        }
        soundResId = soundPool.load(this,R.raw.bellsound,1)
    }

    override fun onPause() {
        super.onPause()
        soundPool.release()
    }
}
