package com.akgarg.jvt;

import jdk.incubator.concurrent.StructuredTaskScope;

import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class StructuredConcurrency {

    private static final Random RANDOM = new Random(System.currentTimeMillis());

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        handleOrderWithoutStructuredConcurrency();
        System.out.println("=========================================");
        handleOrderWithStructuredConcurrency();
    }

    private static void handleOrderWithStructuredConcurrency() throws InterruptedException, ExecutionException {
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            final Future<Boolean> inventory = scope.fork(StructuredConcurrency::updateInventory);
            final Future<Boolean> order = scope.fork(StructuredConcurrency::updateOrder);

            scope.join();
            scope.throwIfFailed();

            System.out.println("Order: " + order.resultNow() + ", Inventory: " + inventory.resultNow());
        }
    }

    // following code is not using structured concurrency
    // it is not safe because if one of the task fails then other task will not be cancelled or if parent thread is cancelled then child threads will not be cancelled which can cause resource leak
    private static void handleOrderWithoutStructuredConcurrency() throws ExecutionException, InterruptedException {
        try (var executor = new ScheduledThreadPoolExecutor(8)) {
            final Future<Object> inventory = executor.submit(StructuredConcurrency::updateInventory);
            final Future<Object> order = executor.submit(StructuredConcurrency::updateOrder);

            final Object theInventory = inventory.get();    // might fails
            final Object theOrder = order.get();    // might fails

            System.out.println("Order: " + theOrder + ", Inventory: " + theInventory);
        }
    }

    private static boolean updateOrder() {
        try {
            System.out.println("Updating order");
            Thread.sleep(1000 + RANDOM.nextInt(2000));
            System.out.println("Order updated successfully");
            return true;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean updateInventory() {
        try {
            System.out.println("Updating inventory");
            Thread.sleep(1000 + RANDOM.nextInt(2000));

            if (RANDOM.nextBoolean()) {
                throw new RuntimeException("Cannot update inventory");
            }

            System.out.println("Inventory updated successfully");
            return true;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
