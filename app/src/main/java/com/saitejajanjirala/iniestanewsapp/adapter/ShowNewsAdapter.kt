package com.saitejajanjirala.iniestanewsapp.adapter

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.ValueEventListener
import com.saitejajanjirala.iniestanewsapp.R
import com.saitejajanjirala.iniestanewsapp.models.Imageinfo

class ShowNewsAdapter(val context: Context, val arraylist:ArrayList<Imageinfo>) :RecyclerView.Adapter<ShowNewsAdapter.ShowNewsViewHolder>(){
    class ShowNewsViewHolder(view:View):RecyclerView.ViewHolder(view){
        val recyclerView:RecyclerView=view.findViewById(R.id.recyclerviewinside)
        val date:TextView=view.findViewById(R.id.datetext)
        val information:TextView=view.findViewById(R.id.information)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShowNewsViewHolder {
        val view=LayoutInflater.from(context).inflate(R.layout.newsitem,parent,false)
        return ShowNewsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return arraylist.size
    }

    override fun onBindViewHolder(holder: ShowNewsViewHolder, position: Int) {
        holder.date.text=arraylist.get(position).date
        holder.information.text=arraylist.get(position).information
        Log.d("arraysllist","${arraylist}")
        val madapter=Showimagesadapter(context,ArrayList<Uri>(),null, arraylist[position].uris!!,"url")
        val linearLayoutManager=LinearLayoutManager(context)
        linearLayoutManager.orientation=LinearLayoutManager.HORIZONTAL
        holder.recyclerView.adapter=madapter
        holder.recyclerView.layoutManager=linearLayoutManager
    }
}