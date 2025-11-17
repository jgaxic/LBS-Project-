package com.example.myapplication

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class EcoActivity: AppCompatActivity() {
    private lateinit var imageView: ImageView
    private lateinit var departmentList: ListView
    private lateinit var titleTextView: TextView
    private lateinit var titleTextView2: TextView
    private lateinit var departmentLists: ListView

    private val departments = listOf(
        Pair("สาขาวิชาธุรกิจอาหาร อาคาร 4/1", null),
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_department)

        titleTextView = findViewById(R.id.titleTextView)
        titleTextView.text = "คณะเทคโนโลยีคหกรรมศาสตร์"

        titleTextView2 = findViewById(R.id.titleTextView2)
        titleTextView2.visibility = TextView.GONE

        imageView = findViewById(R.id.imageView)
        imageView.setImageResource(R.drawable.prefix_08)

        departmentList = findViewById(R.id.department_list)
        departmentList.adapter = CustomAdapter(this, departments)

        departmentLists = findViewById(R.id.faculty_list)
        departmentLists.visibility = ListView.GONE
    }
}