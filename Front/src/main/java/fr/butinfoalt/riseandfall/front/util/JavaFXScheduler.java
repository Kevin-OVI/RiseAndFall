package fr.butinfoalt.riseandfall.front.util;

import fr.butinfoalt.riseandfall.util.scheduler.Scheduler;
import javafx.application.Platform;

/**
 * Utilitaire pour planifier des tâches à exécuter sur le thread JavaFX.
 * Il utilise la méthode Platform.runLater pour exécuter les tâches sur le thread JavaFX.
 */
public class JavaFXScheduler extends Scheduler {
    /**
     * Enveloppe une tâche pour l'exécuter sur le thread JavaFX.
     *
     * @param task La tâche à envelopper.
     * @return Une tâche enveloppée qui sera exécutée sur le thread JavaFX.
     */
    @Override
    protected Runnable wrapTask(Runnable task) {
        return () -> Platform.runLater(task);
    }
}
