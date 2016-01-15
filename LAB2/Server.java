import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.net.InetAddress;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.*;

class Server {
    public static void main(String[] args) {
        
        // Checks for the correct number of arguments
        if (args.length != 3 ) {
            System.out.println("ERROR\nCorrect Usage: java Server <server_port> <mcast_addr> <mcast_port>  ");
            return;
        }
        
        // Creates the Multicast Group
        InetAddress mcastGroup = null;
        try {
            mcastGroup = InetAddress.getByName(args[1]);
        } catch(UnknownHostException uhe) {
            System.out.println("Creating InetAddress obj: " + uhe);
        }
        
        // Creates the Multicast Socket through which it annouces its services
        MulticastSocket mcastServiceSckt = null;
        try {
            mcastServiceSckt = new MulticastSocket(Integer.parseInt(args[2]));
        } catch (IOException ioe) {
            System.out.println("Creating mcast Socket obj: " + ioe);
        }
        
        // Joins the Mcast Group
        try {
            mcastServiceSckt.joinGroup(mcastGroup);
        } catch(IOException ioe) {
            System.out.println("Joining mcast group: " + ioe);
        }
        
        // Scheduling the Mcast Announcer Thread
        Timer tmr = new Timer();
        tmr.schedule(new McastServiceAnnounce(mcastServiceSckt, args[1], Integer.parseInt(args[2]), Integer.parseInt(args[0])),0, 1000);
        
        // Opens Listening Socket
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
        
        // The packet where it receives reply
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
            
        }
        
        
    }
}

class McastServiceAnnounce extends TimerTask
{
    MulticastSocket mcastServiceSckt = null;
    String mcastAddr = null;
    int mcastPort;
    int servicePort;
    
    public McastServiceAnnounce(MulticastSocket mcastServiceSckt, String mcastAddr, int mcastPort,
                                int servicePort) {
        this.mcastServiceSckt = mcastServiceSckt;
        this.mcastAddr = mcastAddr;
        this.mcastPort = mcastPort;
        this.servicePort = servicePort;
    }
    
    
   public void run ()
   {
       DatagramPacket announcePckt = null;
       String announceStr = Integer.toString(this.servicePort) + "," + "127.0.0.1";
       try {
            announcePckt = new DatagramPacket(announceStr.getBytes(), announceStr.getBytes().length, 
                                              InetAddress.getByName(this.mcastAddr), this.mcastPort);
            this.mcastServiceSckt.send(announcePckt);
        } catch(UnknownHostException UnknExcept) {
            System.out.println("Unknown Host Exception: " + UnknExcept);
            return;
        } catch (UnsupportedEncodingException uee) {
            System.out.println("Unknown Encoding Exception: " + uee);
        } catch (IOException ioe) {
            System.out.println("IO Exception: " + ioe);
        }
       
       // DEBUG ONLY
       System.out.println("multicast: " + this.mcastAddr + " " + this.mcastPort + " : " + "127.0.0.1 " + this.servicePort);
       System.out.println("Sent Message: " + announceStr);
       return;
   }
}
