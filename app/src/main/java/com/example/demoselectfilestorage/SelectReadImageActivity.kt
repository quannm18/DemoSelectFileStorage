package com.example.demoselectfilestorage

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.coroutineScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.demoselectfilestorage.adapter.MyAdapter
import com.example.demoselectfilestorage.adapter.SecondAdapter
import kotlinx.coroutines.*
import java.io.File
import java.util.Locale.filter
import java.util.ArrayList

import android.webkit.MimeTypeMap
class SelectReadImageActivity : AppCompatActivity() {
    private val rcvSecond: RecyclerView by lazy { findViewById<RecyclerView>(R.id.rcvSecond) }
    private lateinit var myAdapter: MyAdapter
    private lateinit var secondAdapter: SecondAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_document)

        val path = Environment.getExternalStorageDirectory().toString() + "/Download"
        lifecycle.coroutineScope.launchWhenStarted {
            if (Build.VERSION_CODES.Q == Build.VERSION.SDK_INT) {
                val photos = loadPhotosFromExternalStorage()
                secondAdapter = SecondAdapter(photos.toMutableList())
                Log.e("size","${photos.size}")
                rcvSecond.apply {
                    layoutManager = LinearLayoutManager(applicationContext)
                    adapter = secondAdapter
                    addItemDecoration(DividerItemDecoration(applicationContext,DividerItemDecoration.VERTICAL))
                }
            }else{

                val path = Environment.getExternalStorageDirectory().toString() + "/DCIM/Camera"
                val photos = listFoldersAndFilesFromSDCard(path)
                myAdapter = MyAdapter(photos.toMutableList()){
                    Toast.makeText(applicationContext,"${it.name}",Toast.LENGTH_SHORT).show()
                }
                Log.e("size","${photos.size}")
                rcvSecond.apply {
                    layoutManager = LinearLayoutManager(applicationContext)
                    adapter = myAdapter
                    addItemDecoration(DividerItemDecoration(applicationContext,DividerItemDecoration.VERTICAL))
                }
            }
        }

        Log.e("getCacheDir","${listFoldersAndFilesFromSDCard(path)?.size}")
    }
    private suspend fun loadPhotosFromInternalStorage():MutableList<File>{
        return withContext(Dispatchers.IO){
            (filesDir.listFiles()?.filter { it.canRead()&&it.isFile&&it.name.endsWith(".jpg") }?.map {
                it
            } ?: mutableListOf<File>()) as MutableList<File>
        }
    }
    private suspend fun loadPhotosFromExternalStorage():List<SharedStoragePhoto>{
        return withContext(Dispatchers.IO){
            val photos = mutableListOf<SharedStoragePhoto>()
            val imageCollection = sdk29AndUp {
                MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            }?:MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL)
            val projection = arrayOf(
                MediaStore.Files.FileColumns._ID,
                MediaStore.Files.FileColumns.DATA,
                MediaStore.Files.FileColumns.DISPLAY_NAME,
                MediaStore.Files.FileColumns.RELATIVE_PATH,
                MediaStore.Files.FileColumns.MIME_TYPE,
                MediaStore.Files.FileColumns.DATE_TAKEN,
                MediaStore.Files.FileColumns.SIZE,
            )
            contentResolver.query(imageCollection,projection,null,null,null)?.use {
                    cursor->

//                Log.e("cursor","${cursor.count}")
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val displayNameColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME)
                val relativePathColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.RELATIVE_PATH)
                val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                val mimeTypeColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MIME_TYPE)
                val dateTakenColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_TAKEN)
                val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE)

                cursor.moveToFirst()

                while (cursor.moveToNext()){
                    val id = cursor.getLong(idColumn)
                    val displayName = cursor.getString(displayNameColumn)
                    val relativePath = cursor.getString(relativePathColumn)
                    val data = cursor.getString(dataColumn)
                    val mimeType = cursor.getString(mimeTypeColumn)
                    val timeMillis = cursor.getLong(dateTakenColumn)
                    val size = cursor.getLong(sizeColumn)
                    val contentUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,id)
//                    Log.e("TAG", "loadPhotosFromExternalStorage: ${contentUri}")
                    photos.add(SharedStoragePhoto(id,displayName,relativePath,data,mimeType,timeMillis,size,contentUri))
                }
            }

//            Log.e("size","${photos.size}")
            photos.toList()
        }
    }


    private fun <T> sdk29AndUp(onSdk29:()->T):T?{
        return if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.Q){
            onSdk29.invoke()
        } else null
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




    private fun getExternalPDFFileList(): ArrayList<FileModel>? {
        val cr = contentResolver
        val uri = MediaStore.Files.getContentUri("external")
        val projection = arrayOf(MediaStore.Files.FileColumns._ID, MediaStore.Files.FileColumns.DISPLAY_NAME)
        val selection = (MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE)
        val selectionArgs: Array<String>? = null
        val selectionMimeType = MediaStore.Files.FileColumns.MIME_TYPE + "=?"
        val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension("pdf")
        val selectionArgsPdf = arrayOf(mimeType)
        val cursor = cr.query(uri, projection, selectionMimeType, selectionArgsPdf, null)!!
        val uriList: ArrayList<FileModel> = ArrayList<FileModel>()
        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            val columnIndex = cursor.getColumnIndex(projection[0])
            val fileId = cursor.getLong(columnIndex)
            val fileUri = Uri.parse("$uri/$fileId")
            val displayName = cursor.getString(cursor.getColumnIndex(projection[1]))
            uriList.add(FileModel(displayName, fileUri))
            cursor.moveToNext()
        }
        cursor.close()
        return uriList
    }
}