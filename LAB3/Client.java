import java.net.InetAddress;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.InputStreamReader;

class Client {
    public static void main(String[] args) {
        
        // Checks for the correct number of arguments
        if (args[2].toLowerCase().equals("lookup") && args.length != 4 ) {
            System.out.println("ERROR\nCorrect Usage: java Client <svc_name> <svc_port> lookup <dns_to_search>");
            return;
        } else if (args[2].toLowerCase().equals("register") && args.length != 5 ) {
            System.out.println("ERROR\nCorrect Usage: java Client <svc_name> <svc_port> register <dns> <ip_addr>");
            return;
        }
        
        // The server connection socket
        Socket dataSocket = null;
        
        // The input and output stream handles
        PrintWriter outStream = null;
        BufferedReader inStream = null;
        
        // Tries a connection to the Server
        try {
            dataSocket = new Socket(InetAddress.getByName(args[0]), Integer.parseInt(args[1]));
            outStream  = new PrintWriter(dataSocket.getOutputStream(), true);
            inStream   = new BufferedReader(new InputStreamReader(dataSocket.getInputStream()));            
            
        } catch (IOException ioe) {
            System.out.println("Open TCP Connection: " + ioe);
        }
        
        // Sends the request String to the DNS Server
        String requestStr = null;
        if(args[2].toLowerCase().equals("lookup")) {
            requestStr = "lookup" + "," + args[3] + ","; 
        }
        else if(args[2].toLowerCase().equals("register")) {
            requestStr = "register" + "," + args[3] + "," + args[4];
            
        }
        outStream.println(requestStr);
        System.out.println("Sent: " + requestStr);
        
        // Waits for the reply from the server
        String replyStr = null;
        try {
            replyStr = inStream.readLine();
        } catch (IOException ioe) {
            System.out.println("Error in fetching reply from DNS serv: " + ioe);
        }
        
        // Prints the reply
        System.out.println("Reply from DNS Server: " + replyStr);
        
        // Closes the socket and leaves
        try {
            dataSocket.close();
        } catch (IOException ioe) {
            System.out.println("Error closing socket: " + ioe);
        }
    }
}


