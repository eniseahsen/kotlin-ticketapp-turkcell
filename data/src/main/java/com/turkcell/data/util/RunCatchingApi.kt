package com.turkcell.data.util

import com.turkcell.core.exception.ApiException
import com.turkcell.core.exception.NetworkException
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException

suspend inline fun <T> runCatchingApi(crossinline block: suspend () -> T): Result<T> = try{
    Result.success(block())
}
catch(e: HttpException)
{
    val errorCode = parseErrorCode(e)
    Result.failure(ApiException(code = e.code(), errorMessage = e.message(), errorCode = errorCode, cause = e))
}

catch(e: IOException){
    Result.failure(NetworkException(e))
}
catch(e: Exception)
{
    Result.failure(e)
}

fun parseErrorCode(e: HttpException): String? = try {
    val body = e.response()?.errorBody()?.string() ?: return null
    val json = JSONObject(body)
    val errorObj = json.optJSONObject("error")
    if (errorObj != null) {
        errorObj.optString("code").ifBlank { null }
            ?: errorObj.optString("message").ifBlank { null }
    } else {
        json.optString("error").ifBlank { null }
    }
}catch (_: Exception){
    null
}