import java.net.*;
import java.io.*;
import java.util.*;

public class Server{
	HashMap clients;
	Stack st;
	String[] Colors= {"red","orange","green","blue","magenta"};
	public Server() {
		clients=new HashMap();
		st=new Stack();
		Collections.synchronizedMap(clients);
	}
	public void start() {
		ServerSocket serverSocket=null;
		Socket socket=null;
		try {
			String colorname = null;
			serverSocket=new ServerSocket(7777);
			System.out.println("서버가 준비되었습니다.");
			while(true) {
				socket=serverSocket.accept();
				System.out.println("["+socket.getInetAddress()+":"+socket.getPort()+"로부터 연결요청이 들어왔습니다.");
				if(clients.size()<5) {
					int num=(int)(Math.random()*5);
					if(!st.empty()) {
						while(st.indexOf(Colors[num])>-1) {
							num=(int)(Math.random()*5);
						}
						colorname=Colors[num];
						st.push(colorname);
					}else {
						colorname=Colors[num];
						st.push(colorname);
					}
					ServerReceiver thread=new ServerReceiver(socket,colorname);
					thread.start();
				}else {
					System.out.println("연결요청 거부됨");
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	void nameToAll() {
		Iterator it=clients.keySet().iterator();
		String name="",na="name";
		while(it.hasNext()) {
			name+=(String)it.next()+"\n";
		}
		sendToAll(name,na);
	}
	void sendToAll(String msg,String colorname) {
		Iterator it=clients.keySet().iterator();
		while(it.hasNext()) {
			try {
				DataOutputStream out=(DataOutputStream)clients.get(it.next());
				out.writeUTF(colorname+"\n"+msg);
			}catch(IOException e) {}
		}
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new Server().start();
	}
	class ServerReceiver extends Thread{
		Socket socket;
		DataInputStream in;
		DataOutputStream out;
		String color;
		ServerReceiver(Socket socket,String colorname){
			this.socket=socket;
			color=colorname;
			try {
				in=new DataInputStream(socket.getInputStream());
				out=new DataOutputStream(socket.getOutputStream());
			}catch(IOException e) {}
		}
		public void run() {
			String name="";
			try {
				name=in.readUTF();
				clients.put(name, out);
				nameToAll();
				sendToAll(name+"님이 들어오셨습니다.",color);
				System.out.println("현재 접속자 수 : "+clients.size());
				while(in!=null) {
					sendToAll(in.readUTF(),color);
				}
			}catch(IOException e) {
				
			}finally {
				sendToAll(name+"님이 나가셨습니다.",color);
				clients.remove(name);
				nameToAll();
				st.remove(color);
				System.out.println("현재 접속자 수 : "+clients.size());
			}
		}
	}

}