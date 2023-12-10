package com.wuc.router.annotation

/**
 * @author     wuchao
 * @date       2023/12/10 23:18
 * @description
 */



// 说明当前注解可以修饰的元素，此处表示可以用于标记在类上面
@Target(AnnotationTarget.CLASS)
//说明当前注解可以被保留的时间
@Retention(AnnotationRetention.BINARY)
annotation class Destination(
    /**
     * 当前页面的URL，不能为空
     */
    val url: String,
    /**
     * 对于当前页面的中文描述, 例如 "个人主页"
     */
    val description: String
)
