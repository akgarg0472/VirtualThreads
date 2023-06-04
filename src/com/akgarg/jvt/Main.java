package com.akgarg.jvt;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    public static void main(String[] args) {
        final Runnable runnable = () -> {
            try {
                System.out.println("Thread " + Thread.currentThread().getName() + " is starting");
                Thread.sleep(10_000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                System.out.println("Thread " + Thread.currentThread().getName() + " is exiting");
            }
        };

        final ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();

        for (int i = 1; i <= 10000; i++) {
            executorService.execute(runnable);
        }

        executorService.close();
    }

}
