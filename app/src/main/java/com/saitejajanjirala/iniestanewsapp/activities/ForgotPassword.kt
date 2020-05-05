package com.saitejajanjirala.iniestanewsapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.widget.Button
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.saitejajanjirala.iniestanewsapp.R
import com.saitejajanjirala.iniestanewsapp.utils.Connectivity
import com.saitejajanjirala.iniestanewsapp.utils.EmailValidator
import www.sanju.motiontoast.MotionToast

class ForgotPassword : AppCompatActivity() {

    lateinit var emaillayout: TextInputLayout
    lateinit var emailtext: TextInputEditText
    lateinit var resetpassword: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)
        supportActionBar?.title="ForgotPassword"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        emaillayout=findViewById(R.id.forgotpasswordemaillayout)
        emailtext=findViewById(R.id.forgotpasswordemail)
        resetpassword=findViewById(R.id.Resetpassword)
        emailtext.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(!EmailValidator().isEmailValid(emailtext.text.toString())){
                    emaillayout.setError("Enter Valid email")
                }
                else{
                    emaillayout.isErrorEnabled=false
                }
            }

        })
        resetpassword.setOnClickListener {
            if(emaillayout.isErrorEnabled){
                MotionToast.darkToast(this,"Enter valid email-id",
                    MotionToast.TOAST_WARNING,
                    MotionToast.GRAVITY_CENTER,
                    MotionToast.SHORT_DURATION,
                    ResourcesCompat.getFont(this,R.font.helvetica_regular))
            }
            else{
                val ob= Connectivity(this)
                if(ob.checkconnectivity()){
                    val emailid=emailtext.text.toString()
                    val mauth= FirebaseAuth.getInstance()
                    mauth.sendPasswordResetEmail(emailid)
                        .addOnSuccessListener {
                            MotionToast.darkToast(this,"Reset password link is sent to your mail check your mail",
                                MotionToast.TOAST_INFO,
                                MotionToast.GRAVITY_CENTER,
                                MotionToast.LONG_DURATION,
                                ResourcesCompat.getFont(this,R.font.helvetica_regular))
                            super.onBackPressed()
                        }
                        .addOnFailureListener {
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId==android.R.id.home){
            super.onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }
}
