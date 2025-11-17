package com.example.myapplication

import android.os.Bundle
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class Building80  : AppCompatActivity() {
    private lateinit var imageView: ImageView
    private lateinit var departmentList: ListView
    private lateinit var titleTextView: TextView
    private lateinit var departmentLists: ListView
    private lateinit var titleTextView2: TextView

    private val departments = listOf(
        Pair("ห้องสมุด อาคารเฉลิมพระเกียรติ 80 พรรษา ชั้น 4",R.drawable.prefix_41),
        Pair("Co-working space อาคารเฉลิมพระเกียรติ 80 พรรษา ชั้น 1",  R.drawable.prefix_42),
        Pair("Co-working space อาคารเฉลิมพระเกียรติ 80 พรรษา ชั้น 3",R.drawable.prefix_43),
        Pair("สำนักงานสหกิจศึกษา อาคารเฉลิมพระเกียรติ 80 พรรษา ชั้น 2 ฝั่ง R", R.drawable.prefix_30),
        Pair("โรงอาหาร อาคารเฉลิมพระเกียรติ 80 พรรษา",R.drawable.prefix_31 ),
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_department)

        titleTextView = findViewById(R.id.titleTextView)
        titleTextView.text = "อาคารเฉลิมพระเกียรติ 80 พรรษา"

        titleTextView2 = findViewById(R.id.titleTextView2)
        titleTextView2.visibility = TextView.GONE

        imageView = findViewById(R.id.imageView)
        imageView.setImageResource(R.drawable.prefix_13_)

        departmentList = findViewById(R.id.department_list)
        departmentList.adapter = CustomAdapter(this, departments)

        departmentLists = findViewById(R.id.faculty_list)
        departmentLists.visibility = ListView.GONE
    }
}