package com.istir.weebtoolscompose

import android.graphics.Bitmap
import android.net.Uri

class Manga(
    public var id: Int?,
    public var name: String,
    public var uri: Uri,
    public var currentPosition: Int,
    public var pages: Int,
    public var deleted: Boolean,
    public var modifiedAt: Long,
    public var folderUri: Uri,
    public var folderName: String,
    public var image: String
) {
    override fun toString(): String {
        return "id: $id, name: $name, uri: $uri, currentProgress: $currentPosition, pages: $pages, isDeleted: $deleted, modifiedAt: $modifiedAt, folderName: $folderName, folderUri: $folderUri, image: $image"
    }
}