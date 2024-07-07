package com.linemanwongnai.app.model

sealed class NetworkResult<T>(
    val data: T? = null,
    val error: Exception? = null,
    val status: Status
) {

    class Success<T>(data: T) : NetworkResult<T>(data, null, Status.SUCCESS)

    class Error<T>(exception: Exception) : NetworkResult<T>(null, exception, Status.ERROR)

    class Loading<T> : NetworkResult<T>(null, null, Status.LOADING)

}


