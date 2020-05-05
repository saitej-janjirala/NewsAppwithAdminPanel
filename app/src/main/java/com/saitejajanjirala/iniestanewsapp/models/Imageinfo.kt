package com.saitejajanjirala.iniestanewsapp.models

import android.graphics.Bitmap
import android.net.Uri
import androidx.annotation.Keep
import com.google.android.gms.tasks.Task
import com.google.firebase.database.IgnoreExtraProperties

@Keep
@IgnoreExtraProperties
open class Imageinfo(var date:String?=null,var information:String?=null,var uris:ArrayList<String>?=null) {
}