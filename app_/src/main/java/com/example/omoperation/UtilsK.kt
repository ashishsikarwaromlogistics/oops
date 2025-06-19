package com.example.omoperation

class UtilsK {


    companion object {

        // val a=5
        //        UtilsK.run { a.square() }  in activity
        fun Int.square(): Int {
            return this * this
        }

        fun Int.square1(): Int {
            return this * this
        }
    }

    fun main() {
        val number = 5
        println(number.square())  // Output: 25


    }
}