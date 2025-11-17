package com.example.myapplication

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView

class CustomAdapter(private val context: Context, private val data: List<Pair<String, Int?>>) : BaseAdapter() {

    override fun getCount(): Int = data.size
    override fun getItem(position: Int): Any = data[position]
    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.list_item, parent, false)

        val textView = view.findViewById<TextView>(R.id.itemTextView)
        val imageView = view.findViewById<ImageView>(R.id.itemImageView)

        val (text, imageResId) = data[position]
        textView.text = text

        if (imageResId != null) {
            imageView.setImageResource(imageResId)
            imageView.visibility = View.VISIBLE  // Show image if available
        } else {
            imageView.visibility = View.GONE  // Hide image if not needed
        }

        return view
    }
}
