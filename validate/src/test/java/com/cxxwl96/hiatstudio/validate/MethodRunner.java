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

package com.cxxwl96.hiatstudio.validate;

import com.cxxwl96.hiatstudio.validate.handler.BasicParamHandler;
import com.cxxwl96.hiatstudio.validate.handler.BeanParamHandler;
import com.cxxwl96.hiatstudio.validate.handler.JsonParamHandler;
import com.cxxwl96.hiatstudio.validate.handler.ListParamHandler;
import com.cxxwl96.hiatstudio.validate.handler.ParamValidatorHandler;
import com.cxxwl96.hiatstudio.validate.handler.ReturnDataHandler;
import com.cxxwl96.hiatstudio.validate.metadata.ValidationMetadata;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import cn.hutool.core.util.ReflectUtil;
import lombok.SneakyThrows;

public class MethodRunner {
    @SneakyThrows
    public static void run(Class<?> clazz, String methodName, List<String> paramValues) {
        final Method runMethod = ReflectUtil.getMethodByName(clazz, methodName); // 需要执行的方法
        final List<String> returnData = new ArrayList<>(); // 返回数据
        // 参数校验
        final ValidationResult result = ValidationBuilder.builder(
            new ValidationMetadata(runMethod, paramValues, returnData))
            .addMethodValidator(new ParamValidatorHandler())
            .addArgumentValidator(new BasicParamHandler())
            .addArgumentValidator(new JsonParamHandler())
            .addArgumentValidator(new BeanParamHandler())
            .addArgumentValidator(new ListParamHandler())
            .addArgumentValidator(new ReturnDataHandler())
            .build()
            .validate();
        if (result.isSuccess()) {
            System.out.println("校验通过");
            final String values = Arrays.stream(result.getParamValues())
                .map(item -> item == null ? "null" : item.toString())
                .collect(Collectors.joining(","));
            System.out.println("方法参数：" + values);
            // 调用方法
            ReflectUtil.invoke(clazz.newInstance(), runMethod, result.getParamValues());
        } else {
            System.err.println("校验失败：" + result.getErrorMessage());
        }
    }
}