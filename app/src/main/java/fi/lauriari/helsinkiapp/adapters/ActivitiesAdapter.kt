package fi.lauriari.helsinkiapp.adapters

import android.content.Context
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import fi.lauriari.helsinkiapp.R
import fi.lauriari.helsinkiapp.classes.SingleHelsinkiActivity
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide


class ActivitiesAdapter(
    private val activitiesList: MutableList<SingleHelsinkiActivity>,
    private val context: Context
) :
    RecyclerView.Adapter<ActivitiesAdapter.MyViewHolder>() {

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activitiesrow_layout, parent, false)

        Log.d("activitiesList", activitiesList.toString())

        activitiesList.forEach { activity ->
            activity.name = activity.name?.let { it -> decodeHtmlString(it) }
            activity.infoUrl = activity.infoUrl?.let { it -> decodeHtmlString(it) }
            activity.description = activity.description?.let { it -> decodeHtmlString(it) }
            activity.locality = activity.locality?.let { it -> decodeHtmlString(it) }
        }


        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val item = activitiesList[position]
        val nameTv = holder.itemView.findViewById<TextView>(R.id.name_tv)
        val localityTv = holder.itemView.findViewById<TextView>(R.id.locality_tv)
        val tags_tv = holder.itemView.findViewById<TextView>(R.id.tags_tv)
        val thumbnailIv = holder.itemView.findViewById<ImageView>(R.id.thumbnail_iv)

        nameTv.text = item.name
        localityTv.text = item.locality

        item.tags.forEach {
            if (it != item.tags.last()) tags_tv.append("${it.name}, ") else tags_tv.append(it.name)
        }


        if (item.images.isNotEmpty()) {
            Glide.with(context).load(item.images[0].url)
                .placeholder(R.drawable.loading_animation)
                .error(R.drawable.image_not_available)
                .into(thumbnailIv)
        } else {
            thumbnailIv.setBackgroundResource(R.drawable.image_not_available)
        }

        holder.itemView.setOnClickListener {
            if (item.images.isNotEmpty()) {
                Toast.makeText(
                    context,
                    "Clicked $position",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(context, "No images", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun getItemCount(): Int {
        return activitiesList.size
    }


    //Prevent loading images into cells which don't have images, if going back and fort in the recyclerview
    override fun getItemViewType(position: Int): Int {
        return position
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