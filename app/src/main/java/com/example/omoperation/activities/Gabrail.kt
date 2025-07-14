package com.example.omoperation.activities

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.ui.AppBarConfiguration
import com.example.omoperation.R
import com.example.omoperation.Utils
import com.example.omoperation.databinding.ActivityGabrailBinding
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date


class Gabrail : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityGabrailBinding

    private var partCodeFound = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityGabrailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        binding.fab.setOnClickListener { view ->
            openCamera()
        }
        startCamera()
    }
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.previewView.surfaceProvider)
            }

            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

            val analyzer = ImageAnalysis.Builder().build().also {
                it.setAnalyzer(ContextCompat.getMainExecutor(this)) { imageProxy ->
                    processImageProxy(recognizer, imageProxy)
                }
            }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(this, cameraSelector, preview, analyzer)

        }, ContextCompat.getMainExecutor(this))
    }

    private fun processImageProxy(recognizer: TextRecognizer, imageProxy: ImageProxy) {
        if (partCodeFound) {
            imageProxy.close()
            return
        }

        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    val resultText = visionText.text
                    val match = Regex("Part Code\\s*[:：]\\s*([A-Z0-9\\-]+)", RegexOption.IGNORE_CASE).find(resultText)
                    val partCode = match?.groups?.get(1)?.value

                    if (!partCode.isNullOrEmpty()) {
                        partCodeFound = true
                        findViewById<TextView>(R.id.partCodeText).text = "Part Code: $partCode"
                        Toast.makeText(this, "Detected: $partCode", Toast.LENGTH_LONG).show()
                        // Optionally stop the camera here
                    }
                }
                .addOnFailureListener { it.printStackTrace() }
                .addOnCompleteListener { imageProxy.close() }
        } else {
            imageProxy.close()
        }
    }
    private var photoUri: Uri? = null
    private var currentPhotoPath: String? = null
    private val CAMERA_IMAGE_CODE = 100
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
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
         if (requestCode == CAMERA_IMAGE_CODE && resultCode == RESULT_OK) {
             val imgFile = File(currentPhotoPath)
             if (imgFile.exists()) {
                 val bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath())

                 //binding.imageView.setImageBitmap(bitmap)
                 val image = InputImage.fromBitmap(bitmap, 0)

                 val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
                 recognizer.process(image)
                     .addOnSuccessListener(OnSuccessListener { visionText: Text? ->
                       //  val resultText = visionText!!.getText()
                         val text = visionText!!.text
                         val partCode = extractPartCode(text)
                         Log.d("PART_CODE", "Found: $partCode")
                         binding.partCodeText.setText(partCode)
                     })
                     .addOnFailureListener(OnFailureListener { e: Exception? ->
                         e!!.printStackTrace()
                         Utils.showDialog(this@Gabrail,"error","${e.message}",R.drawable.ic_error_outline_red_24dp)
                     })
             }
        }



    }
    fun extractPartCode(text: String): String {
        val match = Regex("Part Code\\s*[:：]\\s*([A-Z0-9\\-]+)", RegexOption.IGNORE_CASE).find(text)
        return match?.groups?.get(1)?.value ?: "Not found"
    }


}