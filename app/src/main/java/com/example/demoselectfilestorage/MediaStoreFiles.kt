package com.example.demoselectfilestorage

import android.net.Uri

data class MediaStoreFiles(
    val id: Long,
    val contentUri: Uri,
    val displayName: String,
    val relativePath: String,
    val data: String,
    val mimeType: String,
    val timeMillis: Long,
    val size: Long,
)