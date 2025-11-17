package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.ImageView
import android.widget.TextView

class EngineerActivity : AppCompatActivity() {
    private lateinit var imageView: ImageView
    private lateinit var departmentList: ListView
    private lateinit var titleTextView: TextView
    private lateinit var departmentLists: ListView
    private lateinit var titleTextView2: TextView

    private val department = arrayOf(
        "สาขาวิชาการผลิตความแม่นยำสูง อาคาร 18/1",
        "สาขาวิชาวิศวกรรมอุตสาหการ อาคาร 18/1",
        "สาขาวิชาการวิศวกรรมโยธา อาคาร 19",
        "สาขาวิชาการวิศวกรรมโยธา อาคาร 20",
        "สาขาวิชาวิศวกรรมสำรวจ อาคาร 35",
        "สาขาวิชาซ่อมบำรุณอากาศยาน อาคารสิรินธร ชั้น 3",
        "สาขาวิศวกรรมไฟฟ้า",
        "สาขาวิชาวิศวกรรมเครื่องกล",
        "สาขาวิชาวิศวกรรมเคมี",
        "สาขาวิชาวิศวกรรทอิเเล็กทรอนิกส์",
    )


    private val departments = listOf(
        Pair("สาขาวิชาการผลิตความแม่นยำสูง อาคาร 18/1", R.drawable.prefix_13_),
        Pair("สาขาวิชาวิศวกรรมอุตสาหการ อาคาร 18/1",  R.drawable.prefix_14),
        Pair("สาขาวิชาการวิศวกรรมโยธา อาคาร 19", R.drawable.prefix_15),  // No image for this item
        Pair("สาขาวิชาการวิศวกรรมโยธา อาคาร 20",R.drawable.prefix_16),
        Pair("สาขาวิชาวิศวกรรมสำรวจ อาคาร 35", R.drawable.prefix_17),  // No image for this item
        Pair("สาขาวิชาวิศวกรรมเครื่องกล",  R.drawable.prefix_18),
    )
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_department)

        titleTextView = findViewById(R.id.titleTextView)
        titleTextView.text = "คณะวิศวกรรมศาสตร์"

        imageView = findViewById(R.id.imageView)
        imageView.setImageResource(R.drawable.prefix_02)

        titleTextView2 = findViewById(R.id.titleTextView2)


        departmentList = findViewById(R.id.department_list)
        departmentList.adapter = CustomAdapter(this, departments)

        departmentLists = findViewById(R.id.faculty_list)
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, department)
        departmentLists.adapter = adapter
    }
}