package com.example.myapplication

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class IndustryActivity : AppCompatActivity() {
    private lateinit var imageView: ImageView
    private lateinit var departmentList: ListView
    private lateinit var titleTextView: TextView
    private lateinit var departmentLists: ListView
    private lateinit var titleTextView2: TextView

    private val departments = listOf(
        Pair("สาขาวิชาวิศวกรรมสิ่งทอ อาคาร 51 ชั้น 2", null),
        Pair("สาขาวิชาวิศวกรรมเคมีสิ่งทอ อาคาร 24,51 ชั้น 7", null),
        Pair("สาขาวิชาออกแบบสิ่งทอและแฟชั้น อาคาร 51 ชั้น 5,6", null),
        Pair("สาขาวิชาสิางทอและเครื่องนุ่งห่มระตับ ป.โท อาคาร 51 ชั้น 9", null)
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_department)

        titleTextView = findViewById(R.id.titleTextView)
        titleTextView.text = "คณะอุตสาหกรรมสิ่งทอ"

        titleTextView2 = findViewById(R.id.titleTextView2)
        titleTextView2.visibility = TextView.GONE

        imageView = findViewById(R.id.imageView)
        imageView.setImageResource(R.drawable.prefix_07)

        departmentList = findViewById(R.id.department_list)
        departmentList.adapter = CustomAdapter(this, departments)

        departmentLists = findViewById(R.id.faculty_list)
        departmentLists.visibility = ListView.GONE
    }

}