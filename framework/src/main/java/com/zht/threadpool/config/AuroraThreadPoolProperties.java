package com.zht.threadpool.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;


/**
 * 线程池配置属性类
 * 对应 application.yml 中的 aurora.thread-pool 前缀
 */
@Data
@ConfigurationProperties(prefix = "aurora.thread-pool")
public class AuroraThreadPoolProperties {

    /** 核心线程数，默认为 CPU 核心数 + 1 */
    private Integer corePoolSize = Runtime.getRuntime().availableProcessors() + 1;

    /** 最大线程数，默认为 CPU 核心数 * 2 */
    private Integer maxPoolSize = Runtime.getRuntime().availableProcessors() * 2;

    /** 队列容量，默认 1000，防止 OOM */
    private Integer queueCapacity = 1000;

    /** 空闲线程存活时间，默认 60 秒 */
    private Integer keepAliveSeconds = 60;

    /** 线程名前缀，这在查日志和 Dump 分析时非常重要 */
    private String threadNamePrefix = "aurora-thread-";

    /** 是否开启优雅停机，默认开启 */
    private Boolean waitForTasksToCompleteOnShutdown = true;

    /** 优雅停机最大等待时间（秒），默认 30 秒 */
    private Integer awaitTerminationSeconds = 30;
}