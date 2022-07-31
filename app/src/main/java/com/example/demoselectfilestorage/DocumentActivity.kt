package com.example.demoselectfilestorage

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.documentfile.provider.DocumentFile
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.demoselectfilestorage.adapter.MyAdapter
import java.io.File

class DocumentActivity : AppCompatActivity() {
    private val rcvDoc: RecyclerView by lazy { findViewById<RecyclerView>(R.id.rcvDoc) }
    private lateinit var myAdapter: MyAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_document)

        val path = Environment.getExternalStorageDirectory().toString()+"/Download"
        val mList = listFoldersAndFilesFromSDCard(path)
        Log.e(TAG, "onCreate: ${mList.size}", )
        myAdapter = MyAdapter(mList){

        }
        rcvDoc.apply {
            layoutManager = LinearLayoutManager(this@DocumentActivity)
            adapter = myAdapter
            addItemDecoration(DividerItemDecoration(this@DocumentActivity,DividerItemDecoration.VERTICAL))
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
                        if (mFile.name.endsWith(".pdf") or mFile.name.endsWith(".doc") or mFile.name.endsWith(".xls") ){
                            arrayListFolders.add(mFile)
                        }
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
}