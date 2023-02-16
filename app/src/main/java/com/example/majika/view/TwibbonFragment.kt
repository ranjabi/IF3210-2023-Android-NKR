package com.example.majika.view

import android.content.Context
import android.content.pm.PackageManager
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.graphics.RectF
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.hardware.display.DisplayManager
import android.media.Image
import android.media.ImageReader
import android.os.*
import android.provider.MediaStore
import android.util.Log
import android.view.Display
import android.view.Surface
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.res.ResourcesCompat
import com.example.majika.R
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import kotlin.math.max

class TwibbonFragment : Fragment() {
    private lateinit var textureView: TextureView
    private lateinit var handler: Handler
    private lateinit var handlerThread: HandlerThread

    // Camera processing variables
    private lateinit var cameraId: String
    private lateinit var cameraManager: CameraManager
    private lateinit var cameraDevice: CameraDevice
    private lateinit var cameraCaptureSession: CameraCaptureSession
    private lateinit var cameraCaptureRequest: CaptureRequest.Builder

    // Image processing variables
    private lateinit var imageReader: ImageReader
    private lateinit var imageView: ImageView
    private lateinit var finalImageBitmap: Bitmap

    companion object {
        private const val REQUEST_ID_MULTIPLE_PERMISSION = 100
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_twibbon, container, false)
    }

    // Bind textureView, start cameraManager, and ask for permission
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d("LayoutChecker", "onViewCreated called")
        super.onViewCreated(view, savedInstanceState)

        // Bind textureView and get the cameraManager
        textureView = view.findViewById(R.id.texture_view)
        cameraManager = requireContext().getSystemService(Context.CAMERA_SERVICE) as CameraManager
        imageView = requireView().findViewById(R.id.twibbon_frame_image_view)

        // Request permission for camera and write
        getPermissions()

        view.findViewById<ImageButton>(R.id.take_another_button).setOnClickListener {
            restartPreview()
        }
    }

    // Start a new thread and enable the preview again
    override fun onResume() {
        super.onResume()
        Log.d("LayoutChecker", "onResume called")
        startThread()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (isCameraPermGranted()) {
                textureView.surfaceTextureListener = surfaceTextureListener
                Log.d("PreviewCaller", "Called in onResume")
                enablePreview()
            }
        } else {
            if (isCameraPermGranted() && isWritePermGranted()) {
                textureView.surfaceTextureListener = surfaceTextureListener
                Log.d("PreviewCaller", "Called in onResume")
                enablePreview()
            }
        }
    }

    // Release thread onPause
    override fun onPause() {
        super.onPause()
        Log.d("LayoutChecker", "onPause called")
        if (this::cameraDevice.isInitialized) {
            closeCamera()
            stopThread()
        }
    }

