package fr.butinfoalt.riseandfall.util.logging;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * Une classe utilitaire pour gérer les logs de l'application.
 */
public class LogManager {
    /**
     * Méthode pour récupérer la trace d'une exception sous forme de chaîne de caractères.
     *
     * @param throwable L'exception à traiter.
     * @return La trace de l'exception sous forme de chaîne de caractères.
     */
    private static String getExceptionTraceback(Throwable throwable) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        throwable.printStackTrace(ps);
        ps.flush();
        return baos.toString();
    }

    /**
     * Méthode pour récupérer le nom de la classe sans le nom du package.
     *
     * @param fullClassName Le nom complet de la classe (avec le package).
     * @return Le nom de la classe sans le nom du package.
     */
    private static String getClassNameWithoutPackage(String fullClassName) {
        String[] parts = fullClassName.split("\\.");
        return parts[parts.length - 1];
    }

    /**
     * Méthode pour logger un message brut dans le flux spécifié en ajoutant le nom de la classe, le nom de la méthode et le numéro de ligne.
     *
     * @param stream  Le flux dans lequel logger le message.
     * @param message Le message à logger.
     */
    private static void log(PrintStream stream, String message) {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        StackTraceElement lastNonLoggerElement = null;
        for (int i = 1; i < stackTraceElements.length; i++) {
            StackTraceElement element = stackTraceElements[i];
            if (!element.getClassName().equals(LogManager.class.getName())) {
                lastNonLoggerElement = element;
                break;
            }
        }
        String className, methodName;
        int lineNumber;
        if (lastNonLoggerElement == null) {
            className = "UnknownClass";
            methodName = "UnknownMethod";
            lineNumber = -1;
        } else {
            className = getClassNameWithoutPackage(lastNonLoggerElement.getClassName());
            methodName = lastNonLoggerElement.getMethodName();
            lineNumber = lastNonLoggerElement.getLineNumber();
        }
        for (String line : message.split("\n")) {
            stream.printf("[%s.%s:%d] %s%n", className, methodName, lineNumber, line);
        }
    }

    /**
     * Méthode pour logger un message dans le flux de sortie standard.
     *
     * @param message Le message à logger.
     */
    public static void logMessage(String message) {
        log(System.out, message);
    }

    /**
     * Méthode pour logger un message dans le flux d'erreur standard.
     *
     * @param message   Le message à logger.
     * @param throwable L'exception à logger.
     */
    public static void logError(String message, Throwable throwable) {
        log(System.err, message + "\n" + getExceptionTraceback(throwable));
    }

    /**
     * Méthode pour logger un message dans le flux d'erreur standard.
     *
     * @param message Le message à logger.
     */
    public static void logError(String message) {
        log(System.err, message);
    }
}
