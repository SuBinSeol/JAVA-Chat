import java.net.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.*;

import java.io.*;
import java.awt.*;
import java.awt.event.*;

public class Client extends JFrame implements Runnable{
	Socket socket;
	DataOutputStream out;
	DataInputStream in;
	String name="";
	Color c;
	
	JPanel p=new JPanel();
	JPanel p2=new JPanel();
	JPanel p3=new JPanel();
	JTextPane textPane=new JTextPane();
	JTextArea n_ta=new JTextArea(" 접속한 사람 ");
	JTextField tf=new JTextField();
	Button go_bt=new Button("전송");
	Button out_bt=new Button("나가기");
	JScrollPane scroll=new JScrollPane(textPane);
	JScrollPane n_scroll=new JScrollPane(n_ta);

	Client(String nickname){	
		super(nickname);
		name=nickname;
		setLayout(new BorderLayout());
		p.setLayout(new BorderLayout());
		p2.setLayout(new BorderLayout());
		p3.setLayout(new BorderLayout());
		
		p.add(tf,"Center");
		p.add(go_bt,"East");
		p2.add(out_bt,"East");

		add(p2,"North");
		add(scroll,"Center");
		add(n_scroll,"East");
		add(p,"South");
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		EventHandler handler=new EventHandler();
		tf.addKeyListener(new MyKeyListener());
		go_bt.addActionListener(handler);
		out_bt.addActionListener(handler);
		textPane.setEditable(false);
		setBounds(300,200,300,500);
		setVisible(true);
	}
	void startClient() {
		try {
			String serverIp = "127.0.0.1";
			Socket socket=new Socket(serverIp,7777);
			textPane.setText("서버에 연결되었습니다.");
			in=new DataInputStream(socket.getInputStream());
			out=new DataOutputStream(socket.getOutputStream());
			if(out!=null) {
				out.writeUTF(name);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		Thread t=new Thread(this);
		t.start();
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String name = JOptionPane.showInputDialog("닉네임을 입력하세요");
		if(!name.equals("")) {
			Client chat=new Client(name);
			chat.startClient();
		}
	}
	class EventHandler extends FocusAdapter implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			Button button=(Button)e.getSource();
			String msg=tf.getText();
			if(button.getLabel().equals("나가기")) {
				System.exit(0);
			}else if(button.getLabel().equals("전송")) {
				if("".equals(msg))return;
				try {
					out.writeUTF("["+name+"]"+msg);
				}catch(IOException e1) {}
				tf.setText("");
			}
		}
	}
    class MyKeyListener extends KeyAdapter {
        public void keyPressed(KeyEvent e) {
            int keyCode = e.getKeyCode();
            String msg=tf.getText();
            if(keyCode == 10) {//c
				if("".equals(msg))return;
				try {
					out.writeUTF("["+name+"]"+msg);
				}catch(IOException e1) {}
				tf.setText("");
            }
            
        }
    }
	public void changeColor(String color) {
        if(color.equals("red")) {
        	c=Color.red;
        }else if(color.equals("orange")) {
        	c=Color.orange;
        }else if(color.equals("green")) {
        	c=Color.green;
        }else if(color.equals("blue")) {
        	c=Color.blue;
        }else if(color.equals("magenta")) {
        	c=Color.magenta;
        }
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
        StyledDocument doc = textPane.getStyledDocument();
        SimpleAttributeSet styleSet = new SimpleAttributeSet();
		while(in!=null) {
			try {
				String n=in.readUTF();
				String[] t=n.split("\n");
				if(t[0].equals("name")) {
					String name=" 접속한 사람 \n";
					for(int j=1;j<t.length;j++) {
						name+=t[j]+"\n";
					}
					n_ta.setText("\n"+name);
				}else {
					changeColor(t[0]);
		            StyleConstants.setForeground(styleSet, c);  // 전경색 지정
		            try {
		                doc.insertString(doc.getLength(), "\n"+t[1], styleSet);
		                textPane.setCaretPosition(textPane.getDocument().getLength());
		            } catch (BadLocationException e1) {
		                e1.printStackTrace();
		            }
				}
			}catch(IOException e) {}
		}
	}
}