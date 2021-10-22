package fi.lauriari.helsinkiapp.classes

import android.os.Parcelable
import fi.lauriari.helsinkiapp.datamodels.*
import kotlinx.parcelize.Parcelize


@Parcelize
data class SingleHelsinkiItem(
    var id: String?,
    var name: String?,
    var infoUrl: String?,
    var latitude: Double?,
    var longitude: Double?,
    var locality: String?,
    var description: String?,
    var images: List<Images>?,
    var tags: List<Tag>?,
    var whereWhenDuration: Where_when_duration? = null, //not null for activities
    var openingHours: Opening_hours? = null,            //not null for places
    var eventDates: Event_dates? = null                 //not null for events
) : Parcelable