package com.example.omoperation.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.omoperation.R
import com.example.omoperation.adapters.MissingAdapter
import com.example.omoperation.databinding.ActivityMissingListBinding
import com.example.omoperation.model.dataclass.CNWithBoxes
import com.example.omoperation.room.AppDatabase
import kotlinx.coroutines.launch

class MissingList : AppCompatActivity() , MissingAdapter.MissingInterface {
    lateinit var binding: ActivityMissingListBinding
    lateinit var misingcn:ArrayList<String>
    lateinit var db: AppDatabase
    lateinit var scancnbox:ArrayList<CNWithBoxes>
    lateinit var verifycnbox:ArrayList<CNWithBoxes>
    lateinit var missingcnbox:ArrayList<CNWithBoxes>
    lateinit var adapter:MissingAdapter
    lateinit var myinterface:MissingAdapter.MissingInterface
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding=DataBindingUtil. setContentView(this,R.layout.activity_missing_list)
       init()
    }
      fun init(){
          myinterface=this
          scancnbox= ArrayList()
          verifycnbox=ArrayList()
          missingcnbox=ArrayList()
        db=AppDatabase.getDatabase(this)
        misingcn= ArrayList()
        binding.recymissing.setHasFixedSize(true)
        binding.recymissing.layoutManager=LinearLayoutManager(this)
          adapter=MissingAdapter(myinterface,scancnbox)
        binding.recymissing.adapter=adapter
        lifecycleScope.launch {  finddiff()  }


    }

    private suspend fun finddiff() {
      lifecycleScope.launch {
          scancnbox= db.barcodeDao().cnwithboxes() as ArrayList<CNWithBoxes>
          val cnbox=  db.verifydao().getAll()
          for(i in cnbox){
              verifycnbox.add(CNWithBoxes(i.cn.toString(),i.box.toString(),"0"))
          }

          for(i in verifycnbox){
             for(j in scancnbox){
                 if(i.cn.toString().equals(j.cn.toString())){
                     if(!i.box.toString().equals(j.box.toString())){
                         missingcnbox.add(CNWithBoxes(i.cn,i.box.toString(),(i.box.toInt()-j.box.toInt()).toString()))
                     }
                 }
             }
          }

          adapter=MissingAdapter(myinterface,missingcnbox)
          binding.recymissing.adapter=adapter

        }



    }

    override fun missing(cnWithBoxes: CNWithBoxes) {
        startActivity(Intent(this@MissingList,CheckMissing::class.java)
            .putExtra("cn",cnWithBoxes.cn)
            .putExtra("totalbox",cnWithBoxes.box.toInt())
        )
    }
}