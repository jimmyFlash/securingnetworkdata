
package com.jimmy.securingnetworkdata

import android.app.Activity
import android.content.Context
import android.util.Log
import com.datatheorem.android.trustkit.TrustKit
import com.google.gson.Gson
import com.google.gson.JsonParser
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.net.URL
import javax.net.ssl.HttpsURLConnection

/**
 * apps could be required to register with a service where the public key is passed back
 * this is often called a token or secret
 * the public key for the Github server that you're communicating with will be included in the code.
 * It will be used to verify the pet data that comes from the items JSON list
 *
 */
private const val SERVER_PUBLIC_KEY = "MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEP9M/My4tmNiaZRcQtYj58EjGN8N3uSnW/s7FpTh4Q+T3tNVkwVCjmDN+a2qIRTcedQyde0d8CoG3Lp2ZlnPhcw=="

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

    val authenticator = Authenticator()



    val urlString = "https://collinstuart.github.io/posts.json"
    val url = URL(urlString)
    val connection = url.openConnection() as HttpsURLConnection

    /*
      Signing a Request
     */
    // take the request string and turn it into a ByteArray
    val bytesToSign = urlString.toByteArray(Charsets.UTF_8)
    //  bytes get signed using the internal private key and the signature bytes are returned
    val signedData = authenticator.sign(bytesToSign)
    // turn the signature bytes into a Base64 string so that it can easily be sent over the network
    val requestSignature = android.util.Base64.encodeToString(signedData, android.util.Base64.DEFAULT)
    Log.d("PetRequester", "signature for request : $requestSignature")


//     bytesToSign[bytesToSign.size - 1] = 0
    // verify that the signature works
    val signingSuccess = authenticator.verify(signedData, bytesToSign)
    Log.e("PetRequester", "success : $signingSuccess")


//    The HttpsURLConnection will now use the TrustKit socket factory, which will take care of making sure the certificates match
    connection.sslSocketFactory = TrustKit.getInstance().getSSLSocketFactory(url.host)


    doAsync {

      val json = connection.inputStream.bufferedReader().readText()
      connection.disconnect()

      uiThread {

        // Verify received signature
// taking all the JSON content for items and turning it into a ByteArray
        val jsonElement = JsonParser().parse(json)
        Log.e("Service returned json", json)
        val jsonObject = jsonElement.asJsonObject
        val result = jsonObject.get("items").toString()
        val resultBytes = result.toByteArray(Charsets.UTF_8)



        /*
          Verifying a Signature
         */
// retrieving the signature string that is returned and you are turning that into a ByteArray
        val signature = jsonObject.get("signature").toString()
        val signatureBytes = android.util.Base64.decode(signature, android.util.Base64.DEFAULT)

// authenticator to verify the data bytes with the signature bytes, given the server's public key.
        val success = authenticator.verify(signatureBytes, resultBytes, SERVER_PUBLIC_KEY)

//  data is verified, it is passed to the response listener
        if (success) {
          // Process data
          val receivedPets = Gson().fromJson(json, PetResults::class.java)
          responseListener.receivedNewPets(receivedPets)
        }

      }
    }

  }




}