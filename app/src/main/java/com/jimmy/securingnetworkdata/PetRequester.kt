
package com.jimmy.securingnetworkdata

import android.app.Activity
import android.content.Context
import android.util.Log
import com.datatheorem.android.trustkit.TrustKit
import com.google.gson.Gson
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.net.HttpURLConnection
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class PetRequester(listeningActivity: Activity) {

  interface RequestManagerResponse {
    fun receivedNewPets(results: PetResults)
  }

  private val responseListener: RequestManagerResponse
  private val context: Context

  init {
    responseListener = listeningActivity as RequestManagerResponse
    context = listeningActivity.applicationContext
  }

  fun retrievePets() {
    val urlString = "https://collinstuart.github.io/posts.json"
    val url = URL(urlString)
    val connection = url.openConnection() as HttpsURLConnection


//    The HttpsURLConnection will now use the TrustKit socket factory, which will take care of making sure the certificates match
    connection.sslSocketFactory = TrustKit.getInstance().getSSLSocketFactory(url.host)


    doAsync {

      val json = connection.inputStream.bufferedReader().readText()
      connection.disconnect()

      uiThread {
        val receivedPets = Gson().fromJson(json, PetResults::class.java)
        Log.e("iuiugiugiugiugiu", receivedPets.items.toString())
        responseListener.receivedNewPets(receivedPets)
      }
    }

  }




}