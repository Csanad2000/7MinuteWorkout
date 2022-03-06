package com.example.a7minutesworkout

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.a7minutesworkout.databinding.ActivityBmiBinding
import java.math.BigDecimal
import java.math.RoundingMode

class BMIActivity : AppCompatActivity() {

    companion object{
        private const val METRIC_UNITS_VIEW="METRIC_UNIT_VIEW"
        private const val US_UNITS_VIEW="US_UNIT_VIEW"
    }

    private var currentVisibleView:String= METRIC_UNITS_VIEW

    private var binding:ActivityBmiBinding?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityBmiBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setSupportActionBar(binding?.tbBMI)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title="Calculate Bmi"
        binding?.tbBMI?.setNavigationOnClickListener {
            onBackPressed()
        }
        
        binding?.rgUnits?.setOnCheckedChangeListener { _, checkedId:Int ->

            if (checkedId==R.id.rbMetric){
                currentVisibleView= METRIC_UNITS_VIEW
            }else if (checkedId==R.id.rbUs){
                currentVisibleView= US_UNITS_VIEW
            }
            changeVisibility()
        }

        binding?.btnCalculateUnits?.setOnClickListener{
            calculateBMI()
        }
    }

    private fun calculateBMI() {
        if (currentVisibleView == METRIC_UNITS_VIEW) {
            if (validateMetric()) {
                val height: Float = binding?.etMetricHeight?.text.toString().toFloat() / 100
                val weight: Float = binding?.etMetricWeight?.text.toString().toFloat()

                val bmi = weight / (height * height)

                displayBMIResult(bmi)
            } else {
                Toast.makeText(this, "Enter valid numbers", Toast.LENGTH_LONG).show()
            }
        } else if (currentVisibleView == US_UNITS_VIEW) {
            if (validateUS()) {
                val feet: Float = binding?.etUSFeet?.text.toString().toFloat()
                val inches: Float = binding?.etUSInches?.text.toString().toFloat()
                val height: Float = feet * 12 + inches
                val weight: Float = binding?.etUSWeight?.text.toString().toFloat()

                val bmi = weight / (height * height) * 703

                displayBMIResult(bmi)
            } else {
                Toast.makeText(this, "Enter valid numbers", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun changeVisibility(){
        if (currentVisibleView== METRIC_UNITS_VIEW){
            binding?.llUSHeight?.visibility=View.GONE
            binding?.tilMetricWeight?.visibility=View.VISIBLE
            binding?.tilUSWeight?.visibility=View.GONE
            binding?.tilMetricHeight?.visibility=View.VISIBLE
            binding?.etMetricHeight?.text!!.clear()
            binding?.etMetricWeight?.text!!.clear()
        } else if(currentVisibleView== US_UNITS_VIEW){
            binding?.tilMetricHeight?.visibility=View.GONE
            binding?.tilMetricWeight?.visibility=View.GONE
            binding?.tilUSWeight?.visibility=View.VISIBLE
            binding?.llUSHeight?.visibility=View.VISIBLE
            binding?.etUSFeet?.text!!.clear()
            binding?.etUSInches?.text!!.clear()
            binding?.etUSWeight?.text!!.clear()
        }
        binding?.llBMIResult?.visibility=View.INVISIBLE
    }

    private fun displayBMIResult(bmi:Float){
        val bmiLabel:String
        val bmiDescription:String

        if (bmi.compareTo(15f)<=0){
            bmiLabel="Very severely underweight"
            bmiDescription="Oops! You really need to take better care of yourself! Eat more!"
        } else if (bmi.compareTo(16f)<=0){
            bmiLabel="Severely underweight"
            bmiDescription="Oops! You really need to take better care of yourself! Eat more!"
        } else if (bmi.compareTo(18.5f)<=0){
            bmiLabel="Underweight"
            bmiDescription="Oops! You really need to take better care of yourself! Eat more!"
        } else if (bmi.compareTo(25f)<=0){
            bmiLabel="Normal"
            bmiDescription="Congratulations! You are in good shape."
        } else if (bmi.compareTo(30f)<=0) {
            bmiLabel = "Overweight"
            bmiDescription = "Oops! You really need to take better care of yourself! Workout more!"
        } else if (bmi.compareTo(35f)<=0) {
            bmiLabel = "Obese Class | (Moderately obese)"
            bmiDescription = "Oops! You really need to take better care of yourself! Workout more!"
        } else if (bmi.compareTo(40f)<=0) {
            bmiLabel = "Obese Class || (Severely obese)"
            bmiDescription = "OMG! You are in a very dangerous condition! Act now!"
        }else {
            bmiLabel="Obese Class ||| (Very severely obese)"
            bmiDescription = "OMG! You are in a very dangerous condition! Act now!"
        }

        val bmiValue=BigDecimal(bmi.toDouble()).setScale(2,RoundingMode.HALF_EVEN).toString()

        binding?.llBMIResult?.visibility= View.VISIBLE
        binding?.tvBMIValue?.text=bmiValue
        binding?.tvBMIType?.text=bmiLabel
        binding?.tvBMIDescription?.text=bmiDescription

    }

    private fun validateMetric():Boolean{
        var isValid=true
        if (binding?.etMetricWeight?.text.toString().isEmpty()){
            isValid=false
        }else if (binding?.etMetricHeight?.text.toString().isEmpty()){
            isValid=false
        }
            return isValid
    }

    private fun validateUS():Boolean{
        var isValid=true
        if (binding?.etUSWeight?.text.toString().isEmpty()){
            isValid=false
        }else if (binding?.etUSFeet?.text.toString().isEmpty()){
            isValid=false
        }else if (binding?.etUSInches?.text.toString().isEmpty()){
            isValid=false
        }
        return isValid
    }

    override fun onDestroy() {
        super.onDestroy()
        binding=null
    }
}