package com.example.surendar_5541.ssbbanksystemdemoapplication

import android.app.Fragment
import android.os.AsyncTask
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class HttpRequestGetIFScDetails(fragment: Fragment, getIfscData: GetIfscData) : AsyncTask<Any?, Int, JSONObject?>() {

    val REQUEST_METHOD = "GET"
    val READ_TIMEOUT = 15000
    val CONNECTION_TIMEOUT = 15000
    var getIfscData: GetIfscData? = null
    val fragment: Fragment? = fragment

    init {
        this.getIfscData = getIfscData
    }

    override fun doInBackground(vararg args: Any?): JSONObject? {
        val stringUrl = args[0]
        var result: String

        try {
            val myUrl = URL(stringUrl.toString())
            val connection: HttpsURLConnection = myUrl.openConnection() as HttpsURLConnection
            connection.requestMethod = REQUEST_METHOD
            connection.readTimeout = READ_TIMEOUT
            connection.connectTimeout = CONNECTION_TIMEOUT
            connection.connect()

            //Input Streamer
            val streamReader = InputStreamReader(connection.inputStream)
            val response = BufferedReader(streamReader)
            val stringBuilder = StringBuilder()

            for (inputLine in response.readLine()) {
                stringBuilder.append(inputLine)
            }

            response.close()
            streamReader.close()
            //Set our result equal to our stringBuilder
            result = stringBuilder.toString()

        } catch (e: IOException) {
            e.printStackTrace()
            result = ""
        }

        val jsonObject: JSONObject?
        if (result != "") {
            jsonObject = JSONObject(result)
        } else {
            jsonObject = null
        }

        return jsonObject
    }

    override fun onPostExecute(result: JSONObject?) {
        super.onPostExecute(result)
        if (fragment != null && fragment.isVisible) { //Sends only if that fragment is still active
            if (result != null) {
                getIfscData?.getBankAndBranch(result["Bank"].toString(), result["Branch"].toString())
            } else {
                getIfscData?.getBankAndBranch(null, null)
            }

        }
    }
}


/*class myAsync(internal var context: Context, interfaceObj: GetResultInterface) : AsyncTask<Void, Void, String>() {
    internal var interfaceObj: GetResult? = null

    init {
        this.interfaceObj = interfaceObj
    }

    protected fun onPostExecute(result: ArrayList<String>) {
        super.onPostExecute(result)
        interfaceObj!!.processFinish(result)
    }
}  */
