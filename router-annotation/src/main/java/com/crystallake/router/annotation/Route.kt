package com.crystallake.router.annotation

@Target(AnnotationTarget.CLASS)
@Retention(value = AnnotationRetention.BINARY)
annotation class Route(val path: String)
