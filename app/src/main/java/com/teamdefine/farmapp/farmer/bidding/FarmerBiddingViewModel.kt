package com.teamdefine.farmapp.farmer.bidding

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONArray
import org.json.JSONObject

class FarmerBiddingViewModel : ViewModel() {
    private val _receivedData: MutableLiveData<Any?> = MutableLiveData()
    val receivedData: LiveData<Any?>
        get() = _receivedData

    fun initializeSocketListeners(socketIOInstance: Socket?) {
        socketIOInstance?.on("bid-on-crop", onNewBidOnCrop)
    }

    fun joinCropRoom(
        socketIOInstance: Socket?,
        cropId: String,
        firebaseInstance: FirebaseAuth
    ) {
//        socketIOInstance?.emit(
//            "join-crop-room", Utility.bundleToJSONMapping(
//                null, Bundle().apply {
//                    putString("cropId", cropId);
//                    putString("userName", "NitishSharma")
//                })
        )
    }

    private val onNewBidOnCrop =
        Emitter.Listener { args ->
            val nameValuePairs =
                JSONObject(JSONArray(Gson().toJson(args))[0].toString()).getJSONObject("nameValuePairs")

            val jsonObject = JSONObject()
            jsonObject.put("cropId", nameValuePairs.getString("cropId"))
            jsonObject.put("userName", nameValuePairs.getString("userName"))
            jsonObject.put("bidPrice", nameValuePairs.getString("bidPrice"))

            _receivedData.postValue(jsonObject)
        }
}
