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
 * @description  []注解处理器 APT Processor] https://www.cnblogs.com/baiqiantao/p/10250713.html
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
        // processingOver() 如果循环处理完成返回true，否则返回false
        // 避免多次调用 process
        if (roundEnvironment.processingOver()) {
            return false
        }
        println("$TAG >>> process start ...")
        // 获取所有标记了 @Destination 注解的 类的信息
        val allDestinationElements: Set<Element> = roundEnvironment.getElementsAnnotatedWith(Destination::class.java)
        println("$TAG >>> all Destination elements count = ${allDestinationElements.size}")
        // 当未收集到 @Destination 注解的时候，跳过后续流程
        if (allDestinationElements.isEmpty()) {
            return false
        }
        // 将要自动生成的类的类名
        val className = "RouterMapping_${System.currentTimeMillis()}"
        val builder = StringBuilder()
        builder.append("package com.wuc.router.mapping;\n\n")
        builder.append("import java.util.HashMap;\n")
        builder.append("import java.util.Map;\n\n")
        builder.append("public class ").append(className).append(" {\n\n")
        builder.append("    public static Map<String, String> get() {\n\n")
        builder.append("        Map<String, String> mapping = new HashMap<>();\n\n")

        // 遍历所有 @Destination 注解信息，挨个获取详细信息
        for (element in allDestinationElements) {
            val typeElement = element as TypeElement
            // 尝试在当前类上，获取 @Destination 的信息
            val destination: Destination = typeElement.getAnnotation(Destination::class.java) ?: continue
            val url = destination.url
            val description = destination.description
            val realPath = typeElement.qualifiedName.toString()
            println("$TAG >>> url = $url")
            println("$TAG >>> description = $description")
            println("$TAG >>> realPath = $realPath")

            builder.append("        ")
                .append("mapping.put(")
                .append("\"" + url + "\"")
                .append(", ")
                .append("\"" + realPath + "\"")
                .append(");\n")
        }
        builder.append("        return mapping;\n")
        builder.append("    }\n")
        builder.append("}\n")

        println("$TAG >>> class content = \n$builder")
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