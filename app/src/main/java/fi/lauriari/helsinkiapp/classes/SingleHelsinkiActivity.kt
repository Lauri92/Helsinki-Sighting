package fi.lauriari.helsinkiapp.classes

import fi.lauriari.helsinkiapp.datamodels.Images
import fi.lauriari.helsinkiapp.datamodels.Tag

data class SingleHelsinkiActivity(
    var id: String?,
    var name: String?,
    var infoUrl: String?,
    var latitude: Double?,
    var longitude: Double?,
    var locality: String?,
    var description: String?,
    var images: List<Images>,
    var tags: List<Tag>
)