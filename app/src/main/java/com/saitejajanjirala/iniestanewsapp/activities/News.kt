package com.saitejajanjirala.iniestanewsapp.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.OnProgressListener
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.saitejajanjirala.iniestanewsapp.R
import com.saitejajanjirala.iniestanewsapp.adapter.Showimagesadapter
import com.saitejajanjirala.iniestanewsapp.models.Imageinfo
import com.saitejajanjirala.iniestanewsapp.utils.Connectivity
import www.sanju.motiontoast.MotionToast
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class News : AppCompatActivity() {

    lateinit var infolayout:TextInputLayout
    lateinit var infotext:TextInputEditText
    lateinit var choose:Button
    lateinit var upload:Button
    lateinit var mrecyclerview:RecyclerView
    lateinit var layoutManager: LinearLayoutManager
    lateinit var gridLayoutManager: GridLayoutManager
    val PICK_IMAGE_MULTIPLE=1010
    lateinit var mArrayUri:ArrayList<Uri>
    lateinit var adpater:Showimagesadapter
    lateinit var toprelative:RelativeLayout
    private var firebaseStorage: FirebaseStorage? = null
    private var storageReference: StorageReference? = null
    lateinit var count:TextView

    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)
        infolayout=findViewById(R.id.informationlayout)
        infotext=findViewById(R.id.informationtext)
        choose=findViewById(R.id.choose)
        upload=findViewById(R.id.Upload)
        mrecyclerview=findViewById(R.id.uploadrecyclerview)
        layoutManager= LinearLayoutManager(this)
        gridLayoutManager=GridLayoutManager(this,2)
        firebaseStorage = FirebaseStorage.getInstance()
        storageReference = FirebaseStorage.getInstance().reference
        mArrayUri= ArrayList()
        toprelative=findViewById(R.id.topelative)
        count=findViewById(R.id.count)
        choose.setOnClickListener {
            var intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE_MULTIPLE);
        }
        infotext.addTextChangedListener(object:TextWatcher{
            override fun afterTextChanged(p0: Editable?) {

            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(infotext.text.isNullOrBlank()){
                    infolayout.error="You should enter some information regarding news"
                }
                else{
                    infolayout.isErrorEnabled=false
                }
            }

        })
        upload.setOnClickListener {
            val obj=Connectivity(this)
            if(obj.checkconnectivity()) {
                if (mArrayUri.size != 0) {
                    if (!infolayout.isErrorEnabled && !infotext.text.isNullOrBlank()) {
                        val info=infotext.text.toString()
                        val alertDialog: AlertDialog.Builder =AlertDialog.Builder(this)
                        alertDialog.setCancelable(false)
                        val view=LayoutInflater.from(this).inflate(R.layout.uploadprogress,toprelative,false)
                        val progresstext:TextView=view.findViewById(R.id.progresstext)
                        val progressbar:ProgressBar=view.findViewById(R.id.uploadprogress)
                        val totalnum=mArrayUri.size
                        progresstext.text="0/$totalnum images uploaded"
                        alertDialog.setView(view)
                        alertDialog.setNegativeButton("Close"){text,listener->

                        }
                        val dialog:android.app.AlertDialog=alertDialog.create()
                       // alertDialog.create
                        dialog.show()

                        try {
                            var count=0
                            val timeStamp: String =
                                SimpleDateFormat("yyyy/MM/dd \nHH:mm:ss").format(Date())
                            val urls=ArrayList<String>()
                            for(i in 0..mArrayUri.size-1) {
                                val uuid=UUID.randomUUID().toString()
                                val ref = storageReference?.child(
                                    "uploads/" + uuid)
                                ref?.putFile(mArrayUri.get(i))?.addOnSuccessListener(OnSuccessListener<UploadTask.TaskSnapshot> {
                                    count+=1
                                    progresstext.text="$count/$totalnum images uploaded"
                                   // val uri:Uri=it.storage.downloadUrl.result!!
                                    //Log.d("urls",uri.toString())
                                    ref.downloadUrl.addOnSuccessListener {
                                        urls.add(it.toString())
                                    }
                                    if(count==totalnum){
                                        val imageinfo=Imageinfo(timeStamp,info,urls)
                                        val mdatabaseref=FirebaseDatabase.getInstance().reference
                                        val uploadid: String? = mdatabaseref.push().getKey()
                                        if (uploadid != null) {
                                            mdatabaseref.child(uploadid).setValue(imageinfo)
                                        }
                                        Handler().postDelayed({
                                            progresstext.text="Success fully uploaded"
                                            progressbar.visibility=View.GONE
                                        },200)
                                    }
                                })
                                    ?.addOnFailureListener(OnFailureListener { e ->
                                    MotionToast.darkToast(
                                        this, e.message.toString(),
                                        MotionToast.TOAST_ERROR,
                                        MotionToast.GRAVITY_CENTER,
                                        MotionToast.SHORT_DURATION,
                                        ResourcesCompat.getFont(this, R.font.helvetica_regular)
                                    )
                                    progresstext.text="Error!"
                                })
                            }
                        }
                        catch (e:Exception){
                            MotionToast.darkToast(
                                this, e.message.toString(),
                                MotionToast.TOAST_ERROR,
                                MotionToast.GRAVITY_CENTER,
                                MotionToast.SHORT_DURATION,
                                ResourcesCompat.getFont(this, R.font.helvetica_regular)
                            )
                        }
                    }
                    else{
                        MotionToast.darkToast(
                            this,"There should be atleast some information about the news",
                            MotionToast.TOAST_WARNING,
                            MotionToast.GRAVITY_CENTER,
                            MotionToast.SHORT_DURATION,
                            ResourcesCompat.getFont(this, R.font.helvetica_regular)
                        )
                    }
                } else {
                    MotionToast.darkToast(
                        this, "You should select atleast one image to upload",
                        MotionToast.TOAST_WARNING,
                        MotionToast.GRAVITY_CENTER,
                        MotionToast.SHORT_DURATION,
                        ResourcesCompat.getFont(this, R.font.helvetica_regular)
                    )
                }
            }
            else{
                obj.showdialog()
            }
        }
   }
    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        try {
            // When an Image is picked
            if (requestCode == PICK_IMAGE_MULTIPLE && resultCode == Activity.RESULT_OK
                && null != data
            ) {
                // Get the Image from data
                val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
                if (data.data != null) {
                    val mImageUri = data.data
                    // Get the cursor
                    val cursor = contentResolver.query(
                        mImageUri!!,
                        filePathColumn, null, null, null
                    )
                    // Move to first row
                    cursor!!.moveToFirst()
                    val columnIndex = cursor.getColumnIndex(filePathColumn[0])
                   // imageEncoded = cursor.getString(columnIndex)
                    cursor.close()
                    mArrayUri.add(mImageUri)

                } else {
                    if (data.clipData != null) {
                        val mClipData = data.clipData
                        for (i in 0 until mClipData!!.itemCount) {

                            val item = mClipData.getItemAt(i)
                            val uri = item.uri
                            mArrayUri.add(uri)
                            // Get the cursor
                        }
                        Log.v("LOG_TAG", "Selected Images" + mArrayUri.size)
                    }
                }
            } else {
                Toast.makeText(
                    this, "You haven't picked Image",
                    Toast.LENGTH_LONG
                ).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                .show()
        }

        adpater= Showimagesadapter(this,mArrayUri,count,ArrayList<String>(),"uri")
        adpater.setadapter(adpater)

        mrecyclerview.layoutManager=layoutManager
        mrecyclerview.setBackgroundColor(resources.getColor(R.color.grey))
        mrecyclerview.adapter=adpater
        super.onActivityResult(requestCode, resultCode, data)
    }
    override fun onBackPressed() {
        ActivityCompat.finishAffinity(this)

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
