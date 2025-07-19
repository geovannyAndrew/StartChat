package com.gyros.startchat.data

import android.content.Context
import com.gyros.startchat.data.models.CountryCode
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import javax.inject.Inject

class CountryCodesReader @Inject constructor(
    private val context: Context,
    private val assetsPath: String,
    private val moshi: Moshi
) {

    private fun getJsonString(): String {
        val inputStream = context.assets.open(assetsPath)
        val size = inputStream.available()
        val buffer = ByteArray(size)
        inputStream.read(buffer)
        inputStream.close()
        val json = String(buffer, Charsets.UTF_8)
        return json
    }

    fun getCountryCodes(): List<CountryCode> {
        val type = Types.newParameterizedType(
            List::class.java,
            CountryCode::class.java
        )
        val adapter = moshi.adapter<List<CountryCode>>(type)
        val countryCodes = adapter?.fromJson(getJsonString())
        return countryCodes.orEmpty()
    }


}