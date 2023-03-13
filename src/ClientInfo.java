public class ClientInfo {

    private String ipAddress;
    private int port;
    private String hashServer;

    public ClientInfo(String ipAddress, int port, String hashServer) {
        this.ipAddress = ipAddress;
        this.port = port;
        this.hashServer = hashServer;

    }

    public ClientInfo(String ipAddress, int port) {
        this.ipAddress = ipAddress;
        this.port = port;
        this.hashServer = null;

    }

    public String getIpAddress() {
        return ipAddress;
    }

    public int getPort() {
        return port;
    }

    public String getHashServer() {
        return hashServer;
    }

    public void setHashServer(String hashServer) {
        this.hashServer = hashServer;
    }


}
