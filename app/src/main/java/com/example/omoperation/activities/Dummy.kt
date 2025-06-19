package com.example.omoperation.activities

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.omoperation.R
import com.example.omoperation.adapters.DummyAdapt

class Dummy : AppCompatActivity() {
    class Company() {
        lateinit var name: String
        lateinit var objective: String
        lateinit var founder: String
    }
    lateinit var adapter: DummyAdapt
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var company: Company? = null
        company?.let {
            print(it.name)
        }
        company?.run {
            print(this.name)
        }

            val name = String( ).also {
                println("Created person: $it")
            }

        var s = String()

        s = s.also {
            // These operations return new strings, but you're not using them
            // So you must explicitly update 's' if you want changes
            println("Adding Singh")
        }.plus("Singh").plus("Sikarwar")

        println(s)


        setContentView(R.layout.activity_dummy)
        val recyclerView = findViewById<RecyclerView>(R.id.recy)
        val names = listOf("Ashish", "Rahul", "Anjali", "Suman", "Vikram")

        adapter = DummyAdapt(names) { selectedIndices ->
            // Optional: update UI like showing selected count
            Toast.makeText(this, "Selected: ${selectedIndices.size}", Toast.LENGTH_SHORT).show()
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }
}