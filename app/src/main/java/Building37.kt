package com.example.myapplication

import android.os.Bundle
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class Building37 : AppCompatActivity() {
    private lateinit var imageView: ImageView
    private lateinit var departmentList: ListView
    private lateinit var titleTextView: TextView
    private lateinit var departmentLists: ListView
    private lateinit var titleTextView2: TextView

    private val departments = listOf(
        Pair("สนามบาส ชัน 2",  null),
        Pair("ฟิตเนส",  null),
        Pair("ห้องพยาบาล",R.drawable.prefix_37),
        Pair("กองพัฒนานักศึกษา",  R.drawable.prefix_23)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_department)

        titleTextView = findViewById(R.id.titleTextView)
        titleTextView.text = "อาคาร 37 (กองพัฒนาการศึกษา)"

        titleTextView2 = findViewById(R.id.titleTextView2)
        titleTextView2.visibility = TextView.GONE

        imageView = findViewById(R.id.imageView)
        imageView.setImageResource(R.drawable.prefix_23)

        departmentList = findViewById(R.id.department_list)
        departmentList.adapter = CustomAdapter(this, departments)

        departmentLists = findViewById(R.id.faculty_list)
        departmentLists.visibility = ListView.GONE
    }
}