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
import com.cxxwl96.hiatstudio.validate.annotations.JsonParam;
import com.cxxwl96.hiatstudio.validate.annotations.ParamValidator;
import com.cxxwl96.hiatstudio.validate.res.JsonObject;

import org.junit.Test;

import java.util.List;

import javax.validation.constraints.Size;

import cn.hutool.core.collection.CollUtil;

/**
 * JsonParam
 *
 * @author cxxwl96
 * @since 2023/3/14 18:30
 */
public class JsonParamTest {
    @Test
    public void test() {
        final List<String> paramValues = CollUtil.newArrayList("[\"贵州\",\"广东\"]",
            "{\"key1\":\"value1\",\"key2\":[\"value2\",\"value3\"]}");
        MethodRunner.run(this.getClass(), "runMethod", paramValues);
    }

    @ParamValidator(size = 2)
    private void runMethod(
        // 接收JSON字符串对应的类型
        @JsonParam(index = 0) @Size(max = 2) List<String> addresses,
        // 接收JSON字符串对应的类型
        @JsonParam(index = 1) JsonObject jsonObj,
        // 接收JSON字符串对应的类型，通过jsonpath接收
        @JsonParam(index = 1, jsonPath = "$.key2") List<String> key2) {

    }
}
