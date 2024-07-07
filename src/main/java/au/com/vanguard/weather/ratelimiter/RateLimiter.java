package au.com.vanguard.weather.ratelimiter;

import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

@Component
public class RateLimiter {
    private final Map<String, RateLimitedSemaphore> semaphores = new ConcurrentHashMap<>();

    public boolean tryAcquire(String key, int requests, int seconds) {
        long currentTime = System.currentTimeMillis();
        long startTime = currentTime - seconds * 1000;

        cleanupExpiredEntries(startTime);

        RateLimitedSemaphore semaphore = semaphores.computeIfAbsent(key, k -> {
            RateLimitedSemaphore newSemaphore = new RateLimitedSemaphore(requests);
            newSemaphore.setLastAcquireTime(currentTime);
            return newSemaphore;
        });

        boolean acquired = semaphore.tryAcquire();
        if (acquired) {
            semaphore.setLastAcquireTime(currentTime);
        }
        return acquired;
    }

    private void cleanupExpiredEntries(long startTime) {
        Iterator<Map.Entry<String, RateLimitedSemaphore>> iterator = semaphores.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, RateLimitedSemaphore> entry = iterator.next();
            RateLimitedSemaphore semaphore = entry.getValue();
            if (semaphore.getLastAcquireTime() < startTime) {
                iterator.remove();
            }
        }
    }

    private class RateLimitedSemaphore extends Semaphore {
        private volatile long lastAcquireTime;

        public RateLimitedSemaphore(int permits) {
            super(permits);
        }

        public long getLastAcquireTime() {
            return lastAcquireTime;
        }

        public void setLastAcquireTime(long lastAcquireTime) {
            this.lastAcquireTime = lastAcquireTime;
        }
    }
}