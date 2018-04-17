package net.appitiza.moderno.ui.activities.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_users_history.view.*
import net.appitiza.moderno.R
import net.appitiza.moderno.ui.model.CurrentCheckIndata
import net.appitiza.moderno.utils.Utils
import java.util.*

class UserHistoryAdapter(var mContext: Context, val mList: ArrayList<CurrentCheckIndata>) : RecyclerView.Adapter<UserHistoryAdapter.NotificationHolder>() {

    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_users_history, parent, false)
        return NotificationHolder(v)
    }

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: NotificationHolder, position: Int) {
        holder.bindItems(mContext, mList[position])

    }

    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return mList.size
    }

    //the class is hodling the list view
    class NotificationHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(mContext: Context, data: CurrentCheckIndata) {
            itemView.tv_historyitem_date.text = Utils.convertDate(data.checkintime!!.toLong(), "dd MMM yyyy")
            itemView.tv_historyitem_site.text = data.sitename


            if (data.checkintime != 0L) {
                if (data.checkouttime != 0L) {

                    itemView.tv_historyitem_hours.text = Utils.convertHours((data.checkouttime!!.toLong() - data.checkintime!!.toLong()))

                } else {
                    itemView.tv_historyitem_hours.text = mContext.getString(R.string.not_checked_out)
                }
            }



            if (!data.payment.toString().equals("null")) {

                itemView.tv_historyitem_payment.text = data.payment
            } else {
                itemView.tv_historyitem_payment.text = mContext.getString(R.string.not_checked_out)
            }

        }

    }


}