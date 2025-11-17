package com.example.myapplication

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class LibArtActivity : AppCompatActivity() {
    private lateinit var imageView: ImageView
    private lateinit var departmentList: ListView
    private lateinit var titleTextView: TextView
    private lateinit var departmentLists: ListView
    private lateinit var titleTextView2: TextView

    private val departments = listOf(
        Pair("สาขาวิชาการโรงแรม อาคาร 32", R.drawable.prefix_34),
        Pair("สาขาวิชาท่องเที่ยว อาคาร 4/1", R.drawable.prefix_35),
        Pair("สาขาวิชาการพัฒนาาผลิตภัณฑ์ภูมิปัญญาไทย อาคาร 7/2", null),  // No image for this item
        Pair("สาขาวิชาภาษาจีนเพื่อการสื่อสาร อาคาร 36 ชั้น 5",R.drawable.prefix_44),
        Pair("สาขาวิชาภาษาอังกฤษเพื่อการสื่อสาร อาคาร 36 ชั้น 5", R.drawable.prefix_45)  // No image for this item
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_department)

        titleTextView = findViewById(R.id.titleTextView)
        titleTextView.text = "คณะศิลปศาสตร์"

        titleTextView2 = findViewById(R.id.titleTextView2)

        imageView = findViewById(R.id.imageView)
        imageView.setImageResource(R.drawable.prefix_04)

        departmentList = findViewById(R.id.department_list)
        departmentList.adapter = CustomAdapter(this, departments)

        departmentLists = findViewById(R.id.faculty_list)
        departmentLists.visibility = ListView.GONE
    }
}