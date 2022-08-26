package com.crystallake.router.compiler.utils

object Consts {


    const val ROUTE_PACKAGE = "com.crystallake.router.annotation"

    const val ANNOTATION_TYPE_ROUTE = "$ROUTE_PACKAGE.Route"

    const val ACTIVITY = "android.app.Activity"

    const val IROUTE_GROUP = "com.crystallake.router.template.IRouteGroup"
    const val ITROUTE_ROOT = "com.crystallake.router.template.IRouteRoot"

    const val METHOD_LOAD_INTO = "loadInto"

    const val PACKAGE_OF_GENERATE_FILE = "com.crystallake.router.routemap"

    const val DOT = "."
    const val SDK_NAME = "Router"
    const val SEPARATOR = "$$"
    const val SUFFIX_ROOT = "Root"
    const val SUFFIX_GROUP = "Group"

    const val KEY_MODULE_NAME = "ROUTER_MODULE_NAME"

    const val NAME_OF_ROOT = SDK_NAME + SEPARATOR + SUFFIX_ROOT + SEPARATOR

    const val NAME_OF_GROUP = SDK_NAME + SEPARATOR + SUFFIX_GROUP + SEPARATOR
}