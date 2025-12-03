package apps.jizzu.simpletodo.data.database

import android.app.Application
import apps.jizzu.simpletodo.data.models.Task
import java.util.concurrent.Executors

class TaskListRepository(app: Application) {
    private val mTaskDao = TasksDatabase.getInstance(app).taskDAO()
    private val mAllTasksLiveData = mTaskDao.getAllTasksLiveData()
    private val executor = Executors.newSingleThreadExecutor()

    fun getAllTasksLiveData() = mAllTasksLiveData

    fun deleteAllTasks() {
        executor.execute { mTaskDao.deleteAllTasks() }
    }

    fun saveTask(task: Task) {
        executor.execute {
            mTaskDao.saveTask(task)
        }
    }

    fun deleteTask(task: Task) {
        executor.execute { mTaskDao.deleteTask(task) }
    }

    fun updateTask(task: Task) {
        executor.execute { mTaskDao.updateTask(task) }
    }

    fun updateTaskOrder(tasks: List<Task>) {
        executor.execute { mTaskDao.updateTaskOrder(tasks) }
    }

    fun getTasksForSearch(searchText: String) = mTaskDao.getTasksForSearch(searchText)

    fun getAllTasks(): List<Task> = mTaskDao.getAllTasks()
}