package org.communiquons.android.comunic.client.data.models;

/**
 * Calls configuration object
 *
 * @author Pierre HUBERT
 */
public class CallsConfiguration {

    //Private fields
    private boolean enabled;
    private int maximumNumberMembers;
    private String signalServerName;
    private int signalServerPort;
    private boolean isSignalSererSecure;
    private String stunServer;
    private String turnServer;
    private String turnUsername;
    private String turnPassword;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getMaximumNumberMembers() {
        return maximumNumberMembers;
    }

    public void setMaximumNumberMembers(int maximum_number_members) {
        this.maximumNumberMembers = maximum_number_members;
    }

    public String getSignalServerName() {
        return signalServerName;
    }

    public void setSignalServerName(String signalServerName) {
        this.signalServerName = signalServerName;
    }

    public int getSignalServerPort() {
        return signalServerPort;
    }

    public void setSignalServerPort(int signalServerPort) {
        this.signalServerPort = signalServerPort;
    }

    public boolean isSignalSererSecure() {
        return isSignalSererSecure;
    }

    public void setSignalServerSecure(boolean signalSererSecure) {
        this.isSignalSererSecure = signalSererSecure;
    }

    public String getStunServer() {
        return stunServer;
    }

    public void setStunServer(String stunServer) {
        this.stunServer = stunServer;
    }

    public String getTurnServer() {
        return turnServer;
    }

    public void setTurnServer(String turnServer) {
        this.turnServer = turnServer;
    }

    public String getTurnUsername() {
        return turnUsername;
    }

    public void setTurnUsername(String turnUsername) {
        this.turnUsername = turnUsername;
    }

    public String getTurnPassword() {
        return turnPassword;
    }

    public void setTurnPassword(String turnPassword) {
        this.turnPassword = turnPassword;
    }
}
