package com.saitejajanjirala.iniestanewsapp.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.storage.FirebaseStorage
import com.saitejajanjirala.iniestanewsapp.R
import com.squareup.picasso.Picasso

class Showimagesadapter(
    val context: Context,
    val arraylist:ArrayList<Uri>,
    val countext: TextView?,
    val imageurls: ArrayList<String>,
    val type:String) :RecyclerView.Adapter<Showimagesadapter.Showimagesviewholder>(){
    lateinit var madapter:Showimagesadapter
    class Showimagesviewholder(view: View):RecyclerView.ViewHolder(view){
        val image:ImageView=view.findViewById(R.id.imagess)
        val remove: TextView =view.findViewById(R.id.remove)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Showimagesviewholder {
        val view=LayoutInflater.from(context).inflate(R.layout.showimages,parent,false)
        return Showimagesviewholder(view)
    }

    override fun getItemCount(): Int {
        var size=0
        if(type.equals("uri")){
            size+=arraylist.size
        }
        else if(type.equals("url")){
            size+=imageurls.size
        }
        Log.d("size","$size")
        return size
    }
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: Showimagesviewholder, position: Int) {
        if(type.equals("uri")) {
            val size = arraylist.size
            countext!!.text = "Total: $size"
            Picasso.get().load(arraylist.get(position)).error(R.drawable.ic_no_internet).fit().into(holder.image)
            holder.remove.setOnClickListener {
                arraylist.remove(arraylist.get(position))
                madapter.notifyDataSetChanged()
            }
        }
        else if(type.equals("url")){
            holder.remove.visibility=View.GONE
            try {
                Picasso.get().load(arraylist.get(position)).error(R.drawable.ic_no_internet).fit().into(holder.image)

                /*
                val builder: Picasso.Builder = Picasso.Builder(context)
                builder.listener { picasso, uri, exception ->
                    Log.d("exception", exception.message.toString())
                }
             builder.build().load(imageurls.get(position)).error(R.drawable.ic_no_internet).into(holder.image)

                 */

            }
            catch (e:Exception){
                Log.d("trycatch",e.message.toString())
            }
        }
    }
    fun setadapter(adapter:Showimagesadapter){
        madapter=adapter
    }
}