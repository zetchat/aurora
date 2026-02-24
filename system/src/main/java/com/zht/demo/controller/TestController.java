package com.zht.demo.controller;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.concurrent.Executor;

@RestController
@Slf4j
public class TestController {

    // 注入我们的线程池接口（注意是用父类 Executor 或 ThreadPoolTaskExecutor）
    @Resource(name = "auroraExecutor")
    private Executor auroraExecutor;

    @GetMapping("/test/async")
    public String testAsync() {
        // 模拟放个 TraceId
        MDC.put("traceId", UUID.randomUUID().toString());
        log.info("主线程提交任务...");

        for (int i = 0; i < 5; i++) {
            int finalI = i;
            auroraExecutor.execute(() -> {
                // 如果日志里打印出了 traceId，说明 ContextAwareRunnable 生效了
                log.info("子线程执行任务 [{}] - {}", finalI, Thread.currentThread().getName());
                try { Thread.sleep(1000); } catch (InterruptedException e) {}
            });
        }
        return "提交成功";
    }
}
