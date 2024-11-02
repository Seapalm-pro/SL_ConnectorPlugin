package fr.mrbaguette07.slconnector.connector;

public enum MessageTarget {
    /**
     * Sends to all servers that have players connected. (So this doesn't queue with
     * plugin messages)
     */
    ALL_WITH_PLAYERS(Type.SERVER),
    /**
     * Tries to send to all servers. (With plugin messages it queues if no player is
     * connected to server)
     */
    ALL_QUEUE(Type.SERVER),
    /**
     * Sends to all other servers that have players connected. (So this doesn't
     * queue with plugin messages)
     */
    OTHERS_WITH_PLAYERS(Type.SERVER),
    /**
     * Tries to send to all other servers. (With plugin messages it queues if no
     * player is connected to server)
     */
    OTHERS_QUEUE(Type.SERVER),
    /**
     * Send to the players current server.<br>
     * Requires a server name or player parameter.
     */
    SERVER(Type.SERVER),
    /**
     * Send to the players current proxy.<br>
     * Requires a proxy id or player parameter
     */
    PROXY(Type.PROXY),
    /**
     * Send to all connected proxies
     */
    ALL_PROXIES(Type.PROXY),
    /**
     * Send to all proxies that aren't the current proxy
     */
    OTHER_PROXIES(Type.PROXY, Type.PROXY);

    private final Type type;
    private final Type source;

    MessageTarget() {
        this(null, null);
    }

    MessageTarget(Type type) {
        this(type, null);
    }

    MessageTarget(Type type, Type source) {
        this.type = type;
        this.source = source;
    }

    public Type getType() {
        return type;
    }

    public Type getSource() {
        return source;
    }

    public enum Type {
        PROXY,
        SERVER
    }
}
