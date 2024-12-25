package com.example.omoperation.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.omoperation.CustomProgress
import com.example.omoperation.NetworkState
import com.example.omoperation.R
import com.example.omoperation.adapters.BrancheAdapter
import com.example.omoperation.databinding.ActivityBranchesBinding
import com.example.omoperation.room.tables.Branches
import com.example.omoperation.viewmodel.BrancheViewMod


class BranchesAct : AppCompatActivity() , BrancheAdapter.BranchInterface {
    lateinit var binding : ActivityBranchesBinding
    lateinit var adapter: BrancheAdapter
    //lateinit var imgsync : ImageView
    val imgsync : ImageView by lazy { findViewById(R.id.imgsync) }
    lateinit var branchviewmod : BrancheViewMod
    lateinit var cp : CustomProgress



     var senddata:Int=0  //if ==1 send data to another otherwise nothing

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_branches)
        senddata=intent.getIntExtra("senddata",0)
        cp= CustomProgress(this)
        binding.recybranche.setHasFixedSize(false)
        binding.recybranche.layoutManager=LinearLayoutManager(this)
        branchviewmod=ViewModelProvider(this).get(BrancheViewMod::class.java)
        binding.branchviewmod=branchviewmod
        binding.lifecycleOwner=this
       //binding.edtsearch
        branchviewmod.livedata.observe(this, Observer {
            when(it){
                is NetworkState.Error ->{
                    cp.dismiss()
                }
                is NetworkState.Success<*> -> {
                   val list= it.data as List<Branches>
                    adapter=BrancheAdapter(list,this)
                    binding.recybranche.adapter=adapter
                    cp.dismiss()
                    calltofilter()
                }
                is NetworkState.Loading -> {
                    cp.show()
                }
            }

        })
        imgsync.visibility=View.VISIBLE
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

    override fun sendata(branches: Branches) {
      if(senddata==0){}
        else{

          val intent = Intent()

          setResult(-1, intent)
          intent.putExtra("branchcode",branches.BRANCH_BRANCH_CODE)
          finish()
        }
    }
}