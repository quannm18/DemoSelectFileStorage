package com.example.demoselectfilestorage.adapter

import android.R.attr.path
import android.app.RecoverableSecurityException
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.core.app.ActivityCompat.startIntentSenderForResult
import androidx.recyclerview.widget.RecyclerView
import com.example.demoselectfilestorage.ListActivity
import com.example.demoselectfilestorage.MediaStoreFiles
import com.example.demoselectfilestorage.R
import com.example.demoselectfilestorage.listener.IClick
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File


class MyAdapter(val mList: MutableList<File>,var iClick:(File)->Unit) : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvRow: TextView by lazy { itemView.findViewById<TextView>(R.id.tvRow) }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.row,parent,false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val mItem = mList[position]
        holder.tvRow.setText("${mItem.name}")
//        holder.tvRow.setText("${mItem.name} - ${mItem.listFiles().size}")
        holder.itemView.setOnClickListener {
            iClick(mItem)
        }
    }
    override fun getItemCount(): Int {
        return mList.size
    }

    fun setList(mList: MutableList<File>) {
        this.mList.clear()
        this.mList.addAll(mList)
    }
}