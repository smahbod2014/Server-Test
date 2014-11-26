package koda.server;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import koda.handlers.IDManager;

public class Server {
	
	public static final Object lock = new Object();
	public static final Object data_sent = new Object();
	
	public static final int RUNNING = 0;
	public static final int DISCONNECTED_BY_SERVER = 1;
	public static final int CLIENT_INITIATED_DISCONNECT = 1;
	public static final int SERVER_SHUTTING_DOWN = 2;
	
	public static int port = 2406;
	public static String ip = "";
	public static int count = 0;
	
	public static ServerSocket server;
	public static ArrayList<ClientData> client_data = new ArrayList<ClientData>();
	/*public static ArrayList<Socket> list_sockets = new ArrayList<Socket>();
	public static ArrayList<Integer> list_client_states = new ArrayList<Integer>();
	public static ArrayList<DataPackage> list_data = new ArrayList<DataPackage>();*/
	
	public static DataPackage new_user = null;
	public static DataPackage former_user = null;
	
	public static IDManager id_manager = new IDManager(5);
	
	private static Runnable accept = new Runnable() {

		@Override
		public void run() {
			new Thread(send).start();
			new Thread(receive).start();
			
			while (true) {
				try {
					Socket socket = server.accept();
					int state = Server.RUNNING;
					DataPackage dp = new DataPackage(id_manager.allocateId());
					
					ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
					String username = (String) ois.readObject();
					dp.username = username;
					
					ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
					if (dp.id != -1) {
						oos.writeObject("Welcome to the server!");
						//list_client_states.add(RUNNING);
						state = Server.RUNNING;
						new_user = dp;
					} else {
						oos.writeObject("The server is at capacity!");
						//list_client_states.add(DISCONNECTED_BY_SERVER);
						state = Server.DISCONNECTED_BY_SERVER;
					}
					
					//oos.writeObject(dp.id);
					//oos.writeObject(list_data);
					
					list_clients_model.addElement(username + " - " + socket.getInetAddress().getHostAddress() + " - " + socket.getInetAddress().getHostName());
					
					list_sockets.add(socket);
					synchronized (lock) {
						list_data.add(dp);
					}
					
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	};
	
	private static Runnable send = new Runnable() {

		@Override
		public void run() {
			ObjectOutputStream oos;
			
			while (true) {
				for (int i = 0; i < list_sockets.size(); i++) {
					try {
						//System.out.println("Starting send thread");
						oos = new ObjectOutputStream(list_sockets.get(i).getOutputStream());
						int client_state = list_client_states.get(i);
						oos.writeObject(client_state);
						
						//oos = new ObjectOutputStream(list_sockets.get(i).getOutputStream());
						
						synchronized (lock) {
							oos.writeObject(list_data);
						}
						
						oos.writeObject(new_user);
						oos.writeObject(former_user);
						new_user = null;
						former_user = null;
						
						switch (client_state) {
						case RUNNING:
							//do nothing
							break;
						case DISCONNECTED_BY_SERVER:
							//System.out.println("Disconnecting client: " + client_state);
							disconnectClient(i);
							i--;
							break;
						case SERVER_SHUTTING_DOWN:
							//System.out.println("Disconnecting client: " + client_state);
							disconnectClient(i);
							i--;
							break;
						}
					} catch (IOException e) {
						//e.printStackTrace();
					}
				}
				
				
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	};
	
	private static Runnable receive = new Runnable() {

		@Override
		public void run() {
			ObjectInputStream ois;
			
			while (true) {
				for (int i = 0; i < list_sockets.size(); i++) {
					try {
						ois = new ObjectInputStream(list_sockets.get(i).getInputStream());
						int receive_state = (Integer) ois.readObject();
						
						//ois = new ObjectInputStream(list_sockets.get(i).getInputStream());
						DataPackage dp = (DataPackage) ois.readObject();
						
						synchronized(lock) {
							list_data.set(i, dp);
						}
						
						if (receive_state == CLIENT_INITIATED_DISCONNECT) {
							//System.out.println("Client initiated disconnect!");
							disconnectClient(i);
							i--;
						}
						
					} catch (Exception e) {
						//client didn't notify server about disconnecting
						e.printStackTrace();
						disconnectClient(i);
						i--;
					}
				}
				
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	};
	
	public static void disconnectClient(int index) {
		list_clients_model.removeElementAt(index);
		list_client_states.remove(index);
		DataPackage dp = null;
		synchronized (lock) {
			dp = list_data.remove(index);
		}
		list_sockets.remove(index);
		id_manager.releaseId(dp.id);
		former_user = dp;
		//System.out.println("Server releasing id " + dp.id);
	}
	
	public static JFrame frame;
	public static JPanel panel1;
	public static JPanel panel2;
	public static JPanel panel3;
	public static JPanel content;
	public static JButton btn_disconnect;
	public static JList list_clients;
	public static DefaultListModel list_clients_model;
	
	public static void main(String[] args) throws Exception {
		//UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		try {
			ip = InetAddress.getLocalHost().getHostAddress() + ":" + port;

			server = new ServerSocket(port, 0, InetAddress.getLocalHost());
			new Thread(accept).start();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Error: " + e.getMessage(), "Alert", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
		
		btn_disconnect = new JButton();
		btn_disconnect.setText("Disconnect");
		btn_disconnect.setEnabled(true);
		btn_disconnect.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				int selected = list_clients.getSelectedIndex();
				
				if (selected != -1) {
					try {
						list_client_states.set(selected, DISCONNECTED_BY_SERVER);
					} catch (Exception ex) {
						JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage(), "Alert", JOptionPane.ERROR_MESSAGE);
					}
					
				}
			}
		});
		
		list_clients_model = new DefaultListModel();
		list_clients = new JList(list_clients_model);
		list_clients.addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting()) {
					//System.out.println(list_clients.getSelectedIndex());
				}
			}
		});
		
		frame = new JFrame();
		frame.setTitle("Server - " + ip);
		frame.addWindowListener(new WindowListener() {
			
			public void windowOpened(WindowEvent arg0) {}
			public void windowIconified(WindowEvent arg0) {}
			public void windowDeiconified(WindowEvent arg0) {}
			public void windowDeactivated(WindowEvent arg) {}
			
			@Override
			public void windowClosing(WindowEvent arg0) {
				while (list_sockets.size() != 0) {
					for (int i = 0; i < list_client_states.size(); i++) {
						try {
							list_client_states.set(i, SERVER_SHUTTING_DOWN);
						} catch (Exception e) {}
						
					}
				}
				System.exit(0);
			}
			
			public void windowClosed(WindowEvent arg0) {}
			public void windowActivated(WindowEvent arg0) {}
		});
		
		panel1 = new JPanel();
		panel1.setLayout(new GridLayout(1, 1, 1, 1));
		panel1.add(btn_disconnect);
		
		panel2 = new JPanel();
		panel2.add(new JLabel(ip));
		
		panel3 = new JPanel();
		panel3.setLayout(new BorderLayout(1, 1));
		panel3.add(panel1, BorderLayout.NORTH);
		panel3.add(new JScrollPane(list_clients), BorderLayout.CENTER);
		panel3.add(panel2, BorderLayout.SOUTH);
		
		content = new JPanel();
		content.setLayout(new GridLayout(1, 1, 1, 1));
		content.add(panel3);
		content.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		frame.setContentPane(content);
		frame.pack();
		frame.setSize(350, 400);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
}
