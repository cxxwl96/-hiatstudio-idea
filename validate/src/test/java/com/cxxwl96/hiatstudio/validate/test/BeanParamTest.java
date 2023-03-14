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
import com.cxxwl96.hiatstudio.validate.annotations.BeanParam;
import com.cxxwl96.hiatstudio.validate.res.MyBeanParam;

import org.junit.Test;

import java.util.List;

import cn.hutool.core.collection.CollUtil;

/**
 * BeanParamTest
 *
 * @author cxxwl96
 * @since 2023/3/14 19:00
 */
public class BeanParamTest {
    @Test
    public void test() {
        final List<String> paramValues = CollUtil.newArrayList("cyk", "18", "true", "39793666111", "[\"贵州\",\"广东\"]",
            "{\"key1\":\"value1\",\"key2\":[\"value2\",\"value3\"]}");
        MethodRunner.run(this.getClass(), "runMethod", paramValues);
    }

    private void runMethod(
        // 接收JavaBean
        @BeanParam(size = 6) MyBeanParam beanParam) {

    }
}
