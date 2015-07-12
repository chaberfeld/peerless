//////////////////////////////////
////         PEER-LESS        ////  Version 1.0
//////////////////////////////////
// a PEER-to-Peer Server-LESS Distributed File System
//
// 1. Choose a port to run consistently run at
// 2. Create a directory at c:\peerless<port>
// 3. Must place a file into this directory named: "_master.txt" containing at least
//    one other (preferably always online) member node [ip:port]
//    e.g. 127.0.0.1:2004
//    Note: Only the loopback IP supported at this time
// 4. Place peerless.class in any directory
// 5. From this directory, run "java peerless <port>"   e.g. java peerless 2001

 
package com.lab;    // NOTICE COMMENTING

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

//
// Class
//
public class peerless extends Thread {
	
	// GUI variables
	static JFrame frame1;
	static Container pane;
	static JButton btnExist, btnDownload, btnFind, btnContents;
	static JLabel lblServer, lblPort, lblFileName, lblFilePrx;
	static JLabel lblMaster, lblFound;
	static JTextField txtServer, txtPort;
	static JTextField txtFileName, txtFilePrx;
	static Insets insets;
	static JTextArea textMessage;
	static JTextArea textMaster;
	static JTextArea textFound;
	
	// other variables
	static int myPort;
	static String[] arr;
	static String[] arrOut;
	
