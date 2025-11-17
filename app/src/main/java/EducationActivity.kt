package com.example.myapplication

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageView
import android.widget.TextView

class EducationActivity : AppCompatActivity() {
    private lateinit var imageView: ImageView
    private lateinit var departmentList: ListView
    private lateinit var titleTextView: TextView
    private lateinit var departmentLists: ListView
    private lateinit var titleTextView2: TextView

    private val department = arrayOf(
        "สาวิชาเทคโนโลยีโลหการ อาคาร 18/4",
        "สาขาวิชาเทคโนโลยีคอมพิวเตอร์ อาคาร 48 ชั้น 7",
        "สาขาวิชาวิศวกรรมเครื่องกล อาคารสรินธร ชั้น 5",
        "สาขาวิชาเทคโนโลยีอุตสาหการ อาคาร 18/2",
        "สาขาวิชาเทคโนโลยีอุตสาหการ อาคาร 18/3",
        "สาขาวิชาวิศวกรรมอุตสาหการ อาคาร 14/1",
        "สาขาเทคนิคการศึกษา อาคาร 14/1",
        "สาขาวิชาเทคโนโลยีการพิมพ์ อาคาร 23",
        "สาขาวิชาออกแบบผลิตภัณฑ์อุตสาหกรรม อาคาร 9",
        "สาขาวิชาเทคโนโลยีโทรทัศน์และวิทยุการะจายเสียง",
    )

    private val departments = listOf(
        Pair("สาวิชาเทคโนโลยีโลหการ อาคาร 18/4", R.drawable.prefix_26),
        Pair("สาขาวิชาเทคโนโลยีอุตสาหการ อาคาร 18/2", R.drawable.prefix_27_),
        Pair("สาขาวิชาเทคโนโลยีอุตสาหการ อาคาร 18/3", R.drawable.prefix_28_),
    )


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_department)

        titleTextView = findViewById(R.id.titleTextView)
        titleTextView.text = "คณะครุศาสตร์อุตสหกรรม"

        titleTextView2 = findViewById(R.id.titleTextView2)


        imageView = findViewById(R.id.imageView)
        imageView.setImageResource(R.drawable.prefix_06)

        departmentList = findViewById(R.id.department_list)
        departmentList.adapter = CustomAdapter(this, departments)

        departmentLists = findViewById(R.id.faculty_list)
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, department)
        departmentLists.adapter = adapter
    }
}
