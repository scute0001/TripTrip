package com.emil.triptrip.database


/**
 * A generic class that holds a value with its api status.
 * @param <T>
 */
sealed class ResultUtil<out R> {

    data class Success<out T>(val data: T) : ResultUtil<T>()
    data class Fail(val error: String) : ResultUtil<Nothing>()
    data class Error(val exception: Exception) : ResultUtil<Nothing>()
    object Loading : ResultUtil<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[result=$data]"
            is Fail -> "Fail[error=$error]"
            is Error -> "Error[exception=${exception.message}]"
            Loading -> "Loading"
        }
    }
}

/**
 * `true` if [Result] is of catalogType [Success] & holds non-null [Success.data].
 */
val ResultUtil<*>.succeeded
    get() = this is ResultUtil.Success && data != null