	// Main method
	//
	public static void main(String args[]) {
		
	// Set port for the Server role; 
	// edit runtime argument
		if (args.length == 1) {
			try {
				myPort = Integer.parseInt(args[0]);
			} catch (NumberFormatException e) {
				System.err.println("Argument" + args[0] + " must be an integer.");
				System.exit(1);
			}
		} else {
			System.exit(1);
		}
	 
		// Start the Server!
		( new peerless() ).start();
		
		// Load master IP list
		Scanner sc = null;
		try {
			sc = new Scanner(new File("C:\\peerless"+ myPort +"\\_master.txt"));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		List<String> lines = new ArrayList<String>();
		while (sc.hasNextLine()) {
			lines.add(sc.nextLine());
		}
		arr = lines.toArray(new String[0]);
		
		// 1. Use _master and responseType to rebuild active IP/ports textarea
		// 2. Save IP/port of peer with the longest _master file
		// 3. After read-thru, if the longest master found exceeds myPort, download it
		//    & replace the local _master 

		// My master size; init 
		File myMasterFile = new File("C:\\peerless"+ myPort +"\\_master.txt");
		long myMasterLong = myMasterFile.length();
		
		int biggerMaster = (int) myMasterLong;
		
		String biggerIP = "";
		int biggerPort = 0;
		
		// Ask each node for; _master size
		arrOut = new String[arr.length];
		for (int i=0; i<arr.length; i++) {
			int sep = arr[i].indexOf(":");
			if (sep >0) {
				String ip = arr[i].substring(0,sep);
				String port = arr[i].substring(sep+1);
				int remotePortInt = Integer.parseInt(port);         
				 
				try {
					Thread.sleep(50);
         			int myI;
           			Socket myS = new Socket(ip, remotePortInt);     		
        			InputStream is = myS.getInputStream();        
        			PrintWriter pw = new PrintWriter(myS.getOutputStream(), true);
        			
        			pw.println("#");		
          			myI = is.read();   // myI =size of remote _master
          			
          			arrOut[i] = (arr[i] + "   Connected");
          			if (myI > biggerMaster) {
          				biggerMaster = myI;
          				biggerIP = ip;
          				biggerPort = remotePortInt;
           			}
        			myS.close();
        			
        		// end try & catch
				}
            	catch (Exception e2) {          		
            		arrOut[i] = arr[i];  // Not connected
				}
				
			// end sep > 0
			}
		
		// end for loop
		}  //e3
				
		//
		// then, download the largest master file available
		//
		try {
    		String fileWanted = "_master.txt";
    		String fileWritten = "_master.txt";
    		String fx = "!";  
    		String remoteIP = biggerIP;
    		int remotePortInt = biggerPort;	
    		int myI;
    		Socket myS = new Socket(remoteIP, remotePortInt);
    		
    		InputStream is = myS.getInputStream();
    		PrintWriter pw = new PrintWriter(myS.getOutputStream(), true);		
    		
    		// Download file fx
    		if (fx.equals("!") ) {
    				pw.println(fx+fileWanted);		
    				int filesize=6022386; // filesize temporary hardcoded
    				int bytesRead;
    				int current = 0;
    				byte [] mybytearray  = new byte [filesize];
    				FileOutputStream fos = new FileOutputStream("C:\\peerless"+ myPort + "\\" + fileWritten);
    				BufferedOutputStream bos = new BufferedOutputStream(fos);
    				bytesRead = is.read(mybytearray,0,mybytearray.length);
    				current = bytesRead;
    				
    				do {
    					bytesRead =	is.read(mybytearray, current, (mybytearray.length-current));
    					if(bytesRead >= 0) current += bytesRead;
    				} while(bytesRead > -1);
    				bos.write(mybytearray, 0 , current);
    				bos.close();
    				myS.close();
    				textMessage.setText("A new Masterlist has been downloaded!");
    				
        	// end if	
    		}
    		
    	// end try & catch
        }
        catch (Exception e2){ }
		
		// with new master, refresh!
		refreshConnect();
		
		// 
		// Build GUI
		//

		//Set Look and Feel
		try {UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());}
		catch (ClassNotFoundException e) {}
		catch (InstantiationException e) {}
		catch (IllegalAccessException e) {}
		catch (UnsupportedLookAndFeelException e) {}
		
		//Create the frame
		frame1 = new JFrame ("P E E R - L E S S       My Port ---> "+myPort+ "     a PEER-to-Peer Sever-LESS Distributed File System");
		frame1.setSize (1000,625);   
		pane = frame1.getContentPane();
		insets = pane.getInsets();
		pane.setLayout (null);
		
		//Create controls
		btnExist = new JButton ("Exists?");
		btnDownload = new JButton("Download");
		btnFind = new JButton("Find");
		btnContents = new JButton("Contents");
		lblServer = new JLabel ("Peers IP:");
		lblFileName = new JLabel ("Filename:");
		lblFilePrx = new JLabel ("This file from this IP/Port.        Prefix (optional) for downloaded file:");
		lblPort = new JLabel ("Peers Port:");
		lblFound = new JLabel ("File found at:");
		lblMaster = new JLabel ("Some Peers:");
		txtServer = new JTextField (10);
		txtFileName = new JTextField(40);
		txtFilePrx = new JTextField(3);	
		txtPort = new JTextField(5);
		textMessage = new JTextArea(1,100);
		textMessage.setEditable(false);
		textMaster = new JTextArea(20,30);
		textMaster.setEditable(false);
		textFound = new JTextArea(20,30);
		textFound.setEditable(false);
		 
		for (int i=0; i<arr.length; i++) {
			textMaster.append(arrOut[i]+"\n");
		}
		
		//Add all components to panel
		pane.add (lblServer);
		pane.add (lblFilePrx);
		pane.add (lblFileName);
		pane.add (lblPort);
		pane.add (lblMaster);
		pane.add (lblFound);
		pane.add (txtServer);
		pane.add (txtFilePrx);	
		pane.add (txtFileName);	
		pane.add (txtPort);
		pane.add (btnExist);
		pane.add (btnContents);
		pane.add (textMessage);
		pane.add (btnDownload);
		pane.add (btnFind);
		pane.add (textMaster);
		pane.add (textFound);
		
		//Place all components
		lblFileName.setBounds (insets.left + 5, insets.top + 5, lblFileName.getPreferredSize().width, lblFileName.getPreferredSize().height);
		txtFileName.setBounds (lblFileName.getX() + lblFileName.getWidth() + 5, insets.top + 5, txtFileName.getPreferredSize().width, txtFileName.getPreferredSize().height);
		btnFind.setBounds (txtFileName.getX() + txtFileName.getWidth() + 5, insets.top + 5, btnFind.getPreferredSize().width, btnFind.getPreferredSize().height);
		
		lblServer.setBounds (insets.left + 5, insets.top + 30, lblServer.getPreferredSize().width, lblServer.getPreferredSize().height);	
		txtServer.setBounds (lblServer.getX() + lblServer.getWidth() + 5, insets.top + 30, txtServer.getPreferredSize().width, txtServer.getPreferredSize().height);
		lblPort.setBounds (txtServer.getX() + txtServer.getWidth() + 5, insets.top + 30, lblPort.getPreferredSize().width, lblPort.getPreferredSize().height);
		txtPort.setBounds (lblPort.getX() + lblPort.getWidth() + 5, insets.top + 30, txtPort.getPreferredSize().width, txtPort.getPreferredSize().height);
		btnContents.setBounds (txtPort.getX() + txtPort.getWidth() + 5, insets.top + 30, btnContents.getPreferredSize().width, btnContents.getPreferredSize().height);
		
		btnExist.setBounds (insets.left + 5, insets.top + 55, btnExist.getPreferredSize().width, btnExist.getPreferredSize().height);
		btnDownload.setBounds (btnExist.getX() + btnExist.getWidth() + 5, insets.top + 55, btnDownload.getPreferredSize().width, btnDownload.getPreferredSize().height);
		lblFilePrx.setBounds (btnDownload.getX() + btnDownload.getWidth() + 5, insets.top + 60, lblFilePrx.getPreferredSize().width, lblFilePrx.getPreferredSize().height);
		txtFilePrx.setBounds (lblFilePrx.getX() + lblFilePrx.getWidth() + 5, insets.top + 60, txtFilePrx.getPreferredSize().width, txtFilePrx.getPreferredSize().height);
 	
		textMessage.setBounds (insets.left + 5, insets.top + 90, textMessage.getPreferredSize().width, textMessage.getPreferredSize().height);	
		lblMaster.setBounds (insets.left + 50, insets.top + 130, lblMaster.getPreferredSize().width, lblMaster.getPreferredSize().height);
		textMaster.setBounds (insets.left + 150, insets.top + 130, textMaster.getPreferredSize().width, textMaster.getPreferredSize().height);
		lblFound.setBounds (insets.left + 500, insets.top + 130, lblFound.getPreferredSize().width, lblFound.getPreferredSize().height);
		textFound.setBounds (insets.left + 600, insets.top + 130, textFound.getPreferredSize().width, textFound.getPreferredSize().height);		
		
		//Set frame visible
		frame1.setVisible (true);

		//Button action
		btnExist.addActionListener(new btnExistAction()); //Register action
		btnDownload.addActionListener(new btnDownloadAction()); //Register action
		btnFind.addActionListener(new btnFindAction()); //Register action
		btnContents.addActionListener(new btnContentsAction()); //Register action
		
 	// end main()
	}  //e2
	
