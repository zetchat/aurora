package com.zht.threadpool.config;

import com.zht.threadpool.core.ContextAwareRunnable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@Configuration
@EnableConfigurationProperties(AuroraThreadPoolProperties.class)
public class AuroraThreadPoolAutoConfiguration {

    @Bean(name = "auroraExecutor")
    @ConditionalOnMissingBean(name = "auroraExecutor") // 允许用户自定义 Bean 覆盖我们的默认配置
    public ThreadPoolTaskExecutor auroraExecutor(AuroraThreadPoolProperties properties) {
        log.info(">>> [Aurora Framework] 开始初始化企业级线程池...");
        log.info("配置参数: 核心线程数={}, 最大线程数={}, 队列容量={}",
                properties.getCorePoolSize(), properties.getMaxPoolSize(), properties.getQueueCapacity());

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        // 1. 基础参数设置
        executor.setCorePoolSize(properties.getCorePoolSize());
        executor.setMaxPoolSize(properties.getMaxPoolSize());
        executor.setQueueCapacity(properties.getQueueCapacity());
        executor.setKeepAliveSeconds(properties.getKeepAliveSeconds());
        executor.setThreadNamePrefix(properties.getThreadNamePrefix());

        // 2. 拒绝策略：生产环境通常建议 CallerRunsPolicy (主线程自己跑)，保证任务不丢失
        // 除非你对吞吐量要求极高，可以容忍丢失（DiscardPolicy）
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        // 3. 核心装饰器：注入上下文传递逻辑
        executor.setTaskDecorator(ContextAwareRunnable::new);

        // 4. 优雅停机配置：这决定了你发布代码时，会不会中断正在跑的任务
        executor.setWaitForTasksToCompleteOnShutdown(properties.getWaitForTasksToCompleteOnShutdown());
        executor.setAwaitTerminationSeconds(properties.getAwaitTerminationSeconds());

        executor.initialize();
        return executor;
    }
}
