package fi.lauriari.helsinkiapp.datamodels

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


class HelsinkiActivities(
    @SerializedName("meta") val meta: Meta,
    @SerializedName("data") val data: List<ActivityData>,
)


data class HelsinkiPlaces(
    @SerializedName("meta") val meta: Meta,
    @SerializedName("data") val data: List<PlacesData>
)

data class HelsinkiEvents(
    @SerializedName("meta") val meta: Meta,
    @SerializedName("data") val data: List<EventsData>
)


data class ActivityData(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: Name,
    @SerializedName("source_type") val source_type: Source_type,
    @SerializedName("info_url") val info_url: String,
    @SerializedName("modified_at") val modified_at: String,
    @SerializedName("location") val location: Location,
    @SerializedName("description") val description: Description,
    @SerializedName("tags") val tags: List<Tag>,
    @SerializedName("where_when_duration") val where_when_duration: Where_when_duration
)

data class PlacesData(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: Name,
    @SerializedName("source_type") val source_type: Source_type,
    @SerializedName("info_url") val info_url: String,
    @SerializedName("modified_at") val modified_at: String,
    @SerializedName("location") val location: Location,
    @SerializedName("description") val description: Description,
    @SerializedName("tags") val tags: List<Tag>,
    @SerializedName("opening_hours") val opening_hours: Opening_hours
)

data class EventsData(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: Name,
    @SerializedName("source_type") val source_type: Source_type,
    @SerializedName("info_url") val info_url: String,
    @SerializedName("modified_at") val modified_at: String,
    @SerializedName("location") val location: Location,
    @SerializedName("description") val description: Description,
    @SerializedName("tags") val tags: List<Tag>,
    @SerializedName("event_dates") val event_dates: Event_dates
)

data class SingleHelsinkiActivity(
    @SerializedName("id") val id: String?,
    @SerializedName("name") val name: Name,
    @SerializedName("source_type") val source_type: Source_type,
    @SerializedName("info_url") val info_url: String,
    @SerializedName("modified_at") val modified_at: String,
    @SerializedName("location") val location: Location,
    @SerializedName("description") val description: Description,
    @SerializedName("tags") val tags: List<Tag>,
    @SerializedName("where_when_duration") val where_when_duration: Where_when_duration
)

data class SingleHelsinkiPlace(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: Name,
    @SerializedName("source_type") val source_type: Source_type,
    @SerializedName("info_url") val info_url: String,
    @SerializedName("modified_at") val modified_at: String,
    @SerializedName("location") val location: Location,
    @SerializedName("description") val description: Description,
    @SerializedName("tags") val tags: List<Tag>,
    @SerializedName("opening_hours") val opening_hours: Opening_hours
)

data class SingleHelsinkiEvent(
    @SerializedName("id") val id : String,
    @SerializedName("name") val name : Name,
    @SerializedName("source_type") val source_type : Source_type,
    @SerializedName("info_url") val info_url : String,
    @SerializedName("modified_at") val modified_at : String,
    @SerializedName("location") val location : Location,
    @SerializedName("description") val description : Description,
    @SerializedName("tags") val tags : List<Tag>,
    @SerializedName("event_dates") val event_dates : Event_dates
)

@Parcelize
data class Hours(
    @SerializedName("weekday_id") val weekday_id: Int,
    @SerializedName("opens") val opens: String?,
    @SerializedName("closes") val closes: String?,
    @SerializedName("open24h") val open24h: Boolean?
) : Parcelable

@Parcelize
data class Opening_hours(
    @SerializedName("hours") val hours: List<Hours>?,
    @SerializedName("openinghours_exception") val openinghours_exception: String
) : Parcelable

data class Address(
    @SerializedName("street_address") val street_address: String,
    @SerializedName("postal_code") val postal_code: String,
    @SerializedName("locality") val locality: String
)

data class Description(
    @SerializedName("intro") val intro: String,
    @SerializedName("body") val body: String,
    @SerializedName("images") val images: List<Images>
)

@Parcelize
data class Images(
    @SerializedName("url") val url: String,
    @SerializedName("copyright_holder") val copyright_holder: String,
    @SerializedName("license_type") val license_type: License_type,
    @SerializedName("media_id") val media_id: String
) : Parcelable

@Parcelize
data class License_type(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String
) : Parcelable

data class Location(
    @SerializedName("lat") val lat: Double,
    @SerializedName("lon") val lon: Double,
    @SerializedName("address") val address: Address
)

data class Meta(
    @SerializedName("count") val count: Int
)

data class Name(
    @SerializedName("fi") val fi: String,
    @SerializedName("en") val en: String,
    @SerializedName("sv") val sv: String,
    @SerializedName("zh") val zh: String
)

data class Source_type(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String
)

@Parcelize
data class Tag(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String
) : Parcelable

@Parcelize
data class Where_when_duration(
    @SerializedName("where_and_when") val where_and_when: String,
    @SerializedName("duration") val duration: String
) : Parcelable

@Parcelize
data class Event_dates(
    @SerializedName("starting_day") val starting_day: String?,
    @SerializedName("ending_day") val ending_day: String?,
    @SerializedName("additional_description") val additional_description: String?
) : Parcelable