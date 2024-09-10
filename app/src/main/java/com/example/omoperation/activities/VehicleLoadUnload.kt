package com.example.omoperation.activities

import android.Manifest
import android.R.attr
import android.content.ContentValues
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.omoperation.Constants
import com.example.omoperation.OmOperation
import com.example.omoperation.R
import com.example.omoperation.Utils
import com.example.omoperation.adapters.ImageAdapter
import com.example.omoperation.databinding.ActivityVehicleLoadUnloadBinding
import com.example.omoperation.model.vehicleloadunload.VehcleLoadUnloadMod
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this, R.layout.activity_vehicle_load_unload)
        title=findViewById(R.id.title)
        title.setText("Vehicle Load/Unload")
       binding.recyimage.setHasFixedSize(false)
        binding.recyimage.layoutManager=LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false
        )
        adapter= ImageAdapter(this,images)
        binding.recyimage.adapter=adapter
        binding.subBtn.setOnClickListener {
            if(validate()){
                if(Utils.haveInternet(this)){
                  val mod=  VehcleLoadUnloadMod(
                      OmOperation.getPreferences(Constants.BCODE,""),
                      binding.challanSpinner.text.toString(),
                      OmOperation.getPreferences(Constants.EMP_CODE,""),
                      images,
                      binding.remarks.text.toString(),
                      selecttype,
                      binding.fieldVehicleNo.text.toString()

                  )
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
                 selecttype="imageloading"
            }
            else{
                selecttype="imageUnloading"
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

}