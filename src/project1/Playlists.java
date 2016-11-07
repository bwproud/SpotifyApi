package project1;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeSet;

import com.wrapper.spotify.*;
import com.wrapper.spotify.exceptions.*;
import com.wrapper.spotify.methods.*;
import com.wrapper.spotify.methods.authentication.*;
import com.wrapper.spotify.models.*;

public class Playlists {
	private static Api api; 
	private static String user; 
	private static String pid;
	static List<Track> playlist;
	public Playlists(Api api, String user, String pid) throws IOException, WebApiException{
		this.api=api;
		this.user=user;
		this.pid=pid;
		playlist=getSongs(user, pid);
	}
	
	public static List<Artist> getSongsInGenre(String genre) throws IOException, WebApiException{
		List<Artist> artists=new ArrayList<>();
		for(Track song:playlist){
			//System.out.println(song.getName()+" "+song.getArtists());
			try{
			Artist artist = api.getArtist(song.getArtists().get(0).getId()).build().get();
			}catch(Exception e){
				String artistName=song.getArtists().get(0).getName(); 
				String artistId=song.getArtists().get(0).getId();
				//String name= api.getArtist(artistId).build().get().getName();
				System.out.println(artistName+" "+artistId);
			}
			//System.out.println("This artist's name is " + artist.getName()+" and their genre's are"+ artist.getGenres());
//			for(String g: artist.getGenres()){
//				if(g.indexOf(genre)!=-1){
//					//System.out.println(artist.getName());
//					artists.add(artist);
//					break;
//				}
//			}
			//System.out.println();
		}
		System.out.println(artists.size());
		return artists;
	}
	public static List<Track> getSongs(String user, String pid) throws IOException, WebApiException{
		Playlist playlist=null;
		try{
			playlist = api.getPlaylist(user, pid).build().get();
		}catch(Exception e){
			throw new RuntimeException("Local track in first 100 songs. Please remove and try again.");
		}
		int loop = playlist.getTracks().getTotal()/100+1;
		List<Track> tracks = new ArrayList<>();
		for(int i=0; i < loop; i++){
		Page<PlaylistTrack> page=null;
		try{page = api.getPlaylistTracks(user, pid).offset(i*100).build().get();}
		catch(Exception e){
			for(int j=i*100; j<i*100+100;j++){
				try{
					page = api.getPlaylistTracks(user, pid).offset(j).limit(1).build().get();
				}catch(Exception ex){}
				tracks.add(page.getItems().get(0).getTrack());
			}
		}
		List<PlaylistTrack> pT = page.getItems();
		for (int j=0; j< pT.size(); j++){
			tracks.add(pT.get(j).getTrack());
		}
		}
		return tracks;
	}

	
	public List<Track> intersect(String user2, String pID2) throws IOException, WebApiException{
		return intersect(playlist, getSongs(user2, pID2));
	}
	
	private static List<Track> intersect(List<Track> p1, List<Track> p2){
		TreeSet<String> tree = new TreeSet<>();
		List<Track> intersect= new ArrayList<>();
		for(Track song: p1){
			tree.add(song.getId());
		}
		for(Track song: p2){
			if(tree.contains(song.getId())){
				intersect.add(song);	
			}
		}
		return intersect;
	}
	
	public void printSongs(List<Track> songs){
		System.out.printf("%-30.30s  %-30.30s%n", "Songs", "Artists");
		for(Track song: songs){System.out.printf("%-30.30s  %-30.30s%n",song.getName(), song.getArtists().get(0).getName());}
	}
	
	public static List<Track> findHiddenFiles() throws IOException, WebApiException{
		List<Track> f12 = getSongs("coolwaves12", "7fC5yLklob2LMkCZCt2wRQ");
		List<Track> f12Copy = getSongs("coolwaves12", "4RpaJGCFEvROaDtXOvIMpN");
		TreeSet<String> tree = new TreeSet<>();
		List<Track> hiddenSongs= new ArrayList<>();
		for(Track song: f12Copy){
			tree.add(song.getId());
		}
		for(Track song: f12){
			if(!tree.contains(song.getId())){
				hiddenSongs.add(song);	
			}
		}
		return hiddenSongs;
	}
	
