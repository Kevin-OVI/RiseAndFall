package fr.butinfoalt.riseandfall.util.scheduler;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.concurrent.TimeUnit;

/**
 * Utilitaire pour planifier des tâches à exécuter après un certain délai.
 * Il utilise une file de priorité pour gérer les tâches en fonction de leur temps de planification.
 */
public class Scheduler {
    /**
     * File de priorité pour stocker les tâches planifiées.
     */
    private final PriorityQueue<ScheduledTask> tasks = new PriorityQueue<>(Comparator.comparingLong(ScheduledTask::getScheduledTime));
    /**
     * Thread qui exécute les tâches planifiées.
     * Il est mis en veille jusqu'à ce qu'une tâche soit prête
     */
    private Thread schedulerThread;

    /**
     * Planifie une tâche à exécuter après un certain délai.
     * Cette méthode est synchronisée pour éviter les problèmes de concurrence.
     * La tâche est ajoutée à la file de priorité et le thread d'exécution est démarré si nécessaire.
     *
     * @param task     La tâche à exécuter.
     * @param duration Le délai avant l'exécution de la tâche.
     * @param timeUnit L'unité de temps du délai.
     * @return
     */
    public synchronized ScheduledTask schedule(Runnable task, long duration, TimeUnit timeUnit) {
        long scheduledTime = System.currentTimeMillis() + timeUnit.toMillis(duration);
        ScheduledTask scheduledTask = new ScheduledTask(this, task, scheduledTime);
        this.tasks.add(scheduledTask);

        if (this.schedulerThread == null || !this.schedulerThread.isAlive()) {
            this.schedulerThread = new Thread(this::runTasks);
            this.schedulerThread.setDaemon(true);
            this.schedulerThread.start();
        } else {
            this.schedulerThread.interrupt(); // Réveille le thread s'il est en attente
        }
        return scheduledTask;
    }

    /**
     * Planifie une tâche à exécuter après un certain délai en millisecondes.
     *
     * @param task     La tâche à exécuter.
     * @param duration Le délai avant l'exécution de la tâche en millisecondes.
     */
    public ScheduledTask schedule(Runnable task, long duration) {
        return schedule(task, duration, TimeUnit.MILLISECONDS);
    }

    /**
     * Annule une tâche planifiée.
     *
     * @param scheduledTask La tâche planifiée à annuler.
     */
    public synchronized void cancelTask(ScheduledTask scheduledTask) {
        if (!this.tasks.isEmpty() && this.tasks.peek() == scheduledTask && this.schedulerThread != null) {
            this.schedulerThread.interrupt(); // Réveille le thread s'il est en attente et que la tâche est en tête de file
        }
        this.tasks.remove(scheduledTask);
    }

    /**
     * Exécute les tâches planifiées.
     * Cette méthode est exécutée dans un thread séparé.
     * Elle vérifie la file de priorité pour voir si des tâches sont prêtes à être exécutées.
     * Si une tâche est prête, elle l'exécute.
     * Si aucune tâche n'est prête, elle met le thread en veille jusqu'à ce qu'une tâche soit prête.
     * Si une tâche est ajoutée pendant que le thread est en veille, il se réveille et vérifie à nouveau la file de priorité.
     * Quand toutes les tâches sont exécutées, le thread se termine.
     */
    private void runTasks() {
        while (true) {
            Runnable taskToRun = null;
            long timeToWait = -1;
            synchronized (this) {
                long currentTime = System.currentTimeMillis();
                if (this.tasks.isEmpty()) {
                    this.schedulerThread = null;
                    break;
                }
                ScheduledTask scheduledTask = tasks.peek();
                if (scheduledTask.getScheduledTime() <= currentTime) {
                    taskToRun = this.wrapTask(tasks.poll().getTask());
                } else {
                    timeToWait = scheduledTask.getScheduledTime() - currentTime;
                }
            }
            if (taskToRun != null) {
                try {
                    taskToRun.run();
                } catch (Throwable e) {
                    // On affiche simplement les exceptions produites par les tâches
                    e.printStackTrace();
                }
            } else if (timeToWait > 0) {
                try {
                    Thread.sleep(timeToWait);
                } catch (InterruptedException ignored) {
                    // Une nouvelle tâche a été planifiée ou a été annulée, on doit vérifier à nouveau le début de la file
                }
            }
        }
    }

    /**
     * Méthode pour envelopper une tâche avant de l'exécuter.
     * Par défaut, elle ne fait rien et retourne la tâche d'origine.
     * Cette méthode peut être redéfinie pour ajouter un comportement supplémentaire.
     *
     * @param task La tâche à envelopper.
     * @return La tâche enveloppée.
     */
    protected Runnable wrapTask(Runnable task) {
        return task;
    }
}
