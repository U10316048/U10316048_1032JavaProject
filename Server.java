import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class Server{
	
	public static final String HOST = "192.168.163.1";
	public static final int PORT = 9527;
	public static final String DISCONNET_TOKEN = "Disconnect"; 
	public static final String USER_FLAG = "Connect: ";
	private Map<String,ServerThread> cs;
	
	
	
	public static void main(String[] args){
		
		new Server().startup();
		
	}
	
	private void startup(){
		ServerSocket ss = null;
		try {
			ss = new ServerSocket(PORT);
			cs = new HashMap<String,ServerThread>();
			while(true){
				try {
					Socket s = ss.accept();
					new Thread(new ServerThread(s)).start();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(ss!=null){
				try {
					ss.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private class ServerThread implements Runnable{
		private Socket s;
		private BufferedReader br;
		private PrintWriter out;
		private String name;
		private boolean stop = false;
		
		public ServerThread(Socket s) throws IOException{
			this.s = s;
			br = new BufferedReader(new InputStreamReader(s.getInputStream()));
			out = new PrintWriter(s.getOutputStream(),true);
			name = br.readLine();
			name+="["+s.getInetAddress().getHostAddress()+"]";
			cs.put(name,this);
			send(name+" Connected.");
			sendUser();
		}
		
		private void sendUser(){
			String us = USER_FLAG;
			Set<String> keys = cs.keySet();
			for(String u:keys){
				us+=u+",";
			}
			send(us);
		}
		
		private void close(){
			stop = true;
			out.println(DISCONNET_TOKEN);
			cs.remove(name);
			send(name+" Disconneted.");
			sendUser();
		}
		
		private void send(String msg){
			Set<String> keys = cs.keySet();
			for(String key:keys){
				cs.get(key).out.println(msg);
			}
		}
		
		@Override
		public void run(){

				try {
					while(true){
						if(stop){
							break;
						}
						String str = br.readLine();
						if(str.equals(DISCONNET_TOKEN)){
							close();
							break;
						}
						send(name+" : "+str);
					}
				} catch(SocketException e){
					System.out.println(name+"Abnormal exit.");
					close();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					if(s!=null){
						try {
							s.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
				
		}
	}
}
