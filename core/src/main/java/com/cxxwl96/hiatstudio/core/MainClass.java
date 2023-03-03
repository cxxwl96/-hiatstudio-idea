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

package com.cxxwl96.hiatstudio.core;

import com.cxxwl96.hiatstudio.core.validate.annotations.BasicParam;
import com.cxxwl96.hiatstudio.core.validate.annotations.ListParams;
import com.cxxwl96.hiatstudio.core.validate.annotations.ParamValidator;
import com.cxxwl96.hiatstudio.core.validate.ValidationBuilder;
import com.cxxwl96.hiatstudio.core.validate.ValidationMetadata;
import com.cxxwl96.hiatstudio.core.validate.ValidationResult;
import com.cxxwl96.hiatstudio.core.validate.handler.BasicParamHandler;
import com.cxxwl96.hiatstudio.core.validate.handler.BeanParamsHandler;
import com.cxxwl96.hiatstudio.core.validate.handler.ListParamsHandler;
import com.cxxwl96.hiatstudio.core.validate.handler.ParamValidatorHandler;
import com.cxxwl96.hiatstudio.core.validate.handler.ReturnDataHandler;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ReflectUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * MainClass
 *
 * @author cxxwl96
 * @since 2023/2/27 20:22
 */
@Slf4j
public class MainClass {
    public static void main(String[] args) {
        // 需要的参数
        final Method runMethod = ReflectUtil.getMethodByName(MainClass.class, "myRunMethod");
        final List<String> parameters = CollUtil.newArrayList("cyk", "18", "true");
        final ArrayList<String> returnData = new ArrayList<>();

        // 参数校验
        final ValidationResult result = ValidationBuilder.builder(
            new ValidationMetadata(runMethod, parameters, returnData))
            .addMethodValidator(new ParamValidatorHandler())
            .addArgumentValidator(new BasicParamHandler())
            .addArgumentValidator(new BeanParamsHandler())
            .addArgumentValidator(new ListParamsHandler())
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
            ReflectUtil.invoke(new MainClass(), runMethod, result.getParamValues());
        } else {
            System.err.println(result.getErrorMessage());
        }

    }

    @ParamValidator(size = 3)
    private void myRunMethod(@BasicParam(index = 1) @Min(10) @Max(20) int age, @BasicParam(index = 2) boolean married,
        @BasicParam(index = 0) @NotBlank String name, @ListParams List<String> params) {
        log.info("name: {}, age: {}, married:{}", name, age, married);
    }
}
