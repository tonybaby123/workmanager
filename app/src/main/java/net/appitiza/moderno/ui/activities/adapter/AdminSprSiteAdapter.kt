package net.appitiza.moderno.ui.activities.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import net.appitiza.moderno.R
import net.appitiza.moderno.ui.activities.interfaces.UserSiteClick
import net.appitiza.moderno.ui.model.SiteListdata

class AdminSprSiteAdapter : BaseAdapter {

    private var siteList = ArrayList<SiteListdata>()
    private var context: Context? = null
    private var callback: UserSiteClick? = null

    constructor(context: Context, siteList: ArrayList<SiteListdata>,callback : UserSiteClick) : super() {
        this.siteList = siteList
        this.context = context
        this.callback = callback
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {

        val view: View?
        val vh: AdminSpnrSiteHolder

        if (convertView == null) {
            val inflater = context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

            view = inflater.inflate(R.layout.item_admin_spnr_sitelist, parent, false)
            vh = AdminSpnrSiteHolder(view)
            view.tag = vh
        } else {
            view = convertView
            vh = view.tag as AdminSpnrSiteHolder
        }

        vh.tvTitle.text = siteList[position].sitename
        return view
    }

    override fun getItem(position: Int): Any {
        return siteList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return siteList.size
    }
}

private class AdminSpnrSiteHolder(view: View?) {
    val tvTitle: TextView

    init {
        this.tvTitle = view?.findViewById(R.id.tv_checkin_site_item_name) as TextView
    }

    //  if you target API 26, you should change to:
//        init {
//            this.tvTitle = view?.findViewById<TextView>(R.id.tvTitle) as TextView
//            this.tvContent = view?.findViewById<TextView>(R.id.tvContent) as TextView
//        }
}
