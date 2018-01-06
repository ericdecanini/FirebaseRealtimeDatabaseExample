package pandakun.firebaseapplicationtwo

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var valueEventListener: ValueEventListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        writeButton.setOnClickListener { writeData(enterNameField.text.toString(), 52) }
        readButton.setOnClickListener { readData() }
    }

    private fun writeData(name: String, age: Int) {
        // Passing no input parameters in getReference()
        // will reference the root of the database
        val database = FirebaseDatabase.getInstance()
        val myRef = database.reference

        // You can go down through the hierarchy in one line of code
        myRef.child("user").child("name").setValue(name)

        // But storing it in a variable will be better
        // if you'll be using it multiple times
        val userRef = myRef.child("user")
        userRef.child("age").setValue(age)
    }

    private fun readData() {
        val userRef = FirebaseDatabase.getInstance().reference.child("user")
        userRef.child("name").addListenerForSingleValueEvent(object: ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot?) {
                val name = dataSnapshot!!.value as String
                nameTextview.text = name
            }

            override fun onCancelled(databaseError: DatabaseError?) {
                Log.w("MainActivity", "Listener on userRef cancelled: " + databaseError)
            }
        })
    }

    private fun setupAutoRead() {
        val userRef = FirebaseDatabase.getInstance().reference.child("user")

         valueEventListener = object: ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot?) {
                val name = dataSnapshot!!.value as String
                automaticNameField.text = name
            }

            override fun onCancelled(databaseError: DatabaseError?) {
                Log.w("MainActivity", "Listener on userRef cancelled: " + databaseError)
            }
        }
        userRef.addValueEventListener(valueEventListener)
    }

    override fun onStart() {
        super.onStart()
        // We setup the realtime database function here because
        // it may have been previously removed in onStop
        setupAutoRead()
    }

    override fun onStop() {
        super.onStop()
        // Remove the value event listener when the activity stops
        // to prevent an IllegalStateException
        FirebaseDatabase.getInstance().reference.child("user")
                .removeEventListener(valueEventListener)
    }
}
