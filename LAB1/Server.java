import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.net.InetAddress;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;

class Server {
    public static void main(String[] args) {
        
        // Checks for the correct number of arguments
        if (args.length != 1 ) {
            System.out.println("ERROR\nCorrect Usage: java Server <port_number>");
            return;
        }
        
        // Opens Socket
        DatagramSocket selfSocket = null;
        try {
            selfSocket = new DatagramSocket(Integer.parseInt(args[0]));
        } catch(SocketException scktExcept) {
            System.out.println("Exception Creating Socket: " + scktExcept);
            return;
        }
        
        System.out.println("The Server is running on Port " + Integer.parseInt(args[0]) + " @ 127.0.0.1");
        
        // Creates the hashtable where it stores the DNS lookup values
        Map<String, String> databaseDNS = new HashMap<String, String>(1);
        
        // Builds the Datagram to be sent as reply      
        DatagramPacket replyPacket = null;
        
        
        
        while(true) {
            // Reception Buffers
            byte[] recvBuf = new byte[40];
            DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
            
            try {
                selfSocket.receive(packet);
            } catch (IOException ioe) {
                System.out.println("Exception: " + ioe);
            }
            
            //Prints the received data
            String[] recvStr = new String(packet.getData(), Charset.forName("US-ASCII")).split(",");
            
            if (recvStr[0].toLowerCase().equals("lookup")) {
                                 System.out.println("What I got: " + recvStr[0] + " " + recvStr[1]);

                // Lookup command
                String keyValue = databaseDNS.get(recvStr[1]);
                
                System.out.println("Res " + recvStr[1] + " " + "feup.pt");
                
                
                if (keyValue != null) {
                    replyPacket = new DatagramPacket(keyValue.getBytes(), keyValue.getBytes().length, packet.getAddress(), packet.getPort());
                } else {
                    String NOT_FOUND = new String("NOT_FOUND");
                    replyPacket = new DatagramPacket(NOT_FOUND.getBytes(), NOT_FOUND.getBytes().length, packet.getAddress(), packet.getPort());
                }
                        
                
            } else if (recvStr[0].toLowerCase().equals("register") && recvStr.length == 3) {
                System.out.println("What I got: " + recvStr[0] + " " + recvStr[1] + " " + recvStr[2]);
                
                // Lookup command
                String keyValue = databaseDNS.get(recvStr[1]);
                
                if (keyValue != null) {
                    String ALREADY_EXISTS = new String("-1");
                    replyPacket = new DatagramPacket(ALREADY_EXISTS.getBytes(), ALREADY_EXISTS.getBytes().length, packet.getAddress(), packet.getPort());
                } else {
                    
                    databaseDNS.put(recvStr[1], recvStr[2]);
                    
                    String SUCCESS = new String("1");
                    replyPacket = new DatagramPacket(SUCCESS.getBytes(), SUCCESS.getBytes().length, packet.getAddress(), packet.getPort());
                }
            
            } else
                System.out.println("Unrecognized Command: " + recvStr[0] + recvStr[1]);
            
            try {
                selfSocket.send(replyPacket);
            } catch (IOException ioe) {
                System.out.println("Reply: " + ioe);
            }
            
            System.out.println("Database Size: " + databaseDNS.size());
            
            /*
            while(databaseDNS.keys().hasMoreElements()) {
                System.out.println("Database Contents: " + databaseDNS.keys().nextElement().toString());
                databaseDNS.keys().nextElement();
            }*/
        }

        }
        
    }