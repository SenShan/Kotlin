package com.view.kotlin

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.ImageFormat
import android.hardware.camera2.*
import android.media.ImageReader
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.util.SparseIntArray
import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

class MainActivity : AppCompatActivity() ,View.OnClickListener{
    private val ORIENTATIONS = SparseIntArray()

    private var surfaceHolder: SurfaceHolder? = null

    /**
     * 摄像头管理器
     */
    private var cameraManager: CameraManager? = null
    private var characteristics: CameraCharacteristics? = null
    private var childHandler: Handler? = null
    private var mainHandler: Handler? = null

    private var imageReader: ImageReader? = null
    private var captureSession: CameraCaptureSession? = null
    private var cameraDevice: CameraDevice? = null
    private var surView: SurfaceView? = null
    private var previewBuilder:CaptureRequest.Builder?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ORIENTATIONS.append(Surface.ROTATION_0, 90)
        ORIENTATIONS.append(Surface.ROTATION_90, 0)
        ORIENTATIONS.append(Surface.ROTATION_180, 270)
        ORIENTATIONS.append(Surface.ROTATION_270, 180)

        surView=findViewById(R.id.surView)
        button.setOnClickListener(this)
        btnOpenFront.setOnClickListener(this)
        btnOpenBack.setOnClickListener(this)
        btnClose.setOnClickListener(this)

        surfaceHolder = surView?.getHolder()
        surfaceHolder?.setKeepScreenOn(true)
        surfaceHolder?.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                Log.e("sur", "surfaceCreated")
                initCamera()
            }

            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) {
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {}
        })
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btnOpenFront-> openCamera(1)
            R.id.btnOpenBack-> openCamera(0)
            R.id.btnClose-> closeCamera()
            R.id.btnStart-> takePreview()
            R.id.btnStop-> {
                captureSession?.stopRepeating()
            }
            R.id.button-> takePicture()
        }
    }
    private fun initCamera() {
        cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        val handlerThread = HandlerThread("Camera2")
        handlerThread.start()
        childHandler = Handler(handlerThread.looper)
        mainHandler = Handler(mainLooper)
        imageReader = ImageReader.newInstance(1920, 1080, ImageFormat.JPEG, 1)
        imageReader?.setOnImageAvailableListener({ reader ->
            Log.e("take", "拍照数据")
            // 拿到拍照照片数据
            val image = reader.acquireNextImage()
            val buffer = image.planes[0].buffer
            val bytes = ByteArray(buffer.remaining())
            buffer[bytes] //由缓冲区存入字节数组
            val path: String =Environment.getExternalStorageDirectory().absolutePath+"/kotlin/"+System.currentTimeMillis()+ ".jpg"

            Log.e("pictureCallback", path)

            val file = File(path)
            var fos: FileOutputStream? = null
            try {
                if (!file.exists()) {
                    file.createNewFile()
                }
                fos = FileOutputStream(file)
                fos.write(bytes, 0, bytes.size)
                fos.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            image.close()
        }, mainHandler)
        openCamera(1)
    }

    fun openCamera(cameraID: Int) {
        try {
            if (cameraManager != null) {
                characteristics = cameraManager!!.getCameraCharacteristics("" + cameraID)
                cameraManager!!.openCamera("" + cameraID, object : CameraDevice.StateCallback() {
                    override fun onOpened(camera: CameraDevice) {
                        cameraDevice = camera
                        //开启预览
                        takePreview()
                    }

                    override fun onDisconnected(camera: CameraDevice) {
                        this@MainActivity.cameraDevice?.close()
                    }

                    override fun onError(camera: CameraDevice, error: Int) {
                        TODO("Not yet implemented")
                    }
                }, mainHandler)
            }
        } catch (e: NullPointerException) {
            e.printStackTrace()
        } catch (e: SecurityException) {
            e.printStackTrace()
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    fun takePreview() {
        try {
            previewBuilder = cameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            // 将SurfaceView的surface作为CaptureRequest.Builder的目标
            previewBuilder!!.addTarget(surfaceHolder!!.surface)
            cameraDevice!!.createCaptureSession(listOf(surfaceHolder!!.surface,imageReader!!.surface), object : CameraCaptureSession.StateCallback() {
                    override fun onConfigured(session: CameraCaptureSession) {
                        if (null == cameraDevice) {
                            return
                        }
                        captureSession = session
                        try {
                            // 开启3A模式  自动对焦 自动白平衡 自动曝光
                            // 自动对焦
                            previewBuilder!!.set(CaptureRequest.CONTROL_AF_MODE,CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE)
                            //自动白平衡
                            previewBuilder!!.set(CaptureRequest.CONTROL_AWB_MODE,CaptureRequest.CONTROL_AWB_MODE_AUTO)
                            //自动曝光
                            previewBuilder!!.set(CaptureRequest.CONTROL_AE_MODE,CaptureRequest.CONTROL_AE_MODE_ON)
                            // 显示预览
                            val previewRequest = previewBuilder!!.build()
                            captureSession!!.setRepeatingRequest(previewRequest, null, childHandler)
                        } catch (e: CameraAccessException) {
                            e.printStackTrace()
                        }
                    }

                    override fun onConfigureFailed(session: CameraCaptureSession) {
                        TODO("Not yet implemented")
                    }
                }, childHandler
            )
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    private fun takePicture() {
        if (cameraDevice == null || captureSession == null || imageReader == null) {
            Log.e(
                "sur",
                "takePicture," + (cameraDevice == null) + "," + (captureSession == null) + "," + (imageReader == null)
            )
            return
        }
        try {
            val builder = cameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE)
            builder.addTarget(imageReader!!.surface)
            // 开启3A模式  自动对焦 自动白平衡 自动曝光
            // 自动对焦
            builder.set(CaptureRequest.CONTROL_AF_MODE,CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE)
            //自动白平衡
            builder.set(CaptureRequest.CONTROL_AWB_MODE, CaptureRequest.CONTROL_AWB_MODE_AUTO)
            //自动曝光
            builder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON)
            builder.set(CaptureRequest.JPEG_ORIENTATION,ORIENTATIONS.get(1)
            )
            val request = builder.build()
            captureSession!!.capture(request, null, childHandler)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    fun closeCamera() {
        cameraDevice!!.close()
        cameraDevice = null
        captureSession!!.close()
        captureSession = null
    }

    fun releaseCamera() {
        cameraDevice!!.close()
        cameraDevice = null
        captureSession!!.close()
        captureSession = null
        imageReader!!.close()
        imageReader = null
        mainHandler!!.removeCallbacksAndMessages(null)
        mainHandler = null
        childHandler!!.removeCallbacksAndMessages(null)
        childHandler = null
    }

    override fun onDestroy() {
        super.onDestroy()
        releaseCamera()
    }
}
