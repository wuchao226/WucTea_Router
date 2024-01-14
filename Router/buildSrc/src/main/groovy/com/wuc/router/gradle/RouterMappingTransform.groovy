package com.wuc.router.gradle

import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformException
import com.android.build.api.transform.TransformInvocation
import com.android.build.gradle.internal.pipeline.TransformManager

class RouterMappingTransform extends Transform {

  /**
   * 当前 Transform 的名称
   * @return
   */
  @Override
  String getName() {
    return "RouterMappingTransform"
  }

  /**
   * 返回告知编译器，当前Transform需要消费的输入类型
   * 在这里是CLASS类型
   * @return
   */
  @Override
  Set<QualifiedContent.ContentType> getInputTypes() {
    return TransformManager.CONTENT_CLASS
  }

  /**
   * 告知编译器，当前Transform需要收集的范围
   * @return
   */
  @Override
  Set<? super QualifiedContent.Scope> getScopes() {
    return null
  }

  /**
   * 是否支持增量
   * 通常返回False
   * @return
   */
  @Override
  boolean isIncremental() {
    return false
  }

  /**
   * 所有的class收集好以后，会被打包传入此方法
   * @param transformInvocation
   * @throws TransformException
   * @throws InterruptedException
   * @throws IOException
   */
  @Override
  void transform(TransformInvocation transformInvocation)
      throws TransformException, InterruptedException, IOException {
    super.transform(transformInvocation)
  }
}