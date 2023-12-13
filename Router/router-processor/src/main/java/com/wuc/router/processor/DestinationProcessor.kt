package com.wuc.router.processor

import com.google.auto.service.AutoService
import com.wuc.router.annotation.Destination
import java.util.Collections
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement

/**
 * @author     wuchao
 * @date       2023/12/11 00:45
 * @description
 */
@AutoService(Processor::class)
class DestinationProcessor : AbstractProcessor() {

    companion object {
        private const val TAG = "DestinationProcessor"
    }

    /**
     * 编译器找到我们关心的注解后，会回调这个方法
     * @param set
     * @param roundEnvironment
     * @return
     */
    override fun process(set: MutableSet<out TypeElement>?, roundEnvironment: RoundEnvironment): Boolean {

        println("$TAG >>> process start ...")
        // 获取所有标记了 @Destination 注解的 类的信息
        val allDestinationElements: Set<Element> = roundEnvironment.getElementsAnnotatedWith(Destination::class.java)
        println("$TAG >>> all Destination elements count = ${allDestinationElements.size}")
        // 当未收集到 @Destination 注解的时候，跳过后续流程
        if (allDestinationElements.isEmpty()) {
            return false
        }
        // 遍历所有 @Destination 注解信息，挨个获取详细信息
        for (element in allDestinationElements) {
            val typeElement = element as TypeElement
            // 尝试在当前类上，获取 @Destination 的信息
            val destination: Destination = typeElement.getAnnotation(Destination::class.java) ?: continue
            val url = destination.url
            val description = destination.description
            val realPath = typeElement.qualifiedName.toString()
            println("$TAG >>> url = $url");
            println("$TAG >>> description = $description");
            println("$TAG >>> realPath = $realPath");
        }
        return false
    }

    /**
     * 告诉编译器，当前处理器支持的注解类型
     * @return
     */
    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return Collections.singleton(Destination::class.java.canonicalName)
    }

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.RELEASE_11
    }
}