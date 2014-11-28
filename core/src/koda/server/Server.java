package koda.server;

import java.awt.BorderLayout;
import java.awt.Dimension;
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
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import koda.handlers.IDManager;

public class Server {
	
	public static final Object lock = new Object();
	public static final Object data_sent = new Object();
	
	public static volatile boolean package_sent = false;
	
	public static final int MAX_CAPACITY = 5;
	
	public static final int RUNNING = 0;
	public static final int DISCONNECTED_BY_SERVER = 1;
	public static final int CLIENT_INITIATED_DISCONNECT = 1;
	public static final int SERVER_SHUTTING_DOWN = 2;
	
	public static int port = 2406;
	public static String ip = "";
	public static int count = 0;
	public static int tick = 0;
	public static int next_available_index = 0;
	public static int num_clients_connected = 0;
	
	public static synchronized int updateTick() {
		int t = tick;
		tick++;
		return t;
	}
	
	public static ServerSocket server;
	//public static ArrayList<ClientData> client_data = new ArrayList<ClientData>();
	public static ClientData[] client_data = new ClientData[MAX_CAPACITY];
	public static ClientPackage client_package = new ClientPackage(MAX_CAPACITY);
	/*public static ArrayList<Socket> list_sockets = new ArrayList<Socket>();
	public static ArrayList<Integer> list_client_states = new ArrayList<Integer>();*/
	//public static ArrayList<DataPackage> list_data = new ArrayList<DataPackage>();
	
	public static DataPackage new_user = null;
	public static DataPackage former_user = null;
	
	public static IDManager id_manager = new IDManager(MAX_CAPACITY);
	
	private static Runnable accept = new Runnable() {

		@Override
		public void run() {
			new Thread(send).start();
			new Thread(receive).start();
			
			while (true) {
				try {
					Socket socket = server.accept();
					
					ClientData cd = new ClientData();
					int id = id_manager.allocateId();
					int state = Server.RUNNING;
					DataPackage dp = new DataPackage();
					
					//initialize this
					cd.id = id;
					cd.socket = socket;
					cd.state = state;
					cd.data_package = dp;
					dp.id = id;
					
					
					ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
					String username = (String) ois.readObject();
					dp.username = username;
					
					String model = "ID " + id + ": " + username + " - " + socket.getInetAddress().getHostAddress() + " - " + socket.getInetAddress().getHostName();
					
					cd.model = model;
					
					client_data[id] = cd;
					
					ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
					if (id != 0) {
						oos.writeObject("Welcome to the server!");
						//list_client_states.add(RUNNING);
						state = Server.RUNNING;
						new_user = dp;
						num_clients_connected++;
					} else {
						oos.writeObject("The server is at capacity!");
						//list_client_states.add(DISCONNECTED_BY_SERVER);
						state = Server.DISCONNECTED_BY_SERVER;
					}
					
					client_package.state = state;
					client_package.id = id;
					list_clients_model.addElement(model);
					//oos = new ObjectOutputStream(socket.getOutputStream());
					//oos.writeObject(client_package);
					oos.writeObject(client_package);
					
					client_package.list_data[id] = dp;
					//client_data[next_available_index] = new ClientData(dp, state, model, socket);
					
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
				for (int i = 0; i < MAX_CAPACITY; i++) {
					if (client_data[i] == null) {
						continue;
					}
					
					try {
						ClientData cd = client_data[i];
						
						oos = new ObjectOutputStream(cd.socket.getOutputStream());
						client_package.state = cd.state;
						client_package.new_user = new_user;
						client_package.former_user = former_user;
						
						
						
						
						oos.writeObject(client_package);
						oos.reset();
						
						
						switch (client_package.state) {
						case RUNNING:
							//do nothing
							break;
						case DISCONNECTED_BY_SERVER:
							//System.out.println("Disconnecting client: " + client_state);
							disconnectClient(i);
							//i--;
							break;
						case SERVER_SHUTTING_DOWN:
							//System.out.println("Disconnecting client: " + client_state);
							disconnectClient(i);
							//i--;
							break;
						}
					} catch (IOException e) {
						//e.printStackTrace();
					}
				}
				
				//new_user = null;
				//former_user = null;
				
				
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
				for (int i = 0; i < MAX_CAPACITY; i++) {
					if (client_data[i] == null) {
						continue;
					}
					
					try {
						ClientData cd = client_data[i];
						ois = new ObjectInputStream(cd.socket.getInputStream());
						//int receive_state = (Integer) ois.readObject();
						ServerPackage packet = (ServerPackage) ois.readObject();
						
						//ois = new ObjectInputStream(list_sockets.get(i).getInputStream());
						//DataPackage dp = (DataPackage) ois.readObject();
						
						//synchronized(lock) {
						client_package.list_data[i] = packet.dp;
						//}
						
						switch (packet.state) {
						case CLIENT_INITIATED_DISCONNECT:
							disconnectClient(i);
							//i--;
							break;
						}
						
					} catch (Exception e) {
						//client didn't notify server about disconnecting
						e.printStackTrace();
						disconnectClient(i);
						//i--;
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
	
	public static void getUpdatedIndex() {
		for (int i = 0; i < MAX_CAPACITY; i++) {
			if (client_data[i] == null) {
				next_available_index = i;
				return;
			}
		}
	}
	
	public static void disconnectClient(int index) {
		list_clients_model.removeElementAt(index);
		former_user = client_package.list_data[index];
		client_package.list_data[index] = null;
		client_data[index] = null;
		id_manager.releaseId(index);
		num_clients_connected--;
	}
	
	public static JFrame frame;
	public static JPanel panel1;
	public static JPanel panel2;
	public static JPanel panel3;
	public static JPanel content;
	public static JButton btn_disconnect;
	public static JList<String> list_clients;
	public static JList<String> log_statements;
	public static DefaultListModel<String> list_clients_model;
	
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
						String string_id = list_clients.getSelectedValue();
						string_id = string_id.substring(string_id.indexOf(" ") + 1, string_id.indexOf(":"));
						int id = Integer.parseInt(string_id);
						
						client_data[id].state = Server.DISCONNECTED_BY_SERVER;
					} catch (Exception ex) {
						JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage(), "Alert", JOptionPane.ERROR_MESSAGE);
					}				
				}
			}
		});
		
		list_clients_model = new DefaultListModel<String>();
		list_clients = new JList<String>(list_clients_model);
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
				while (num_clients_connected > 0) {
					for (int i = 0; i < MAX_CAPACITY; i++) {
						if (client_data[i] == null) {
							continue;
						}
						
						try {
							ClientData cd = client_data[i];
							cd.state = SERVER_SHUTTING_DOWN;
							//list_client_states.set(i, SERVER_SHUTTING_DOWN);
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
		//panel3.setMaximumSize(new Dimension(350, 400));
		
		log_statements = new JList<String>();
		JPanel panel4 = new JPanel();
		panel4.setLayout(new BorderLayout(1, 1));
		panel4.add(new JLabel("Log"), BorderLayout.NORTH);
		panel4.add(new JScrollPane(log_statements), BorderLayout.CENTER);
		//panel4.add(panel3, BorderLayout.WEST);
		panel4.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
		panel4.setMinimumSize(new Dimension(700 - 350, 400));
		
		
		content = new JPanel();
		content.setLayout(new GridLayout(1, 2, 1, 1));
		content.add(panel3);
		//content.add(panel4);
		content.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		frame.setContentPane(content);
		frame.pack();
		frame.setSize(350, 400);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
}
