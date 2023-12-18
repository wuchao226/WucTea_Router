package com.wuc.router.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project

class RouterPlugin implements Plugin<Project> {

  // 实现 apply 方法，注入插件的逻辑
  @Override
  void apply(Project project) {
    // 1. 自动帮助用户传递路径参数到注解处理器中
    if (project.extensions.findByName("kapt") != null) {
      project.extensions.findByName("kapt").arguments {
        arg("root_project_dir", project.rootProject.projectDir.absolutePath)
      }
    }
    // 2. 实现旧的构建产物的自动清理
    project.clean.doFirst {
      // 删除 上一次构建生成的 router_mapping目录
      File routerMappingDir = new File(project.rootProject.projectDir, "router_mapping")
      if (routerMappingDir.exists()) {
        routerMappingDir.deleteDir()
      }
    }
    println("I am from RouterPlugin, apply from ${project.name}")
    // 2、注册 RouterExtension
    project.getExtensions().create("router", RouterExtension)

    project.afterEvaluate {
      // 4、获取 RouterExtension
      RouterExtension extension = project["router"]
      println("用户设置的wiki路径为：${extension.wikiDir}")
    }
  }
}