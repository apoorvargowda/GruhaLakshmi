package com.gov.gruhalakshmi

import android.R.array
import android.R.id
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.google.gson.Gson
import com.gov.gruhalakshmi.common.LanguageHelper
import com.gov.gruhalakshmi.common.PreferenceHelper
import com.gov.gruhalakshmi.common.ProgressDialog
import com.gov.gruhalakshmi.shared.K
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream


class BankDetails : AppCompatActivity() {
    private lateinit var loader: Dialog
    private var applicantDetails:ApplicationData? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bank_details)
        loader = ProgressDialog.progressDialog(this)

        findViewById<AppCompatButton>(R.id.btnValidate).setOnClickListener {
            if(!validate()) return@setOnClickListener

            val approvedStatus = intent.getStringExtra("APPROVED")
            if(approvedStatus.equals("A",true)){

                if(applicantDetails?.bank_account_no.isNullOrEmpty()){
                    val appdata = Gson().toJson(applicantDetails).toString()
                    val prefs = PreferenceHelper.defaultPrefs(this)
                    prefs.edit().putString("TEMP_DATA",appdata).commit()

                    val intent = Intent(this, Declaration::class.java)
                    val data = Gson().toJson(applicantDetails).toString()
                    intent.putExtra("APPLICANT_DATA", data)
                    startActivity(intent)
                }else {
                    val intent = Intent(this, UploadFiles::class.java)
                    val data = Gson().toJson(applicantDetails).toString()
                    intent.putExtra("APPLICANT_DATA", data)
                    startActivity(intent)
                }
            }else {
                val intent = Intent(this, SelectHusband::class.java)
                val data = Gson().toJson(applicantDetails).toString()
                intent.putExtra("APPLICANT_DATA", data)
                startActivity(intent)
            }
        }
        findViewById<ImageView>(R.id.ivBackBtn).setOnClickListener {
            finish()
        }
        findViewById<AppCompatButton>(R.id.fetchIFSC).setOnClickListener {
            val ifsc = findViewById<TextView>(R.id.ifscTxt).text
            if(ifsc.isNullOrEmpty() || ifsc.toString().length < 3) return@setOnClickListener
            val ifsc_data = loadJSONFromAsset(ifsc.toString().get(0).toString())
            val ifscmaster = Gson().fromJson(ifsc_data,IFSCmasterData::class.java)
            val index = ifscmaster.indexOfFirst{
                it.IFSC == ifsc.toString()
            }
            if(index < 0){
                K.showMessageDialog("Not found IFSC code","Error",this)
                val name = findViewById<TextView>(R.id.bankNametxt)
                name.text = ""
                val branch = findViewById<TextView>(R.id.bankbranchTxt)
                branch.text = ""
            }else {
                val item  = ifscmaster.get(index)
                val name = findViewById<TextView>(R.id.bankNametxt)
                name.text = item.BANK
                val branch = findViewById<TextView>(R.id.bankbranchTxt)
                branch.text = item.BRANCH
            }

        }
    }

    fun validate():Boolean{
        val ifsc = findViewById<TextView>(R.id.ifscTxt).text
        val name = findViewById<TextView>(R.id.bankNametxt).text
        val branch = findViewById<TextView>(R.id.bankbranchTxt).text
        val account_no = findViewById<TextView>(R.id.bankAccountNumber).text
        val reenter_no = findViewById<TextView>(R.id.renterBankAccount).text

        if(ifsc.isNullOrEmpty() || name.isNullOrEmpty() || branch.isNullOrEmpty()
            || account_no.isNullOrEmpty() || reenter_no.isNullOrEmpty()){
            K.showMessageDialog("Bank Details are required","Error",this)
            return false
        }

        if( !account_no.toString().equals(reenter_no.toString())) {
            K.showMessageDialog("Account Number is not matching","Error",this)
            return false
        }
        val appData = intent.getStringExtra("APPLICANT_DATA")
        applicantDetails = Gson().fromJson(appData,ApplicationData::class.java)
        applicantDetails?.bank_branch = branch.toString()
        applicantDetails?.bank_name = name.toString()
        applicantDetails?.bank_account_no = account_no.toString()
        applicantDetails?.ifsc_code = ifsc.toString()
        return true
    }

    fun loadJSONFromAsset(charcode:String): String? {
        var filename:String = "AB.json"
        if("ab".contains(charcode,true))
            filename = "AB.json"
        else if("cdefg".contains(charcode,true))
            filename = "CDEFG.json"
        else if("hi".contains(charcode,true))
            filename = "HI.json"
        else if("jkmnopqr".contains(charcode,true))
            filename = "JKMNOPQR.json"
        else if("s".contains(charcode,true))
            filename = "S.json"
        else if("tuvwxyz".contains(charcode,true))
            filename = "TUVWXYZ.json"


        var json: String? = null
        json = try {
            val `is`: InputStream = getAssets().open(filename)
            val size: Int = `is`.available()
            val buffer = ByteArray(size)
            `is`.read(buffer)
            `is`.close()
            String(buffer, Charsets.UTF_8)
        } catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }
        return json
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