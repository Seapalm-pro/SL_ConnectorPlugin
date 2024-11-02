package fr.mrbaguette07.slconnector;

import java.util.concurrent.CompletableFuture;

public abstract class ResponseHandler<T> {

    private final CompletableFuture<T> future;

    protected ResponseHandler(CompletableFuture<T> future) {
        this.future = future;
    }

    public CompletableFuture<T> getFuture() {
        return future;
    }

    public static class Boolean extends ResponseHandler<java.lang.Boolean> {
        public Boolean(CompletableFuture<java.lang.Boolean> future) {
            super(future);
        }
    }

    public static class String extends ResponseHandler<java.lang.String> {
        public String(CompletableFuture<java.lang.String> future) {
            super(future);
        }
    }

    public static class Location extends ResponseHandler<LocationInfo> {
        public Location(CompletableFuture<LocationInfo> future) {
            super(future);
        }
    }

    public static class PlayerInfo extends ResponseHandler<BridgeCommon.PlayerInfo> {
        public PlayerInfo(CompletableFuture<BridgeCommon.PlayerInfo> future) {
            super(future);
        }
    }
}
