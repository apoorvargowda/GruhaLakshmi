package com.gov.gruhalakshmi

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.net.http.SslError
import android.os.Build
import android.os.Bundle
import android.view.View
import android.webkit.SslErrorHandler
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import com.gov.gruhalakshmi.common.LanguageHelper
import com.gov.gruhalakshmi.common.PreferenceHelper
import com.gov.gruhalakshmi.common.ProgressDialog
import com.gov.gruhalakshmi.loginData.loginResponse
import com.gov.gruhalakshmi.network.repositories.Status
import com.gov.gruhalakshmi.shared.K
import com.gov.gruhalakshmi.shared.extension.getViewModel
import com.gov.gruhalakshmi.viewModel.AuthViewModel
import com.mikelau.croperino.Croperino
import com.mikelau.croperino.CroperinoConfig
import com.mikelau.croperino.CroperinoFileUtil
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*
import androidx.lifecycle.Observer
import com.google.gson.JsonObject
import com.gov.gruhalakshmi.common.CustomWebView


class UploadFiles : AppCompatActivity() {


    private var image_1:String = ""
    private var image_2:String = ""
    private var image_3:String = ""
    private var image_4:String = ""
    private var state:Int = 100

    private var applicantDetails:ApplicationData? = null



    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_files)


        val appData = intent.getStringExtra("APPLICANT_DATA")
        applicantDetails = Gson().fromJson(appData,ApplicationData::class.java)

//        if(applicantDetails?.husband_status.equals("addnew")){
//            findViewById<LinearLayout>(R.id.husaadhaarlayout).visibility = View.VISIBLE
//        }

        if(!applicantDetails?.bank_account_no.isNullOrEmpty()){
            findViewById<LinearLayout>(R.id.passbooklayout).visibility = View.VISIBLE
        }

                //Initialize on every usage
            CroperinoConfig(
                "IMG_" + System.currentTimeMillis() + ".jpg",
                "/Gruhalakshmi/Pictures",
                "/sdcard/Gruhalakshmi/Pictures"
            )
            CroperinoFileUtil.verifyStoragePermissions(this)
            CroperinoFileUtil.setupDirectory(this)

        findViewById<Button>(R.id.rcBtn).setOnClickListener {
            //Prepare Camera
            try {
                state = 101
                Croperino.prepareCamera(this)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        findViewById<Button>(R.id.bankBtn).setOnClickListener {
            //Prepare Camera
            try {
                state = 102
                Croperino.prepareCamera(this)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        findViewById<Button>(R.id.hofBtn).setOnClickListener {
            //Prepare Camera
            try {
                state = 103
                Croperino.prepareCamera(this)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        findViewById<Button>(R.id.husBtn).setOnClickListener {
            //Prepare Camera
            try {
                state = 104
                Croperino.prepareCamera(this)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        findViewById<AppCompatButton>(R.id.btnHusbandSubmit).setOnClickListener {
            if(!validate()) {
                K.showMessageDialog("Please fill required details", "Error", this)
            return@setOnClickListener
            }

            val data = Gson().toJson(applicantDetails).toString()
            val prefs = PreferenceHelper.defaultPrefs(this)
            prefs.edit().putString("TEMP_DATA",data).commit()
            val intent = Intent(this,Declaration::class.java)
            startActivity(intent)
        }

        findViewById<ImageView>(R.id.ivBackBtn).setOnClickListener {
            finish()
        }


    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            CroperinoConfig.REQUEST_TAKE_PHOTO -> if (resultCode == RESULT_OK) {
                /* Parameters of runCropImage = File, Activity Context, Image is Scalable or Not, Aspect Ratio X, Aspect Ratio Y, Button Bar Color, Background Color */
                Croperino.runCropImage(
                    CroperinoFileUtil.getTempFile(),
                    this,
                    true,
                    1,
                    1,
                    R.color.gray,
                    com.mikelau.croperino.R.color.gray_variant
                )
            }
            CroperinoConfig.REQUEST_PICK_FILE -> if (resultCode == RESULT_OK) {
                CroperinoFileUtil.newGalleryFile(data, this)
                Croperino.runCropImage(
                    CroperinoFileUtil.getTempFile(),
                    this,
                    true,
                    0,
                    0,
                    R.color.gray,
                    com.mikelau.croperino.R.color.gray_variant
                )
            }
            CroperinoConfig.REQUEST_CROP_PHOTO -> if (resultCode == RESULT_OK) {
                val i = Uri.fromFile(CroperinoFileUtil.getTempFile())


                var mSaveBit: File // Your image file

                val filePath: String = CroperinoFileUtil.getTempFile().path
                val bitmap = BitmapFactory.decodeFile(filePath)
                // convert bmp to base64
                val byteArrayOutputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
                val byteArray: ByteArray = byteArrayOutputStream.toByteArray()
                when(state) {
                    101 -> {
                        findViewById<ImageView>(R.id.rcImage).setImageBitmap(bitmap)
                        image_1 = Base64.getEncoder().encodeToString(byteArray)
                    }
                    102 -> {
                        findViewById<ImageView>(R.id.bankImage).setImageBitmap(bitmap)
                        image_2 = Base64.getEncoder().encodeToString(byteArray)
                    }
                    103 -> {
                        findViewById<ImageView>(R.id.hofImage).setImageBitmap(bitmap)
                        image_3 = Base64.getEncoder().encodeToString(byteArray)
                    }
                    104 -> {
                        findViewById<ImageView>(R.id.husImage).setImageBitmap(bitmap)
                        image_4 = Base64.getEncoder().encodeToString(byteArray)
                    }
                }

                print(image_2)
            }
            else -> {}
        }
    }

    fun validate():Boolean{

        if(!applicantDetails?.bank_account_no.isNullOrEmpty()){
            if(image_2.isNullOrEmpty()) return false
            applicantDetails?.passbook_image = image_2
            return true
        }

//        if(!image_1.isNullOrEmpty() && !image_3.isNullOrEmpty()){
//            applicantDetails?.rc_image = image_1
//            applicantDetails?.hof_image = image_3
//            return true
//        }

       return false
    }

    override fun attachBaseContext(newBase: Context?) {
        // Retrieve the language code from the base application class or preferences
        val app = newBase?.applicationContext as BaseApplication
        val languageCode: String =
            app.getLanguageCode() // Replace with your implementation

        // Set the language for the activity
        val context: Context = LanguageHelper.wrapContext(newBase!!, languageCode)
        super.attachBaseContext(context)
    }


}