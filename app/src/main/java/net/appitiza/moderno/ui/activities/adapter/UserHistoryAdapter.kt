package net.appitiza.moderno.ui.activities.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_users_history.view.*
import kotlinx.android.synthetic.main.item_users_notification.view.*
import net.appitiza.moderno.R
import net.appitiza.moderno.ui.activities.interfaces.NotificationClick
import net.appitiza.moderno.ui.model.CurrentCheckIndata
import net.appitiza.moderno.ui.model.NotificationData

class UserHistoryAdapter(val mList: ArrayList<CurrentCheckIndata>) : RecyclerView.Adapter<UserHistoryAdapter.NotificationHolder>() {

    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_users_history, parent, false)
        return NotificationHolder(v)
    }

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: NotificationHolder, position: Int) {
        holder.bindItems(mList[position])

    }

    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return mList.size
    }

    //the class is hodling the list view
    class NotificationHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(data: CurrentCheckIndata) {
            itemView.tv_historyitem_date.text = data.checkintime
            itemView.tv_historyitem_site.text = data.sitename
            itemView.tv_historyitem_hours.text = ""
            itemView.tv_historyitem_payment.text = data.payment


        }
    }

}