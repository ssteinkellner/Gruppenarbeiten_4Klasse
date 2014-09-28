package tgm.sew.hit.roboterfabrik.arbeiter;

import java.util.ArrayList;
import java.util.Collections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import tgm.sew.hit.roboterfabrik.Sekretariat;
import tgm.sew.hit.roboterfabrik.statisch.Bauplan;


/**
 * 
 * @author Martin Kritzl
 * @version 20140925
 * 
 * Ist fuer die Sortierung der Parts und dadurch die Produktion der Threadees verantwortlich
 */
public class Montagemitarbeiter extends Mitarbeiter {

	private String[] parts;

	private Lagermitarbeiter auslagerung;
	
	private static Logger logger;

	/**Erzeugt einen neuen Montagemitarbeiter
	 * 
	 * @param sekretariat
	 */
	
	public Montagemitarbeiter(Sekretariat sekretariat) {
		super(sekretariat);
		this.auslagerung = new Lagermitarbeiter(sekretariat);
		logger = Logger.getLogger(sekretariat.getBauplan().getLogPath());
	}

	/**Holt sich alle notwendigen Teile vom lagermitarbeiter und gibt sie gleich wieder
	 * zurueck wenn diese nicht vollstaendig sind. Im zweiten Fall wird ebenso eine Pause
	 * eingelegt und auf lagermitarbeiter gewartet
	 * 
	 * @return Ob alle Teile angefordert werden konnten
	 */
	
	private boolean getAllParts() {
		String[] needParts = this.sekretariat.getBauplan().getParts();
		String[] parts;
		String part;
		for (int i = 0; i < needParts.length; i++) {
			parts = this.sekretariat.getLagermitarbeiter().getParts(parts[i], this.sekretariat.getBauplan().getPartCount(parts[i]));
			logger.log(Level.INFO, "Montagemitarbeiter " + this.getId() + ": Habe folgende Parts erhalten: " + getConcatElements(this.parts));
			for (int j = 0; j < parts.length; j++) {
				part = parts[i];
				if (part != null) {
					this.parts[i]=part;
				} else {
					this.sekretariat.getLagermitarbeiter().addParts(this.parts);
					logger.log(Level.INFO, "Montagemitarbeiter " + this.getId() + ": Gebe folgende Parts zurueck: " + getConcatElements(this.parts));
					logger.log(Level.INFO, "Montagemitarbeiter " + this.getId() + ": Frage in " + this.sekretariat.getBauplan().getTimeRetry() + "ms erneut nach Teilen");
					Thread.sleep(this.sekretariat.getBauplan().getTimeRetry());
					this.wait(this.sekretariat.getLagermitarbeiter());
					return false;
				}
			}
		}
		
	}

	/**
	 * 
	 * @param array Ein array gefuellt mit Strings
	 * @return  Ein String der alle Elemente des Eingabearrays, getrennt mit dem im Bauplan
	 * 			festgelegten Trennzeichen, beeinhaltet
	 */
	
	private String getConcatElements(String[] array) {
		String concatParts = "";
		for (int i = 0; i < array.length-1;i++) {
			if (array[i] != null) {
				concatParts += array[i] + this.sekretariat.getBauplan().getDelimiter();
			}
		}
		//concatParts += this.parts[this.parts.length-1];
		return concatParts;
	}
	
	/**
	 * uebergibt dem Dateizugriff den hinzuzufuegenden Threadee
	 */
	
	private void deliverProduct() {
		String[] addThreadee;
		addThreadee[0]=this.sekretariat.getBauplan().getName() + "-ID" + this.sekretariat.getNewProductId() + ", Mitarbeiter-ID" + this.getId();
		addThreadee[1]=getConcatElements(this.parts);
		auslagerung.addParts(this.sekretariat.getBauplan().getDeliverPath(), addThreadee);
		logger.log(Level.INFO, addThreadee[0] + "\r\n" + addThreadee[1]); 
		this.parts = null;
	}
	
	/**Sortiert einen Part
	 * 
	 * @param part Ein String der einen Part repraesentiert
	 * @return Einen String der einen sortierten/fertigen Part repraesentiert
	 */

	public String sortPart(String part) {
		String delimiter = this.sekretariat.getBauplan().getDelimiter();
		String prefix = part.substring(0, part.indexOf(delimiter));
		part = part.substring(part.indexOf(delimiter)+delimiter.length(), part.length());
		ArrayList<Integer> parts = new ArrayList<Integer>();
		try{
			for (int i = 0; i < this.sekretariat.getBauplan().getPartLength()-1;i++){
				parts.add(Integer.parseInt(part.substring(0, part.indexOf(delimiter))));
				part = part.substring(part.indexOf(delimiter)+delimiter.length(), part.length());
			}
			parts.add(Integer.parseInt(part.substring(0, part.length())));
		} catch(NumberFormatException e) {
			logger.log(Level.ERROR, "Montagemitarbeiter " + this.getId() + ": Fehlerhafter Part(NumberFormatException)");
		}
		Collections.sort(parts);
		String sortedPart = prefix + delimiter;
		for (int number : parts) {
			sortedPart += number + delimiter;
		}
		sortedPart = sortedPart.substring(0, sortedPart.lastIndexOf(delimiter));
		return sortedPart;
	}
	
	/**
	 * Holt staendig die erforderlichen Parts, sortiert sie und bringt sie ins Lager
	 */
	
	public void run() {
		while(!this.sekretariat.getEmployees().isShutdown()) {
			if (getAllParts()) {
				for (int i = 0; i < this.parts.length;i++) {
					parts[i] = sortPart(parts[i]);
				}
				deliverProduct();
			}
		}
		
	}

}
