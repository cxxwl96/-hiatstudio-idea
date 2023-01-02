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

import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.InvocationResult;
import org.apache.maven.shared.invoker.Invoker;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * MavenInvoker
 *
 * @author cxxwl96
 * @since 2023/1/1 15:11
 */
@Slf4j
public class MavenInvoker {
    private final static String LOCAL_REPO = "local/repo";

    public static ClassLoader invoke() {
        return invoke(Thread.currentThread().getContextClassLoader());
    }

    public static ClassLoader invoke(ClassLoader parent) {
        // 执行maven依赖下载
        mavenInvoke();
        // 过滤下载的jar文件
        List<File> jarFiles = FileUtil.loopFiles(new File(LOCAL_REPO))
            .stream()
            .filter(file -> file.getPath().toLowerCase().endsWith(FileUtil.JAR_FILE_EXT))
            .collect(Collectors.toList());
        log.info("Jar file num: {}", jarFiles.size());
        // 加载jar文件到加载器中
        final ArrayList<URL> urls = new ArrayList<>();
        for (File jarFile : jarFiles) {
            URL url = null;
            try {
                url = new URL("jar", "", jarFile.getAbsolutePath());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            urls.add(url);
        }
        if (parent == null) {
            return new URLClassLoader(urls.toArray(new URL[0]));
        } else {
            return new URLClassLoader(urls.toArray(new URL[0]), parent);
        }
    }

    private static void mavenInvoke() throws MavenInvokeException {
        try {
            log.info("Maven invoke...");
            InvocationRequest request = new DefaultInvocationRequest();
            // 设置pom文件路径
            request.setPomFile(new File("pom.xml"));
            // 执行的maven命令
            request.setGoals(Collections.singletonList("compile"));

            Invoker invoker = new DefaultInvoker();
            // maven安装路径
            // invoker.setMavenHome(new File(SystemUtil.get("MAVEN_HOME")));
            invoker.setMavenHome(new File("plugin/apache-maven-3.8.1"));
            // 设置仓库地址、
            File repoDir = new File(LOCAL_REPO);
            if (!repoDir.exists()) {
                repoDir.mkdirs();
            }
            invoker.setLocalRepositoryDirectory(repoDir);
            // 日志处理
            // invoker.setLogger(new PrintStreamLogger(System.err, InvokerLogger.ERROR));
            // 重写maven输出显示信息
            // invoker.setOutputHandler(log::info);

            InvocationResult result = invoker.execute(request);

            // 判断是否执行成功
            if (result.getExitCode() == 0) {
                log.info("Maven invoke success");
                return;
            }
            if (result.getExecutionException() != null) {
                throw new MavenInvokeException(result.getExecutionException());
            }
        } catch (Exception exception) {
            throw new MavenInvokeException(exception);
        }
    }
}
