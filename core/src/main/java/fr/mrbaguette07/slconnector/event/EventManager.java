package fr.mrbaguette07.slconnector.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Gestionnaire d'événements pour SLConnector
 */
public class EventManager {
    private final Map<Class<? extends ProxyEvent>, List<Consumer<? extends ProxyEvent>>> listeners = new HashMap<>();

    /**
     * Enregistre un listener pour un type d'événement
     * 
     * @param eventClass La classe de l'événement
     * @param listener Le listener à enregistrer
     * @param <T> Le type d'événement
     */
    public <T extends ProxyEvent> void registerListener(Class<T> eventClass, Consumer<T> listener) {
        listeners.computeIfAbsent(eventClass, k -> new ArrayList<>()).add(listener);
    }

    /**
     * Désenregistre un listener pour un type d'événement
     * 
     * @param eventClass La classe de l'événement
     * @param listener Le listener à désenregistrer
     * @param <T> Le type d'événement
     */
    public <T extends ProxyEvent> void unregisterListener(Class<T> eventClass, Consumer<T> listener) {
        List<Consumer<? extends ProxyEvent>> eventListeners = listeners.get(eventClass);
        if (eventListeners != null) {
            eventListeners.remove(listener);
        }
    }

    /**
     * Déclenche un événement et appelle tous les listeners enregistrés
     * 
     * @param event L'événement à déclencher
     * @param <T> Le type d'événement
     * @return L'événement après traitement
     */
    @SuppressWarnings("unchecked")
    public <T extends ProxyEvent> T callEvent(T event) {
        List<Consumer<? extends ProxyEvent>> eventListeners = listeners.get(event.getClass());
        if (eventListeners != null) {
            for (Consumer<? extends ProxyEvent> listener : new ArrayList<>(eventListeners)) {
                try {
                    ((Consumer<T>) listener).accept(event);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return event;
    }

    /**
     * Désenregistre tous les listeners
     */
    public void unregisterAll() {
        listeners.clear();
    }

    /**
     * Obtient le nombre de listeners pour un type d'événement
     * 
     * @param eventClass La classe de l'événement
     * @return Le nombre de listeners
     */
    public int getListenerCount(Class<? extends ProxyEvent> eventClass) {
        List<Consumer<? extends ProxyEvent>> eventListeners = listeners.get(eventClass);
        return eventListeners != null ? eventListeners.size() : 0;
    }
}
