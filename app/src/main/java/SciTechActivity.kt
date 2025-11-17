package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.ImageView
import android.widget.TextView

class SciTechActivity : AppCompatActivity() {
    private lateinit var imageView: ImageView
    private lateinit var departmentList: ListView
    private lateinit var titleTextView: TextView
    private lateinit var departmentLists: ListView

    private val department = arrayOf(
        "สาขาวิทยาการคอมพิวเตอร์",
        "สาขาชีววิทยา",
        "สาขาวิชาเคมี อาคาร 10,33",
        "สาชาวิชาฟิสิกส์",
        "สาขาวิชาการจัดการ อาคาร 50 7",
        "สาขาเทคโนโลยีการถ่ายภาพและภาพยนต์ อาคาร 25",
        "สาขาวิชาเทคโนโลยีเครื่องเรือนและการออกแบบ อาคาร 15/5",
        "สาขาวิชาเทคโนโลยีการพิมพ์ อาคาร 23",
        "สาขาวิชาออกแบบผลิตภัณฑ์อุตสาหกรรม อาคาร 9",
        "สาขาวิชาเทคโนโลยีโทรทัศน์และวิทยุการะจายเสียง",
    )


    private val departments = listOf(
        Pair("สาขาวิชาเคมี อาคาร 10,33", R.drawable.prefix_10),  // Example with image
        Pair("สาขาเทคโนโลยีการถ่ายภาพและภาพยนต์ อาคาร 25", R.drawable.prefix_12),  // No image for this item
        Pair("สาขาวิชาเทคโนโลยีเครื่องเรือนและการออกแบบ อาคาร 15/5",  R.drawable.prefix_38),  // Example with image
        Pair("สาขาวิชาเทคโนโลยีการพิมพ์ อาคาร 23", R.drawable.prefix_11),  // No image for this item
        Pair("สาขาวิชาออกแบบผลิตภัณฑ์อุตสาหกรรม อาคาร 9", R.drawable.prefix_09),  // Example with image
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_department)

        titleTextView = findViewById(R.id.titleTextView)
        titleTextView.text = "คณะวิทยาศาสตร์และเทคโนโลยี"

        imageView = findViewById(R.id.imageView)
        imageView.setImageResource(R.drawable.prefix_01)

        departmentList = findViewById(R.id.department_list)
        departmentList.adapter = CustomAdapter(this, departments)

        departmentLists = findViewById(R.id.faculty_list)
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, department)
        departmentLists.adapter = adapter
    }
}