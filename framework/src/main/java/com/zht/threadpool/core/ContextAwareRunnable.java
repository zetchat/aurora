package com.zht.threadpool.core;

import org.slf4j.MDC;
import java.util.Map;

/**
 * 上下文感知 Runnable
 * 作用：在任务提交时“快照”主线程的上下文，在任务执行时“恢复”到子线程
 */
public class ContextAwareRunnable implements Runnable {

    private final Runnable target;
    private final Map<String, String> contextMap;

    public ContextAwareRunnable(Runnable target) {
        this.target = target;
        // 1. [主线程] 抓取当前 MDC 上下文（比如 TraceId, UserId）
        this.contextMap = MDC.getCopyOfContextMap();
    }

    @Override
    public void run() {
        // 2. [子线程] 如果有上下文，先恢复
        if (contextMap != null) {
            MDC.setContextMap(contextMap);
        }
        try {
            // 3. 执行真正的业务逻辑
            target.run();
        } finally {
            // 4. [子线程] 清理现场，必须做！否则线程复用时会带着旧的上下文，导致数据污染
            MDC.clear();
        }
    }
}
