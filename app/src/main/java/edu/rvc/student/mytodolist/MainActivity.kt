package edu.rvc.student.mytodolist

import android.support.v7.app.AppCompatActivity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var task = findViewById<EditText>(R.id.txtTask)
        var names = findViewById<EditText>(R.id.txtMessage)
        var btnMessage = findViewById<Button>(R.id.btnMessage)
        var messages = findViewById<TextView>(R.id.txtNotes)

        var ref = FirebaseDatabase.getInstance().getReference("Message")

        btnMessage.setOnClickListener {
            txtTask.requestFocus()

            var messageid = ref.push().key
            var messageg = Message(messageid.toString(), task.text.toString(), names.text.toString())

            hideKeyboard()
            task.setText("")
            names.setText("")
            txtTask.requestFocus()
            ref.child(messageid.toString()).setValue(messageg).addOnCompleteListener {
                Toast.makeText(this, "Task Added!",Toast.LENGTH_SHORT).show()
            }

            ref.addValueEventListener( object: ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    messages.text = ""
                    val children = dataSnapshot.children
                    children.forEach{
                        print("data: " + it.toString())
                        if (messages.text.toString() != ""){
                            messages.text = messages.text.toString() + "\n" + "Task: " + it.child("name").value.toString() + " Desc: " + it.child("message").value.toString()
                        } else {
                            messages.text = "My Tasks: "
                            messages.text = messages.text.toString() + "\n" + "Task: " + it.child("name").value.toString() + " Desc: " + it.child("message").value.toString()
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w("Message", "Failed to read value", error.toException())
                }
            })

        }

        // Write a message to the database
        //val database = FirebaseDatabase.getInstance()
        //val myRef = database.getReference("message")
        //myRef.setValue("Hello, World!")
    }

    fun hideKeyboard() {
        try {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        } catch (e: Exception) {
            // TODO: handle exception
        }

    }
}
