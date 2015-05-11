import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class Login extends JFrame implements ActionListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	JLabel loginLabel;
	JButton loginButton;
	JTextField loginTextField;
	
	public Login(){
		
		
		loginLabel = new JLabel("User name:");
		loginButton = new JButton("Login");
		loginTextField = new JTextField(20);
		
		add(loginLabel);
		add(loginTextField);
		add(loginButton);
		
		loginButton.addActionListener(this);
		loginTextField.addKeyListener(new enter());
	}
	
	public void close(){
		this.setVisible(false);
	}
	
	public void actionPerformed(ActionEvent e){
		String name = loginTextField.getText();
		if(name==null || name.equals("")){
			JOptionPane.showMessageDialog(null,"User name is invalid!","Note",JOptionPane.WARNING_MESSAGE);
			return;
		}
		new Client(name);
		close();
	}
	
	private class enter extends KeyAdapter{
		@Override
		public void keyPressed(KeyEvent e){
			if(e.getKeyCode()==KeyEvent.VK_ENTER){
				String name = loginTextField.getText();
				if(name==null || name.equals("")){
					JOptionPane.showMessageDialog(null,"User name is invalid!","Note",JOptionPane.WARNING_MESSAGE);
					return;
				}
				new Client(name);
				close();
			}
		}
	}
	
	public static void main(String[] args){
		Login frame = new Login();
		frame.setTitle("Login");
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(350,100);
		frame.setResizable(false);
		frame.setLayout(new FlowLayout());
		frame.setVisible(true);
	}
}
