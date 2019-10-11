package com.example.futsal_ursus.network

import com.example.futsal_ursus.models.events.ServerErrorEvent
import com.example.futsal_ursus.models.events.UnauthorizedEvent
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import java.lang.Exception

class APIRequest {

    fun get(url: String, func: (responseMap: Map<String, String>) -> Unit,
            headers: List<Pair<String, Any>> = emptyList(), acceptedCodes: List<Int> = emptyList()){
        GlobalScope.launch {
            val request = url.httpGet()
            reaction(request, func, headers, acceptedCodes)
        }
    }

    fun post(url: String, data: Any? = null, func: (responseMap: Map<String, String>) -> Unit,
             headers: List<Pair<String, Any>> = emptyList(), acceptedCodes: List<Int> = emptyList()) {
        GlobalScope.launch {
            val body = Gson().toJson(data).toString()
            val request = url.httpPost().body(body)
            reaction(request, func, headers, acceptedCodes)
        }
    }

    fun reaction(request: Request, func: (responseMap: Map<String, String>) -> Unit,
                 headers: List<Pair<String, Any>> = emptyList(), acceptedCodes: List<Int> = emptyList()){
        if (headers.isEmpty())
            request.header("Content-Type" to "application/json")
        else
            headers.forEach {
                request.appendHeader(it)
            }
        request.responseString{ req, response, result ->
            if (response.statusCode == 200 || response.statusCode in acceptedCodes) {
                val objType = object : TypeToken<Map<String, String>>() {}.type
                val map: Map<String, String> = try {
                    Gson().fromJson(result.get(), objType)
                } catch (e: Exception){
                    emptyMap()
                }
                func(map)
            }
            else if (response.statusCode == 403)
                EventBus.getDefault().post(UnauthorizedEvent())
            else
                EventBus.getDefault().post(ServerErrorEvent(response.statusCode, response.responseMessage))
        }
    }
}