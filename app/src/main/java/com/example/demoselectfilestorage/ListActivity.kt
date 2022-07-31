package com.example.demoselectfilestorage

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.example.demoselectfilestorage.adapter.MyAdapter
import java.io.File


class ListActivity : AppCompatActivity() {
    private val rcvMain: RecyclerView by lazy { findViewById<RecyclerView>(R.id.rcvMain) }
    private lateinit var mAdapter: MyAdapter
    lateinit var pkgManager: PackageManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)
        pkgManager = packageManager
        val path = Environment.getExternalStorageDirectory().toString()
        Log.e("TAG", "onCreate: $path")
        val list = listFoldersAndFilesFromSDCard("$path")
//        for (i in 0 until list!!.size) {
//
//            Log.e("list $i", "${list[i]}")
//        }
        mAdapter = list?.let { MyAdapter(it) }!!

        rcvMain.apply {
            addItemDecoration(DividerItemDecoration(this@ListActivity, DividerItemDecoration.VERTICAL))
            adapter = mAdapter
        }

    }

    fun listFoldersAndFilesFromSDCard(path: String?): ArrayList<File>? {
        val arrayListFolders = ArrayList<File>()
        try {
            val dir = File(path).listFiles()
            // here dir will give you list of folder/file in hierarchy
            if (null != dir && dir.size > 0) {
                for (i in dir.indices) {
                    if (dir[i].isDirectory) {
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
    @SuppressLint("Range")
    fun dumpImageMetaData(uri: Uri) {
        var contentResolver = applicationContext.contentResolver
        // The query, because it only applies to a single document, returns only
        // one row. There's no need to filter, sort, or select fields,
        // because we want all fields for one document.
        val cursor: Cursor? = contentResolver.query(
            uri, null, null, null, null, null
        )

        cursor?.use {
            if (it.moveToFirst()) {
                val displayName: String =
                    it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                Log.i("TAG", "Display Name: $displayName")

                val sizeIndex: Int = it.getColumnIndex(OpenableColumns.SIZE)
                val size: String = if (!it.isNull(sizeIndex)) {
                    it.getString(sizeIndex)
                } else {
                    "Unknown"
                }
                Log.i("TAG", "Size: $size")
            }
        }
    }
}