package com.example.omoperation.activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.omoperation.CustomProgress
import com.example.omoperation.NetworkState
import com.example.omoperation.R
import com.example.omoperation.adapters.EmployeeAdapter
import com.example.omoperation.databinding.ActivityEmployeesBinding
import com.example.omoperation.room.tables.Employees
import com.example.omoperation.viewmodel.EmployeeViewmod
import kotlinx.coroutines.launch

class EmployeesAct : AppCompatActivity() , EmployeeAdapter.EmployeeInterface {
    lateinit var binding : ActivityEmployeesBinding
    lateinit var adapter: EmployeeAdapter
    //lateinit var imgsync : ImageView
    val imgsync : ImageView by lazy { findViewById(R.id.imgsync) }
    lateinit var branchviewmod : EmployeeViewmod
    lateinit var cp : CustomProgress

    var senddata:Int=0  //if ==1 send data to another otherwise nothing

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= DataBindingUtil.setContentView(this,R.layout.activity_employees)
        senddata=intent.getIntExtra("senddata",0)
        cp= CustomProgress(this)
        binding.recybranche.setHasFixedSize(false)
        binding.recybranche.layoutManager= LinearLayoutManager(this)
        branchviewmod= ViewModelProvider(this).get(EmployeeViewmod::class.java)
        binding.empviewmod=branchviewmod
        binding.lifecycleOwner=this
        branchviewmod.livedata.observe(this, Observer {
            when(it){
                is NetworkState.Error ->{
                    cp.dismiss()
                }
                is  NetworkState.Success<*> -> {
                    val list= it.data as List<Employees>
                    adapter= EmployeeAdapter(list,this)
                    binding.recybranche.adapter=adapter
                    cp.dismiss()
                    calltofilter()
                }
                is NetworkState.Loading -> {
                    cp.show()
                }
            }

        })
        imgsync.setOnClickListener {
            branchviewmod.getonlinebranch()
        }
    }
    fun calltofilter(){
        binding.edtsearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No action needed here
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapter.filter.filter(s)
            }

            override fun afterTextChanged(s: Editable?) {
                // No action needed here
            }
        })
    }
    override fun sendata(branches: Employees) {
        if(senddata==0){}
        else{

            val intent = Intent()

            setResult(-1, intent)
            intent.putExtra("empcode",branches.EMP_EMP_CODE)
            finish()
        }
    }
}