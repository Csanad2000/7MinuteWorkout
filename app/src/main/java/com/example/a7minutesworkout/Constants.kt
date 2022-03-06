package com.example.a7minutesworkout

object Constants {
    fun defaultExerciseList():ArrayList<ExerciseModel>{
        val exerciseList=ArrayList<ExerciseModel>()

        val jumpingJacks=ExerciseModel(1,"Jumping Jacks",R.drawable.jj,false,false)
        exerciseList.add(jumpingJacks)

        return exerciseList
    }
}