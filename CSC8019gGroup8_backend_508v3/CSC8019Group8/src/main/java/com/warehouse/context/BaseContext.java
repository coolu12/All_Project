package com.warehouse.context;

/**
 * Provides a thread-local context to store and retrieve user-specific data, such as user identifiers,
 * ensuring that data is isolated to the current thread and not shared inadvertently among multiple requests.
 * @author Zilong Li
 * @since 2024-05-08
 */
public class BaseContext {

    /**
     * ThreadLocal variable to store the user ID for the current thread, ensuring that the user ID
     * is specific to each thread and not shared across different threads.
     */
    public static ThreadLocal<String> threadLocal = new ThreadLocal<>();

    /**
     * Sets the current user's ID in the thread-local variable.
     * @param id The user ID to be set for the current thread.
     */
    public static void setCurrentId(String id) {
        threadLocal.set(id);
    }

    /**
     * Retrieves the current user's ID from the thread-local variable.
     * @return The user ID stored in the current thread.
     */
    public static String getCurrentId() {
        return threadLocal.get();
    }

    /**
     * Clears the thread-local variable by removing the user ID stored for the current thread.
     * This should be called at the end of each request to prevent memory leaks.
     */
    public static void removeCurrentId() {
        threadLocal.remove();
    }
}