	public static List<String> getArtists(){
		TreeSet<String> artist = new TreeSet<>();
		for(int i=0; i<playlist.size(); i++){
			artist.add(playlist.get(i).getArtists().get(0).getName()+"&*"+playlist.get(i).getArtists().get(0).getId());
		}
		List<String> artists = new ArrayList<>(artist);
		for(int i = 0; i<artists.size(); i++)
		{
			artists.set(i, artists.get(i).substring(0, artists.get(i).indexOf("&*")));
		}
		return artists;
	}
	public static List<String> getAlbum(){
		TreeSet<String> album = new TreeSet<>();
		for(int i=0; i<playlist.size(); i++){
			album.add(playlist.get(i).getAlbum().getName()+"&*"+playlist.get(i).getAlbum().getId());
		}
		List<String> albums = new ArrayList<>(album);
		for(int i = 0; i<albums.size(); i++)
		{
			albums.set(i, albums.get(i).substring(0, albums.get(i).indexOf("&*")));
		}
		return albums;
	}
	public static List<String> findDupSongs(){
		List<String> song = new ArrayList<>();
		for(int i=0; i<playlist.size(); i++){
			song.add(playlist.get(i).getName()+" - "+playlist.get(i).getArtists().get(0).getName());
		}
		List<String> songSet = new ArrayList<>(new TreeSet<>(song));
		
		for(String s : songSet)
		{
			song.remove(s);
		}
		return song;
	}
	
	static Map<String, Integer> numSongsPerArtist(){
		Map<String, Integer> countSongs = new HashMap<>();
		for(int i = 0; i<playlist.size(); i++){
			String artist = playlist.get(i).getArtists().get(0).getName();
			try{
				countSongs.put(artist, countSongs.get(artist) + 1);
			}catch(Exception e){
				countSongs.put(artist, 1);
			}
		}
		return countSongs;
	}
	
	private static Map<String, Integer> sortByComparator(Map<String, Integer> unsortMap, final boolean ascending){
        List<Entry<String, Integer>> list = new LinkedList<Entry<String, Integer>>(unsortMap.entrySet());

        // Sorting the list based on values
        Collections.sort(list, new Comparator<Entry<String, Integer>>()
        {
            public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2){
                if (ascending){
                    return o1.getValue().compareTo(o2.getValue());
                } else {
                    return o2.getValue().compareTo(o1.getValue());
                }
            }
        });
        
        // Maintaining insertion order with the help of LinkedList
        Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
        for (Entry<String, Integer> entry : list)
        {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }
        
	public static List<String> topArtists(int num){
		Map<String, Integer> sorted = sortByComparator(numSongsPerArtist(), false);
		List<String> order = new ArrayList<>();
		int i=1;
		for(String s: sorted.keySet()){
			order.add(i+". "+s +" - "+ sorted.get(s));
			i++;
			if(i>num){break;}
		}
		return order;
	}
	
	public static List<String> numSongs(int num){
		Map<String, Integer> sorted = sortByComparator(numSongsPerArtist(), false);
		List<String> order = new ArrayList<>();
		for(String s: sorted.keySet()){
			if(sorted.get(s)<num){continue;}
			order.add(s +" - "+ sorted.get(s));
		}
		return order;
	}
	
	//public List<Track> mostIndieSongs(){
		
	//}
	
	public String getUser(){
		return user;
	}
	
	public String getPID(){
		return pid;
	}
	
	public void setPlaylist(String usr, String pID) throws IOException, WebApiException{
		user=usr;
		pid=pID;
		playlist=getSongs(user, pid);
	}
}
