package com.saitejajanjirala.iniestanewsapp.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.saitejajanjirala.iniestanewsapp.R
import com.saitejajanjirala.iniestanewsapp.utils.Connectivity
import com.saitejajanjirala.iniestanewsapp.utils.EmailValidator
import www.sanju.motiontoast.MotionToast

class LoginActivity : AppCompatActivity() {

    lateinit var forgotpassword: TextView
    lateinit var signuptext:TextView
    lateinit var login: Button
    lateinit var emaillayout:TextInputLayout
    lateinit var emailtext: TextInputEditText
    lateinit var passwordlayout: TextInputLayout
    lateinit var passwordtext:TextInputEditText
    lateinit var mauth: FirebaseAuth
    lateinit var mprogressbar: ProgressBar
    lateinit var loginasadmin:TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        emaillayout=findViewById(R.id.emaillayout)
        emailtext=findViewById(R.id.loginemail)
        passwordlayout=findViewById(R.id.passwordlayout)
        passwordtext=findViewById(R.id.loginpassword)
        login=findViewById(R.id.Login)
        mprogressbar=findViewById(R.id.loginbar)
        signuptext=findViewById(R.id.signuptext)
        forgotpassword=findViewById(R.id.forgotpassword)
        loginasadmin=findViewById(R.id.loginasadmin)
        forgotpassword.setOnClickListener {
            startActivity(Intent(this@LoginActivity,ForgotPassword::class.java))
        }
        signuptext.setOnClickListener {
            startActivity(Intent(this@LoginActivity,SignupActivity::class.java))
        }
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
        loginasadmin.setOnClickListener {
            startActivity(Intent(this,LoginasAdmin::class.java))
        }
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
            if(passwordlayout.isErrorEnabled||emaillayout.isErrorEnabled){
                //Toast.makeText(this@LoginActivity,"Enter Valid details",Toast.LENGTH_LONG).show()
                MotionToast.darkToast(this,"Fill the details properly",
                    MotionToast.TOAST_WARNING,
                    MotionToast.GRAVITY_CENTER,
                    MotionToast.SHORT_DURATION,
                    ResourcesCompat.getFont(this,R.font.helvetica_regular))
            }
            else{
                login.isEnabled=false
                val ob= Connectivity(this@LoginActivity)
                if(ob.checkconnectivity()) {
                    mprogressbar.visibility= View.VISIBLE
                    val id=emailtext.text.toString()
                    val pwd=passwordtext.text.toString()
                    mauth=FirebaseAuth.getInstance()
                    mauth.signInWithEmailAndPassword(id,pwd)
                        .addOnCompleteListener { it ->
                            if(it.isSuccessful){
                                mprogressbar.visibility=View.GONE
                                if(mauth.currentUser!!.isEmailVerified){
                                    login.isEnabled=true
                                    MotionToast.darkToast(this,"You have Logged in success fully",
                                        MotionToast.TOAST_SUCCESS,
                                        MotionToast.GRAVITY_CENTER,
                                        MotionToast.LONG_DURATION,
                                        ResourcesCompat.getFont(this,R.font.helvetica_regular))
                                    val sharedPreferences=getSharedPreferences("user", Context.MODE_PRIVATE)
                                    sharedPreferences.edit().putString("uid",mauth.uid.toString()).apply()
                                            val intent=Intent(this,Shownews::class.java)
                                            startActivity(intent)

                                }
                                else{
                                    val dialog= AlertDialog.Builder(this)
                                    mprogressbar.visibility=View.VISIBLE
                                    dialog.setTitle("Verification")
                                    dialog.setCancelable(false)
                                    dialog.setMessage("Your email is not verified check your specified email or do you want to resend it")
                                    dialog.setNegativeButton("No"){text,listener-> }
                                    dialog.setPositiveButton("Resend"){text,listener->
                                        mauth.currentUser!!.sendEmailVerification()
                                            .addOnCompleteListener {
                                                if(it.isSuccessful){
                                                    mprogressbar.visibility=View.GONE
                                                    login.isEnabled=true
                                                    MotionToast.darkToast(this,"Verification link is sent to your mail check it",
                                                        MotionToast.TOAST_INFO,
                                                        MotionToast.GRAVITY_CENTER,
                                                        MotionToast.LONG_DURATION,
                                                        ResourcesCompat.getFont(this,R.font.helvetica_regular))
                                                }
                                            }
                                            .addOnFailureListener {
                                                mprogressbar.visibility=View.GONE
                                                login.isEnabled=true
                                                MotionToast.darkToast(this,it.message.toString(),
                                                    MotionToast.TOAST_ERROR,
                                                    MotionToast.GRAVITY_CENTER,
                                                    MotionToast.LONG_DURATION,
                                                    ResourcesCompat.getFont(this,R.font.helvetica_regular))
                                            }
                                    }
                                    dialog.create()
                                    dialog.show()
                                }
                            }
                        }
                        .addOnFailureListener {
                            mprogressbar.visibility=View.GONE
                            login.isEnabled=true
                            MotionToast.darkToast(this,it.message.toString(),
                                MotionToast.TOAST_ERROR,
                                MotionToast.GRAVITY_CENTER,
                                MotionToast.LONG_DURATION,
                                ResourcesCompat.getFont(this,R.font.helvetica_regular))
                        }
                }
                else{
                    ob.showdialog()
                }
            }
        }
    }

    override fun onBackPressed() {
        ActivityCompat.finishAffinity(this@LoginActivity)
    }
}
