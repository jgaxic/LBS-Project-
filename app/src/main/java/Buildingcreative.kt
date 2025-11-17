package com.example.myapplication

import android.os.Bundle
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class Buildingcreative : AppCompatActivity() {
    private lateinit var imageView: ImageView
    private lateinit var departmentList: ListView
    private lateinit var titleTextView: TextView
    private lateinit var departmentLists: ListView
    private lateinit var titleTextView2: TextView

    private val departments = listOf(
        Pair("UTK Shop",R.drawable.prefix_20 ),
        Pair("Co-working space ชั้น 7 อาคารปฎิบัติการเทคโนโลยีเชิงสร้างสรรค์",  R.drawable.prefix_25),
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_department)

        titleTextView = findViewById(R.id.titleTextView)
        titleTextView.text = "อาคาร 36"

        titleTextView2 = findViewById(R.id.titleTextView2)
        titleTextView2.visibility = TextView.GONE

        imageView = findViewById(R.id.imageView)
        imageView.setImageResource(R.drawable.prefix_22)

        departmentList = findViewById(R.id.department_list)
        departmentList.adapter = CustomAdapter(this, departments)

        departmentLists = findViewById(R.id.faculty_list)
        departmentLists.visibility = ListView.GONE
    }
}