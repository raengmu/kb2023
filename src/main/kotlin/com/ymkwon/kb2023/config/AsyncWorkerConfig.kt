package com.ymkwon.kb2023.config

import org.slf4j.MDC
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.task.TaskDecorator
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor

@Configuration
@EnableAsync
class AsyncWorkerConfig(
    private val applProperties: ApplicationProperties
) {
    @Bean
    fun taskExecutor(): ThreadPoolTaskExecutor {
        val taskExecutor = ThreadPoolTaskExecutor()
        val poolSize = applProperties.asyncTaskPool.maxPoolSize
        taskExecutor.setThreadNamePrefix("AsyncTask-")
        taskExecutor.corePoolSize = poolSize
        taskExecutor.maxPoolSize = poolSize * 2
        taskExecutor.queueCapacity = poolSize * 5
        taskExecutor.setTaskDecorator(LoggingTaskDecorator())
        taskExecutor.setWaitForTasksToCompleteOnShutdown(true)

        return taskExecutor
    }

    /**
     * Logging caller thread id
     */
    class LoggingTaskDecorator : TaskDecorator {
        override fun decorate(task: Runnable): Runnable {
            val callerThreadContext = MDC.getCopyOfContextMap()
            return Runnable {
                callerThreadContext?.let {
                    MDC.setContextMap(it)
                }
                task.run()
            }
        }
    }
}
