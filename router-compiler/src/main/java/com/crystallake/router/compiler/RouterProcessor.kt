package com.crystallake.router.compiler

import com.crystallake.router.annotation.Route
import com.crystallake.router.annotation.RouteMeta
import com.crystallake.router.annotation.RouteType
import com.crystallake.router.compiler.utils.Consts
import com.crystallake.router.compiler.utils.Consts.ACTIVITY
import com.crystallake.router.compiler.utils.Consts.ANNOTATION_TYPE_ROUTE
import com.crystallake.router.compiler.utils.Consts.IROUTE_GROUP
import com.crystallake.router.compiler.utils.Consts.ITROUTE_ROOT
import com.crystallake.router.compiler.utils.Consts.METHOD_LOAD_INTO
import com.crystallake.router.compiler.utils.Consts.NAME_OF_GROUP
import com.crystallake.router.compiler.utils.Consts.PACKAGE_OF_GENERATE_FILE
import com.google.auto.service.AutoService
import com.squareup.javapoet.*
import java.util.*
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.annotation.processing.SupportedAnnotationTypes
import javax.lang.model.element.Element
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.lang.model.type.TypeMirror


@AutoService(Processor::class)
@SupportedAnnotationTypes(ANNOTATION_TYPE_ROUTE)
class RouterProcessor : BaseProcessor() {

    private var iProvider: TypeMirror? = null

    private val rootMap = mutableMapOf<String, String>()

    private val groupMap by lazy {
        mutableMapOf<String, MutableSet<RouteMeta>>()
    }

    @Synchronized
    override fun init(processingEnv: ProcessingEnvironment?) {
        super.init(processingEnv)

    }

    override fun process(
        annotations: MutableSet<out TypeElement>?,
        roundEnv: RoundEnvironment?
    ): Boolean {
        if (!annotations.isNullOrEmpty()) {
            val routeElements: Set<Element?>? =
                roundEnv?.getElementsAnnotatedWith(Route::class.java)
            try {
                parseRoutes(routeElements)
            } catch (e: Exception) {

            }
            return true

        }
        return false
    }

    private fun parseRoutes(routeElements: Set<Element?>?) {
        if (!routeElements.isNullOrEmpty()) {

            rootMap.clear()

            val type_Activity = elementUtils?.getTypeElement(ACTIVITY)?.asType()

            routeElements.forEach {
                it?.let { element ->
                    val tm = element.asType()
                    val route = element.getAnnotation(Route::class.java)
                    var routeMeta: RouteMeta?
                    if (types?.isSubtype(tm, type_Activity) == true) {
                        routeMeta = RouteMeta(route, element, RouteType.ACTIVITY, null)
                        categories(routeMeta)
                    }
                }
            }
            buildGroupRouter(routeElements)

            buildRootRouter(routeElements)


        }
    }

    private fun setRootMap(entrySet: MutableMap<String, MutableSet<RouteMeta>>) {
        entrySet.entries.forEach { entry ->
            val groupName = entry.key
            val groupFileName = Consts.NAME_OF_GROUP + groupName

            rootMap[groupName] = groupFileName
        }
    }

    private fun buildGroupRouter(routeElements: Set<Element?>?) {

        val type_IRouteGroup = elementUtils?.getTypeElement(IROUTE_GROUP)

        /**
         * interface IRouteGroup {
        fun loadInto(atlas: MutableMap<String?, RouteMeta?>?)
        }
         */

        //入参
        val inputMapTypeOfGroup = ParameterizedTypeName.get(
            ClassName.get(MutableMap::class.java),
            ClassName.get(String::class.java),
            ClassName.get(RouteMeta::class.java)
        )

        /**
         * 参数名称
         */
        val groupParamSpec = ParameterSpec.builder(inputMapTypeOfGroup, "atlas").build()


        /**
         * 方法
         */
        groupMap.entries.forEach { entry ->
            /**
             * 方法
             */
            val loadIntoMethodOfGroupBuilder = MethodSpec.methodBuilder(METHOD_LOAD_INTO)
                .addAnnotation(Override::class.java)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(groupParamSpec)

            val groupData = entry.value
            groupData.forEach { routeMeta ->
                val className = ClassName.get(routeMeta.rawType as TypeElement)

                loadIntoMethodOfGroupBuilder.addStatement(
                    "atlas.put(\$S,\$T.build(\$T.${routeMeta.type}, \$T.class, \$S, \$S, null,${routeMeta.priority},${routeMeta.extra}))",
                    routeMeta.path,
                    ClassName.get(RouteMeta::class.java),
                    ClassName.get(RouteType::class.java),
                    className,
                    routeMeta.path.lowercase(),
                    routeMeta.group.lowercase()
                )

            }

            val groupFileName = NAME_OF_GROUP + moduleName
            JavaFile.builder(
                PACKAGE_OF_GENERATE_FILE,
                TypeSpec.classBuilder(groupFileName)
                    .addModifiers(Modifier.PUBLIC)
                    .addSuperinterface(ClassName.get(type_IRouteGroup))
                    .addMethod(loadIntoMethodOfGroupBuilder.build())
                    .build()
            ).build().writeTo(mFiler)

        }

    }

