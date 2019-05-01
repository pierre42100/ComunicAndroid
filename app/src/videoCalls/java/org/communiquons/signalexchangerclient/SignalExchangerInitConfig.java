package org.communiquons.signalexchangerclient;

/**
 * Signal exchanger configuration intialization
 *
 * @author Pierre HUBERT
 */
public class SignalExchangerInitConfig {

    //Private fields
    private String domain;
    private int port;
    private String clientID;
    private boolean isSecure;

    public SignalExchangerInitConfig() {

    }

    public SignalExchangerInitConfig(String domain, int port, String clientID, boolean isSecure) {
        this.domain = domain;
        this.port = port;
        this.clientID = clientID;
        this.isSecure = isSecure;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getClientID() {
        return clientID;
    }

    public void setClientID(String clientID) {
        this.clientID = clientID;
    }

    public boolean isSecure() {
        return isSecure;
    }

    public void setSecure(boolean secure) {
        isSecure = secure;
    }
}
