import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Client extends JFrame implements ActionListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JTextArea chatArea;
	JTextField inputArea;
	JPanel panel;
	JButton send;
	JScrollPane jsp1,jsp2;
	JList userList;
	DefaultListModel member;
	String name;
	Socket s;
	BufferedReader br;
	PrintWriter out;
	boolean stop = false;
	
	public Client(String name){
		this.name = name;
		this.setTitle("Chat || User : ["+this.name+"]");
		this.setLocationRelativeTo(null);
		this.setSize(800,600);
		this.setResizable(false);
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e){
				out.println(Server.DISCONNET_TOKEN);
			}
		});
		
		chatArea = new JTextArea();
		jsp1 = new JScrollPane(chatArea);
		inputArea = new JTextField(50);
		
		inputArea.addKeyListener(new enter());
		
		send = new JButton("Send");
		
		send.addActionListener(this);
		
		member = new DefaultListModel();
		member.addElement("");
		
		userList = new JList(member);
		userList.setFixedCellWidth(200);
		
		jsp2 = new JScrollPane(userList);
		panel = new JPanel();
		
		panel.add(inputArea);
		panel.add(send);
		
		this.add(jsp1);
		this.add(panel,BorderLayout.SOUTH);
		this.add(jsp2,BorderLayout.WEST);
		
		connect();
		
		this.setVisible(true);
	}
	
	public void connect(){
		try {
			s = new Socket(Server.HOST,Server.PORT);
			br = new BufferedReader(new InputStreamReader(s.getInputStream()));
			out = new PrintWriter(s.getOutputStream(),true);
			out.println(name);
			new Thread(new ReceiveThread()).start();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void send(){
		String word = inputArea.getText();
		if(word==null || word.equals("")){
			return;
		}
		out.println(word);
		inputArea.setText("");
	}
	
	public void actionPerformed(ActionEvent e){
		if(e.getSource()==send){
			send();
		}
	}
	
	private void close(){
		stop = true;
	}
	
	private void handleList(String str){
		String users = str.substring(Server.USER_FLAG.length());
		String[] us = users.split(",");
		member.removeAllElements();
		member.addElement("User Online >>> ");
		for(String u:us){
			member.addElement(u);
		}
	}
	
	private void receive() throws IOException{
		String str = br.readLine();
		if(str.equals(Server.DISCONNET_TOKEN)){
			close();
		}
		if(str.startsWith(Server.USER_FLAG)){
			handleList(str);
			return;
		}
		chatArea.setText(chatArea.getText()+str+"\n");
	}
	
	private class enter extends KeyAdapter{
		@Override
		public void keyPressed(KeyEvent e){
			if(e.getKeyCode()==KeyEvent.VK_ENTER){
				send();
			}
		}
	}
	
	private class ReceiveThread implements Runnable{
		@Override
		public void run(){
			
				try {
					while(true){
						if(stop) break;
						receive();
					}
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					if(s!=null){
						try {
							s.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
						System.exit(0);
					}
				}
		}
	}
	
}
