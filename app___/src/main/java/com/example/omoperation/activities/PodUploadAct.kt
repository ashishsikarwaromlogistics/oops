package com.example.omoperation.activities

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.omoperation.Constants
import com.example.omoperation.OmOperation
import com.example.omoperation.R
import com.example.omoperation.Utils
import com.example.omoperation.adapters.ImageAdapter
import com.example.omoperation.databinding.ActivityPodUploadBinding
import com.example.omoperation.model.pod.PodMod
import com.example.omoperation.network.ApiClient
import com.example.omoperation.network.ServiceInterface
import com.example.omoperation.model.CommonMod
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date


class PodUploadAct : AppCompatActivity() , ImageAdapter.ImageInterface {
    lateinit var binding: ActivityPodUploadBinding
    private val CAMERA_IMAGE_CODE = 100
    private val CAMERA_REQUEST_CODE = 101
    private val GALLERY_REQUEST_CODE = 102
    var CN_TRANSIT_STATUS_I = 0

    var imageList = ArrayList<String>()
    var imagebase64 = ArrayList<String>()
    lateinit var adapter : ImageAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_pod_upload)
       val  title : TextView= findViewById(R.id.title)
        title.setText("POD Upload")
        binding.recyImages.setHasFixedSize(true)
        binding.recyImages.layoutManager=
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false
        )
        adapter= ImageAdapter(this,imageList)
        binding.recyImages.adapter=adapter
        binding.searchBtn.setOnClickListener {
            serachpod()
        }
        binding.uploadBtn.setOnClickListener {
            uploadpod()
        }
        binding.floatingBtnImage.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.CAMERA),
                    CAMERA_REQUEST_CODE
                )
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    CAMERA_REQUEST_CODE
                )
            } else {
                openCamera();
            }
        }
    }

    private fun uploadpod() {
        if(imageList.size==0){
          Utils.showDialog(this,"error","Please select atleast one image",R.drawable.ic_error_outline_red_24dp)
            return
        }
        if (Utils.haveInternet(this)) {
            imagebase64.clear()

            for (path in imageList) {
                val uri = Uri.fromFile(File(path))
                //base64Images.add(imageToString(bitmap));
                try {
                    imagebase64.add(
                        Utils.imageToString(
                            MediaStore.Images.Media.getBitmap(
                                this.contentResolver,
                                uri
                            )
                        )
                    )
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            val mod = PodMod(
                binding.cnText.text.toString(),
                OmOperation.getPreferences(Constants.EMP_CODE, ""),
                OmOperation.getPreferences(Constants.BCODE, ""),
                imagebase64,
                0.0,
                0.0,
                ""
            )
            lifecycleScope.launch {
                val response = ApiClient.getClient().create(ServiceInterface::class.java)
                    .podupload(Utils.getheaders(), mod)
                if (response.code() == 200) {
                    Utils.showDialog(
                        this@PodUploadAct,
                        "Success" + response.code(),
                        response.body()!!.response,
                        R.drawable.ic_success
                    )

                } else Utils.showDialog(
                    this@PodUploadAct,
                    "error code " + response.code(),
                    response.message(),
                    R.drawable.ic_error_outline_red_24dp
                )

            }
//"https://api.omlogistics.co.in/cn_pod_upload1.php

        }
    }

    private fun serachpod() {
        if (Utils.haveInternet(this)) {
            val mod = CommonMod()
            mod.status = "damage"
            mod.cn_no = binding.cnText.text.toString()
            lifecycleScope.launch {
                val response = ApiClient.getClient().create(ServiceInterface::class.java)
                    .cn_detail(Utils.getheaders(), mod)
                if (response.code() == 200) {
                    if (response.body()!!.error.equals("false")) {

                        val CNNO: String = response.body()!!.user.CNNO.toString()
                        val CNDATE: String = response.body()!!.user.CNDATE.toString()
                        val CNE: String = response.body()!!.user.CNE.toString()
                        val CNR: String = response.body()!!.user.CNR.toString()
                        val BFROM: String = response.body()!!.user.BFROM.toString()
                        val BTO: String = response.body()!!.user.BTO.toString()
                        val BOOKING_MODE: String = response.body()!!.user.BOOKING_MODE.toString()
                        val STATUS: String = response.body()!!.user.STATUS.toString()
                        val POD_STATUS: String = response.body()!!.user.POD_STATUS.toString()
                        val CN_TRANSIT_STATUS: String =
                            response.body()!!.user.CN_TRANSIT_STATUS.toString()
                        if (POD_STATUS == "Y" || POD_STATUS == "B" || POD_STATUS == "YM") {
                            binding.table.setVisibility(View.GONE)
                            binding.floatingBtnImage.hide()
                            binding.uploadBtn.setVisibility(View.GONE)
                            Utils.showDialog(
                                this@PodUploadAct,
                                "error",
                                "POD Already Uploaded",
                                R.drawable.ic_error_outline_red_24dp
                            )

                        } else {
                            binding.colCnoValue.setText(CNNO)
                            binding.colCndateValue.setText(CNDATE)
                            binding.colConnameValue.setText(CNE)
                            binding.colStatusValue.setText(CNR)
                            binding.colFromValue.setText(BFROM)
                            binding.colToValue.setText(BTO)
                            binding.colModeValue.setText(BOOKING_MODE)
                            binding.colExpValue.setText(STATUS)
                            binding.table.setVisibility(View.VISIBLE)
                            binding.floatingBtnImage.show()
                            binding.uploadBtn.setVisibility(View.VISIBLE)
                            CN_TRANSIT_STATUS_I = CN_TRANSIT_STATUS.toInt()
                        }
                    } else Utils.showDialog(
                        this@PodUploadAct,
                        "error true ",
                        response.body()!!.msg,
                        R.drawable.ic_error_outline_red_24dp
                    )

                } else
                    Utils.showDialog(
                        this@PodUploadAct,
                        "error code " + response.code(),
                        response.message(),
                        R.drawable.ic_error_outline_red_24dp
                    )
            }
        }
    }

    private var photoUri: Uri? = null
    private var currentPhotoPath: String? = null
    private fun openCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            // Create the File where the photo should go
            var photoFile: File? = null
            try {
                photoFile = createImageFile()
            } catch (ex: IOException) {
                // Error occurred while creating the File
                ex.printStackTrace()
            }

            // Continue only if the File was successfully created
            if (photoFile != null) {
                photoUri =
                    FileProvider.getUriForFile(
                        this,
                        "com.example.omoperation.fileprovider",
                        photoFile
                    )
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                startActivityForResult(takePictureIntent, CAMERA_IMAGE_CODE)
            }
        }
    }


    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir =
            getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",  /* suffix */
            storageDir /* directory */
        )

        // Save a file: path for use with ACTION_VIEW intents

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.absolutePath // Get the file path here

        return image

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAMERA_IMAGE_CODE && resultCode == RESULT_OK) {
            imageList.add(currentPhotoPath!!)
            adapter.notifyDataSetChanged()
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

    override fun sendPosition(position: Int) {
      //  TODO("Not yet implemented")
    }


}