
package com.jimmy.securingnetworkdata

import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.recyclerview_item_row.view.*


class RecyclerAdapter(private val pets: ArrayList<Pet>) :
  RecyclerView.Adapter<RecyclerAdapter.PhotoHolder>() {

  override fun getItemCount() = pets.size

  override fun onBindViewHolder(holder: PhotoHolder, position: Int) {
    val pet = pets[position]
    holder.bindPet(pet)
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoHolder {
    val inflatedView = parent.inflate(R.layout.recyclerview_item_row, false)
    return PhotoHolder(inflatedView)
  }

  class PhotoHolder(theView: View) : RecyclerView.ViewHolder(theView), View.OnClickListener {
    private val view = theView
    private var pet: Pet? = null

    init {
      theView.setOnClickListener(this)
    }

    override fun onClick(view: View) {
      val context = itemView.context
      val showDetailsIntent = Intent(context, PetDetailActivity::class.java)
      showDetailsIntent.putExtra(PET_KEY, pet)
      context.startActivity(showDetailsIntent)
    }

    fun bindPet(pet: Pet) {
      this.pet = pet
      Picasso.with(view.context).load(pet.url).into(view.itemImage)
      view.itemDate.text = pet.date
      view.itemName.text = pet.name
    }

    companion object {
      private const val PET_KEY = "PET"
    }
  }
}