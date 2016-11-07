package project1;


import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;
import javax.swing.text.DefaultCaret;

import com.wrapper.spotify.*;
import com.wrapper.spotify.exceptions.*;
import com.wrapper.spotify.methods.*;
import com.wrapper.spotify.methods.authentication.*;
import com.wrapper.spotify.models.*;

public class Runner implements ActionListener{
	private Api api; 
    private JFrame frame;
    private JTextArea txtOutput;
    private JScrollPane spOutput;
    private JButton printSongs, intersect,findDupSongs, topArtists, numSongs, changePlaylist;
    private Playlists p;
	public static void main(String[] args) throws IOException, WebApiException {
		Runner r = new Runner();
	}
	
	public Runner() throws IOException, WebApiException{
		api= Setup.clientCredentialsAPI();
//		List<Track> topSongs = api.getTopTracks().build().get();
//		for(Track s: topSongs){
//			System.out.println(s.getName());
//		}
//		System.out.println("test over");
		//printSongs(findHiddenFiles());
		//System.out.println();
		//System.out.println(api.getArtist("4KWTAlx2RvbpseOGMEmROg").build().get().getName());
		p = new Playlists(api,"coolwaves12", "7fC5yLklob2LMkCZCt2wRQ");
		p.getSongsInGenre("folk");
//		DevLogToolGUI();
	}

	
	  public void DevLogToolGUI(){
	        frame = new JFrame("LogTool");
	        frame.setLayout(new GridBagLayout());
	        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

	        txtOutput  = new JTextArea();
	        txtOutput.setLineWrap(true);
	        txtOutput.setWrapStyleWord(true);
	        txtOutput.setColumns(65);
	        txtOutput.setRows(20);
	        txtOutput.setEditable(false);
	        
	        
	        spOutput  = new JScrollPane(txtOutput);

	        GridBagConstraints gBC = new GridBagConstraints();

	        printSongs = new JButton("Print Playlist");
	        intersect = new JButton("Intersect");
	        findDupSongs = new JButton("Find Duplicates");
	        topArtists = new JButton("Top Artists");
	        numSongs = new JButton("Number of Songs");
	        changePlaylist  = new JButton("Change Playlist");
	        
	        printSongs.setEnabled(true);
	        intersect.setEnabled(true);
	        findDupSongs.setEnabled(true);
	        topArtists.setEnabled(true);
	        numSongs.setEnabled(true);
	        changePlaylist.setEnabled(true);

	        gBC.ipadx = gBC.ipady = 1;
	        gBC.fill  = GridBagConstraints.BOTH;
	        gBC.insets.set(2,2,2,2);
	        gBC.weightx = gBC.weighty = 0;


	        gBC.gridx = (0);
	        gBC.gridy = (0);
	        frame.add(printSongs,gBC);

	        gBC.gridx = (1);
	        gBC.gridy = (0);
	        frame.add(intersect,gBC);

	        gBC.gridx = (2);
	        gBC.gridy = (0);
	        frame.add(findDupSongs,gBC);
	        
	        gBC.gridx = (3);
	        gBC.gridy = (0);
	        frame.add(topArtists,gBC);
	        
	        gBC.gridx = (4);
	        gBC.gridy = (0);
	        frame.add(numSongs,gBC);
	        
	        gBC.gridx = (5);
	        gBC.gridy = (0);
	        frame.add(changePlaylist,gBC);
	        
	        


	        printSongs.addActionListener(this);
	        intersect.addActionListener(this);
	        findDupSongs.addActionListener(this);
	        topArtists.addActionListener(this);
	        numSongs.addActionListener(this);
	        changePlaylist.addActionListener(this);
	        
	        gBC.gridx = (0);
	        gBC.gridy = (1);
	        gBC.gridwidth = 7;
	        gBC.ipadx = gBC.ipady = 15;
	        gBC.fill  = GridBagConstraints.BOTH;
	        gBC.insets.set(15,15,15,15);
	        gBC.weightx = gBC.weighty = 1;
	        frame.add(spOutput,gBC);

	        frame.pack();
	        frame.setVisible(true);
	    }
	  
	    public void actionPerformed(ActionEvent e) {
	        
	        if (e.getSource() == printSongs)
	        {
	            txtOutput.setText("");
	        	txtOutput.append(String.format("%-30.30s  %-30.30s  %-30.30s%n", "Song", "Artist", "Album"));
	      		for(Track song: p.playlist){txtOutput.append(String.format("%-30.30s  %-30.30s  %-30.30s\n",song.getName(), song.getArtists().get(0).getName(), song.getAlbum().getName()));}
	        }
	        else if (e.getSource() == intersect)
	        {   
	        	String tempLines;
	            tempLines =  JOptionPane.showInputDialog(this.frame,"Enter a semi-colon seperated user and playlist to compare against","1220766452;4eKdiwCcDMD3YqrzwIrLRl");
	            String user = tempLines.substring(0, tempLines.indexOf(";"));
	            String pid = tempLines.substring(tempLines.indexOf(";")+1);
	            txtOutput.setText("");
	            try {
	            	List<Track> com = p.intersect(user, pid);
					for(Track s: com){
						txtOutput.append(s.getName()+" - "+s.getArtists().get(0).getName()+"\n");
					}
					txtOutput.append("You had "+ com.size()+ " songs in common!");
				} catch (Exception ex) {}

	        }
	        else if (e.getSource() == findDupSongs)
	        {
	            txtOutput.setText("");
	            List<String> dups =p.findDupSongs();
	        	for(String s : dups){
	        		txtOutput.append(s+"\n");
	        	}
	        	txtOutput.append("You had "+ dups.size()+ " duplicate songs!");
	        }
	        else if (e.getSource() ==topArtists)
	        {

	            String tempLines;
	            tempLines =  JOptionPane.showInputDialog(this.frame,"Find the top __ Artists in the Playlist",50);
	            int lim = Integer.parseInt(tempLines);
	            txtOutput.setText("");
	            for(String s: p.topArtists(lim)){
	            	txtOutput.append(s+"\n");
	            }
	        }
	        else if (e.getSource() ==numSongs)
	        {

	            String tempLines;
	            tempLines =  JOptionPane.showInputDialog(this.frame,"Find all artists with more than N number of songs",30);
	            int lim = Integer.parseInt(tempLines);
	            txtOutput.setText("");
	            for(String s: p.numSongs(lim)){
	            	txtOutput.append(s+"\n");
	            }
	        }
	        else if (e.getSource() ==changePlaylist)
	        {

	            String tempLines;
	            tempLines =  JOptionPane.showInputDialog(this.frame,"Enter a semi-colon seperated user and playlist","echristinem;2fWeQCEdnlJ6DpF8UAtksL");
	            String user = tempLines.substring(0, tempLines.indexOf(";"));
	            String pid = tempLines.substring(tempLines.indexOf(";")+1);
	            txtOutput.setText("");
	            try {
					p.setPlaylist(user, pid);
				} catch (Exception ex){}
				txtOutput.append("Playlist has been changed to "+ pid + " by "+ user);
	        }
	    }

}