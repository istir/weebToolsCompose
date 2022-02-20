package com.istir.weebtoolscompose

import android.net.Uri

class Manga(
    public var id: Int?,
    public var name: String,
    public var uri: Uri,
    public var currentPosition: Int,
    public var pages: Int,
    public var deleted: Boolean,
    public var modifiedAt: Long
) {
    override fun toString(): String {
        return "id: $id, name: $name, uri: $uri, currentProgress: $currentPosition, pages: $pages, isDeleted: $deleted, modifiedAt: $modifiedAt"
    }
}