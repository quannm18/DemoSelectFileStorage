package com.example.demoselectfilestorage

import android.graphics.Bitmap
import android.net.Uri

data class SharedStoragePhoto(
    val id: Long,
    val name: String,
    val relativePath: String,
    val data: String,
    val mimeType: String,
    val timeMillis: Long,
    val size: Long,
    val contentUri: Uri,
)
