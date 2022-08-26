package com.crystallake.router.facade

import android.content.Context
import android.net.Uri
import android.os.Bundle
import com.crystallake.router.Router
import com.crystallake.router.annotation.RouteMeta
import com.crystallake.router.facade.callback.NavigationCallback

class Postcard : RouteMeta {

    var bundle: Bundle? = null
    var context: Context? = null
    var uri: Uri? = null
    var flags: Int = 0
    var action: String? = null

    constructor() : this(null, null)
    constructor(path: String?, group: String?) : this(path, group, null, null)

    constructor(path: String?, group: String?, uri: Uri?, bundle: Bundle?) : super() {
        this.path = path
        this.group = group
        this.uri = uri
        this.bundle = bundle ?: Bundle()
    }


    fun navigation(mContext: Context? = null, callback: NavigationCallback? = null): Any? {
        return Router.getInstance().navigation(mContext, this, -1, callback)
    }


}