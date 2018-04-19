package net.appitiza.moderno.ui.activities.services

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.iid.FirebaseInstanceIdService
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import net.appitiza.moderno.constants.Constants
import net.appitiza.moderno.utils.PreferenceHelper
import java.util.HashMap


class FirebaseIDService: FirebaseInstanceIdService() {
    private lateinit var db: FirebaseFirestore
    private var useremail by PreferenceHelper(Constants.PREF_KEY_IS_USER_EMAIL, "")
    private var usertype by PreferenceHelper(Constants.PREF_KEY_IS_USER_USER_TYPE, "")
    override fun onTokenRefresh() {
        val refreshedToken = FirebaseInstanceId.getInstance().token.toString()
        updateFcm(refreshedToken)
        super.onTokenRefresh()
    }
    private fun updateFcm(refreshedToken : String) {
        db = FirebaseFirestore.getInstance()
        val map = HashMap<String, Any>()
        map[Constants.USER_TOKEN] = refreshedToken
        db.collection(Constants.COLLECTION_USER)
                .document(useremail)
                .set(map, SetOptions.merge())
        if (usertype.equals("user")) {
            FirebaseMessaging.getInstance().subscribeToTopic("notification");
        }
    }
}