package ch.skyfy.tictactoe.views.animation;

import ch.skyfy.tictactoe.utils.TypedMap;
import javafx.application.Platform;
import javafx.concurrent.Task;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadPoolExecutor;

public class AnimationQueue extends TypedMap {

    private final Semaphore semaphore;

    private final ThreadPoolExecutor executor;

    public AnimationQueue(Semaphore semaphore) {
        this.semaphore = semaphore;
        executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(1, r -> {
            final Thread t = Executors.defaultThreadFactory().newThread(r);
            t.setName("Thread Executor");
            t.setDaemon(true);
            return t;
        });
    }

    public void executeAwait(Runnable runnable) {
        final Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                return null;
            }
        };
        task.setOnRunning(workerStateEvent -> runnable.run());
        task.run();

        try {
            semaphore.acquire(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void executeAwait2(Runnable runnable){
        final CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            runnable.run();
            latch.countDown();
        });
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public ThreadPoolExecutor getExecutor() {
        return executor;
    }
}
