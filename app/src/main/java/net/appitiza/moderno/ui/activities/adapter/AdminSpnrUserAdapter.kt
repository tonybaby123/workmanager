package net.appitiza.moderno.ui.activities.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import kotlinx.android.synthetic.main.item_admin_site_list.view.*
import net.appitiza.moderno.R
import net.appitiza.moderno.ui.activities.interfaces.AdminSiteClick
import net.appitiza.moderno.ui.activities.interfaces.UserClick
import net.appitiza.moderno.ui.model.SiteListdata
import net.appitiza.moderno.ui.model.UserListdata

class AdminSpnrUserAdapter : BaseAdapter {

    private var userList = ArrayList<UserListdata>()
    private var context: Context? = null
    private var callback: UserClick? = null

    constructor(context: Context, typeList: ArrayList<UserListdata>,callback: UserClick) : super() {
        this.userList = typeList
        this.context = context
        this.callback = callback
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {

        val view: View?
        val vh: UserHolder

        if (convertView == null) {
            val inflater = context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

            view = inflater.inflate(R.layout.item_admin_spnr_user, parent, false)
            vh = UserHolder(view)
            view.tag = vh
        } else {
            view = convertView
            vh = view.tag as UserHolder
        }

        vh.tvTitle.text = userList[position].username
        return view
    }

    override fun getItem(position: Int): Any {
        return userList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return userList.size
    }
}

private class UserHolder(view: View?) {
    val tvTitle: TextView

    init {
        this.tvTitle = view?.findViewById(R.id.tv_spnr_user_name) as TextView
    }

    //  if you target API 26, you should change to:
//        init {
//            this.tvTitle = view?.findViewById<TextView>(R.id.tvTitle) as TextView
//            this.tvContent = view?.findViewById<TextView>(R.id.tvContent) as TextView
//        }
}
