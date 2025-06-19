package com.example.omoperation.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.omoperation.R
class OcrScanAct : AppCompatActivity() {
   /* private lateinit var previewView: PreviewView
    private lateinit var captureButton: Button
    private lateinit var cameraExecutor: ExecutorService
    private var imageAnalyzer: ImageAnalysis? = null*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ocr_scan)

       /* previewView = findViewById(R.id.previewView)
        captureButton = findViewById(R.id.captureButton)

        cameraExecutor = Executors.newSingleThreadExecutor()

        startCamera()

        captureButton.setOnClickListener {
            scanTextFromCamera()
        }
    }

    private fun startCamera() {
        val cameraProviderFuture: ListenableFuture<ProcessCameraProvider> =
            ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            imageAnalyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()

            cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalyzer)
        }, ContextCompat.getMainExecutor(this))
    }

    private fun scanTextFromCamera() {
        imageAnalyzer?.setAnalyzer(cameraExecutor, { imageProxy ->
            val mediaImage = imageProxy.image
            if (mediaImage != null) {
                val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
                recognizer.process(image)
                    .addOnSuccessListener { visionText ->
                        extractText(visionText)
                    }
                    .addOnFailureListener { e ->
                        Log.e("OCR", "Text recognition failed", e)
                    }
                    .addOnCompleteListener {
                        imageProxy.close()
                    }
            }


        })
    }

    private fun extractText(visionText: Text) {
        val extractedText = StringBuilder()
        for (block in visionText.textBlocks) {
            for (line in block.lines) {
                extractedText.append(line.text).append("\n")
                if(line.text.contains("-") && line.text.startsWith("00")){
                    val parts = line.text.split("-")
                //    if(parts.size==2){
                        Log.d("Ashish Sikarwar", "Extracted Text: ${line.text}")
                    val intent = Intent().apply {
                        putExtra("result_key", line.text)
                    }
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                    break

                   // }

                }
                Log.d("Ashish Sikarwar", "Extracted Text: ${line.text}")
            }
        }
    //    Log.d("OCR", "Extracted Text: $extractedText")
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }*/}
}