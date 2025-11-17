package com.example.myapplication

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.mapbox.maps.extension.style.expressions.dsl.generated.color
import com.mapbox.maps.plugin.annotation.generated.PointAnnotation

class PointAnnotationAdapter(
    context: Context,
    private val items: List<Pair<PointAnnotation, Double>>
) : ArrayAdapter<Pair<PointAnnotation, Double>>(context, R.layout.list_item, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.list_item, parent, false)
        val listItemTextView = view.findViewById<TextView>(R.id.itemTextView)
        val imageView = view.findViewById<ImageView>(R.id.itemImageView)

        // Get the current item
        val (pointAnnotation, distance) = items[position]

        // Set the display text for each item
        listItemTextView.text = "${pointAnnotation.textField} (${distance.toInt()} เมตร)"
        imageView.visibility = View.GONE

        return view
    }
}
