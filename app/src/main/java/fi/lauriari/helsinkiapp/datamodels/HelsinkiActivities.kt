package fi.lauriari.helsinkiapp.datamodels

import com.google.gson.annotations.SerializedName

class HelsinkiActivities(
    @SerializedName("meta") val meta: Meta,
    @SerializedName("data") val data: List<Data>,
)

data class Data(

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

data class Images(

    @SerializedName("url") val url: String,
    @SerializedName("copyright_holder") val copyright_holder: String,
    @SerializedName("license_type") val license_type: License_type,
    @SerializedName("media_id") val media_id: String
)


data class License_type(

    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String
)

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

data class Tag(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String

)

data class Where_when_duration(

    @SerializedName("where_and_when") val where_and_when: String,
    @SerializedName("duration") val duration: String
)