package com.wuc.router.processor

import com.google.auto.service.AutoService
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.wuc.router.annotation.Destination
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.Writer
import java.util.Collections
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement
import javax.tools.JavaFileObject

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

        val rootDir = processingEnv.options["root_project_dir"]
        println("$TAG >>> rootDir = $rootDir")
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

        val destinationJsonArray: JsonArray = JsonArray()

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

            val item = JsonObject()
            item.addProperty("url", url)
            item.addProperty("description", description)
            item.addProperty("realPath", realPath)
            destinationJsonArray.add(item)
        }
        builder.append("        return mapping;\n")
        builder.append("    }\n")
        builder.append("}\n")

        val mappingFullClassName = "com.wuc.router.mapping.$className"

        println("$TAG >>> mappingFullClassName = $mappingFullClassName")
        println("$TAG >>> class content = \n$builder")

        // 写入自动生成的类到本地文件中
        try {
            val source: JavaFileObject = processingEnv.filer // 返回实现Filer接口的对象，用于创建文件、类和辅助文件
                .createSourceFile(mappingFullClassName) // 创建源文件
            val writer: Writer = source.openWriter()
            writer.write(builder.toString())
            writer.flush()
            writer.close()
        } catch (ex: Exception) {
            throw RuntimeException("Error while create file", ex)
        }

        // 写入JSON到本地文件中

        // 检测父目录是否存在
        if (rootDir.isNullOrEmpty()) {
            throw RuntimeException("root_project_dir not exist!")
        }
        val rootDirFile = File(rootDir)
        if (!rootDirFile.exists()) {
            throw RuntimeException("rootDirFile not exist!")
        }
        // 创建 router_mapping 子目录
        val routerFileDir = File(rootDirFile, "router_mapping")
        if (!routerFileDir.exists()) {
            routerFileDir.mkdir()
        }
        val mappingFile = File(routerFileDir, "mapping_" + System.currentTimeMillis() + ".json")
        // 写入json内容
        try {
            val out: BufferedWriter = BufferedWriter(FileWriter(mappingFile))
            val jsonStr: String = destinationJsonArray.toString()
            out.write(jsonStr)
            out.flush()
            out.close()
        } catch (throwable: Throwable) {
            throw RuntimeException("Error while writing json", throwable)
        }

        println("$TAG >>> process finish.")
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