
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;

public class Balancer {

	final static int WRITE = 0;
	final static int READ = 1;
	final static int CLIENT1 = 1;
	final static int CLIENT2 = 2;
	final static int SD1 = 0;
	final static int SD2 = 1;
	final static int SD3 = 3;
	static String ip1 = "localhost";
	static String ip2 = "localhost";
	static String ip3 = "localhost";
	static ArrayList<ArrayList<Integer>> requests;

	static boolean pausado = false;
	/*
	 * Index newRequest
	 * 0 - clientID
	 * 1 - typeOfConection
	 * 2 - server
	 * 3 - number
	 * */

	public static void main(String[] args) {
		ip1 = args[0];
		ip2 = args[1];
		ip3 = args[2];
		requests = new ArrayList<ArrayList<Integer>>();
		
		
		new Thread() { //1- Process the requests and add then to the list
			
			public void run() {
				try{
					// Instancia o ServerSocket ouvindo a porta 12345
		
					// System.out.println("server ouvindo a porta 12345");
					ServerSocket server = new ServerSocket(11345);
					//Data of clients
					while (true) {
						
						// o método accept() bloqueia a execução até que
						// o server receba um pedido de conexão
						Socket client = server.accept();
						ArrayList<Integer> lista = new ArrayList<Integer>();
						
						
						ObjectInputStream inp = new ObjectInputStream(client.getInputStream());
						int clientID = inp.readInt();
						int typeOfConection = inp.readInt();
						// choose btw the 3 servers
						Random gerador = new Random();
						int i = gerador.nextInt(3)+1;
						
		
						lista.add(clientID);
						lista.add(typeOfConection);
						lista.add(i);
		
		
						if(typeOfConection == WRITE){ //WRITE
							int number = inp.readInt();
							lista.add(number);
						}
						requests.add(lista);
						//System.out.println(requests.size());
		
						inp.close();
						client.close();
		
					}
		
				} catch (Exception e) {
					System.out.println("Erro (Thread1): " + e.getMessage());
				}
			}
		}.start();
		

		new Thread() { // do the consistency
			
			public void run() {
				try {
					while (true) {
						//System.out.println("D-pausado "+ pausado );
						System.out.print("");
						if(!pausado) {
							distributeTasks();
							Thread.currentThread().sleep(100);
						}
					}
				} catch (Exception e) {
					System.out.println("Erro (Thread2): " + e.getMessage());
				}
			}
		}.start();
		
		new Thread() { // 3- do the consistency
			
			public void run() {
				try{
					
					ServerSocket server = new ServerSocket(22222);

					while (true) {
						
						Socket client = server.accept();
						ObjectInputStream inp = new ObjectInputStream(client.getInputStream());
						pausado=true;
						System.out.println("Processo de consistência iniciado, enfileirando requisições");
						//System.out.println("Requisicoes em fila: " + requests.size());
						int reqSize = requests.size();
						
						int clientID = inp.readInt();
						String s = inp.readUTF();

						inp.close();
						client.close();
						
						switch (clientID) {
							case SD1:
								consitency(s,ip2,12222);
								consitency(s,ip3,12223);
								break;
							case SD2:
								consitency(s,ip1,33331);
								consitency(s,ip3,12223);
								break;
							case SD3:
								consitency(s,ip1,33331);
								consitency(s,ip2,12222);
								break;
						
						}
						Thread.currentThread().sleep(100);
						int newReq = requests.size()-reqSize;
						//System.out.println("Requisicoes em fila:" + requests.size());
						System.out.println("Procedimento de consistência finalizado, foram enfileiradas "+ newReq + " requisições");
						pausado=false;
						//System.out.println("C-pausado "+ pausado );
		
					}

		
				} catch (Exception e) {
					System.out.println("Erro (Thread3): " + e.getMessage());
				}
			}
		}.start();
		
	
	}
	public static void consitency(String s,String ip,int porta) {

		try {
			Socket consis = new Socket(ip, porta);
			ObjectOutputStream num = new ObjectOutputStream(consis.getOutputStream());
			num.flush();
			num.writeUTF(s); 
			num.reset();
			num.close();
			consis.close();
			
		} catch (Exception e) {
			System.out.println("Erro (consitency): " + e.getMessage() + " Porta " + porta );
		}
	}
	
	
	public static void distributeTasks() {
		
		try{

			System.out.print("");
			if(!requests.isEmpty()) {
				System.out.print("");
				//System.out.println("D-Requisicoes em fila: " + requests.size());
				ArrayList<Integer> newRequest = requests.get(0);
				requests.remove(0);
				//System.out.println("D-Requisicoes em fila: " + requests.size());
				ObjectOutputStream num;
				

				//System.out.println(newRequest.get(1));
				
				if(newRequest.get(1) == WRITE){ //WRITE
					System.out.println("Valor "+newRequest.get(3) + " recebido do cliente "+ newRequest.get(0) +" direcionada para o servidor de dados "+ newRequest.get(2));
					switch (newRequest.get(2)) {
						case 1:
							Socket sd1 = new Socket(ip1, 11111);
							num = new ObjectOutputStream(sd1.getOutputStream());
							num.flush();
							num.writeInt(newRequest.get(1)); 
							num.flush();
							num.writeInt(newRequest.get(3)); 
							num.flush();
							num.close();
							sd1.close();
							break;
						
						case 2:
							Socket sd2 = new Socket(ip2, 11112);
							num = new ObjectOutputStream(sd2.getOutputStream());
							num.flush();
							num.writeInt(newRequest.get(1)); 
							num.flush();
							num.writeInt(newRequest.get(3));
							num.flush();
							num.reset();
							num.close();
							sd2.close();
							break;
							
						case 3:
							Socket sd3 = new Socket(ip3, 11113);
							num = new ObjectOutputStream(sd3.getOutputStream());
							num.flush();
							num.writeInt(newRequest.get(1)); 
							num.flush();
							num.writeInt(newRequest.get(3)); //send the signal to read
							num.flush();
							num.reset();
							num.close();
							sd3.close();
							break;
							

					}
					
					
				}else{ //READ	
					System.out.println("Leitura encaminhada pelo cliente "+ newRequest.get(0) +" direcionada para o servidor de dados "+ newRequest.get(2));
					switch (newRequest.get(2)) {
						case 1:
							Socket sd1 = new Socket(ip1, 11111);
							num = new ObjectOutputStream(sd1.getOutputStream());
							num.writeInt(newRequest.get(1)); //send the signal to read
							num.flush();
							num.close();
							sd1.close();
							break;
							
						case 2:
							Socket sd2 = new Socket(ip2, 11112);
							num = new ObjectOutputStream(sd2.getOutputStream());
							num.writeInt(newRequest.get(1)); 
							num.flush();
							num.close();
							sd2.close();
							break;
							
						case 3:
							Socket sd3 = new Socket(ip3, 11113);
							num = new ObjectOutputStream(sd3.getOutputStream());
							num.writeInt(newRequest.get(1)); 
							num.flush();
							num.close();
							sd3.close();
							break;

					}
				}
				
				

			}
		}catch (Exception e) {
			System.out.println("Erro(distributeTasks): " + e.getMessage());
		}
		
	}
}
