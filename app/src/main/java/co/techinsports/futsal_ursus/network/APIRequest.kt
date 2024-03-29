package co.techinsports.futsal_ursus.network

import co.techinsports.futsal_ursus.models.data.deserializers.MapDeserializer
import co.techinsports.futsal_ursus.models.events.ServerErrorEvent
import co.techinsports.futsal_ursus.models.events.UnauthorizedEvent
import co.techinsports.futsal_ursus.prefs
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
            acceptedCodes: List<Int> = emptyList(), deserializer: ResponseDeserializable<Any> = MapDeserializer(),
            flag: String? = null
    ){
        GlobalScope.launch {
            val request = url.httpGet()
            reaction(request, func, headers, acceptedCodes, deserializer, flag)
        }
    }

    fun post(url: String, data: Any, func: (r: Any?) -> Unit, headers: List<Pair<String, Any>> = emptyList(),
             acceptedCodes: List<Int> = emptyList(), deserializer: ResponseDeserializable<Any> = MapDeserializer(),
             flag: String? = null
    ) {
        GlobalScope.launch {
            val body = Gson().toJson(data).toString()
            val request = url.httpPost().body(body)
            reaction(request, func, headers, acceptedCodes, deserializer, flag)
        }
    }

    fun reaction(request: Request, func: (r: Any?) -> Unit, headers: List<Pair<String, Any>> = emptyList(),
                 acceptedCodes: List<Int> = emptyList(), deserializer: ResponseDeserializable<Any>, flag: String?){
        request.header("Content-Type" to "application/json")
        println(prefs.login_token)
        if (prefs.login_token != null)
            request.appendHeader("Authorization" to "Token ${prefs.login_token}")
        if (!headers.isEmpty())
            headers.forEach {
                request.appendHeader(it)
            }

        request.responseObject(deserializer){ req, response, result ->
            if (response.statusCode == 200 || response.statusCode in acceptedCodes) {
                val (r, err) = result
                if (err != null) {
                    EventBus.getDefault().postSticky(ServerErrorEvent(response.statusCode, err.message, flag))
                    //TODO: właściwie to jest błąd aplikacji, trzeba zgłaszać
                }
                else
                    func(r)
            }
            else if (response.statusCode == 403 || response.statusCode == 401)
                EventBus.getDefault().postSticky(UnauthorizedEvent())
            else
                EventBus.getDefault().postSticky(ServerErrorEvent(response.statusCode, response.responseMessage, flag))
        }
    }
}