package com.zeeshanelahi.barcodescannerandcameraxdemo.model

import android.content.Context

enum class ApiEndpoint(val path: String) {
    GET_SEATS("all-mssv-seats"),
    MARK_ATTENDANCE("excel"),
}

object ApiFactory {
    private var domain: String? = null

    private fun getDomain(context: Context): String {
         if(domain == null) {
             domain =
                 SharedPreferencesHelper
                .getInstance(context)
                .getString(StringValue.LINK_SERVER_KEY, StringValue.LINK_SERVER_VALUE_DEFAULT)
        }
        return domain ?: throw IllegalStateException("Domain is not initialized. Call ApiFactory.init(context) first.")
    }

    fun createApi(endpoint: ApiEndpoint, context: Context): String {
        return "${getDomain(context)}/${endpoint.path}"
    }
}

