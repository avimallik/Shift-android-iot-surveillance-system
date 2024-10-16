package com.armavi.shift.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.armavi.shift.R
import com.google.android.material.snackbar.Snackbar

class IPSignIn : AppCompatActivity() {

    lateinit var ipTxt: EditText
    lateinit var ipTxtDatabase: EditText
    lateinit var proceedBtn: Button

    //Variables
    val sharedPrefFile_ip = "IP_Prefs"
    val sharedPrefFile_ip_database = "IP_Prefs_Database"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ipsign_in)

        //Variables
        val sharedPreferences: SharedPreferences = this.getSharedPreferences(sharedPrefFile_ip, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        //UI Components Declaration
        proceedBtn = findViewById(R.id.proceed_btn)
        ipTxt = findViewById(R.id.ip_text)
        ipTxtDatabase = findViewById(R.id.ip_text_database)

        if(sharedPreferences.getString("ip_pref", "") != ""){
            Toast.makeText(applicationContext, "Not empty", Toast.LENGTH_SHORT).show()
            intentLauncher()

        }else{
            Toast.makeText(applicationContext, "Empty", Toast.LENGTH_SHORT).show()
        }

        //Proceed button
        proceedBtn.setOnClickListener {
            if(ipTxt.text.toString() == "" || ipTxtDatabase.text.toString() == ""){
                showSnackBar("Please provide the Detector & Database IP !", this)
            }else{
                val ipPrefSave = ipTxt.text.toString()
                val ipPrefSaveDatabase = ipTxtDatabase.text.toString()

                editor.putString("ip_pref", ipPrefSave)
                editor.putString("ip_pref_database", ipPrefSaveDatabase)

                editor.apply()
                editor.commit()
                intentLauncher()
            }
        }

        //Session checking
        val ipPrefDisp = sharedPreferences.getString("ip_pref", "")
        val ipPrefDispDatabase = sharedPreferences.getString("ip_pref_database", "")

        ipTxt.setText(ipPrefDisp)
        ipTxtDatabase.setText(ipPrefDispDatabase)

    }

    fun showSnackBar(message: String?, activity: Activity?) {
        if (null != activity && null != message) {
            Snackbar.make(
                activity.findViewById(android.R.id.content),
                message, Snackbar.LENGTH_SHORT
            ).show()
        }
    }

    private fun intentLauncher(){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    override fun onBackPressed() {
        finish()
    }

}