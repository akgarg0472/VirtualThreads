package com.akgarg.jvt;

import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;
import java.util.stream.IntStream;

@SuppressWarnings("unused")
public class PlatformAndVirtualThreadMetrics {

    public static void main(String[] args) {
        platformThreadNewVirtualThreadPerTaskExecutorThroughput();
    }

    private static void platformThreadNewVirtualThreadPerTaskExecutorThroughput() {
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            IntStream.range(0, 2_000_000)
                    .forEach(i -> executor.submit(() -> {
                        System.out.println("Virtual Thread spawned: " + i);
                        Thread.sleep(Duration.ofMillis(100));
                        System.out.println("Virtual Thread died: " + i);
                        return i;
                    }));
        }
    }

    private static void platformThreadNewThreadPerTaskExecutorThroughput() {
        try (var executor = Executors.newThreadPerTaskExecutor(Executors.defaultThreadFactory())) {
            IntStream.range(0, 8_500)
                    .forEach(i -> executor.submit(() -> {
                        Thread.sleep(Duration.ofSeconds(1));
                        System.out.println(i);
                        return i;
                    }));
        }
    }

    private static void platformThreadNewCachedThreadPoolThroughput() {
        try (var executor = Executors.newCachedThreadPool(Executors.defaultThreadFactory())) {
            IntStream.range(0, 8_500)
                    .forEach(i -> executor.submit(() -> {
                        Thread.sleep(Duration.ofSeconds(1));
                        System.out.println(i);
                        return i;
                    }));
        }
    }

    private static void printPlatformThreadsCounter() {
        final var platformThreadsCounter = new AtomicInteger(0);

        while (true) {
            new Thread(() -> {
                final int count = platformThreadsCounter.incrementAndGet();
                System.out.println("Platform thread count: " + count);
                LockSupport.park();
            }).start();
        }
    }

    private static void printVirtualThreadsCount() {
        final var virtualThreadCounter = new AtomicInteger(0);

        while (true) {
            Thread.startVirtualThread(() -> {
                final int count = virtualThreadCounter.incrementAndGet();
                System.out.println("Thread counter: " + count);
                LockSupport.park();
            });
        }
    }

}
