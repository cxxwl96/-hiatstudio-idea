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

package com.cxxwl96.hiatstudio;

import com.cxxwl96.hiatstudio.validate.CustomValidatorHandler;
import com.cxxwl96.hiatstudio.validate.ValidationBuilder;
import com.cxxwl96.hiatstudio.validate.ValidationChain;
import com.cxxwl96.hiatstudio.validate.ValidationMetadata;
import com.cxxwl96.hiatstudio.validate.ValidationResult;
import com.cxxwl96.hiatstudio.validate.annotations.BasicParam;
import com.cxxwl96.hiatstudio.validate.annotations.BeanParam;
import com.cxxwl96.hiatstudio.validate.annotations.IgnoreField;
import com.cxxwl96.hiatstudio.validate.annotations.JsonParam;
import com.cxxwl96.hiatstudio.validate.annotations.ListParam;
import com.cxxwl96.hiatstudio.validate.annotations.ParamValidator;
import com.cxxwl96.hiatstudio.validate.annotations.ReturnData;
import com.cxxwl96.hiatstudio.validate.custom.In;
import com.cxxwl96.hiatstudio.validate.handler.BasicParamHandler;
import com.cxxwl96.hiatstudio.validate.handler.BeanParamsHandler;
import com.cxxwl96.hiatstudio.validate.handler.JsonParamHandler;
import com.cxxwl96.hiatstudio.validate.handler.ListParamsHandler;
import com.cxxwl96.hiatstudio.validate.handler.ParamValidatorHandler;
import com.cxxwl96.hiatstudio.validate.handler.ReturnDataHandler;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ReflectUtil;
import lombok.Data;
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
        final List<String> parameters = CollUtil.newArrayList("cyk", "18", "true", "39793666111", "[\"贵州\",\"广东\"]",
            "{\"key1\":\"value1\",\"key2\":[\"value2\",\"value3\"]}");
        final ArrayList<String> returnData = new ArrayList<>();

        // 参数校验
        final ValidationResult result = ValidationBuilder.builder(
            new ValidationMetadata(runMethod, parameters, returnData))
            .addMethodValidator(new ParamValidatorHandler())
            .addArgumentValidator(new BasicParamHandler())
            .addArgumentValidator(new JsonParamHandler())
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

    // size: 长度校验
    // customValidatorHandler: 自定义校验处理器
    @ParamValidator(size = 6, customValidatorHandler = MyValidatorHandler.class)
    private void myRunMethod(
        // 接收字符串
        @BasicParam(index = 0) @NotBlank String name,
        // 接收整型、自定义注解@In校验
        @BasicParam(index = 1) @Min(10) @Max(20) @In(values = {11, 12, 13, 18}) int age,
        // 接收boolean
        @BasicParam(index = 2) boolean married,
        // 接收字符串、正则校验
        @BasicParam(index = 3) @Pattern(regexp = "[1-9][0-9]{4,10}") String qq,
        // 接收JSON字符串对应的类型
        @JsonParam(index = 4) List<String> addresses,
        // 接收JSON字符串对应的类型
        @JsonParam(index = 5) JsonObject jsonObj,

        // 接收JavaBean
        @BeanParam BeanParams beanParams,

        // 接收List<String>
        @ListParam List<String> listParams,

        // 返回数据
        @ReturnData List<String> returnData) {

        // 方法体..........
        System.out.println();
    }

    @Data
    public static class BeanParams {
        @NotBlank
        private String name;

        @Min(10)
        @Max(20)
        private int age;

        private boolean married;

        @Pattern(regexp = "[1-9][0-9]{4,10}")
        private String qq;

        @IgnoreField
        private String ignoreField;

        // 接收JSON字符串对应的类型
        @JsonParam(index = 4)
        private List<String> addresses;

        // 接收JSON字符串对应的类型
        @JsonParam(index = 5)
        private JsonObject jsonObj;

        // setter方式注入
        private void setName(String name) {
            this.name = name;
            // 这里可以做其他操作
        }
    }

    @Data
    public static class JsonObject {
        private String key1;

        private List<String> key2;
    }

    public static class MyValidatorHandler implements CustomValidatorHandler {
        /**
         * 自定义校验处理
         *
         * @param params 入参参数
         * @param chain 校验链
         * @throws IllegalArgumentException 参数校验异常
         */
        @Override
        public void handle(List<String> params, ValidationChain chain) throws IllegalArgumentException {
            chain.intercept();
            if (params.size() != 6) {
                throw new IllegalArgumentException("Invalid params.");
            }
        }
    }
}
