package com.microservices;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.List;

public class Worker {
    public static void main(String[] args) {
        System.out.println("Java Worker запущено! Очікую на задачі...");

        // Параметри підключення до нашої бази PostgreSQL (збігаються з docker-compose)
        String dbUrl = "jdbc:postgresql://db:5432/micrositedb";
        String dbUser = "myuser";
        String dbPassword = "mypassword";

        try (JedisPool pool = new JedisPool("redis", 6379)) {
            try (Jedis jedis = pool.getResource()) {
                
                while (true) {
                    List<String> result = jedis.brpop(0, "task_queue");

                    if (result != null && !result.isEmpty()) {
                        String messageRaw = result.get(1);
                        System.out.println("\nОтримано нове повідомлення: " + messageRaw);

                        // 1. Розпаковуємо JSON, який нам прислав Django
                        JSONObject jsonMessage = new JSONObject(messageRaw);
                        int taskId = jsonMessage.getInt("task_id");
                        String taskDesc = jsonMessage.getString("task");

                        // 2. Імітуємо виконання задачі
                        System.out.println("Обробка задачі №" + taskId + " (" + taskDesc + ")...");
                        try {
                            Thread.sleep(5000); 
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }

                        // 3. З'єднуємося з БД і змінюємо статус на SUCCESS
                        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword)) {
                            // Django називає таблиці як "назва_додатка" + "_" + "назва_моделі"
                            String sql = "UPDATE tasks_taskrequest SET status = ? WHERE id = ?";
                            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                                pstmt.setString(1, "SUCCESS");
                                pstmt.setInt(2, taskId);
                                pstmt.executeUpdate();
                            }
                            System.out.println("Задачу успішно виконано! Статус оновлено в БД.");
                        } catch (Exception e) {
                            System.err.println("Помилка оновлення БД: " + e.getMessage());
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Помилка підключення до Redis: " + e.getMessage());
        }
    }
}