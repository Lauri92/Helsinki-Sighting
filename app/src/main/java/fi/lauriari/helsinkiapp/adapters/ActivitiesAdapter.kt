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
import fi.lauriari.helsinkiapp.datamodels.HelsinkiActivities
import retrofit2.Response
import android.graphics.Paint
import android.widget.ImageView
import com.bumptech.glide.Glide


class ActivitiesAdapter(
    private val activitiesList: MutableList<SingleHelsinkiActivity>,
    val context: Context
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
        val thumbnail_iv = holder.itemView.findViewById<ImageView>(R.id.thumbnail_iv)

        nameTv.text = item.name
        localityTv.text = item.locality
        if (item.images.isNotEmpty()) {
            Glide.with(context).load(item.images[0].url)
                .placeholder(R.drawable.image_not_available)
                .error(R.drawable.image_not_available)
                .into(thumbnail_iv)
        }

        holder.itemView.setOnClickListener {
            if (item.images.isNotEmpty()) {
                Toast.makeText(
                    context,
                    "Clicked $position and the first image url is: ${item.images[0].url}",
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

    /**
     * Decode HTML values from string
     */
    private fun decodeHtmlString(string: String): String {
        return Html
            .fromHtml(string, Html.FROM_HTML_MODE_COMPACT)
            .toString().trim()
    }
}