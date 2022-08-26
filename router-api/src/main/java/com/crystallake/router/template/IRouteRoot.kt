package com.crystallake.router.template

interface IRouteRoot {
    fun loadInto(routes: MutableMap<String, Class<out IRouteGroup>>)
}