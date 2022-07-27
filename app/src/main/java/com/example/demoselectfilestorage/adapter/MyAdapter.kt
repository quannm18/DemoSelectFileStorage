package com.example.demoselectfilestorage.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.demoselectfilestorage.R
import java.io.File

class MyAdapter(val mList: ArrayList<File>) : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvRow: TextView by lazy { itemView.findViewById<TextView>(R.id.tvRow) }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.row,parent,false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val mItem = mList[position]
        holder.tvRow.setText("${mItem.name} - ${mItem.listFiles().size}")
    }

    override fun getItemCount(): Int {
        return mList.size
    }
}