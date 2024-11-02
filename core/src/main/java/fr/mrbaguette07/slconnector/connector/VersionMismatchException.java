package fr.mrbaguette07.slconnector.connector;

public class VersionMismatchException extends Exception {
    private final int receivedVersion;
    private final int supportedVersion;

    public VersionMismatchException(int receivedVersion, int supportedVersion, String message) {
        super(message);
        this.receivedVersion = receivedVersion;
        this.supportedVersion = supportedVersion;
    }

    public int getReceivedVersion() {
        return receivedVersion;
    }

    public int getSupportedVersion() {
        return supportedVersion;
    }
}
