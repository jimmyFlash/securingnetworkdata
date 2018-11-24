
package com.jimmy.securingnetworkdata

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.datatheorem.android.trustkit.TrustKit

import java.io.IOException
import java.util.ArrayList
import kotlinx.android.synthetic.main.activity_main.recyclerView


class MainActivity : AppCompatActivity(), PetRequester.RequestManagerResponse {

  private val petList: ArrayList<Pet> = ArrayList()
  private lateinit var petRequester: PetRequester
  private lateinit var linearLayoutManager: LinearLayoutManager
  private lateinit var adapter: RecyclerAdapter

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    linearLayoutManager = LinearLayoutManager(this)
    recyclerView.layoutManager = linearLayoutManager

    adapter = RecyclerAdapter(petList)
    recyclerView.adapter = adapter

//    initialize TrustKit
    TrustKit.initializeWithNetworkSecurityConfiguration(this)

    petRequester = PetRequester(this)
  }

  override fun onStart() {
    super.onStart()
    if (petList.size == 0) {
      retrievePets()
    }
  }

  private fun retrievePets() {
    try {
      petRequester.retrievePets()
    } catch (e: IOException) {
      e.printStackTrace()
    }

  }

  override fun receivedNewPets(results: PetResults) {
    for (pet in results.items) {
      petList.add(pet)
    }
    adapter.notifyItemInserted(petList.size)
  }
}
