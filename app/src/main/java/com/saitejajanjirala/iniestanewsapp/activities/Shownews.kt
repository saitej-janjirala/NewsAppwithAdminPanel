package com.saitejajanjirala.iniestanewsapp.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.saitejajanjirala.iniestanewsapp.R
import com.saitejajanjirala.iniestanewsapp.adapter.ShowNewsAdapter
import com.saitejajanjirala.iniestanewsapp.models.Imageinfo
import com.saitejajanjirala.iniestanewsapp.utils.Connectivity
import www.sanju.motiontoast.MotionToast

class Shownews : AppCompatActivity() {

    lateinit var refresh:SwipeRefreshLayout
    lateinit var mrecyclerview:RecyclerView
    lateinit var layoutManager: LinearLayoutManager
    lateinit var madapter:ShowNewsAdapter
    lateinit var progressrelative:RelativeLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shownews)
        refresh=findViewById(R.id.refreshlayout)
        progressrelative=findViewById(R.id.progressrelative)
        mrecyclerview=findViewById(R.id.recyclerview)
        dorefresh()
        refresh.setOnRefreshListener {
            dorefresh()
            refresh.isRefreshing=false
        }

    }
    fun dorefresh(){
        val obj=Connectivity(this)
        if(obj.checkconnectivity()) {
            progressrelative.visibility= View.VISIBLE
            val ref = FirebaseDatabase.getInstance().reference
            try {
                val menuListener = object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            try {
                                val arraylist=ArrayList<Imageinfo>()
                                dataSnapshot.children.forEach {
                                   arraylist.add( it.getValue(Imageinfo::class.java)!!)
                                    Log.d("values", "${ it.getValue(Imageinfo::class.java)}")
                                }
                                madapter= ShowNewsAdapter(this@Shownews,arraylist)
                                layoutManager=LinearLayoutManager(this@Shownews)
                                mrecyclerview.adapter=madapter
                                mrecyclerview.layoutManager=layoutManager
                                progressrelative.visibility= View.GONE
                            }catch (e: Exception) {
                                Log.d("exception",e.message.toString())
                                MotionToast.darkToast(
                                    this@Shownews, e.message.toString(),
                                    MotionToast.TOAST_ERROR,
                                    MotionToast.GRAVITY_CENTER,
                                    MotionToast.SHORT_DURATION,
                                    ResourcesCompat.getFont(this@Shownews, R.font.helvetica_regular)
                                )
                                progressrelative.visibility= View.GONE


                            }
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            MotionToast.darkToast(
                                this@Shownews, "${databaseError.toException()}",
                                MotionToast.TOAST_ERROR,
                                MotionToast.GRAVITY_CENTER,
                                MotionToast.SHORT_DURATION,
                                ResourcesCompat.getFont(this@Shownews, R.font.helvetica_regular)
                            )
                            progressrelative.visibility= View.GONE

                        }

                }
                ref.addValueEventListener(menuListener)

            } catch (e: Exception) {
                progressrelative.visibility= View.GONE
                Toast.makeText(this, e.message.toString(), Toast.LENGTH_LONG).show()
            }

        }
        else{
            obj.showdialog()
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.mainmenu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.logout->{
                getSharedPreferences("user", Context.MODE_PRIVATE).edit().clear().apply()
                startActivity(Intent(this,LoginActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)

        }

    }
}
