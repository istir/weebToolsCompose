package com.istir.weebtoolscompose

import android.net.Uri

class Manga(
    public var name: String,
    public var uri: Uri,
    public var currentPosition: Int,
    public var pages: Int,
    public var deleted: Boolean
) {
//TODO
}