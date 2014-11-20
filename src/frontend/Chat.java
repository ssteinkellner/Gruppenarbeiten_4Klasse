package frontend;
import filter.BadWordFilter;
import filter.Caps;
import filter.Translator;
import interfaces.Activatable;
import interfaces.Connection;
import interfaces.Recievable;
import interfaces.Sendable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import common.Output;

/**
 * Die hauptklasse des Programms
 * Sie speichert die nachrichten, l�sst sie abrufen und neue senden
 * @author Steinkellner Sebastian
 * @version 2014.11.19
 */
public class Chat{
	private Communicator communicator;
	private LinkedList<String> messages;

	private Connection activeConnection;
	private ArrayList<Connection> connections;
	private HashMap<String,Sendable> sendables;
	private HashMap<String,Recievable> recievables;
	private HashMap<String,Activatable> activatables;
	
	public Chat(){
		messages = new LinkedList<String>();
		connections = new ArrayList<Connection>();
		sendables = new HashMap<String,Sendable>();
		recievables = new HashMap<String,Recievable>();
		activatables = new HashMap<String,Activatable>();
	}
	
	/**
	 * setzt die aktive verbindung
	 * @param connection verbindung, die verwendet werden soll
	 */
	public void setActiveConnection(Connection connection){
		if(!connections.contains(connection)){
			connections.add(connection);
		}
		
		if(activeConnection!=null && activeConnection.isOpen()){
			activeConnection.close();
		}
		
		activeConnection = connection;
		
		sendables.clear();
		recievables.clear();
		activatables.clear();
		
		{	// kapselung f�r tempor�re benennungen
			Caps temp = new Caps(activeConnection);
			sendables.put("c",temp);
			activatables.put("c", temp);
		}{	// kapselung f�r tempor�re benennungen
			Translator temp = new Translator(sendables.get("c"));
			sendables.put("t",temp);
			activatables.put("t", temp);
		}{	// kapselung f�r tempor�re benennungen
			BadWordFilter b = new BadWordFilter(activeConnection);
			recievables.put("bwf",b);
			activatables.put("bwf", b);
		}
		communicator = new Communicator(sendables.get("c"), recievables.get("bwf"));
	}
	
	public ArrayList<Connection> getConnections(){
		return connections;
	}

	public Connection getActiveConection(){
		return activeConnection;
	}

	public void recieve(){
		String text = communicator.recieve();
		messages.add(text);
		Output.debug("Added '"+text+"' to messages!");
	}

	public void send(String text) {
		messages.add("You: "+text);
		communicator.send(text);
	}
	
	public String messagesToString(){
		String text="";
		Iterator<String> i = messages.iterator();
		while(i.hasNext()){
			if(text!=""){ text+="\n"; }
			text += i.next();
		}
		
		return text;
	}
	
	public String getLastMessage(){
		return messages.getLast();
	}
	
	public void enableFilters(boolean active){
		Iterator<Activatable> i = activatables.values().iterator();
		while(i.hasNext()){
			i.next().setEnabled(active);
		}
	}
}
