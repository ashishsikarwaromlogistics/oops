package com.example.omoperation.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.TextView

class MyExpandableListAdapter(
    private val context: Context,
    private val headerList: List<String>,
    private val childList: HashMap<String, List<String>>
) : BaseExpandableListAdapter() {

    override fun getGroupCount(): Int = headerList.size

    override fun getChildrenCount(groupPosition: Int): Int =
        childList[headerList[groupPosition]]?.size ?: 0

    override fun getGroup(groupPosition: Int): Any = headerList[groupPosition]

    override fun getChild(groupPosition: Int, childPosition: Int): Any =
        childList[headerList[groupPosition]]?.get(childPosition) ?: ""

    override fun getGroupId(groupPosition: Int): Long = groupPosition.toLong()

    override fun getChildId(groupPosition: Int, childPosition: Int): Long = childPosition.toLong()

    override fun hasStableIds(): Boolean = false
    override fun getGroupView(
        groupPosition: Int,
        p1: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View? {
        val view = convertView ?: LayoutInflater.from(context).inflate(android.R.layout.simple_expandable_list_item_1, parent, false)
        val textView = view.findViewById<TextView>(android.R.id.text1)
        textView.text = getGroup(groupPosition).toString()
        return view
    }

    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        p2: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View? {
        val view = convertView ?: LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, parent, false)
        val textView = view.findViewById<TextView>(android.R.id.text1)
        textView.text = getChild(groupPosition, childPosition).toString()
        return view
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean = true



   /* override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, parent, false)
        val textView = view.findViewById<TextView>(android.R.id.text1)
        textView.text = getChild(groupPosition, childPosition).toString()
        return view
    }*/
}
