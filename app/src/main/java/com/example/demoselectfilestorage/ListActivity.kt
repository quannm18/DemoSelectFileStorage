package com.example.demoselectfilestorage

import android.content.ContentUris
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
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
//
//    private fun loadAudio(context: Context): List<AudioModel>? {
//        val tempList: MutableList<AudioModel> = ArrayList<AudioModel>()
//        val uri = MediaStore.Downloads.EXTERNAL_CONTENT_URI
//        val cursor: Cursor = context.getContentResolver().query(uri, projection, null, null, null)
//        if (cursor.count > 0) {
//            while (cursor.moveToNext()) {
//                val albumID = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ALBUM_ID))
//                val imgPath = Uri.parse("content://media/external/audio/albumart")
//                val imgParse = ContentUris.withAppendedId(imgPath, albumID)
//                tempList.add(
//                    AudioModel(
//                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.TITLE)),
//                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)),
//                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID)),
//                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATA)),
//                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DURATION)),
//                        imgParse.toString()
//                    )
//                )
//            }
//            cursor.close()
//        }
//        return tempList
//    }
}