/*package com.example.surendar_5541.ssbbanksystemdemoapplication
import android.os.AsyncTask
import android.view.View
import android.widget.Toast
import org.json.JSONObject
import java.io.IOException
import java.net.URL
import javax.net.ssl.HttpsURLConnection
import java.io.InputStreamReader
import java.io.BufferedReader

class HttpGetRequest : AsyncTask<String,Void,String>() {
    val REQUEST_METHOD = "GET"
    val READ_TIMEOUT = 15000
    val CONNECTION_TIMEOUT = 15000

    override fun doInBackground(vararg urls: String?): String? {
        val stringUrl = urls[0]
        var result: String

        try {
            val myUrl = URL(stringUrl)
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

        } catch(e: IOException){
            e.printStackTrace()
            result = ""
           // result = "Exception occurred"
        }
        return result
    }


} */