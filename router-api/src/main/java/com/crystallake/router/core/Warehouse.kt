package com.crystallake.router.core

import com.crystallake.router.annotation.RouteMeta
import com.crystallake.router.template.IRouteGroup

class Warehouse {

    companion object {

        @JvmStatic
        val routes: MutableMap<String, RouteMeta> = mutableMapOf()

        val groupsIndex: MutableMap<String, Class<out IRouteGroup>> = mutableMapOf()

    }
}