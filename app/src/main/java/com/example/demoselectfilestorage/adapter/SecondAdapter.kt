package com.example.demoselectfilestorage.adapter

import android.R.attr.path
import android.app.RecoverableSecurityException
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.core.app.ActivityCompat.startIntentSenderForResult
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.demoselectfilestorage.ListActivity
import com.example.demoselectfilestorage.MediaStoreFiles
import com.example.demoselectfilestorage.R
import com.example.demoselectfilestorage.SharedStoragePhoto
import com.example.demoselectfilestorage.listener.IClick
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File


class SecondAdapter(val mList: MutableList<SharedStoragePhoto>) : RecyclerView.Adapter<SecondAdapter.MyViewHolder>() {

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvRow: TextView by lazy { itemView.findViewById<TextView>(R.id.tvRow) }
        val imageView: ImageView by lazy { itemView.findViewById<ImageView>(R.id.imgRow2) }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.row2,parent,false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val mItem = mList[position]
        holder.tvRow.setText("${mItem.name}")

        holder.imageView.setImageURI(mItem.contentUri)
//        Glide.with(holder.imageView)
//            .load(mItem.contentUri)
//            .circleCrop()
//            .into(holder.imageView)
    }
    override fun getItemCount(): Int {
        return mList.size
    }

    fun setList(mList: MutableList<SharedStoragePhoto>) {
        this.mList.clear()
        this.mList.addAll(mList)
    }
}