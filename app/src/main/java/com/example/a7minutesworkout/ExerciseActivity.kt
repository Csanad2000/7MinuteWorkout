package com.example.a7minutesworkout

import android.app.Dialog
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.a7minutesworkout.databinding.ActivityExerciseBinding
import com.example.a7minutesworkout.databinding.DialogCustomBackConfirmationBinding
import java.util.*
import kotlin.collections.ArrayList

class ExerciseActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    private var binding:ActivityExerciseBinding?=null

    private var restTimer:CountDownTimer?=null
    private var restProgress=0

    private var exerciseTimer:CountDownTimer?=null
    private var exerciseProgress=0

    private var restTimerDuration:Long=10
    private var exerciseTimerDuration:Long=30

    private var exerciseList:ArrayList<ExerciseModel>?=null
    private var currentExercisePosition=-1

    private var tts:TextToSpeech?=null
    private var player:MediaPlayer?=null

    private var exerciseAdapter:ExerciseStatusAdapter?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityExerciseBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setSupportActionBar(binding?.tbExercise)
        //if?
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        exerciseList=Constants.defaultExerciseList()

        tts= TextToSpeech(this,this)

        binding?.tbExercise?.setNavigationOnClickListener {
            customDialogForBackButton()
        }

        setupRestView()
        setupExerciseStatusRecyclerView()
    }

    override fun onBackPressed() {
        customDialogForBackButton()
    }

    private fun customDialogForBackButton(){
        val customDialog= Dialog(this)
        val dialogBinding =DialogCustomBackConfirmationBinding.inflate(layoutInflater)
        customDialog.setContentView(dialogBinding.root)
        customDialog.setCanceledOnTouchOutside(false)
        dialogBinding.btnYes.setOnClickListener {
            this@ExerciseActivity.finish()
            customDialog.dismiss()
        }
        dialogBinding.btnNo.setOnClickListener {
            customDialog.dismiss()
        }
        customDialog.show()
    }

    private fun setupExerciseStatusRecyclerView(){
        binding?.rvExerciseStatus?.layoutManager=LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)

        exerciseAdapter=ExerciseStatusAdapter(exerciseList!!)
        binding?.rvExerciseStatus?.adapter=exerciseAdapter
    }

    private fun setupRestView(){

        try {
            val soundURI= Uri.parse("android.resource://com.example.a7minutesworkout/"+R.raw.press_start)
            player=MediaPlayer.create(applicationContext,soundURI)
            player?.isLooping=false
            player?.start()
        }catch (e:Exception){
            e.printStackTrace()
        }

        if (restTimer != null) {
            restTimer?.cancel()
            restProgress = 0
        }
        binding?.ivImage?.visibility= View.INVISIBLE
        binding?.tvUpcomingLabel?.visibility=View.VISIBLE
        binding?.tvUpcomingName?.visibility=View.VISIBLE

        binding?.pbProgress?.max=10
        binding?.pbProgress?.progress=100

        if(restTimer!=null){
            restTimer?.cancel()
            restProgress=0
        }

        binding?.tvUpcomingName?.text=exerciseList!![currentExercisePosition+1].getName()

        binding?.tvTitle?.text="GET READY"

        setRestProgressBar()
    }

    private fun setupExerciseView(){
        if (exerciseTimer != null) {
            exerciseTimer?.cancel()
            exerciseProgress = 0
        }
        binding?.ivImage?.visibility= View.VISIBLE
        binding?.tvUpcomingLabel?.visibility=View.INVISIBLE
        binding?.tvUpcomingName?.visibility=View.INVISIBLE

        binding?.pbProgress?.max=30
        binding?.pbProgress?.progress=300

        if(exerciseTimer!=null){
            exerciseTimer?.cancel()
            exerciseProgress=0
        }

        speakOut(exerciseList!![currentExercisePosition].getName())

        binding?.tvTitle?.text=exerciseList!![currentExercisePosition].getName()
        binding?.ivImage?.setImageResource(exerciseList!![currentExercisePosition].getImage())


        setExerciseProgressBar()
    }

    private fun setRestProgressBar(){
        binding?.pbProgress?.progress=restProgress
        restTimer=object:CountDownTimer(restTimerDuration*1000,1000) {
            override fun onTick(p0: Long) {
                binding?.pbProgress?.progress=10-++restProgress
                binding?.tvTimer?.text=(10-restProgress).toString()
            }

            override fun onFinish() {
                currentExercisePosition++
                exerciseList!![currentExercisePosition].setSelected(true)
                exerciseAdapter!!.notifyDataSetChanged()
                setupExerciseView()
            }

        }.start()
    }

    private fun setExerciseProgressBar(){
        binding?.pbProgress?.progress=exerciseProgress
        exerciseTimer=object:CountDownTimer(exerciseTimerDuration*1000,1000) {
            override fun onTick(p0: Long) {
                binding?.pbProgress?.progress=30-++exerciseProgress
                binding?.tvTimer?.text=(30-exerciseProgress).toString()
            }

            override fun onFinish() {



                if(currentExercisePosition<exerciseList?.size!!-1){
                    exerciseList!![currentExercisePosition].setSelected(false)
                    exerciseList!![currentExercisePosition].setCompleted(true)
                    exerciseAdapter!!.notifyDataSetChanged()
                    setupRestView()
                }else {
                    val intent=Intent(this@ExerciseActivity,FinishActivity::class.java)
                    finish()
                    startActivity(intent)
                }
            }

        }.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (restTimer != null) {
            restTimer!!.cancel()
            restProgress = 0
        }

        if (exerciseTimer != null) {
            exerciseTimer!!.cancel()
            exerciseProgress = 0
        }

        if (tts!=null){
            tts!!.stop()
            tts!!.shutdown()
        }

        if (player!=null){
            player!!.stop()
        }
        binding = null
    }

    override fun onInit(status: Int) {
        if (status==TextToSpeech.SUCCESS){
            val result=tts?.setLanguage(Locale.ENGLISH)

            if (result==TextToSpeech.LANG_MISSING_DATA||result==TextToSpeech.LANG_NOT_SUPPORTED){
                Log.e("TTS","The language specified is not supported")
            }
        }else{
            Log.e("TTS","Initialization failed!")
        }
    }

    private fun speakOut(text:String){
        tts!!.speak(text,TextToSpeech.QUEUE_FLUSH,null,"")
    }
}