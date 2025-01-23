package com.example.omoperation.activities
import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.omoperation.CustomProgress
import com.example.omoperation.NetworkState
import com.example.omoperation.R
import com.example.omoperation.Utils
import com.example.omoperation.adapters.ImageAdapter
import com.example.omoperation.databinding.ActivityVehicleImageBinding
import com.example.omoperation.network.ApiClient
import com.example.omoperation.network.ServiceInterface
import com.example.omoperation.viewmodel.VehilcleViewMod
import com.example.omoperation.model.vehcleimage.VehcleImageMod
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Objects
import javax.inject.Inject

@AndroidEntryPoint
class VehicleImage : AppCompatActivity() , ImageAdapter.ImageInterface {
   @Inject
    lateinit var VEHCLEVIEWMOD: VehilcleViewMod
    lateinit var binding: ActivityVehicleImageBinding
    lateinit var cp: CustomProgress
    lateinit var imageadapter: ImageAdapter
     var imagelist= ArrayList<String>()
     var bitmaplist= ArrayList<String>()
    private val CAMERA_IMAGE_CODE = 100
    val pd : CustomProgress by lazy { CustomProgress(this) }
    var isvalid=false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_vehicle_image)
        cp= CustomProgress(this)
        imageadapter= ImageAdapter(this,imagelist)
        binding.recyimage.setHasFixedSize(true)
        binding.recyimage.layoutManager=GridLayoutManager(this,4)
        binding.recyimage.adapter=imageadapter
        binding.vehiclemod=VEHCLEVIEWMOD
        binding.lifecycleOwner=this
        VEHCLEVIEWMOD.livedata.observe(this, Observer{
            isvalid=false
            when(it)
            {
              is NetworkState.Loading->{
                  cp.show()
              }

                is NetworkState.Error -> {
                    cp.dismiss()
                    Utils.showDialog(this,it.title,it.message,R.drawable.ic_error_outline_red_24dp)
                }
                is NetworkState.Success<*> -> {
                    isvalid=true
                    val a=it.data.toString()
                    Utils.showDialog(this,"Success","Vehicle Verified Successfully",R.drawable.ic_success)
                     binding.vehcleno.isEnabled=false
                     cp.dismiss()
                }
            }
        })
        binding.pickimage.setOnClickListener {
            pickImage()
        }
        binding.submit.setOnClickListener {
            if(isvalid)
            submit()
            else Utils.showDialog(this,"error","Please validate vehicle first",R.drawable.ic_error_outline_red_24dp)
        }


    }

    fun pickImage(){
        val items = arrayOf<CharSequence>(
            "Take Photo", "Choose from Library",
            "Cancel"
        )
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Add Photo!")
        builder.setItems(
            items
        ) { dialog, item ->
            if (items[item] == "Take Photo") {
                callfrom = "Camera"
                requestPermissionsBasedOnVersion()
            } else if (items[item] == "Choose from Library") {
                callfrom = "Gallery"
                requestPermissionsBasedOnVersion()
            } else if (items[item] == "Cancel") {
                dialog.dismiss()
            }
        }
        builder.show()

    }

    var callfrom = "Camera"
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

    // Method to handle the permission results
    private val requestPermissions = registerForActivityResult<Array<String>, Map<String, Boolean>>(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results: Map<String, Boolean> ->
        handlePermissionsResult(
            results
        )
    }


    private fun handlePermissionsResult(results: Map<String, Boolean>) {
        // Iterate through the results and check which permissions were granted
        for ((permission, isGranted) in results) {
            // Handle each permission result here
            if (isGranted) {
                  proceedWithFunctionality();
            } else {
            }
        }
        // If permissions are granted, proceed with the required functionality
        proceedWithFunctionality()
    }

    private fun requestPermissionsBasedOnVersion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            if (arePermissionsGranted(
                    arrayOf(
                        Manifest.permission.READ_MEDIA_IMAGES,
                        Manifest.permission.READ_MEDIA_VIDEO,
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
                    )
                )
            ) {
                proceedWithFunctionality()
            } else {
                requestPermissions.launch(
                    arrayOf(
                        Manifest.permission.READ_MEDIA_IMAGES,
                        Manifest.permission.READ_MEDIA_VIDEO, Manifest.permission.CAMERA,
                        Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
                    )
                )
            }
        }
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (arePermissionsGranted(
                    arrayOf(
                        Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.CAMERA,
                        Manifest.permission.READ_MEDIA_VIDEO
                    )
                )
            ) {
                proceedWithFunctionality()
            } else {
                requestPermissions.launch(
                    arrayOf(
                        Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.CAMERA,
                        Manifest.permission.READ_MEDIA_VIDEO
                    )
                )
            }
        }
        else {
            if (arePermissionsGranted(
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA
                    )
                )
            ) {
                proceedWithFunctionality()
            } else {
                requestPermissions.launch(
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA
                    )
                )
            }
        }
    }

    private fun arePermissionsGranted(permissions: Array<String>): Boolean {
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }

    // Method to proceed with the required functionality if permissions are granted
    private fun proceedWithFunctionality() {
        if (callfrom.equals("Camera", ignoreCase = true)) {
            openCamera()
        } else {
            val intent = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )
            intent.setType("image/*")
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            startActivityForResult(
                Intent.createChooser(intent, "Select Picture"),
                PICK_IMAGE
            )
        }
    }
    val PICK_IMAGE = 2

     override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && null != data) {
            val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
            if (data.clipData != null) {
                val count =
                    data.clipData!!.itemCount //evaluate the count before the for loop --- otherwise, the count is evaluated every loop.
                for (i in 0 until count) {
                    val imageUri = data.clipData!!.getItemAt(i).uri
                    val file = File(imageUri.path)
                    val filePath = file.path.split(":".toRegex()).dropLastWhile { it.isEmpty() }
                        .toTypedArray()
                    val image_id = filePath[filePath.size - 1]
                    var imagePath = ""
                    // Cursor cursor = getContentResolver().query(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, MediaStore.Images.Media._ID + " = ? ", new String[]{image_id}, null);
                    val cursor = contentResolver.query(
                        imageUri,
                        filePathColumn,
                        MediaStore.Images.Media._ID + " = ? ",
                        arrayOf(image_id),
                        null
                    )
                    if (cursor != null) {
                        cursor.moveToFirst()
                        imagePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
                        cursor.close()
                    } ///storage/emulated/0/DCIM/Camera/IMG_20230111_144951.jpg
                    imagelist.add(imagePath)
                }
            } else {
                val selectedImage = data.data
                try {
                    val b = MediaStore.Images.Media.getBitmap(
                        this@VehicleImage.getContentResolver(),
                        selectedImage
                    )
                    val imagePath = Utils.getPath(this@VehicleImage, selectedImage)
                    imagelist.add(imagePath)
                } catch (e: Exception) {
                }

            }
           imageadapter.notifyDataSetChanged()
        }
         else if (requestCode == CAMERA_IMAGE_CODE && resultCode == RESULT_OK) {
            imagelist.add(currentPhotoPath!!)
            imageadapter.notifyDataSetChanged()
         }



    }

  /*  override fun sendPosition(position: Int) {

    }*/
    override fun sendPosition(position: Int) {
      imagelist.removeAt(position)
       imageadapter.notifyDataSetChanged()

       }
    private fun imageToString(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream)
        val imgBytes = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(imgBytes, Base64.DEFAULT)
    }

    fun submit(){
        bitmaplist.clear()
        pd.show()
        for (path in imagelist) {
            val uri = Uri.fromFile(File(path))
            //base64Images.add(imageToString(bitmap));
            try {
                bitmaplist.add(imageToString(
                    MediaStore.Images.Media.getBitmap(
                        this.contentResolver,
                        uri
                    )
                ))
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        val mod= VehcleImageMod(bitmaplist,binding.vehcleno.text.toString())
        lifecycleScope.launch {
           val resp= ApiClient.getClient().create(ServiceInterface::class.java).image_upload_lorry(Utils.getheaders(),mod)
           if(resp.code()==200){
               pd.dismiss()
               if(resp.body()!!.error.equals("false")){
                   Utils.showDialog(
                       this@VehicleImage,
                       "Success",
                       resp.body()!!.response,
                       R.drawable.ic_success
                   )

               }

           }else {
               pd.dismiss()
               Utils.showDialog(this@VehicleImage,"error",resp.code().toString(),R.drawable.ic_error_outline_red_24dp)
        }}

    }
//4505241000845
}