	//
    // refreshConnect()
    //   
    public static void refreshConnect() {
    	
    	// get fresh master; load into arr[]
    	Scanner sc = null;
		try {
			sc = new Scanner(new File("C:\\peerless"+ myPort +"\\_master.txt"));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		List<String> lines = new ArrayList<String>();
		while (sc.hasNextLine()) {
			lines.add(sc.nextLine());
		}
		arr = lines.toArray(new String[0]);

    	// test each node by asking for _master size;
    	// rewrite textMaster textarea
    	arrOut = new String[arr.length];
    	for (int i=0; i<arr.length; i++) {
    		int sep = arr[i].indexOf(":");
    		if (sep >0) {
    			String ip = arr[i].substring(0,sep);
    			String port = arr[i].substring(sep+1);
    			int remotePortInt = Integer.parseInt(port);         

    			try {
    				Thread.sleep(50);
    				int myI;
    				Socket myS = new Socket(ip, remotePortInt);     		
    				InputStream is = myS.getInputStream();        
    				PrintWriter pw = new PrintWriter(myS.getOutputStream(), true);
    				pw.println("#");		
    				myI = is.read();   // myI =size of remote _master
    				arrOut[i] = (arr[i] + "   Connected");
    				myS.close();
    			}	
    			catch (Exception e2){          		
    				arrOut[i] = arr[i];  // Not connected

    			//end try
    			}

    		// end sep>0
    		}

    	// end for loop
    	} 
    // end refreshConnect()
    }
    
	
	
		
	//	
	// run()    SERVER ROLE
	// 
    public void run() {
         
    	// loop
        while (true) {
        	try {
        		ServerSocket ss = new ServerSocket(myPort);
        		Socket myS = ss.accept(); // from now on the same as client sockets
           
        		BufferedReader br = new BufferedReader(
        				new InputStreamReader( myS.getInputStream() ) );
        		OutputStream os = myS.getOutputStream();
            
        		String request = br.readLine();    // receive something
        		String test = request.substring(0,1);
        		String fileName = request.substring(1);
             
        		// File Existence fx             	 	 
        		if (test.equals("?") ) {
             
        			File f = new File("C:\\peerless"+ myPort +"\\"+ fileName);
        			if( f.exists() && !f.isDirectory() ) {
        				if ( f.length() < 6000000) {
        					os.write(1);          	// 1=ok
        				} else {
        					os.write(3);			// 3 = too large
        				}
               		} else {
        				os.write(2);          // 2=not found
            		}
        		} 
            
        		// Master size Inquiry   	 	 
        		if (test.equals("#") ) {      
        			File f = new File("C:\\peerless"+ myPort +"\\_master.txt");
        			int masterSize = (int) f.length();
        			if( f.exists() && !f.isDirectory() ) {
        					os.write( (int) f.length() );			// size of master list on this server
               		} else {
               				os.write(-1);          			// -1 = missing
            		}
        		} 
         	 
        		// Download file fx        
        		if (test.equals("!") ) {
        			File myFile = new File ("C:\\peerless"+ myPort +"\\"+ fileName);
        			if (myFile.length() > 0 && myFile.length() < 6000000) {
        				byte [] mybytearray  = new byte [(int)myFile.length()];
        				FileInputStream fis = new FileInputStream(myFile);
        				BufferedInputStream bis = new BufferedInputStream(fis);
        				bis.read(mybytearray,0,mybytearray.length);                  
        				os.write(mybytearray,0,mybytearray.length);
        				os.flush();
        				myS.close();
        			} else {
        				myS.close();
        			}
                }  //e5
            
        		// Contents of c:/peerless<myPort>             
        		if (test.equals("@") ) {
        			String dirname = ("C:\\peerless" + myPort);
        			File f1 = new File(dirname);
        			if (f1.isDirectory()) {
        				
        				String send = "DIRECTORY OF " + dirname +"|";
        				os.write(send.getBytes());
        				String s[] = f1.list();
    			
        				for (int i=0; i<s.length; i++) {
        					File f = new File(dirname + "\\" + s[i]);
        					if (f.isDirectory()) {
        						
        						send = (s[i] + "  DIR" +"|");
                				os.write(send.getBytes());
        					} else {
        						
        						send = (s[i] +"|");
                				os.write(send.getBytes());
        					}
        				}
        			} else {
        				
        				String send = (dirname + " is not a directory" +"|");
        				os.write(send.getBytes());
        			}
                }     		
        		myS.close();ss.close(); 
        	}
        	catch (Exception e){  }
        	
        // end loop
        }
    // end run()
    }    
    
    //
    // Existence
    //
    public static class btnExistAction implements ActionListener {
    	public void actionPerformed (ActionEvent e){
	
    		//System.out.println("Filename = "+ txtFileName.getText() );
    		//System.out.println("IP = "+ txtServer.getText() );
    		//System.out.println("Port = "+ txtPort.getText() );
    		
    		// Client role
    		//
    		try {
        		String fileWanted = txtFileName.getText();
        		String fileWritten = txtFilePrx.getText() + fileWanted;
        		String fx = "?";  // existence (=?)   download (=!)  master.size (=#)
        		String remoteIP = txtServer.getText();
        		String remotePort = txtPort.getText();
        		int remotePortInt = Integer.parseInt(remotePort);
        		
        		//String myLine;
        		int myI;
        		Socket myS = new Socket(remoteIP, remotePortInt);
        		
        		InputStream is = myS.getInputStream();
        		PrintWriter pw = new PrintWriter(myS.getOutputStream(), true);
        		    		
        		// File existence fx
        		if (fx.equals("?") ) {        
        			pw.println(fx+fileWanted);
        			pw.println();
        			myI = is.read();
        			//System.out.println(myI);
        			if (myI == 1) textMessage.setText("File exists!");
        			if (myI == 2) textMessage.setText("File does not exist...");
        			if (myI == 3) textMessage.setText("File exceeds 6,000KB  cannot download!!");
        			if (myI!=1 && myI!=2 && myI!=3) {
        				textMessage.setText("Master size: " + myI);	
        			// end if
        			}
        			
        			myS.close();
        			
        		// end if
        		}
        		
        	// end try & catch	
            }
            catch (Exception e2){ }
    		
    	// end action method
    	}
    // end inner class
    }  //e2
 
    
    //
    // Download
    //   
    public static class btnDownloadAction implements ActionListener {
    	public void actionPerformed (ActionEvent e){
	
    		//System.out.println("Filename = "+ txtFileName.getText() );
    		//System.out.println("IP = "+ txtServer.getText() );
    		//System.out.println("Port = "+ txtPort.getText() );
    		
    		try {
        		String fileWanted = txtFileName.getText();
        		String fileWritten = txtFilePrx.getText() + fileWanted;
        		String fx = "!";  // existence (=?)   download (=!)
        		String remoteIP = txtServer.getText();
        		String remotePort = txtPort.getText();
        		int remotePortInt = Integer.parseInt(remotePort);
        		
        		//String myLine;
        		int myI;
        		Socket myS = new Socket(remoteIP, remotePortInt);
        		
        		InputStream is = myS.getInputStream();   
        		PrintWriter pw = new PrintWriter(myS.getOutputStream(), true);
        		      		
        		// Download file fx
        		if (fx.equals("!") ) {
        				pw.println(fx+fileWanted);		
        				int filesize=6022386; // filesize temporary hardcoded
        				int bytesRead;
        				int current = 0;
        				byte [] mybytearray  = new byte [filesize];
        				FileOutputStream fos = new FileOutputStream("C:\\peerless"+ myPort + "\\" + fileWritten);
        				BufferedOutputStream bos = new BufferedOutputStream(fos);
        				bytesRead = is.read(mybytearray,0,mybytearray.length);
        				current = bytesRead;
        				
        				do {
        					bytesRead =	is.read(mybytearray, current, (mybytearray.length-current));
        					if(bytesRead >= 0) current += bytesRead;
        				} while(bytesRead > -1);
        				bos.write(mybytearray, 0 , current);
        				bos.close();
        				myS.close();
        				textMessage.setText("Your file has been downloaded!");
        				
            	// end if	
        		}
        		
        	// end try & catch
            }
            catch (Exception e2){ }
    		
    	// end action method
    	}
    // end inner class
    }
    
    
    //
    // Find
    //
    public static class btnFindAction implements ActionListener {
    	public void actionPerformed (ActionEvent e){
    		
    		// prep
    		textFound.setText(null);
	
    		// Ask each node whether they have the requested file
    		for (int i=0; i<arr.length; i++) {
    			
    			String ip = arr[i].substring(0,arr[i].indexOf(":"));
    			String port = arr[i].substring(arr[i].indexOf(":")+1);
    			//System.out.println(ip+" "+port);
    			 
    			synchronized (this) {
    				try {
    					Thread.sleep(50);
    					String fileWanted = txtFileName.getText();
    					int remotePortInt = Integer.parseInt(port);         		
    					int myI;
    					Socket myS = new Socket(ip, remotePortInt);
            		
    					InputStream is = myS.getInputStream();             
    					PrintWriter pw = new PrintWriter(myS.getOutputStream(), true);
    					
    					pw.println("?"+fileWanted);		
    					myI = is.read();        		
    					if (myI == 1) textFound.append(arr[i]+"\n");       		
    					myS.close();
    					
    				// end try & catch
    				}
                		catch (Exception e2){ }
    				
    			// end synch block
    			}
    			
    		// end for
    		} 
    		     			
    	// end action method
    	}
    		
    		
    // end inner class
    }

   
    //
    // Contents of a remote directory
    //   
    public static class btnContentsAction implements ActionListener {
    	public void actionPerformed (ActionEvent e){
	
    		try {
    			textFound.setText(null);
        		String fx = "@";  // existence (=?)   download (=!)  get dir (=@)
        		String remoteIP = txtServer.getText();
        		String remotePort = txtPort.getText();
        		int remotePortInt = Integer.parseInt(remotePort);
        		
        		int myI;  // bytes read
        		Socket myS = new Socket(remoteIP, remotePortInt);  
        		
        		PrintWriter pw = new PrintWriter(myS.getOutputStream(), true);
        		InputStream is = myS.getInputStream();
        		
        		pw.println(fx);	  		
        		int bytesRead;
        		int current = 0;
        		byte[] mybytearray  = new byte [100000];
        		
        		bytesRead = is.read(mybytearray,0,mybytearray.length);	   		
        		current = bytesRead;
        				
        		do {
        			bytesRead =	is.read(mybytearray, current, (mybytearray.length-current));
        			if(bytesRead >= 0) current += bytesRead;
        		} while(bytesRead > -1);
        		String dirString = new String(mybytearray);
        		
        		int pos = dirString.indexOf("|",0);      		
        		String dirLine = dirString.substring(0, pos);
        		textFound.append(dirLine +"\n");
        		
        		for (int j=0; j<30; j++) {
        			int pos1 = pos+1;
        			pos = dirString.indexOf("|",pos1);
        			dirLine = dirString.substring(pos1, pos);
        			textFound.append(dirLine +"\n");
        		}
        		
        		myS.close();
            	
        	// end try & catch
            }
            catch (Exception e2){ }
    		
    	// end method
    	}
    // end inner class
    }




//////////////////////////last statement /////////////////////////////
//end outer class
}

