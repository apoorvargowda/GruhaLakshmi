package com.gov.gruhalakshmi.shared

import android.app.Activity
import android.app.AlertDialog
import android.text.Html
import java.text.SimpleDateFormat
import java.util.Date

class K {
    companion object {

        val CALL_LISTDIST = 200
        val CALL_SEND_OTP = 201
        val CALL_HSMURL = 202
        val CALL_FETCHCOMPLED = 203
        val CALL_VALIDATEOTP = 204
        val CALL_SUBMIT = 205
        val CALL_RCCHECK = 206



        fun showMessageDialog(msg:String, title:String,
                              activity: Activity){
            val builder = AlertDialog.Builder(activity)
            builder.setMessage(msg)
            if(!title.isNullOrEmpty()){
                builder.setTitle(title)
            }
            builder.setCancelable(true)

            builder.setPositiveButton("Ok", {
                    dialog, _ ->
                dialog.dismiss()
            })
            val dlg = builder.create()
            dlg.setCancelable(false)
            dlg.show()
        }

        fun showHtmlMessageDialog(msg:String, title:String,
                              activity: Activity){
            val builder = AlertDialog.Builder(activity)
            builder.setMessage(Html.fromHtml(msg))
            if(!title.isNullOrEmpty()){
                builder.setTitle(title)
            }
            builder.setCancelable(true)

            builder.setPositiveButton("Ok", {
                    dialog, _ ->
                dialog.dismiss()
            })
            val dlg = builder.create()
            dlg.setCancelable(false)
            dlg.show()
        }

        fun strToDate(dateStr:String) : Date {
            val format = SimpleDateFormat("dd/MM/yyyy")
            val newDate: Date = format.parse(dateStr)
            return newDate
        }

        fun dateToString(date: Date) :String {
            val format = SimpleDateFormat("dd/MM/yyyy")
            val dateStr: String = format.format(date)
            return dateStr
        }
    }
}