//    override fun onStop() {
//        super.onStop()
//        Log.d("LayoutChecker", "onStopped called")
//        closeCamera()
//        stopThread()
//    }

    // Enable camera preview
    private fun enablePreview() {
        Log.d("previewMethod", "Enabling camera preview")
        if (textureView.isAvailable) {
            setupCamera()
            transformImage(textureView.width, textureView.height)
            openCamera()
            imageReader = ImageReader.newInstance(1080, 1920, ImageFormat.JPEG, 2)
            imageReader.setOnImageAvailableListener(setOnImageAvailableListener, null)

            requireView().findViewById<ImageButton>(R.id.capture_button).setOnClickListener {
                cameraCaptureRequest =
                    cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE)
                cameraCaptureRequest.addTarget(imageReader.surface)
                cameraCaptureSession.capture(cameraCaptureRequest.build(), null, null)
            }


        }
    }

    // Restart the preview (only works when the image is actually blocking)
    private fun restartPreview() {
        Log.d("previewMethod", "Restarting camera preview")
        if (this::finalImageBitmap.isInitialized && imageView.drawable.constantState != ResourcesCompat.getDrawable(
                resources,
                R.drawable.twibbon_frame_png,
                null
            )
        ) {
            closeCamera()
            imageView.setImageResource(R.drawable.twibbon_frame_png)
            enablePreview()
        } else {
            Log.d("previewMethod", "imageView not initialized")
        }
    }

    private val surfaceTextureListener = object : TextureView.SurfaceTextureListener {
        override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
            Log.d("surfaceTexture", "Now Available")

            enablePreview()
        }

        override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {
            Log.d("surfaceTexture", "Now Change Sizes")
        }

        override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
            Log.d("surfaceTexture", "Now Destroyed")
            surface.release()
            return true
        }

        override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {
//            Log.d("surfaceTexture", "Now Updated")
        }

    }

    private val cameraStateCallback = object : CameraDevice.StateCallback() {
        override fun onOpened(camera: CameraDevice) {
            cameraDevice = camera
            cameraCaptureRequest = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            val surface = Surface(textureView.surfaceTexture)
            cameraCaptureRequest.addTarget(surface)

            cameraDevice.createCaptureSession(
                listOf(surface, imageReader.surface),
                captureStateCallback,
                handler
            )

        }

        override fun onDisconnected(camera: CameraDevice) {
            cameraDevice.close()
        }

        override fun onError(camera: CameraDevice, error: Int) {
            Log.e("cameraStateCallback", "Error connecting to camera")
        }

    }

    private val captureStateCallback = object : CameraCaptureSession.StateCallback() {
        override fun onConfigured(session: CameraCaptureSession) {
            cameraCaptureSession = session
            cameraCaptureSession.setRepeatingRequest(cameraCaptureRequest.build(), null, null)
        }

        override fun onConfigureFailed(session: CameraCaptureSession) {
            Log.e("captureStateCallback", "Session failed")
        }

    }

    private val setOnImageAvailableListener = ImageReader.OnImageAvailableListener {
        val image = imageReader.acquireLatestImage()

        // Create bitmaps for the 1st and 2nd image
        val rawPictureBitmap = changeImageToBitmap(image)
        val twibbonBitmap = BitmapFactory.decodeResource(resources, R.drawable.twibbon_frame_png)

        // Rotate image to be upright and mirror it
        val pictureBitmap = mirrorBitmap(rotateBitmap(rawPictureBitmap, 270f))

        val scaleFactor =
            (twibbonBitmap.width.toFloat() / pictureBitmap.width).coerceAtMost(twibbonBitmap.height.toFloat() / pictureBitmap.height)

        val scaledPictureBitmap =
            Bitmap.createScaledBitmap(
                pictureBitmap,
                (pictureBitmap.width * scaleFactor).toInt(),
                (pictureBitmap.height * scaleFactor).toInt(),
                false
            )

        // Create a new bitmap with the dimensions of the 2nd image (the twibbon frame)
        finalImageBitmap =
            Bitmap.createBitmap(twibbonBitmap.width, twibbonBitmap.height, twibbonBitmap.config)
        val canvas = Canvas(finalImageBitmap)

        // Draw the picture bitmap in the canvas
        canvas.drawBitmap(scaledPictureBitmap, 0f, 0f, null)

        // Draw the twibbon bitmap in the canvas
        canvas.drawBitmap(twibbonBitmap, 0f, 0f, null)

        // Prepare to replace layout
//        val layout: ViewGroup = requireView().findViewById(R.id.twibbon_layout)
//        val textureView: TextureView = requireView().findViewById(R.id.texture_view)

        // Insert the image data into the imageView
        imageView.setImageBitmap(finalImageBitmap)

        // Replace the view
//        layout.removeView(textureView)

        // Saving the image
        val outputStream = ByteArrayOutputStream()
        finalImageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        val bytes = outputStream.toByteArray()
//        saveImage(bytes)

        // Close image and imageReader
        image.close()
        closeCamera()

        Toast.makeText(requireContext(), "Image Captured", Toast.LENGTH_SHORT).show()
    }

    // Background thread handler
    private fun startThread() {
        Log.d("ThreadChecker", "Starting thread")
        handlerThread = HandlerThread("cameraThread").also { it.start() }
        handler = Handler(handlerThread.looper)
    }

    private fun stopThread() {
        Log.d("ThreadChecker", "Stopping thread")
        handlerThread.quitSafely()
        try {
            handlerThread.join()
        } catch (_: InterruptedException) {

        }
    }

    // Start the camera
    @SuppressLint("MissingPermission")
    private fun openCamera() {
        cameraManager.openCamera(cameraId, cameraStateCallback, handler)

    }

    // Get the front camera
    private fun setupCamera() {
        val listOfCameras: Array<String> = cameraManager.cameraIdList

        for (id in listOfCameras) {
            val cameraCharacteristics = cameraManager.getCameraCharacteristics(id)

            // Choose front facing camera
            if (cameraCharacteristics.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_FRONT) {
                cameraId = id
                break
            }
        }
    }

    // Close the camera and release the resource
    private fun closeCamera() {
        cameraDevice.close()
        imageReader.close()
        Log.d("ClosingCamera", "Successfully close the camera")
    }

    private fun transformImage(width: Int, height: Int) {
        val matrix = Matrix()
        val displayManager =
            requireContext().getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
        val display = displayManager.getDisplay(Display.DEFAULT_DISPLAY)
        val rotation = display.rotation

        val cameraChar = cameraManager.getCameraCharacteristics(cameraId)
        val map = cameraChar.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
        val previewSize = map!!.getOutputSizes(SurfaceTexture::class.java).first()

        val textureRectF = RectF(0f, 0f, width.toFloat(), height.toFloat())
        val previewRectF = RectF(0f, 0f, previewSize.height.toFloat(), previewSize.width.toFloat())
        val centerX = textureRectF.centerX()
        val centerY = textureRectF.centerY()

        if (isRotated(rotation)) {
            previewRectF.offset(centerX - previewRectF.centerX(), centerY - previewRectF.centerY())
            matrix.setRectToRect(textureRectF, previewRectF, Matrix.ScaleToFit.FILL)

            val scale =
                max(width.toFloat() / previewSize.width, height.toFloat() / previewSize.height)
            matrix.postScale(scale, scale, centerX, centerY)
            matrix.postRotate((90 * (rotation - 2)).toFloat(), centerX, centerY)
        }

        textureView.setTransform(matrix)
    }

    private fun isRotated(rotation: Int): Boolean {
        return rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270
    }

    private fun rotateBitmap(source: Bitmap, angle: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, false)
    }

    private fun mirrorBitmap(source: Bitmap): Bitmap {
        val matrix = Matrix()
        matrix.preScale(-1f, 1f)
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, false)
    }

    // Save the image to an external storage
    private fun saveImage(bytes: ByteArray) {
        lateinit var outputStream: OutputStream

        // API level 30 and above uses MediaStore instead of normal outputStream
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val resolver = requireActivity().contentResolver
            val values = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, "test.jpg")
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            }

            val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            uri?.let { outputStream = resolver.openOutputStream(it)!! }
        } else {
            val path = Environment.getExternalStorageDirectory().toString()
            val file = File(path, "test.jpg")
            outputStream = FileOutputStream(file)
        }
        try {
            outputStream.write(bytes)
            outputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun getPermissions() {
        val listPermissionRequired: MutableList<String> = mutableListOf()

        // Request camera permission
        if (!isCameraPermGranted()) {
            Log.d("PermissionManager", "Add camera to request")
            listPermissionRequired.add(Manifest.permission.CAMERA)
        }

        // Request write permission
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            if (!isWritePermGranted()) {
                Log.d("PermissionManager", "Add write external storage to request")
                listPermissionRequired.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }

        if (listPermissionRequired.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                listPermissionRequired.toTypedArray(), REQUEST_ID_MULTIPLE_PERMISSION
            )
        }
    }

    private fun isCameraPermGranted(): Boolean {
        val cameraPerm =
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)

        if (cameraPerm == PackageManager.PERMISSION_GRANTED) {
            return true
        }

        return false
    }

    private fun isWritePermGranted(): Boolean {
        val writePerm = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        if (writePerm == PackageManager.PERMISSION_GRANTED) {
            return true
        }

        return false
    }

    // Convert an image into a bitmap
    private fun changeImageToBitmap(image: Image): Bitmap {
        val buffer = image.planes[0].buffer
        val bytes = ByteArray(buffer.capacity())
        buffer.get(bytes)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_ID_MULTIPLE_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    enablePreview()
                } else {
                    // Permission denied
                    getPermissions()
                }
            }
        }
    }

}