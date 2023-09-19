package com.example.stack.data.dataclass
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ViewCount(
    @Json(name = "text") val text: String?,
    @Json(name = "short") val short: String?
)
@JsonClass(generateAdapter = true)
data class Thumbnails(
    @Json(name = "url") val url: String?,
    @Json(name = "width") val width: Int?,
    @Json(name = "height") val height: Int?
)

@JsonClass(generateAdapter = true)
data class RichThumbnail(
    @Json(name = "url") val url: String?,
    @Json(name = "width") val width: Int?,
    @Json(name = "height") val height: Int?
)

@JsonClass(generateAdapter = true)
data class Channel(
    @Json(name = "name") val name: String?,
    @Json(name = "id") val id: String?,
    @Json(name = "thumbnails") val thumbnails: List<Thumbnails>?,
    @Json(name = "link") val link: String?
)
@JsonClass(generateAdapter = true)
data class Accessibility(
    @Json(name = "title") val title: String?,
    @Json(name = "duration") val duration: String?
)

@JsonClass(generateAdapter = true)
data class VideoItem(
    @Json(name = "type") val type: String?,
    @Json(name = "id") val id: String?,
    @Json(name = "title") val title: String?,
    @Json(name = "publishedTime") val publishedTime: String?,
    @Json(name = "duration") val duration: String?,
    @Json(name = "viewCount") val viewCount: ViewCount?,
    @Json(name = "thumbnails") val thumbnails: List<Thumbnails>?,
    @Json(name = "richThumbnail") val richThumbnail: RichThumbnail?,
    @Json(name = "descriptionSnippet") val descriptionSnippet: List<DescriptionSnippet>?,
    @Json(name = "channel") val channel: Channel?,
    @Json(name = "accessibility") val accessibility: Accessibility?,
    @Json(name = "link") val link: String?,
    @Json(name = "shelfTitle") val shelfTitle: String? = null,
)

@JsonClass(generateAdapter = true)
data class DescriptionSnippet(
    @Json(name = "text") val text: String?
)

fun VideoItem.toExerciseYoutube(exerciseId: String) = ExerciseYoutube(
    exerciseId = exerciseId,
    youtubeId = id ?: "",
    youtubeTitle = title ?: "",
    views = viewCount?.text ?: "",
    publishedTime = publishedTime ?: "",
    thumbnailUrl = thumbnails?.firstOrNull()?.url ?: "",
    transcript = null,
    instruction = null
)


