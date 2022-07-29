package com.example.demoselectfilestorage

import android.app.RecoverableSecurityException
import android.content.pm.PackageManager
import android.media.MediaScannerConnection
import android.media.MediaScannerConnection.OnScanCompletedListener
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.example.demoselectfilestorage.adapter.MyAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File


class ListActivity : AppCompatActivity() {
    private val rcvMain: RecyclerView by lazy { findViewById<RecyclerView>(R.id.rcvMain) }
    private lateinit var intentSenderRequest: ActivityResultLauncher<IntentSenderRequest>
    private lateinit var mAdapter: MyAdapter
    lateinit var pkgManager: PackageManager
    private lateinit var deletedUriImage: Uri
    private var mList: MutableList<File> = mutableListOf()

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)
        pkgManager = packageManager
        val path = Environment.getExternalStorageDirectory().toString() + "/Download"
//        val path = Environment.getExternalStorageDirectory().toString()
        Log.e("TAG", "onCreate: $path")
        mList = listFoldersAndFilesFromSDCard("$path")

        mAdapter = MyAdapter(mList!!) {
            MediaScannerConnection.scanFile(this@ListActivity, arrayOf(it.absolutePath), null, OnScanCompletedListener { s, uri ->
                lifecycleScope.launch {
                    // android 11 and above
                    if (Build.VERSION_CODES.Q <= Build.VERSION.SDK_INT) {
                        deletePhotoFromExternalStorage(uri)
                        deletedUriImage = uri
                    } else {
                        // 10
                        it.delete()
                    }
                }
            })
        }

        Log.e("count", "${mList.size}")
        rcvMain.apply {
            addItemDecoration(DividerItemDecoration(this@ListActivity, DividerItemDecoration.VERTICAL))
            adapter = mAdapter
        }
        intentSenderRequest = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
            if (it.resultCode == RESULT_OK) {
                if (Build.VERSION_CODES.Q == Build.VERSION.SDK_INT) {
                    lifecycleScope.launch {
                        deletePhotoFromExternalStorage(deletedUriImage)
                    }
                }
                Toast.makeText(this, "Delete saved successfully", Toast.LENGTH_SHORT).show()
            } else Toast.makeText(this, "Delete saved failure", Toast.LENGTH_SHORT).show()
        }

    }

    private fun listFoldersAndFilesFromSDCard(path: String?): MutableList<File> {
        val arrayListFolders: MutableList<File> = mutableListOf()
        try {
            val dir = File(path).listFiles()
            if (null != dir && dir.isNotEmpty()) {
                for (i in dir.indices) {
                    if (dir[i].isFile) {
                    val mFile = File(dir[i].toString())
                    arrayListFolders.add(mFile)
                    // Here you can call recursively this function for file/folder hierarchy
                    } else {
                        // do what ever you want with files
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return arrayListFolders
    }

    private suspend fun deletePhotoFromExternalStorage(contentUri: Uri) {
        Log.e("uri", "$contentUri")
        return withContext(Dispatchers.IO) {
            try {
                contentResolver.delete(contentUri, null, null)
            } catch (e: SecurityException) {
                val intentSender = when {
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                        MediaStore.createDeleteRequest(contentResolver, listOf(contentUri)).intentSender
                    }
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                        val recoverableSecurityException = e as? RecoverableSecurityException
                        recoverableSecurityException?.userAction?.actionIntent?.intentSender
                    }
                    else -> null
                }
                intentSender?.let {
                    intentSenderRequest.launch(IntentSenderRequest.Builder(it).build())
                }
            }
        }
    }

}
