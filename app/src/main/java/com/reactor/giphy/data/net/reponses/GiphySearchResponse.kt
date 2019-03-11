package com.reactor.giphy.data.net.reponses

import com.google.gson.annotations.SerializedName

data class GifPreview(@SerializedName("webp") val url: String)

data class Images(@SerializedName("fixed_height_downsampled") val preview: GifPreview)

data class Data(val images: Images)

data class Pagination(
    @SerializedName("total_count")
    val total: Int,
    val count: Int,
    val offset: Int
)

data class SearchResponse(
    val data: List<Data>,
    val pagination: Pagination
)
