package com.example.lab_1

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProfileActivity : AppCompatActivity() {

    private lateinit var userProfile: User
    private lateinit var databaseReference: DatabaseReference

    private lateinit var name: TextView
    private lateinit var phone: EditText
    private lateinit var surname: TextView
    private lateinit var eMail: TextView
    private lateinit var editNickName: EditText
    private lateinit var bSave: Button
    private lateinit var uid: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        editNickName = findViewById(R.id.editNickName)
        phone = findViewById(R.id.editTextPhone)


        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            uid = FirebaseAuth.getInstance().currentUser!!.uid
            getData()
        }


        bSave = findViewById(R.id.bSave)
        bSave.setOnClickListener(){
            setData("PhoneNumber", phone.text.toString())
            setData("NickName", editNickName.text.toString())
        }
    }

    private fun setData(key: String?, value: String?){
        val database = FirebaseDatabase.getInstance()
        val reference = database.getReference("AuthorizedUsers/$uid/$key")
        reference.setValue(value)
    }


    private fun getData(){
        databaseReference = FirebaseDatabase.getInstance().getReference("AuthorizedUsers/$uid")

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    userProfile = dataSnapshot.getValue(User::class.java)!!

                    name = findViewById(R.id.textName)
                    eMail = findViewById(R.id.textMail)

                    name.text = "Name: " + FirebaseAuth.getInstance().currentUser!!.displayName
                    eMail.text = "Email: " + FirebaseAuth.getInstance().currentUser!!.email

                    editNickName.setText(userProfile.NickName)
                    phone.setText(userProfile.PhoneNumber)
                }
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}