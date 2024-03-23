package com.dev.vault.service.interfaces.task;

import com.dev.vault.helper.exception.DevVaultException;
import com.dev.vault.helper.exception.NotLeaderOfProjectException;
import com.dev.vault.helper.exception.NotMemberOfProjectException;
import com.dev.vault.helper.exception.ResourceNotFoundException;
import com.dev.vault.model.task.enums.TaskStatus;

public interface TaskProgressService {

    /**
     * Marks a task as completed and updates its status in the database. If the task has already been completed, a DevVaultException is thrown.
     *
     * @param taskId     the ID of the task to mark as completed.
     * @param projectId  the ID of the project that the task belongs to.
     * @param taskStatus the new status of the task (must be TaskStatus.COMPLETED).
     * @throws DevVaultException           if the task has already been completed, or if the task status is not TaskStatus.COMPLETED.
     * @throws ResourceNotFoundException   if the task or project with the given IDs cannot be found in the database.
     * @throws NotMemberOfProjectException if the current user is not a member of the project with the given ID.
     * @throws NotLeaderOfProjectException if the current user is not a leader or admin of the project with the given ID.
     */
    void markTaskAsCompleted(Long taskId, Long projectId, TaskStatus taskStatus);
}
