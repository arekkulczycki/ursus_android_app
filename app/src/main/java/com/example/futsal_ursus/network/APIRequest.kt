package com.example.futsal_ursus.network

import com.example.futsal_ursus.models.data.deserializers.MapDeserializer
import com.example.futsal_ursus.models.events.ServerErrorEvent
import com.example.futsal_ursus.models.events.UnauthorizedEvent
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import com.google.gson.Gson
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus

class APIRequest {

    fun get(url: String, func: (r: Any?) -> Unit, headers: List<Pair<String, Any>> = emptyList(),
            acceptedCodes: List<Int> = emptyList(), deserializer: ResponseDeserializable<Any> = MapDeserializer()
    ){
        GlobalScope.launch {
            val request = url.httpGet()
            reaction(request, func, headers, acceptedCodes, deserializer)
        }
    }

    fun post(url: String, data: Any? = null, func: (r: Any?) -> Unit, headers: List<Pair<String, Any>> = emptyList(),
             acceptedCodes: List<Int> = emptyList(), deserializer: ResponseDeserializable<Any> = MapDeserializer()
    ) {
        GlobalScope.launch {
            val body = Gson().toJson(data).toString()
            val request = url.httpPost().body(body)
            reaction(request, func, headers, acceptedCodes, deserializer)
        }
    }

    fun reaction(request: Request, func: (r: Any?) -> Unit, headers: List<Pair<String, Any>> = emptyList(),
                 acceptedCodes: List<Int> = emptyList(), deserializer: ResponseDeserializable<Any>){
        if (headers.isEmpty())
            request.header("Content-Type" to "application/json")
        else
            headers.forEach {
                request.appendHeader(it)
            }

        request.responseObject(deserializer){ req, response, result ->
            if (response.statusCode == 200 || response.statusCode in acceptedCodes) {
                val (r, err) = result
                if (err != null)
                    EventBus.getDefault().postSticky(ServerErrorEvent(response.statusCode, err.message))
                else
                    func(r)
            }
            else if (response.statusCode == 403)
                EventBus.getDefault().postSticky(UnauthorizedEvent())
            else
                EventBus.getDefault().postSticky(ServerErrorEvent(response.statusCode, response.responseMessage))
        }
    }
}