package net.appitiza.moderno.ui.activities.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import net.appitiza.moderno.R
import net.appitiza.moderno.ui.model.SiteListdata

class SiteStatussAdapter : BaseAdapter {

    private var typeList = ArrayList<String>()
    private var context: Context? = null

    constructor(context: Context, typeList: ArrayList<String>) : super() {
        this.typeList = typeList
        this.context = context
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {

        val view: View?
        val vh: SiteStatusHolder

        if (convertView == null) {
            val inflater = context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

            view = inflater.inflate(R.layout.item_admin_site_type, parent, false)
            vh = SiteStatusHolder(view)
            view.tag = vh
        } else {
            view = convertView
            vh = view.tag as SiteStatusHolder
        }

        vh.tvTitle.text = typeList[position]

        return view
    }

    override fun getItem(position: Int): Any {
        return typeList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return typeList.size
    }
}

private class SiteStatusHolder(view: View?) {
    val tvTitle: TextView

    init {
        this.tvTitle = view?.findViewById(R.id.tv_site_type) as TextView
    }

    //  if you target API 26, you should change to:
//        init {
//            this.tvTitle = view?.findViewById<TextView>(R.id.tvTitle) as TextView
//            this.tvContent = view?.findViewById<TextView>(R.id.tvContent) as TextView
//        }
}
