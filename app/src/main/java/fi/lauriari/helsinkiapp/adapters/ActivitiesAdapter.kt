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

        activitiesList.forEach { activity ->
            activity.name = activity.name?.let { it -> decodeHtmlString(it) }
            activity.infoUrl = activity.infoUrl?.let { it -> decodeHtmlString(it) }
            activity.description = activity.description?.let { it -> decodeHtmlString(it) }
            activity.locality = activity.locality?.let { it -> decodeHtmlString(it) }
        }


        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val nameTv = holder.itemView.findViewById<TextView>(R.id.name_tv)

        nameTv.text = activitiesList[position].name
        nameTv.paintFlags = nameTv.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        holder.itemView.findViewById<TextView>(R.id.info_url_tv).text =
            activitiesList[position].infoUrl
        holder.itemView.findViewById<TextView>(R.id.description_tv).text =
            activitiesList[position].description
        holder.itemView.findViewById<TextView>(R.id.locality_tv).text =
            activitiesList[position].locality

        holder.itemView.setOnClickListener {
            Toast.makeText(context, "Clicked $position", Toast.LENGTH_SHORT).show()
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