    private fun buildRootRouter(routeElements: Set<Element?>?) {
        if (!routeElements.isNullOrEmpty()) {
            rootMap.clear()
            val type_Activity = elementUtils?.getTypeElement(ACTIVITY)?.asType()
            val type_IRouteRoot = elementUtils?.getTypeElement(ITROUTE_ROOT)
            val type_IRouteGroup = elementUtils?.getTypeElement(IROUTE_GROUP)

            /**
             * ```Map<String, Class<? extends IRouteGroup>>```
             */

            val inputMapTypeOfRoot = ParameterizedTypeName.get(
                ClassName.get(MutableMap::class.java),
                ClassName.get(String::class.java),
                ParameterizedTypeName.get(
                    ClassName.get(Class::class.java),
                    WildcardTypeName.subtypeOf(ClassName.get(type_IRouteGroup))
                )
            )

            /**
             * 参数名称
             */
            val rootParamSpec = ParameterSpec.builder(inputMapTypeOfRoot, "routes").build()

            /**
             * 方法名称
             */
            val loadIntoMethodOfRootBuilder = MethodSpec.methodBuilder(METHOD_LOAD_INTO)
                .addAnnotation(Override::class.java)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(rootParamSpec)

            setRootMap(groupMap)

            if (rootMap.isNotEmpty()) {
                rootMap.forEach { entry ->
                    loadIntoMethodOfRootBuilder.addStatement(
                        "routes.put(\$S,\$T.class)",
                        entry.key,
                        ClassName.get(Consts.PACKAGE_OF_GENERATE_FILE, entry.value)
                    )
                }
            }

            val className = Consts.NAME_OF_ROOT + moduleName
            JavaFile.builder(
                PACKAGE_OF_GENERATE_FILE,
                TypeSpec.classBuilder(className)
                    .addSuperinterface(ClassName.get(type_IRouteRoot))
                    .addModifiers(Modifier.PUBLIC)
                    .addMethod(loadIntoMethodOfRootBuilder.build())
                    .build()
            ).build().writeTo(mFiler)

        }
    }


//    override fun getSupportedAnnotationTypes(): MutableSet<String> {
//        return mutableSetOf(Route::class.java.canonicalName)
//    }


    private fun categories(routeMeta: RouteMeta) {
        if (routeVerify(routeMeta)) {
            val routeMetas = groupMap[routeMeta.group]
            if (routeMetas.isNullOrEmpty()) {
                val routeMetaSet: MutableSet<RouteMeta> = TreeSet<RouteMeta>(
                    Comparator<RouteMeta> { r1, r2 ->
                        try {
                            return@Comparator r1.path?.compareTo(r2.path ?: "") ?: 0
                        } catch (npe: NullPointerException) {
                            return@Comparator 0
                        }
                    })

                routeMetaSet.add(routeMeta)
                if (!routeMeta.group.isNullOrEmpty()) {
                    groupMap[routeMeta.group!!] = routeMetaSet
                }

            } else {
                routeMetas.add(routeMeta)
            }
        }
    }

    private fun routeVerify(meta: RouteMeta): Boolean {
        val path = meta.path
        if (path.isNullOrEmpty() || !path.startsWith("/")) {
            return false
        }

        if (meta.group.isNullOrEmpty()) {
            try {
                val defaultGroup = path.substring(1, path.indexOf("/", 1))
                if (defaultGroup.isNullOrEmpty()) {
                    return false
                }
                meta.group = defaultGroup
                return true
            } catch (e: Exception) {
                return false
            }
        }
        return true
    }
}