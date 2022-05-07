package com.dicoding.android.zsstoryapp.utils

import android.Manifest

object AppConstants {
    var token: String = ""

    const val EXTRA_PHOTO = "EXTRA_PHOTO"
    const val EXTRA_CREATED_AT = "EXTRA_CREATED_AT"
    const val EXTRA_NAME = "EXTRA_NAME"
    const val EXTRA_DESC = "EXTRA_DESC"
    const val EXTRA_TOKEN = "EXTRA_TOKEN"
    const val CAMERA_X_RESULT = 200
    val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    const val REQUEST_CODE_PERMISSIONS = 10
    const val INITIAL_PAGE_INDEX = 1
    const val EXTRA_MAP = "MAP"
    const val EXTRA_MAP_NAME = "NAME"
}