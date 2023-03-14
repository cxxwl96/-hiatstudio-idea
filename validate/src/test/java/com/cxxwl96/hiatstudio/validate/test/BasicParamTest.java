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

package com.cxxwl96.hiatstudio.validate.test;

import com.cxxwl96.hiatstudio.validate.MethodRunner;
import com.cxxwl96.hiatstudio.validate.annotations.BasicParam;
import com.cxxwl96.hiatstudio.validate.annotations.ParamValidator;
import com.cxxwl96.hiatstudio.validate.res.In;

import org.junit.Test;

import java.util.List;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import cn.hutool.core.collection.CollUtil;

/**
 * BasicParamTest
 *
 * @author cxxwl96
 * @since 2023/3/14 18:21
 */
public class BasicParamTest {
    @Test
    public void test() {
        final List<String> paramValues = CollUtil.newArrayList("cyk", "18", "true", "39793666111");
        MethodRunner.run(this.getClass(), "runMethod", paramValues);
    }

    @ParamValidator(size = 4)
    private void runMethod(
        // 接收字符串
        @BasicParam(index = 0) @NotBlank String name,
        // 接收整型、自定义注解@In校验
        @BasicParam(index = 1) @Min(10) @Max(20) @In(values = {11, 12, 13, 18}) int age,
        // 接收boolean
        @BasicParam(index = 2) boolean married,
        // 接收字符串、正则校验
        @BasicParam(index = 3) @Pattern(regexp = "[1-9][0-9]{4,10}") String qq) {

    }
}
