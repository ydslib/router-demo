package com.crystallake.router.compiler

import com.crystallake.router.compiler.utils.Consts
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Filer
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

abstract class BaseProcessor : AbstractProcessor() {

    var mFiler: Filer? = null
    var types: Types? = null
    var elementUtils: Elements? = null
    var moduleName: String? = null


    override fun init(processingEnv: ProcessingEnvironment?) {
        super.init(processingEnv)
        mFiler = processingEnv?.filer
        types = processingEnv?.typeUtils
        elementUtils = processingEnv?.elementUtils

        val options = processingEnv?.options

        if (!options.isNullOrEmpty()) {
            moduleName = options[Consts.KEY_MODULE_NAME]
        }

        if (!moduleName.isNullOrEmpty()){
            moduleName = moduleName?.replace("[^0-9a-zA-Z_]+".toRegex(), "")
            println("The user has configuration the module name, it was [$moduleName]")
        }else{
            throw RuntimeException("ARouter::Compiler >>> No module name, for more information, look at gradle log.")
        }
    }

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.latestSupported()
    }

    override fun getSupportedOptions(): MutableSet<String> {
        return mutableSetOf()
    }
}