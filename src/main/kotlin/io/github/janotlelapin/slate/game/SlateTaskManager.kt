package io.github.janotlelapin.slate.game

import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitScheduler
import org.bukkit.scheduler.BukkitTask

class SlateTaskManager : TaskManager {
    private val tasks: ArrayList<Int> = ArrayList()

    override fun register(task: BukkitTask) {
        tasks.add(task.taskId)
    }

    override fun cancel(taskId: Int) {
        tasks.remove(taskId)
        if (scheduler.isQueued(taskId)) scheduler.cancelTask(taskId)
    }

    override fun clear() {
        val it = tasks.iterator()
        while (it.hasNext()) {
            val task = it.next()
            if (scheduler.isQueued(task))
                scheduler.cancelTask(task)

            it.remove()
        }
    }

    private val scheduler: BukkitScheduler
        get() = Bukkit.getScheduler()
}