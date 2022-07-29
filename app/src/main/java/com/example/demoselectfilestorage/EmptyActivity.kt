package com.example.demoselectfilestorage

import android.app.RecoverableSecurityException
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.lifecycle.coroutineScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.demoselectfilestorage.adapter.MyAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class EmptyActivity : AppCompatActivity() {
    private val rcvEmpty: RecyclerView by lazy { findViewById<RecyclerView>(R.id.rcvEmpty) }
    private lateinit var mAdapter: MyAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_empty)
        val path = Environment.getExternalStorageDirectory().toString()
        val mList = listFoldersAndFilesFromSDCard(path)

        mAdapter = MyAdapter(mList) {
            if (Build.VERSION_CODES.Q <= Build.VERSION.SDK_INT) {
                lifecycle.coroutineScope.launch {
                    deletePhotoFromExternalStorage(it.path.toUri())
                }
            } else {
                it.delete()
            }
        }
        rcvEmpty.apply {
            layoutManager = LinearLayoutManager(this@EmptyActivity)
            adapter = mAdapter
            addItemDecoration(DividerItemDecoration(this@EmptyActivity, DividerItemDecoration.VERTICAL))

        }

    }


    private fun listFoldersAndFilesFromSDCard(path: String?): MutableList<File> {
        val arrayListFolders: MutableList<File> = mutableListOf()
        try {
            val dir = File(path).listFiles()
            if (null != dir && dir.isNotEmpty()) {
                for (i in dir.indices) {
                    if (dir[i].isDirectory) {
                        val mFile = File(dir[i].toString())
                        if (mFile.listFiles().size==0)
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
//                    intentSenderRequest.launch(IntentSenderRequest.Builder(it).build())
                }
            }
        }
    }

}