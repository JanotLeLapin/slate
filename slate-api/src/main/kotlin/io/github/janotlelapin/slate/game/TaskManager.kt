package io.github.janotlelapin.slate.game

import org.bukkit.scheduler.BukkitTask

interface TaskManager {
    /**
     * Registers a task for the game
     */
    fun register(task: BukkitTask)

    /**
     * Cancels the task associated with the specified id
     */
    fun cancel(taskId: Int)

    /**
     * Cancels every pending task handled by this manager
     */
    fun clear()
}
