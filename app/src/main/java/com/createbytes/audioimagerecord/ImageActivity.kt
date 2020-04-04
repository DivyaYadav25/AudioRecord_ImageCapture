package com.createbytes.audioimagerecord

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_image.*
import java.io.File
import java.io.IOException
import java.util.*

class ImageActivity : AppCompatActivity(),PermissionListener {
    private var PERMISSION_CODE = 1212
    private var mActivity: Activity? = null
    private var mCustomPermission: List<String>? = null
    private var mPerpermissionListener:PermissionListener? = null

    var REQUEST_TAKE_PHOTO = 5
    val REQUEST_IMAGE_PICKER = 3
    private var cameraUri : Uri? = null
    private var imagesAdapter: ImageAdapter? =null
    private val clickedImageList = ArrayList<ClickedImage>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)

        imagesAdapter = ImageAdapter(ArrayList<ClickedImage>())
        rv_image?.layoutManager = LinearLayoutManager(this)
        rv_image?.adapter = imagesAdapter

        iv_upload_image?.setOnClickListener {
            val multiplePermission = java.util.ArrayList<String>()
            multiplePermission.add(Manifest.permission.CAMERA)
            multiplePermission.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            multiplePermission.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            if (checkAndRequestPermission(this, multiplePermission, this)) {
                imagePicker()
            }
        }


    }
    fun checkAndRequestPermission(
        activity: Activity,
        permissions: List<String>,
        permissionListener: PermissionListener
    ): Boolean {
        mActivity = activity
        mPerpermissionListener = permissionListener
        mCustomPermission = permissions
        if (Build.VERSION.SDK_INT >= 23) {
            val listPermissionsAssign: MutableList<String> =
                ArrayList()
            for (per in permissions) {
                if (ContextCompat.checkSelfPermission(
                        activity.applicationContext,
                        per
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    listPermissionsAssign.add(per)
                }
            }
            if (!listPermissionsAssign.isEmpty()) {
                ActivityCompat.requestPermissions(
                    activity,
                    listPermissionsAssign.toTypedArray(),
                    PERMISSION_CODE
                )
                return false
            }
        }
        return true
    }
    private fun imagePicker() {
        val options = arrayOf("Take Photo","Choose from Gallery")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Add Photo")
        builder.setItems(options){dialogInterface, i ->
            if(options[i]=="Take Photo"){
                takePhotoIntent()
            } else if(options[i]=="Choose from Gallery"){
                chooseFromGalleryIntent()
            }
        }
        builder.show()
    }
    override fun onPermissionGranted(mCustomPermission: List<String?>?) {
        imagePicker()
    }

    override fun onPermissionDenied(mCustomPermission: List<String?>?) {
    }
    private fun takePhotoIntent() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if(intent.resolveActivity(this.packageManager)!=null){
            try{
                cameraUri = createImageFile()
            }catch (ex: IOException){

            }
            if(cameraUri!=null){
                intent.putExtra(MediaStore.EXTRA_OUTPUT,cameraUri)
                startActivityForResult(intent,REQUEST_TAKE_PHOTO)
            }
        }
    }

    private fun chooseFromGalleryIntent() {
        val getIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        getIntent.type = "image/*"
        startActivityForResult(getIntent, REQUEST_IMAGE_PICKER)
    }

    @Throws(IOException::class)
    private fun createImageFile(): Uri? {
        val imageFileName ="temp_capture"
        val storageDir =this.externalCacheDir
        val image = File.createTempFile(imageFileName,".jpgDir",storageDir)
        return FileProvider.getUriForFile(this,packageName+".provider",image)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == REQUEST_IMAGE_PICKER && resultCode==Activity.RESULT_OK && data!=null){
            try{
                val imagePath = getRealPathFromURI(this,data.data) as Uri
                val intent = CropImage.activity(imagePath).getIntent(this)
                startActivityForResult(intent, CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)

            }catch (e: Exception){
                e.printStackTrace()
            }
        }else if(requestCode == REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK){
            try{
                val intent = CropImage.activity(cameraUri).getIntent(this)
                startActivityForResult(intent, CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)

            }catch (ex: Exception){
                ex.printStackTrace()
            }
        }else if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                val resultUri = result.getUri()
                if (resultUri == null)
                    return
                uploadImageApi(resultUri.path!!)

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.getError()
                error.printStackTrace()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    @Throws(Exception::class)
    private fun getRealPathFromURI(imageActivity: ImageActivity, data: Uri?): Any {
        val result: String?
        val cursor = imageActivity.contentResolver.query(data!!, null, null, null, null)
        if (cursor == null) {
            result = data.path
        } else {
            cursor.moveToFirst()
            val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            result = cursor.getString(idx)
            cursor.close()
        }
        return Uri.fromFile(File(result!!))
    }

    private fun uploadImageApi(path: String) {
        val file = File(path)
        val clickedImage = ClickedImage()
        clickedImage.uri = Uri.fromFile(file)
        clickedImageList.add(clickedImage)
        imagesAdapter?.setImageData(clickedImageList)
        imagesAdapter!!.notifyDataSetChanged()

    }
}

interface PermissionListener {
    fun onPermissionGranted(mCustomPermission: List<String?>?)
    fun onPermissionDenied(mCustomPermission: List<String?>?)
}