package com.example.omoperation.activities

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.view.MotionEvent
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.View.OnTouchListener
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.omoperation.Constants
import com.example.omoperation.CustomProgress
import com.example.omoperation.OmOperation
import com.example.omoperation.R
import com.example.omoperation.Utils
import com.example.omoperation.adapters.ImageAdapter
import com.example.omoperation.databinding.ActivityVehicleLoadUnloadBinding
import com.example.omoperation.model.CommonMod
import com.example.omoperation.model.vehicleloadunload.VehcleLoadUnloadMod
import com.example.omoperation.network.ApiClient
import com.example.omoperation.network.ServiceInterface
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class VehicleLoadUnload : AppCompatActivity(), ImageAdapter.ImageInterface {
    lateinit var binding: ActivityVehicleLoadUnloadBinding
    val images=ArrayList<String>()
    var selecttype="imageloading"
    lateinit var adapter : ImageAdapter
    lateinit var title : TextView
    val REQUEST_IMAGE_CAPTURE=1
    val REQUEST_IMAGE_BROWSE=2
    lateinit var listist: List<String>
    private var arraydapter: ArrayAdapter<String>? = null
    var status="loadChallan"
    val cp:CustomProgress by lazy { CustomProgress(this) }
    var loadtype=1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this, R.layout.activity_vehicle_load_unload)
        loadtype=intent.getIntExtra("loadtype",1).toInt()
        listist=ArrayList()
        title=findViewById(R.id.title)
        title.setText("Vehicle Load/Unload")
       binding.recyimage.setHasFixedSize(false)
        binding.recyimage.layoutManager=LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false
        )
        binding.fieldVehicleNo.setOnFocusChangeListener(OnFocusChangeListener { v: View?, hasFocus: Boolean ->
            if (!hasFocus) {
                val vehicle: String = binding.fieldVehicleNo.getText().toString().trim { it <= ' ' }
                if (TextUtils.isEmpty(vehicle)) {
                    binding.fieldVehicleNo.setError(getString(R.string.required))
                } else {
                    getChallanList(vehicle)
                }
            }
        })
        adapter= ImageAdapter(this,images)
        binding.recyimage.adapter=adapter
        binding.subBtn.setOnClickListener {
            if(validate()){
                if(Utils.haveInternet(this)){
                    cp.show()

                    lifecycleScope.launch {
                        val mod=  VehcleLoadUnloadMod(
                            OmOperation.getPreferences(Constants.BCODE,""),
                            binding.challanSpinner.text.toString(),
                            OmOperation.getPreferences(Constants.EMP_CODE,""),
                            images,
                            binding.remarks.text.toString(),
                            selecttype,
                            binding.fieldVehicleNo.text.toString()

                        )
                        val response=ApiClient.getClient().create(ServiceInterface::class.java).vluimage(Utils.getheaders(),mod)
                        cp.dismiss()
                        if(response!!.code()==200){

                            if(response.body()?.error.equals("false")){
                                images.clear()
                                binding.fieldVehicleNo.setText("")
                                binding.challanSpinner.setText("")
                                binding.remarks.setText("")
                                adapter.notifyDataSetChanged()

                               Utils.showDialog(this@VehicleLoadUnload,"Success",response.body()?.response.toString(),R.drawable.ic_success)
                            }
                            else{
                                Utils.showDialog(this@VehicleLoadUnload,"Fail",response.body()!!.response.toString(),R.drawable.ic_error_outline_red_24dp)
                            }
                        }
                        else
                            Utils.showDialog(this@VehicleLoadUnload,"error code${response.code()}","${response.message()}",R.drawable.ic_error_outline_red_24dp)

                    }


                }
            }
        }
        binding.browse.setOnClickListener {
            cameraPermissionRequest.launch(Manifest.permission.CAMERA)

           // cameraPermissionRequest
        }
        binding.select.setOnClickListener {
            opend()
        }
        if(loadtype==2){
            binding.select.setText("Unloading")
            status="unloadChallan"
            selecttype="imageUnloading"
            binding.challanSpinner.setHint("Select Gate Entry No.")
        }

    }
    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            // Create the file where the photo should go
            val photoFile: File? = try {
                createImageFile()
            } catch (ex: IOException) {
                // Error occurred while creating the file
                null
            }
            // Continue only if the file was successfully created
            photoFile?.also {
                val photoURI: Uri = FileProvider.getUriForFile(
                    this,
                    "com.example.omoperation.fileprovider", // Replace with your package name
                    it
                )
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }
    var currentPhotoPath=""
    private fun createImageFile(): File {

        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            images.add(currentPhotoPath)
            adapter.notifyDataSetChanged()

        }
        else if (requestCode == REQUEST_IMAGE_BROWSE && resultCode == RESULT_OK) {
            val selectedImageUri: Uri? = data?.data
            val imagePath = selectedImageUri?.let { getPathFromUri(it) }
           images.add(imagePath!!)
            adapter.notifyDataSetChanged()

        }
    }



    // Function to create an image Uri where the camera will save the photo

    private val cameraPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
          //  dispatchTakePictureIntent()

            openGallery()
        } else {
            // Permission denied
        }
    }

    private val galleryPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            openGallery()
        } else {
            // Permission denied
        }
    }
    fun openGallery(){
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_IMAGE_BROWSE)
    }
    fun validate():Boolean{
        if(binding.fieldVehicleNo.text.toString().equals("")){
            Utils.showDialog(this,"error","Vehicle name is missing",R.drawable.ic_error_outline_red_24dp)
            return false
        }
        else if(binding.challanSpinner.text.toString().equals("")){
            Utils.showDialog(this,"error","Challan is Missing",R.drawable.ic_error_outline_red_24dp)
            return false
        }
        else if(binding.remarks.text.toString().equals("")){
            Utils.showDialog(this,"error","Remarks is Missing",R.drawable.ic_error_outline_red_24dp)
            return false
        }
        return true
    }
    private fun opend() {
        val items = arrayOf<CharSequence>("Loading", "Unloading")
        // }
        val builder = AlertDialog.Builder(this@VehicleLoadUnload)
        builder.setItems(items) { dialog: DialogInterface, item: Int ->
            if(item==0){
                 status="loadChallan"
                 selecttype="imageloading"
                 binding.challanSpinner.setHint("Select Challan Number")

            }
            else{

                status="unloadChallan"
                selecttype="imageUnloading"
                binding.challanSpinner.setHint("Select Gate Entry No.")
            }
            binding.select.setText( items[item])
            dialog.dismiss()
        }
        builder.show()
    }


    override fun sendPosition(position: Int) {
       // TODO("Not yet implemented")
    }
    private fun getPathFromUri(uri: Uri): String? {
        var filePath: String? = null
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(uri, projection, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                filePath = it.getString(columnIndex)
            }
        }
        return filePath
    }
    private fun getChallanList(vehicleNo: String) {
        val mod: CommonMod = CommonMod()
        //mod.status="loadChallan"
        mod.status=status
        mod.bcode=OmOperation.getPreferences(Constants.BCODE,"")
        mod.lorryno=vehicleNo
        lifecycleScope.launch {
           val response= ApiClient.getClient().create(ServiceInterface::class.java).vluimage(Utils.getheaders(),mod)
           if(response.code()==200){
               if(response.body()?.error.equals("false")){
                  /* listist = response.body()?.response!! as List<String>
                   arraydapter =ArrayAdapter<String>(this@VehicleLoadUnload, R.layout.challan_list_item, listist)
                   binding.challanSpinner.setAdapter<ArrayAdapter<String>>(arraydapter)
                  */

                   // Create a list of data for the spinner


                   // Create an ArrayAdapter using the string array and a default spinner layout
                   listist = response.body()?.response!! as List<String>
                   arraydapter =
                       ArrayAdapter<String>(this@VehicleLoadUnload, R.layout.challan_list_item, listist)

                   // Specify the layout to use when the list of choices appears

                   // Apply the adapter to the spinner
                  binding.challanSpinner.setAdapter(arraydapter)
                   //binding.challanSpinner.setText(listist.get(0))
                   binding. challanSpinner.setOnTouchListener(OnTouchListener { v: View, event: MotionEvent? ->
                       (v as AutoCompleteTextView).showDropDown()
                       false
                   })
               }
               else{
                   Utils.showDialog(this@VehicleLoadUnload,"error true${response.code()}",response.body().toString(),R.drawable.ic_error_outline_red_24dp)
                   listist = ArrayList()
                   arraydapter =
                       ArrayAdapter (this@VehicleLoadUnload, R.layout.challan_list_item, listist)
                   binding.challanSpinner.setAdapter<ArrayAdapter<String>>(arraydapter)
                   binding.challanSpinner.setText("")
               }
           }
            else Utils.showDialog(this@VehicleLoadUnload,"error code${response.code()}","${response.message()}",R.drawable.ic_error_outline_red_24dp)


        }

    }
}