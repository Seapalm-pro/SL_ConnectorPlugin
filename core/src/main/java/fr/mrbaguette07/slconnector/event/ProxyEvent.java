package fr.mrbaguette07.slconnector.event;

/**
 * Classe de base pour tous les événements proxy de SLConnector
 */
public abstract class ProxyEvent {
    private boolean cancelled = false;

    /**
     * Vérifie si l'événement a été annulé
     * 
     * @return true si l'événement est annulé
     */
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * Définit l'état d'annulation de l'événement
     * 
     * @param cancelled true pour annuler l'événement
     */
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
