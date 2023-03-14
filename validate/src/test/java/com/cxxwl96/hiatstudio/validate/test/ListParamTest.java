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
import com.cxxwl96.hiatstudio.validate.annotations.ListParam;

import org.junit.Test;

import java.util.List;

import javax.validation.constraints.Size;

import cn.hutool.core.collection.CollUtil;

/**
 * ListParamTest
 *
 * @author cxxwl96
 * @since 2023/3/14 19:10
 */
public class ListParamTest {
    @Test
    public void test() {
        final List<String> paramValues = CollUtil.newArrayList("cyk", "18", "true", "39793666111");
        MethodRunner.run(this.getClass(), "runMethod", paramValues);
    }

    private void runMethod(
        // 接收List<String>
        @ListParam(size = 4) @Size(max = 3) List<String> listParam) {

    }
}
