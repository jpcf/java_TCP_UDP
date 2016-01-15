import java.net.ServerSocket;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

class Server {
    public static void main(String[] args) {
        
        // Checks for the correct number of arguments
        if (args.length != 1 ) {
            System.out.println("ERROR\nCorrect Usage: java Server <service_port>");
            return;
        }
        
        // Creates the socket through which the Clients try a connection
        ServerSocket connSocket = null;
        Socket dataSocket = null;
        PrintWriter outStream = null;
        BufferedReader inStream = null;
        
        // Creates the hashtable where it stores the DNS lookup values
        Map<String, String> databaseDNS = new HashMap<String, String>(1);
        
        try {
            // Creates the Server Socket
            connSocket = new ServerSocket(Integer.parseInt(args[0]));
            
            // Blocks until a Client Connects to it, and we get the data transf. socket
            dataSocket = connSocket.accept();
            
            // Creates the Streams through which it gets/sends data
            outStream = new PrintWriter(dataSocket.getOutputStream(), true); 
            inStream  = new BufferedReader(new InputStreamReader(
                                                                dataSocket.getInputStream()));
            
        } catch(IOException ioe) {
            System.out.println("Opening Server Socket: " + ioe);
        }
        
        String temp = null;
        while(true) {
            
            // Waits for a new request...
            try {
                temp = inStream.readLine();
                if (temp == null) {
                    // If we didn't catch anything, we try again next time
                    continue;
                }
                System.out.println("What I got: " + temp);
            } catch (IOException ioe) {
                System.out.println("Reading form socket: " + ioe);
            }
            
            // If we got this far, it means we got a meaningful request..
            // We now process it, and reply to it
            
            String [] recvStr = temp.split(",");
            
            if (recvStr[0].toLowerCase().equals("lookup")) {
                                 System.out.println("What I got: " + recvStr[0] + " " + recvStr[1]);

                // Lookup command
                String keyValue = databaseDNS.get(recvStr[1]);
                
                System.out.println("Res " + recvStr[1] + " " + "feup.pt");
                
                
                if (keyValue != null) {
                    outStream.println(keyValue);
                } else {
                    outStream.println("NOT_FOUND");
                }
                        
                
            } else if (recvStr[0].toLowerCase().equals("register") && recvStr.length == 3) {
                System.out.println("What I got: " + recvStr[0] + " " + recvStr[1] + " " + recvStr[2]);
                
                // Lookup command
                String keyValue = databaseDNS.get(recvStr[1]);
                
                if (keyValue != null) {
                    outStream.println("-1");
                } else {
                    databaseDNS.put(recvStr[1], recvStr[2]);
                    outStream.println("1");
                }
            } else
                System.out.println("Unrecognized Command: " + recvStr[0] + recvStr[1]); 
            
            // Restarts the socket for a new connection
            try {
                dataSocket = connSocket.accept();
                outStream = new PrintWriter(dataSocket.getOutputStream(), true); 
                inStream  = new BufferedReader(new InputStreamReader(
                                                                dataSocket.getInputStream()));
            } catch (IOException ioe) {
                System.out.println("Reopen Socket: " + ioe);
            }
            
        }
                
    }
}