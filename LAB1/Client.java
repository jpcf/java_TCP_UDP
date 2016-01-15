import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.net.InetAddress;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Random;

class Client {
    public static void main(String[] args) {
        // Checks for the correct number of arguments
        
        if (args[2].toLowerCase().equals("register") && (args.length != 5)) {
            System.out.println("ERROR\nCorrect Usage: java Client <host_name> <port_number> <oper> <DNS Name> <IP Address> "+ args.length);
            return;
        }
        else if (args[2].toLowerCase().equals("lookup") && (args.length != 4)) {
            System.out.println("ERROR\nCorrect Usage: java Client <host_name> <port_number> <oper> <DNS Name> "+ args.length);
            return;
        }
        else if(!args[2].toLowerCase().equals("register") && !args[2].toLowerCase().equals("lookup"))
            System.out.println("ERROR\n Operation (arg#3) must be either 'register' or 'lookup'  " + args.length);
        
        
        // Defining the socket port of this client, randomizing from the server port
        Random rndGen = new Random();
        int _port = Integer.parseInt(args[1]) + rndGen.nextInt(100);
        
        // Opens Socket
        DatagramSocket scktToServer; 
        try {
            scktToServer = new DatagramSocket(_port);
        } catch(SocketException scktExcept) {
            System.out.println("OpenSocket: " + scktExcept);
            return;
        }
        
        // Builds the Datagrams to be sent/received       
        DatagramPacket requestPacket = null;

        byte[] recvBuf = new byte[40];
        DatagramPacket replyPacket = new DatagramPacket(recvBuf, recvBuf.length);
        String recvStr = null;
        
        if(args[2].toLowerCase().equals("lookup")) {
            String requestStr = "lookup" + "," + args[3] + ","; 
            
            //Prepares the packet
            try {
                requestPacket = new DatagramPacket(requestStr.getBytes(), requestStr.getBytes("US-ASCII").length, InetAddress.getByName(args[0]), Integer.parseInt(args[1]));
            } catch(UnknownHostException UnknExcept) {
                System.out.println("Unknown Host Exception: " + UnknExcept);
                return;
            } catch (UnsupportedEncodingException uee) {
                System.out.println("Unknown Encoding Exception: " + uee);
            }
        }
        else if(args[2].toLowerCase().equals("register")) {
            String requestStr = "register" + "," + args[3] + "," + args[4];
            
            //Prepares the packet
            try {
                requestPacket = new DatagramPacket(requestStr.getBytes(), requestStr.getBytes("US-ASCII").length, InetAddress.getByName(args[0]), Integer.parseInt(args[1]));
            } catch(UnknownHostException UnknExcept) {
                System.out.println("Unknown Host Exception: " + UnknExcept);
                return;
            } catch (UnsupportedEncodingException uee) {
                System.out.println("Unknown Encoding Exception: " + uee);
            }
            
        }
        
        // Sends the datagram
        try {
            scktToServer.send(requestPacket);
        } catch (IOException ioe) {
            System.out.println("IO Exception: " + ioe);
        }
        
        try {
            scktToServer.receive(replyPacket);
        } catch (IOException ioe) {
            System.out.println("Receiving reply: " + ioe);
        }
        
        // Prints the received reply from the server
        recvStr = new String(replyPacket.getData());
        System.out.println("What I got from the Server: " + recvStr);
    }
}