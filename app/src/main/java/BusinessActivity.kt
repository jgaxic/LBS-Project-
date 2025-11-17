package com.example.myapplication

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class BusinessActivity : AppCompatActivity() {
    private lateinit var imageView: ImageView
    private lateinit var departmentList: ListView
    private lateinit var titleTextView: TextView
    private lateinit var departmentLists: ListView
    private lateinit var titleTextView2: TextView


    private val departments = listOf(
        Pair("สาขาวิชาเทคโนโลยีสารสนเทศและธุรกิจดิจิทัล อาคาร 5 ชั้น 3", null),
        Pair("สาขาวิชาการตลาด อาคาร 50 ชั้น 4", null),
        Pair("สาขาวิชาการเงิน อาคาร 5 ชั้น 5", null),  // No image for this item
        Pair("สาขาวิชาการบัญชี อาคาร 50 ชั้น 6",null),
        Pair("สาขาวิชาการจัดการ อาคาร 50 7", null),  // No image for this item
        Pair("สาขาวิชาการสื่อสารธุรกิจระหว่างประเทศ ชั้น 8", null)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_department)

        titleTextView = findViewById(R.id.titleTextView)
        titleTextView.text = "คณะบริหารธุรกิจ"

        titleTextView2 = findViewById(R.id.titleTextView2)
        titleTextView2.visibility = TextView.GONE

        imageView = findViewById(R.id.imageView)
        imageView.setImageResource(R.drawable.prefix_03)



        departmentList = findViewById(R.id.department_list)
        departmentList.adapter = CustomAdapter(this, departments)

        departmentLists = findViewById(R.id.faculty_list)
        departmentLists.visibility = ListView.GONE
    }

}