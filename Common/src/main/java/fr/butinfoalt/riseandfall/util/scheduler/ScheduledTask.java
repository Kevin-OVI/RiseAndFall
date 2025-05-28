package fr.butinfoalt.riseandfall.util.scheduler;

import fr.butinfoalt.riseandfall.util.ToStringFormatter;

/**
 * Représente une tâche planifiée avec son temps de planification.
 */
public final class ScheduledTask {
    private final Scheduler scheduler;
    private final Runnable task;
    private final long scheduledTime;

    /**
     * Constructeur pour créer une tâche planifiée.
     *
     * @param scheduler     Le planificateur auquel cette tâche est associée.
     * @param task          La tâche à exécuter.
     * @param scheduledTime Le temps auquel la tâche doit être exécutée, en millisecondes depuis l'époque Unix (1er janvier 1970).
     */
    ScheduledTask(Scheduler scheduler, Runnable task, long scheduledTime) {
        this.scheduler = scheduler;
        this.task = task;
        this.scheduledTime = scheduledTime;
    }

    /**
     * Retourne la tâche qui doit être exécutée.
     *
     * @return La tâche à exécuter, encapsulée dans un objet Runnable.
     */
    public Runnable getTask() {
        return task;
    }

    /**
     * Retourne le temps auquel cette tâche doit être exécutée.
     *
     * @return Le temps de planification de la tâche, en millisecondes depuis l'époque Unix (1er janvier 1970).
     */
    public long getScheduledTime() {
        return scheduledTime;
    }

    /**
     * Annule cette tâche planifiée.
     */
    public void cancel() {
        this.scheduler.cancelTask(this);
    }

    @Override
    public String toString() {
        return new ToStringFormatter("ScheduledTask")
                .add("task", this.task)
                .add("scheduledTime", this.scheduledTime)
                .build();
    }
}
