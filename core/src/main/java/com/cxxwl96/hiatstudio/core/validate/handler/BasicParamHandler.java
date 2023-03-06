/*
 * Copyright (c) 2021-2023, jad (cxxwl96@sina.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cxxwl96.hiatstudio.core.validate.handler;

import com.alibaba.fastjson.util.TypeUtils;
import com.cxxwl96.hiatstudio.core.validate.ArgumentValidatorHandler;
import com.cxxwl96.hiatstudio.core.validate.ValidationMetadata;
import com.cxxwl96.hiatstudio.core.validate.annotations.BasicParam;
import com.cxxwl96.hiatstudio.core.validate.annotations.ValidatorHandler;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.DynamicType;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import cn.hutool.core.util.ReflectUtil;
import cn.hutool.extra.validation.BeanValidationResult;
import cn.hutool.extra.validation.ValidationUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 处理器：@BasicParam注解校验处理器
 *
 * @author cxxwl96
 * @since 2023/3/3 15:49
 */
@Slf4j
@ValidatorHandler(annotation = BasicParam.class)
public class BasicParamHandler implements ArgumentValidatorHandler {
    /**
     * 参数校验处理
     *
     * @param metadata 校验元数据
     * @param parameter 参数
     * @param index 参数索引
     * @param paramName 参数名
     * @return 校验通过参数的值
     * @throws Exception 参数校验失败异常
     */
    @Override
    public Object handle(ValidationMetadata metadata, Parameter parameter, int index, String paramName)
        throws Exception {
        final List<String> paramValues = metadata.getParamValues(); // 输入的参数值
        final BasicParam basicParam = parameter.getAnnotation(BasicParam.class);
        // 校验参数取值是否越界
        if (basicParam.index() < 0 || basicParam.index() >= paramValues.size()) {
            final String error = String.format(Locale.ROOT,
                "Out of range. There are only %d input parameters, but \"%s\" takes a %d parameter.",
                paramValues.size(), paramName, basicParam.index() + 1);
            throw new IndexOutOfBoundsException(error);
        }
        // 获取输入的字符串参数
        final String paramValueString = paramValues.get(basicParam.index());
        // 参数值类型转换
        Object paramValue;
        try {
            // 复杂的类型转换，将字符串参数转换为对应类型的参数
            paramValue = TypeUtils.cast(paramValueString, parameter.getType(), null);
        } catch (Exception exception) {
            log.error(exception.getMessage(), exception);
            final String error = String.format(Locale.ROOT,
                "The type of parameter \"%s\" does not match the type of the input parameter. An \"%s\" is expected, but \"%s\" is entered.",
                paramName, parameter.getType(), paramValueString);
            throw new ClassCastException(error);
        }
        // 校验@BasicParam修饰的参数
        // 使用字节码增强动态生成bean对象，将@BasicParam修饰的参数及其对应的校验注解动态生成javabean
        // 最后通过validate校验
        DynamicType.Builder<Object> basicDynamicBean = new ByteBuddy().subclass(Object.class).name("BasicValidateBean");
        // 过滤得到除开@BasicParam注解的其他注解
        List<Annotation> validAnno = Arrays.stream(parameter.getAnnotations())
            .filter(annotation -> annotation.annotationType() != BasicParam.class)
            .collect(Collectors.toList());
        // 将被@BasicParam修饰的参数存入动态bean中
        final Object validInstance = basicDynamicBean.defineField(paramName, parameter.getType(), Visibility.PUBLIC)
            .annotateField(validAnno)
            .make()
            .load(ClassLoader.getSystemClassLoader())
            .getLoaded()
            .newInstance();
        // 给validInstance字段赋值
        ReflectUtil.setFieldValue(validInstance, paramName, paramValue);
        // 最终通过validate进行校验
        final BeanValidationResult result = ValidationUtil.warpValidate(validInstance);
        if (!result.isSuccess()) {
            for (BeanValidationResult.ErrorMessage message : result.getErrorMessages()) {
                throw new IllegalArgumentException(message.getPropertyName() + message.getMessage());
            }
        }
        // 校验通过则返回参数值
        return paramValue;
    }
}
