import java.util.*;

class Test {
	    public static void main(String[] args) {

		    Map<String, String> databaseDNS = new HashMap<String, String>(1);

		    databaseDNS.put("1234", "primeiro");
		    databaseDNS.put("2345", "segundo");
		    databaseDNS.put("3456", "terceiro");

		    String[] str = new String(args[0]).split(",");

		    System.out.println("Stuff: " + str[0].length() + str[1].length());
	    }
}
