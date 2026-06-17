package com.microservices;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import java.util.List;

public class Worker {
    public static void main(String[] args) {
        System.out.println("Java Worker is running and waiting for tasks...");

        try (JedisPool pool = new JedisPool("redis", 6379)) {
            try (Jedis jedis = pool.getResource()) {
                
                while (true) {
                    List<String> result = jedis.brpop(0, "task_queue");

                    if (result != null && !result.isEmpty()) {
                        String message = result.get(1);
                        System.out.println("\nGet new task:" + message);

                        try {
                            System.out.println("Processing task...");
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }

                        System.out.println("Task processed successfully: " + message);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error connecting to Redis: " + e.getMessage());
        }
    }
}