package com.saitejajanjirala.iniestanewsapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.saitejajanjirala.iniestanewsapp.R
import com.saitejajanjirala.iniestanewsapp.utils.Connectivity
import com.saitejajanjirala.iniestanewsapp.utils.EmailValidator
import www.sanju.motiontoast.MotionToast

class SignupActivity : AppCompatActivity() {

    lateinit var namelayout:TextInputLayout
    lateinit var nametext:TextInputEditText
    lateinit var emaillayout:TextInputLayout
    lateinit var signupemail:TextInputEditText
    lateinit var phonelayout:TextInputLayout
    lateinit var phonetext: TextInputEditText
    lateinit var passwordlayout: TextInputLayout
    lateinit var signuppassword:TextInputEditText
    lateinit var confirmpasswordlayout: TextInputLayout
    lateinit var signupconfirmpassword: TextInputEditText
    lateinit var Signup: Button
    lateinit var mauth: FirebaseAuth
    lateinit var mprogressbar: ProgressBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        supportActionBar?.title="Registration"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mprogressbar=findViewById(R.id.signupbar)
        emaillayout=findViewById(R.id.emaillayout)
        signupemail=findViewById(R.id.signupemail)
        passwordlayout=findViewById(R.id.passwordlayout)
        signuppassword=findViewById(R.id.signuppassword)
        confirmpasswordlayout=findViewById(R.id.confirmpasswordlayout)
        signupconfirmpassword=findViewById(R.id.signupconfirmpassword)
        Signup=findViewById(R.id.Signup)
        FirebaseApp.initializeApp(this@SignupActivity)
        mauth = FirebaseAuth.getInstance()
        signupemail.addTextChangedListener(object:TextWatcher{
            override fun afterTextChanged(p0: Editable?) {

            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(!EmailValidator().isEmailValid(signupemail.text.toString()
                    )){
                    emaillayout.error="Enter valid email"
                }
                else{
                    emaillayout.isErrorEnabled=false
                }
            }
        })
        signuppassword.addTextChangedListener(object:TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(p0!!.length<6 || signuppassword.text.isNullOrBlank()){
                    passwordlayout.error="password shouldn't be less than 6 characters"
                }
                else{
                    passwordlayout.isErrorEnabled=false
                }
            }
        })
        signupconfirmpassword.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                if(p0!!.length<6 || signupconfirmpassword.text.isNullOrBlank()){
                    confirmpasswordlayout.error="password shouldn't be less than 6 characters"
                }
                else if(signupconfirmpassword.text.toString()!=signuppassword.text.toString()){
                    confirmpasswordlayout.error="both passwords should be same"
                }
                else {
                    confirmpasswordlayout.isErrorEnabled = false
                }

            }
        })
        Signup.setOnClickListener {
            if(emaillayout.isErrorEnabled||passwordlayout.isErrorEnabled||confirmpasswordlayout.isErrorEnabled){
                //Toast.makeText(this@SignupActivity,"Fill the details Properly",Toast.LENGTH_LONG).show()
                MotionToast.darkToast(this,"Fill the details properly",
                    MotionToast.TOAST_WARNING,
                    MotionToast.GRAVITY_CENTER,
                    MotionToast.SHORT_DURATION,
                    ResourcesCompat.getFont(this,R.font.helvetica_regular))
            }
            else{
                val ob= Connectivity(this@SignupActivity)
                if(ob.checkconnectivity()) {
                    mprogressbar.visibility= View.VISIBLE
                    try {
                        val id = signupemail.text.toString()
                        val password = signuppassword.text.toString()
                        Signup.isEnabled = false
                        mauth.createUserWithEmailAndPassword(id, password)
                            .addOnCompleteListener {
                                if (it.isSuccessful) {
                                    MotionToast.darkToast(this,"Sucessfully Registered check your email for verification link and login again",
                                        MotionToast.TOAST_INFO,
                                        MotionToast.GRAVITY_CENTER,
                                        MotionToast.LONG_DURATION,
                                        ResourcesCompat.getFont(this,R.font.helvetica_regular))
                                    Signup.isEnabled = true
                                    mprogressbar.visibility=View.GONE
                                    super.onBackPressed()
                                }
                            }
                            .addOnFailureListener {
                                MotionToast.darkToast(this,it.message.toString(),
                                    MotionToast.TOAST_ERROR,
                                    MotionToast.GRAVITY_CENTER,
                                    MotionToast.LONG_DURATION,
                                    ResourcesCompat.getFont(this,R.font.helvetica_regular))
                                Signup.isEnabled = true
                                mprogressbar.visibility=View.GONE
                            }
                    }
                    catch (e:Exception){
                        MotionToast.darkToast(this,e.message.toString(),
                            MotionToast.TOAST_ERROR,
                            MotionToast.GRAVITY_CENTER,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(this,R.font.helvetica_regular))
                        Signup.isEnabled = true
                        mprogressbar.visibility= View.GONE
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
