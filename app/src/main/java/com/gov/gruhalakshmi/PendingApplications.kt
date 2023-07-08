package com.gov.gruhalakshmi

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.gov.gruhalakshmi.R
import com.gov.gruhalakshmi.aadharData.Male
import com.gov.gruhalakshmi.common.LanguageHelper
import com.gov.gruhalakshmi.common.PermissionsDelegate
import com.gov.gruhalakshmi.common.PreferenceHelper
import com.gov.gruhalakshmi.common.ProgressDialog
import com.gov.gruhalakshmi.loginData.loginResponse
import com.gov.gruhalakshmi.network.repositories.Status
import com.gov.gruhalakshmi.shared.K
import com.gov.gruhalakshmi.shared.extension.getViewModel
import com.gov.gruhalakshmi.viewModel.AuthViewModel
import org.json.JSONArray
import org.json.JSONObject

class PendingApplications : AppCompatActivity(),PendingApplicationsAdapter.ItemClickListener {
    private lateinit var rcAdapter:PendingApplicationsAdapter
    private lateinit var recyclerview: RecyclerView
    private lateinit var rcArray:ArrayList<ApplicationData>


    private lateinit var loader: Dialog
    private val vm: AuthViewModel by lazy {
        getViewModel { AuthViewModel() }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pending_applications)
        loader = ProgressDialog.progressDialog(this)
        setVM()

        recyclerview = findViewById(R.id.recycler)
        getPendingData()
        findViewById<ImageView>(R.id.ivBackBtn).setOnClickListener {
            finish()
        }

    }

    fun getPendingData() {
        var data = ArrayList<ApplicationData>()
        val prefs = PreferenceHelper.defaultPrefs(this)
        var str = ""
        var index  = 1
        do {
            str = prefs.getString(index.toString(),"")!!
            if(!str.isNullOrEmpty()) {
                index++
                    val obj = Gson().fromJson(str, ApplicationData::class.java)
                    data.add(obj)
            }
        }while(!str.isNullOrEmpty())
        //Log.d(data.toString(),"Gruhalakshmi")
        SetListData(data)
    }

    private fun SetListData(data: ArrayList<ApplicationData>) {
        if (data.size > 0) {
            rcArray = data
            recyclerview.layoutManager = LinearLayoutManager(this)
            rcAdapter = PendingApplicationsAdapter(this, data)
            rcAdapter.setClickListener(this)

            //myCaveatsAdapter!!.setClickListener(this)
            recyclerview.adapter = rcAdapter
            //noRecords.visibility = View.GONE
            recyclerview.visibility = View.VISIBLE
        } else {
            //noRecords.visibility = View.VISIBLE
            recyclerview.visibility = View.GONE
        }
    }

    override fun onItemModelClick(view: View?, position: Int) {

        val obj = rcArray.get(0)
        obj.Session =  System.currentTimeMillis().toString()
        val objArray = ArrayList<ApplicationData>()
        objArray.add(obj)

        val submitData = SubmitData(objArray)
        //Log.d(submitData.toString(),"Gruhalakshmi")

        val json = Gson().toJson(submitData)

        vm.callAPI(K.CALL_SUBMIT,submitData )
        loader.show()

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setVM() {
        vm.result.observe(this, Observer {

            when (it?.status) {
                Status.LOADING -> {
                    loader.show()
                }
                Status.SUCCESS -> {
                    loader.dismiss()
                    it.data?.let { it ->
                        val jobj = JSONObject(it)
                        val error = jobj.optString("saved")
                        K.showMessageDialog(it,"Status",this)
                    }
                }
                Status.ERROR -> {
                    loader.dismiss()

                    it.message?.let { message ->
                        Toast.makeText(this,message, Toast.LENGTH_LONG).show()
                        //benContainer.showSnackBar(message, Snackbar.LENGTH_SHORT)
                    }

                }
                else -> null
            }

        })
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