package com.saitejajanjirala.iniestanewsapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.saitejajanjirala.iniestanewsapp.R
import com.saitejajanjirala.iniestanewsapp.utils.Connectivity
import com.saitejajanjirala.iniestanewsapp.utils.EmailValidator
import www.sanju.motiontoast.MotionToast

class LoginasAdmin : AppCompatActivity() {

    lateinit var login: Button
    lateinit var emaillayout: TextInputLayout
    lateinit var emailtext: TextInputEditText
    lateinit var passwordlayout: TextInputLayout
    lateinit var passwordtext: TextInputEditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loginas_admin)
        supportActionBar?.title="Admin Login"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        emaillayout=findViewById(R.id.adminemaillayout)
        emailtext=findViewById(R.id.adminloginemail)
        passwordlayout=findViewById(R.id.adminpasswordlayout)
        passwordtext=findViewById(R.id.adminloginpassword)
        login=findViewById(R.id.adminlogin)
        emailtext.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(p0: Editable?) {

            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(!EmailValidator().isEmailValid(emailtext.text.toString())){
                    emaillayout.error="Enter valid email"
                }
                else{
                    emaillayout.isErrorEnabled=false
                }
            }
        })
        passwordtext.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(passwordtext.text!!.length <6 || !passwordtext.text.toString().isNotBlank()){
                    passwordlayout.error="password shouldn't be less than 6 characters"
                }
                else{
                    passwordlayout.isErrorEnabled=false
                }
            }
        })
        login.setOnClickListener {
            if (passwordlayout.isErrorEnabled || emaillayout.isErrorEnabled) {
                //Toast.makeText(this@LoginActivity,"Enter Valid details",Toast.LENGTH_LONG).show()
                MotionToast.darkToast(
                    this, "Fill the details properly",
                    MotionToast.TOAST_WARNING,
                    MotionToast.GRAVITY_CENTER,
                    MotionToast.SHORT_DURATION,
                    ResourcesCompat.getFont(this, R.font.helvetica_regular)
                )
            } else {
                val obj=Connectivity(this)
                if(obj.checkconnectivity()){
                    try{
                    val password=passwordtext.text.toString()
                    val email=emailtext.text.toString()
                    val db= Firebase.firestore
                    val docref=db.document("admin/admindetails")
                    docref.get().addOnSuccessListener {
                        val map = it.data
                        val map2=HashMap<String,String>()
                        map2.put("email",email)
                        map2.put("password",password)
                        Toast.makeText(this,"$map", Toast.LENGTH_LONG).show()
                        if (!map!!.equals(map2)) {
                            MotionToast.darkToast(
                                this, "Email or password might be wrong",
                                MotionToast.TOAST_WARNING,
                                MotionToast.GRAVITY_CENTER,
                                MotionToast.SHORT_DURATION,
                                ResourcesCompat.getFont(this, R.font.helvetica_regular)
                            )
                        } else {
                            MotionToast.darkToast(
                                this, "Succcess",
                                MotionToast.TOAST_SUCCESS,
                                MotionToast.GRAVITY_CENTER,
                                MotionToast.SHORT_DURATION,
                                ResourcesCompat.getFont(this, R.font.helvetica_regular)
                            )
                            startActivity(Intent(this, News::class.java))

                        }
                    }

                    }
                    catch(e:Exception){
                        MotionToast.darkToast(
                            this, e.message.toString(),
                            MotionToast.TOAST_SUCCESS,
                            MotionToast.GRAVITY_CENTER,
                            MotionToast.SHORT_DURATION,
                            ResourcesCompat.getFont(this, R.font.helvetica_regular)
                        )
                        startActivity(Intent(this, News::class.java))
                    }


                }
                else{
                    obj.showdialog()
                }

            }
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId==android.R.id.home){
            super.onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }
}
