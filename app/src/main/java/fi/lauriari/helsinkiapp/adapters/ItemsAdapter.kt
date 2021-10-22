package fi.lauriari.helsinkiapp.adapters

import android.content.Context
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import fi.lauriari.helsinkiapp.R
import fi.lauriari.helsinkiapp.classes.SingleHelsinkiItem
import android.widget.ImageView
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import fi.lauriari.helsinkiapp.fragments.BrowseFragmentDirections


class ItemsAdapter(
    private var itemsList: MutableList<SingleHelsinkiItem>,
    private val context: Context
) :
    RecyclerView.Adapter<ItemsAdapter.MyViewHolder>() {

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        removeDuplicates()
        decodeObjects()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activitiesrow_layout, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val item = itemsList[position]
        val nameTv = holder.itemView.findViewById<TextView>(R.id.name_tv)
        val localityTv = holder.itemView.findViewById<TextView>(R.id.locality_tv)
        val tagsTv = holder.itemView.findViewById<TextView>(R.id.tags_tv)
        val thumbnailIv = holder.itemView.findViewById<ImageView>(R.id.thumbnail_iv)

        nameTv.text = item.name
        localityTv.text = item.locality

        item.tags?.forEach {
            if (it == item.tags!![0]) {
                tagsTv.append(" ${it.name}, ")
            } else {
                if (it != item.tags!!.last()) tagsTv.append("${it.name}, ") else tagsTv.append(it.name)
            }
        }

        if (item.images != null) {
            if (item.images!!.isNotEmpty()) {
                Glide.with(context).load(item.images!![0].url)
                    .placeholder(R.drawable.loading_animation)
                    .error(R.drawable.image_not_available)
                    .into(thumbnailIv)
            } else {
                thumbnailIv.setBackgroundResource(R.drawable.image_not_available)
            }
        } else {
            thumbnailIv.setBackgroundResource(R.drawable.image_not_available)
        }

        holder.itemView.setOnClickListener {
            val action = BrowseFragmentDirections.actionBrowseFragmentToSingleItemFragment(item)
            holder.itemView.findNavController()
                .navigate(action)
        }
    }

    override fun getItemCount(): Int {
        return itemsList.size
    }

    //Prevent loading images into cells which don't have images, if going back and fort in the recyclerview
    override fun getItemViewType(position: Int): Int {
        return position
    }

    private fun decodeObjects() {
        itemsList.forEach { item ->
            item.name = item.name?.let { it -> decodeHtmlString(it) }
            item.infoUrl = item.infoUrl?.let { it -> decodeHtmlString(it) }
            item.description = item.description?.let { it -> decodeHtmlString(it) }
            item.locality = item.locality?.let { it -> decodeHtmlString(it) }
        }
    }

    private fun removeDuplicates() {
        if (itemsList[0].eventDates != null) {
            itemsList =
                itemsList.distinctBy { it.description } as MutableList<SingleHelsinkiItem>
        }
    }

    /**
     * Decode HTML values from string
     */
    private fun decodeHtmlString(string: String): String {
        return Html
            .fromHtml(string, Html.FROM_HTML_MODE_COMPACT)
            .toString().trim()
    }
}