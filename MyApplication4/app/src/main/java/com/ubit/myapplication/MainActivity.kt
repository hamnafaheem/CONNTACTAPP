package com.ubit.myapplication

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.ubit.myapplication.databinding.ActivityMainBinding

import android.net.Uri




class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    fun getContacts(view: View) {
        var intent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
        resultLauncher.launch(intent)

    }

    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            if (data != null) {
                val contactUri = data.data
                if (contactUri != null) {
                    extractCompanyName(contactUri)
                }
            }
        }
    }


    fun extractCompanyName(contactUri: Uri) {
        val projection = arrayOf(ContactsContract.CommonDataKinds.Organization.COMPANY)

        contentResolver.query(
            ContactsContract.Data.CONTENT_URI,
            projection,
            ContactsContract.Data.CONTACT_ID + " = ?" + " AND " + ContactsContract.Data.MIMETYPE + " = ?",
            arrayOf(contactUri.lastPathSegment, ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE),
            null
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                val companyName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Organization.COMPANY))
                if (!companyName.isNullOrEmpty()) {
                    // Do something with the company name
                    binding.companyNameTextView.text = "Company Name: $companyName"
                } else {
                    println("No company name found.")
                }
            } else {
                println("Cursor is empty.")
            }
        }
    }




}