package com.example.omoperation.activities

import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.KeyEvent
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.omoperation.Constants
import com.example.omoperation.LocationService
import com.example.omoperation.LoggerService
import com.example.omoperation.OmOperation
import com.example.omoperation.R
import com.example.omoperation.UserRepository
import com.example.omoperation.Utils
import com.example.omoperation.adapters.Dash_Adapt
import com.example.omoperation.adapters.DrawerAdapter
import com.example.omoperation.databinding.ActivityDashboardBinding
import com.example.omoperation.model.MIS
import com.example.omoperation.room.AppDatabase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Named


@AndroidEntryPoint
class DashboardAct : AppCompatActivity()   {
    lateinit var binding : ActivityDashboardBinding
    var drawerLayout: DrawerLayout? = null
    var actionBarDrawerToggle: ActionBarDrawerToggle? = null
    lateinit  var tvemp: TextView
    lateinit var tvname: TextView
    lateinit var empname: TextView
    @Inject
    lateinit var dashadap: Dash_Adapt
    @Inject
    lateinit var draweadpter: DrawerAdapter

    @Inject
    lateinit var LoggerService: LoggerService
    @Inject
    @Named("sql")
    lateinit var userrepo: UserRepository
    private val CAMERA_IMAGE_CODE = 100
    private val CAMERA_REQUEST_CODE = 101
    lateinit var db :AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding=DataBindingUtil.setContentView(this,R.layout.activity_dashboard)





        // Start the location tracking service
    //    val serviceIntent = Intent(this, LocationService::class.java)

           // startService(serviceIntent)



        db=AppDatabase.getDatabase(this)
        lifecycleScope.launch {
            val timeThreshold = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                .format(Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000))
            db.restorebarcodedao().deleteAfter24Hours(timeThreshold)
        }
       lifecycleScope.launch {
           if(!OmOperation.getPreferences2(Constants.todaydate,"").equals(Utils.GetTodayDate())){
               OmOperation.savePreferences2(Constants.MISDATA,"[]")
               OmOperation.savePreferences2(Constants.todaydate,"")
           }


        /*   val jsonString = OmOperation.getPreferences2(Constants.MISDATA, "")
           val mydata: ArrayList<MIS> = jsonString?.takeIf { it.isNotEmpty() }?.let {
               Gson().fromJson(it, object : TypeToken<ArrayList<MIS>>() {}.type)
           } ?: arrayListOf()
           mydata.add(
               MIS(OmOperation.getPreferences(Constants.EMP_CODE,""),
               OmOperation.getPreferences(Constants.EMP_CODE,""),
               "123456789","CHALLAN")
           )
           OmOperation.savePreferences2(Constants.MISDATA,mydata.toString())*/
       }

        empname=findViewById(R.id.empname)
       // WhatsAppService.sendWhatsAppMessage("+918285497507", "Your otp is 1234567");
        empname.setText(OmOperation.getPreferences(Constants.EMPNAME,""))
        LoggerService.print()
        userrepo.saveUser()
        tvemp = findViewById(R.id.tvemp)
        tvname = findViewById(R.id.tvname)
        drawerLayout = findViewById(R.id.my_drawer_layout)
        actionBarDrawerToggle =  ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close)
        drawerLayout!!.addDrawerListener(actionBarDrawerToggle!!)
        actionBarDrawerToggle!!.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        init()
        click()
    }

    private fun click() {
        if(OmOperation.getPreferences(Constants.PROFILE,"").equals("")){}
        else{
            binding.image.setImageBitmap(Utils.getCircularBitmap(Utils.getBitmapToBase64(OmOperation.getPreferences(Constants.PROFILE,""))))
        }
        binding.image.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.CAMERA), CAMERA_REQUEST_CODE);
            } else {
                openCamera();
            }

        }
    }

    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, CAMERA_IMAGE_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAMERA_IMAGE_CODE && resultCode == RESULT_OK) {
            // Get the captured image
            val photo = data!!.extras!!["data"] as Bitmap?
            // Set the image in ImageView
            binding.image.setImageBitmap(Utils.getCircularBitmap(photo))
            OmOperation.savePreferences(Constants.PROFILE,Utils.imageToString((photo)))
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            } else {
                // Handle permission denial
            }
        }
    }




    fun init(){
        binding.version.text = "V" + getPackageManager().getPackageInfo(getPackageName(), 0).versionName.toString()

        binding.footerText.text =  "\u00a9 " + Calendar.getInstance()[Calendar.YEAR] + ". Powered By OM Logistics Ltd"

        tvname.setText(OmOperation.getPreferences(Constants.BNAME,"")+"("
                +OmOperation.getPreferences(Constants.BCODE,"")+")")
        tvemp.setText(OmOperation.getPreferences(Constants.EMPNAME,"")+"("+OmOperation.getPreferences(Constants.EMP_CODE,"")+")")


        binding.recyMenu.setHasFixedSize(true)
        binding.recyMenu.layoutManager=LinearLayoutManager(this)
        binding.recyMenu.adapter=draweadpter

        binding.recyDash.setHasFixedSize(true)
        binding.recyDash.layoutManager=GridLayoutManager(this,3)
        // dashadap= Dash_Adapt(this)
        binding.recyDash.adapter=dashadap

    }
    fun Back(v : View){
        // onBackPressed()
        drawerLayout!!.open()
    }

    override fun onResume() {
        super.onResume()
        if(OmOperation.getPreferences(Constants.ISLOGIN,"0").equals("1")){

        }
        else{

            finish()
        }
    }


    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitapp()
        }
        return true
    }
    fun exitapp(){
        AlertDialog.Builder(this).apply {
            setTitle("Exit App")
            setMessage("Are you sure you want to exit?")

            // Handle the "Yes" button
            setPositiveButton("Yes") { _, _ ->
                // Close the app
                finish()
            }

            // Handle the "No" button
            setNegativeButton("No") { dialog, _ ->
                // Dismiss the dialog
                dialog.dismiss()
            }

            setCancelable(true)
        }.create().show()
    }


}