

import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Random;

public class Client {
	final static int WRITE = 0;
	final static int READ = 1;
	final static int CLIENT1 = 1;
	final static int CLIENT2 = 2;
	static String ipBalancer = "localhost";
	
	public static void main(String[] args) {
		ipBalancer = args[0];
		try {
			
			new Thread() { //cliente 1
							
				public void run() {
					try{
						while(true){

							Socket client = new Socket(ipBalancer, 11345);
							ObjectOutputStream num = new ObjectOutputStream(client.getOutputStream());
							
							Random gerador = new Random();
							int i = gerador.nextInt(2);
							
							
							
							if(i==0){ //read
								num.flush();
								num.writeInt(CLIENT1); //send the client id
								num.flush();
								num.writeInt(READ); //send the signal to read
								System.out.println("Leitura enviada");
								// num.close();
								num.flush();
								num.reset();
							}else{ //write
								i = 1 + gerador.nextInt(1000001); //random number btw 1 and 1000000
								num.flush();
								num.writeInt(CLIENT1); //send the client id
								num.flush();
								num.writeInt(WRITE);  //send the signal to write
								System.out.println("Valor "+ i + " enviado");
								// num.close();
								num.flush();
								num.writeInt(i);
								num.flush();
								num.reset();
							}
							
							num.close();
							client.close();
							
							i = 50 + gerador.nextInt(201); 
							Thread.currentThread().sleep(i);
						}
					}catch (Exception e) {
						System.out.println("Erro: " + e.getMessage());
					}
				}
			}.start();
			
			new Thread() { //cliente 2
				
				public void run() {
					try{
						while(true){

							Socket client2 = new Socket(ipBalancer, 11345);
							ObjectOutputStream num = new ObjectOutputStream(client2.getOutputStream());
							
							Random gerador = new Random();
							int i = gerador.nextInt(2);
							
							
							
							if(i==0){ //read
								num.flush();
								num.writeInt(CLIENT2); //send the client id
								num.flush();
								num.writeInt(READ); //send the signal to read
								System.out.println("Leitura enviada");
								// num.close();
								num.flush();
								num.reset();
							}else{ //write
								i = 1 + gerador.nextInt(1000001); //random number btw 1 and 1000000
								num.flush();
								num.writeInt(CLIENT2); //send the client id
								num.flush();
								num.writeInt(WRITE);  //send the signal to write
								System.out.println("Valor "+ i + " enviado");
								// num.close();
								num.flush();
								num.writeInt(i);
								num.flush();
								num.reset();
							}
							num.close();
							client2.close();
							i = 50 + gerador.nextInt(201); 
							Thread.currentThread().sleep(i);
						}
					}catch (Exception e) {
						System.out.println("Erro: " + e.getMessage());
					}
				}
			}.start();

			
		} catch (Exception e) {
			System.out.println("Erro: " + e.getMessage());
		}
	}

	
}
