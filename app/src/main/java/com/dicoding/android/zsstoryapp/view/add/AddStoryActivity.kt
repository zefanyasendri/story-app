package com.dicoding.android.zsstoryapp.view.add

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.dicoding.android.zsstoryapp.R
import com.dicoding.android.zsstoryapp.databinding.ActivityAddStoryBinding
import com.dicoding.android.zsstoryapp.utils.AppConstants
import com.dicoding.android.zsstoryapp.utils.AppConstants.CAMERA_X_RESULT
import com.dicoding.android.zsstoryapp.utils.AppConstants.REQUEST_CODE_PERMISSIONS
import com.dicoding.android.zsstoryapp.utils.AppConstants.REQUIRED_PERMISSIONS
import com.dicoding.android.zsstoryapp.utils.reduceFileImage
import com.dicoding.android.zsstoryapp.utils.rotateBitmap
import com.dicoding.android.zsstoryapp.utils.uriToFile
import com.dicoding.android.zsstoryapp.view.camera.CameraActivity
import com.dicoding.android.zsstoryapp.view.main.MainActivity
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.io.File

class AddStoryActivity : AppCompatActivity() {
    private lateinit var viewModel: AddStoryViewModel
    private lateinit var binding: ActivityAddStoryBinding
    private lateinit var token: String

    // Permission
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    getString(R.string.no_permission),
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }
    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = getString(R.string.actionbar_add_story)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        token = intent.getStringExtra(AppConstants.EXTRA_TOKEN).toString()

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        setupViewModel()
        setupAction()

    }

    private fun setupViewModel(){
        viewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[AddStoryViewModel::class.java]
    }

    private fun setupAction() {
        binding.btnCamera.setOnClickListener { startCameraX() }
        binding.btnGallery.setOnClickListener { startGallery() }
        binding.btnUpload.setOnClickListener { uploadStory() }
    }

    // CameraX
    private fun startCameraX() {
        val intent = Intent(this, CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }
    private var getFile: File? = null
    private val launcherIntentCameraX = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == CAMERA_X_RESULT) {
            val myFile = it.data?.getSerializableExtra("picture") as File
            val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean
            val result = rotateBitmap(BitmapFactory.decodeFile(myFile.path), isBackCamera)

            // Agar tidak ter Rotate
            val bytes = ByteArrayOutputStream()
            result.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
            val path = MediaStore.Images.Media.insertImage(this@AddStoryActivity.contentResolver, result, "Title", null)
            val uri = Uri.parse(path.toString())
            getFile = uriToFile(uri, this@AddStoryActivity)

            binding.ivPreview.setImageBitmap(result)
        }
    }

    // Gallery
    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }
    private val launcherIntentGallery = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, this@AddStoryActivity)
            getFile = myFile
            binding.ivPreview.setImageURI(selectedImg)
        }
    }

    // Upload
    private fun uploadStory() {
        if (getFile != null) {
            val file = reduceFileImage(getFile as File)
            val description = binding.etDescription.text.toString().toRequestBody("text/plain".toMediaType())
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                file.name,
                requestImageFile
            )

            viewModel.addStory(imageMultipart, description, token)
            viewModel.resMessage.observe(this) {
                val intent = Intent(this@AddStoryActivity, MainActivity::class.java)
                startActivity(intent)
                Toast.makeText(this, "Story is $it", Toast.LENGTH_SHORT).show()
                finish()
            }
        } else {
            Toast.makeText(this@AddStoryActivity, getString(R.string.reminder_input_image), Toast.LENGTH_SHORT).show()
        }
    }

    // Option Menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}