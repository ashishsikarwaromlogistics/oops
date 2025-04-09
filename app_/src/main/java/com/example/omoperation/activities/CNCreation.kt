package com.example.omoperation.activities

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.example.omoperation.Constants
import com.example.omoperation.OmOperation
import com.example.omoperation.R
import com.example.omoperation.Utils
import com.example.omoperation.databinding.ActivityCncreationBinding
import com.example.omoperation.fragments.InvoiceDetails
import com.example.omoperation.fragments.MainDetails
import com.example.omoperation.model.cncreation.CnCreationMod
import com.example.omoperation.network.ApiClient
import com.example.omoperation.network.ServiceInterface
import com.example.omoperation.viewmodel.MainDetailsViewMod
import com.google.gson.Gson
import kotlinx.coroutines.launch

class CNCreation : AppCompatActivity() {
    lateinit var title:TextView
    lateinit var mod: CnCreationMod
    lateinit var binding:ActivityCncreationBinding
    private val sharedViewModel: MainDetailsViewMod by viewModels()
    var count=1 //count=1 means in amindetails and 2 menas in invoce details
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding=DataBindingUtil. setContentView(this,R.layout.activity_cncreation)
            //  callfrag(MainDetails())
        title=findViewById(R.id.title)
        title.setText("CN Creation")
        OmOperation.savePreferences(Constants.CNCREATION,"")
        binding.tvprev.visibility= View.GONE
        binding.tvnext.setOnClickListener {
           if(count==1){
               count=2
               binding.tvprev.visibility= View.VISIBLE
               callfrag(InvoiceDetails())
               sharedViewModel.collectdata()
           }
            else{
                sumbitdata()
            }

        }
        binding.tvprev.setOnClickListener {
            count=1
            binding.tvprev.visibility= View.GONE
            onBackPressed()
        }
        supportFragmentManager.beginTransaction()
            .add(R.id.framelayout, MainDetails()) // R.id.fragment_container is the ID of your FrameLayout
          //  .addToBackStack(null)
            .commit()

        sharedViewModel.sharedData.observe(this, Observer { data ->
            // Handle the data received from the fragment
            Toast.makeText(this, "Data received: $data", Toast.LENGTH_SHORT).show()
        })

      //  callfrag(MainDetails())
    }

    private fun sumbitdata() {
        if(Utils.haveInternet(this)){
            var mod=CnCreationMod()
            mod=Gson().fromJson(OmOperation.getPreferences(Constants.CNCREATION,""),CnCreationMod::class.java)
           if(mod.invoices==null){
              Utils.showDialog(this@CNCreation,"error","Please add one Invoces",R.drawable.ic_error_outline_red_24dp)
               return
           }
            mod.status="cnentry"
            mod.bcode=OmOperation.getPreferences(Constants.BCODE,"")
            mod.empcode="55555"//OmOperation.getPreferences(Constants.EMP_CODE,"")
            lifecycleScope.launch {
               val resp= ApiClient.getClient().create(ServiceInterface::class.java).CNCreation(Utils.getheaders(),mod)
               if(resp.code()==200){
if(resp.body()!!.error.equals("false")){
    Utils.showDialog(this@CNCreation,"Success","cn generated : "+resp.body()!!.response,R.drawable.ic_success)

}
                   else{
    Utils.showDialog(this@CNCreation,"error true","cn not generate",R.drawable.ic_error_outline_red_24dp)

}
               }
               else{
                   Utils.showDialog(this@CNCreation,"error"+resp.code(),resp.message(),R.drawable.ic_error_outline_red_24dp)
               }


            }
        }
    }

    fun callfrag(fragment: Fragment){
      //  val fragment = MainDetails()

        // Begin a fragment transaction
        supportFragmentManager.beginTransaction()
            .replace(R.id.framelayout, fragment) // R.id.fragment_container is the ID of your FrameLayout
            .addToBackStack(null)

            .commit()
    }
}