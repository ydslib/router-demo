package com.crystallake.router.template

import com.crystallake.router.annotation.RouteMeta

interface IRouteGroup {
    fun loadInto(atlas: MutableMap<String, RouteMeta>)
}