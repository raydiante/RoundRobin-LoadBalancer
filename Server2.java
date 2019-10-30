import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class Server2 {

	final static int WRITE = 0;
	final static int READ = 1;
	final static int SD1 = 0;
	final static int SD2 = 1;
	final static int SD3 = 3;
	static String ipBalancer = "localhost";
	
	public static void main(String[] args) throws IOException {
		ipBalancer = args[0];
		String s;
		FileWriter arq = new FileWriter("SD2.txt",false);
		
		
		
		new Thread() { // do the consistency
			public void run() {
				try{
					ServerSocket serverConsistency = new ServerSocket(12222);

					while (true) {
						Socket clientConsistency = serverConsistency.accept();
						ObjectInputStream inp = new ObjectInputStream(clientConsistency.getInputStream());
						
						String s = inp.readUTF();
						writeFile(s);

						inp.close();
						clientConsistency.close();
		
					}
		
				} catch (Exception e) {
					System.out.println("Erro (thread): " + e.getMessage());
				}
			}
		}.start();
		
		try {
			ServerSocket server = new ServerSocket(11112);
			//Data of clients
			while (true) {
				// o método accept() bloqueia a execução até que
				// o server receba um pedido de conexão
				Socket client = server.accept();
				
				ObjectInputStream inp = new ObjectInputStream(client.getInputStream());
				int typeOfConection = inp.readInt();

			    
				if(typeOfConection == WRITE) {
					int numb = inp.readInt();
					boolean resp = verificaPrimo(numb);
				    
					if(resp) {
						s="O valor "+numb+ " é primo";
						System.out.println(s);
					}else {
						s="O valor "+numb+ " não é primo";
						System.out.println(s);
					}
					writeFile(s);
					consitencySignal(s,ipBalancer);
				}else {
					
					readFile();
				}

				

				inp.close();
				client.close();

			}

		} catch (Exception e) {
			System.out.println("Erro (server): " + e.getMessage());
		}
		
	}
	
	public static boolean verificaPrimo(int n) {

		for (int i = 2; i < n; i++) {
			if (n % i == 0) {
				return false;
			}
		}
		return true;

	}
	
	public static void consitencySignal(String s,String ipBalancer) {

		try {
			Socket consis = new Socket(ipBalancer, 22222);
			ObjectOutputStream num = new ObjectOutputStream(consis.getOutputStream());
			num.writeInt(SD2); 
			num.flush();
			num.writeUTF(s); 
			num.flush();
			num.close();
			consis.close();
			
		} catch (Exception e) {
			System.out.println("Erro (consitencySignal): " + e.getMessage());
		}
	}
	
	public static void readFile() {

		try {
			BufferedReader buffRead = new BufferedReader(new FileReader("SD2.txt"));
	        String linha = "";
	        while (true) {
	            if (linha != null) {
	                System.out.println(linha);
	 
	            } else
	                break;
	            linha = buffRead.readLine();
	        }
	        buffRead.close();
			
		} catch (Exception e) {
			System.out.println("Erro (readFile): " + e.getMessage());
		}
	}
	
	public static void writeFile(String s) {

		try {
			FileWriter arq = new FileWriter("SD2.txt",true);
		    PrintWriter gravarArq = new PrintWriter(arq);
			gravarArq.printf(s);
			gravarArq.printf("\n");
			arq.close();
		} catch (Exception e) {
			System.out.println("Erro (writeFile): " + e.getMessage());
		}
	}
	public static void consitencySignalToFinish() {

		try{
			Socket consis = new Socket(ipBalancer, 12312);
			ObjectOutputStream num = new ObjectOutputStream(consis.getOutputStream());
			num.writeInt(1); 
			num.flush();
			num.close();
			consis.close();
		} catch (Exception e) {
			System.out.println("Erro (consitencySignalToFinish): " + e.getMessage());
		}
	}
}
