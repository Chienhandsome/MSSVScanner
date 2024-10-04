package com.zeeshanelahi.barcodescannerandcameraxdemo.model.repo

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.zeeshanelahi.barcodescannerandcameraxdemo.SendMessageCallback
import com.zeeshanelahi.barcodescannerandcameraxdemo.model.SharedPreferencesHelper
import com.zeeshanelahi.barcodescannerandcameraxdemo.model.StringValue
import org.json.JSONArray
import org.json.JSONObject
import kotlin.math.log

class MssvQueueManager(val context: Context) {
    private val queue: MutableMap<String, Boolean> = mutableMapOf()
    private val successQueue: MutableMap<String, Boolean> = mutableMapOf()
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper

    companion object {
        @Volatile
        private var INSTANCE: MssvQueueManager? = null

        fun getInstance(context: Context): MssvQueueManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: MssvQueueManager(context).also { INSTANCE = it }
            }
        }
    }

    private val callback = object : SendMessageCallback {

        override fun onMessageSentSucced() {
            TODO("Not yet implemented")
        }

        override fun onMessageFailed(mssv: String?) {
            addMssvToQueue(mssv!!)
        }
    }

    init{
        sharedPreferencesHelper = SharedPreferencesHelper.getInstance(context)
        syncQueueWithMemory()
    }

    fun addMssvToQueue(mssv: String) {
        queue[mssv] = false // false indicates the mssv has not been sent yet
        backupQueueToMemoryAndDisk()
    }

    public fun processQueue() {
        Log.d("MssvQueueManager", "Processing queue...")
        val jsonArray = JSONArray()
        val iterator = queue.iterator()

        while (iterator.hasNext()) {
            val entry = iterator.next()
            if (!entry.value) { // Only process items that have not been sent yet
                jsonArray.put(entry.key.toInt())
                entry.setValue(true) // Mark as sent
            }
        }

        if (jsonArray.length() > 0) {
            sendMessageToServer(jsonArray, context)
        }
    }

    private fun sendMessageToServer(jsonArray: JSONArray, context: Context) {
        try {
            ServerInteractor.getInstance(context).sendMessageToServer(context, jsonArray, callback)

            // Move successfully sent items to successQueue
            val iterator = queue.iterator()
            while (iterator.hasNext()) {
                val entry = iterator.next()
                if (entry.value) {
                    successQueue[entry.key] = true
                    iterator.remove() // Remove from queue after successful send
                }
            }

            backupQueueToMemoryAndDisk()
            backupSuccessQueueToDisk()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Backup queue to memory and disk
    private fun backupQueueToMemoryAndDisk() {
        val jsonArray = JSONArray()
        for ((mssv, sent) in queue) {
            val jsonObject = JSONObject().apply {
                put("mssv", mssv)
                put("sent", sent)
            }
            jsonArray.put(jsonObject)
        }
        sharedPreferencesHelper.saveString(StringValue.WAITNG_QUEUE_KEY, jsonArray.toString())
    }

    // Backup successQueue to disk
    private fun backupSuccessQueueToDisk() {
        val jsonArray = JSONArray()
        for ((mssv, sent) in successQueue) {
            val jsonObject = JSONObject().apply {
                put("mssv", mssv)
                put("sent", sent)
            }
            jsonArray.put(jsonObject)
        }
        sharedPreferencesHelper.saveString(StringValue.SUCCES_QUEUE_KEY, jsonArray.toString())
    }

    private fun syncQueueWithMemory() {
        val savedQueue = sharedPreferencesHelper.getString("mssv_queue", null)
        if (!savedQueue.isNullOrEmpty()) {
            val jsonArray = JSONArray(savedQueue)
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                queue[jsonObject.getString("mssv")] = jsonObject.getBoolean("sent")
            }
        }

        val savedSuccessQueue = sharedPreferencesHelper.getString("mssv_success_queue", null)
        if (!savedSuccessQueue.isNullOrEmpty()) {
            val jsonArray = JSONArray(savedSuccessQueue)
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                successQueue[jsonObject.getString("mssv")] = jsonObject.getBoolean("sent")
            }
        }
    }
}
