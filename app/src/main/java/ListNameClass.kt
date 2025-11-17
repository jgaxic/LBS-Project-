package com.example.myapplication

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import com.mapbox.maps.plugin.annotation.generated.PointAnnotation


class ListNameClass(context: Context, private val mainActivity: MainActivity) : Dialog(context) {

    init {
        setCancelable(true)
    }

    /*override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.list_name)

        val lv = findViewById<ListView>(R.id.nearestplaces_listview) as ListView

        val view = LayoutInflater.from(context).inflate(R.layout.list_item, null)
        //val listItem_textView = view.findViewById<View>(R.id.listitem_text) as TextView

        val listItem_textView = R.id.listitem_text

        lv.adapter = ArrayAdapter<Pair<PointAnnotation, Double>>(context, R.layout.list_name, listItem_textView, mainActivity.greenAnnotations_PointAnnotation_Distance)
        lv.onItemClickListener =
            OnItemClickListener { arg0, view, position, id -> // When clicked, show a toast with the TextView text
                Toast.makeText(
                    context, mainActivity.greenAnnotations_PointAnnotation_Distance[position].first.textField,
                    Toast.LENGTH_SHORT
                ).show()
            }
    }*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.list_name)

        val lv = findViewById<ListView>(R.id.nearestplaces_listview)

        // Set the custom adapter with the list of pairs
        val adapter = PointAnnotationAdapter(context, mainActivity.greenAnnotations_PointAnnotation_Distance)
        lv.adapter = adapter

        lv.onItemClickListener = OnItemClickListener { _, _, position, _ ->
            // Show a toast with the textField of the PointAnnotation
            val pointAnnotation = mainActivity.greenAnnotations_PointAnnotation_Distance[position].first

            mainActivity.pointAnnotationManager!!.clickListeners[0].onAnnotationClick(pointAnnotation)
            //Toast.makeText(context, pointAnnotation.textField, Toast.LENGTH_SHORT).show()

            this.dismiss()
        }
    }
}