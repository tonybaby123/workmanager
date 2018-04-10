package net.appitiza.moderno.ui.activities.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_admin_site_list.view.*
import net.appitiza.moderno.R
import net.appitiza.moderno.ui.activities.interfaces.AdminSiteClick
import net.appitiza.moderno.ui.model.SiteListdata

class AdminSiteAdapter(val userList: ArrayList<SiteListdata>, val callback: AdminSiteClick) : RecyclerView.Adapter<AdminSiteAdapter.ViewHolder>() {

    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_admin_site_list, parent, false)
        return ViewHolder(v)
    }

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(userList[position])
        holder.itemView.setOnClickListener {
            callback.onSiteClick(userList[position])
        }
    }

    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return userList.size
    }

    //the class is hodling the list view
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(data: SiteListdata) {
            itemView.tv_site_item_name.text = data.sitename


        }
    }

}