package tgm.sew.hit.roboterfabrik.arbeiter;

import java.util.Random;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import tgm.sew.hit.roboterfabrik.Sekretariat;

/**
 * 
 * @author Martin Kritzl
 * @version 20140925
 * 
 * Ist fuer die Lieferung der einzelnen Teile zustaendig
 */
public class Lieferant extends Mitarbeiter {

	private String currentPart;
	
	private static Logger logger;

	/**Erzeugt einen neuen Lieferanten und legt einen zufaelligen zu erzeugenden Part fest
	 * 
	 * @param sekretariat
	 */
	public Lieferant(Sekretariat sekretariat) {
		super(sekretariat);
		logger = Logger.getLogger(sekretariat.getBauplan().getLogPath());
		changePart();
	}
	
	/**
	 * aendert zufaellig den Part der geliefert werden soll
	 */

	public String changePart() {
		//Holt sich alle verf�gbaren Part Typen vom Bauplan
		String[] parts = this.sekretariat.getBauplan().getPartNames();
		//Generiert eine Random zahl, welche entscheidet welcher Teil dran sein soll
		int random = new Random().nextInt(parts.length);
		this.currentPart = parts[random];
		logger.log(Level.INFO, "Lieferant ID" + this.getId() + ": Habe Art des Teils auf " + this.currentPart + " geaendert");
		return this.currentPart;
	}
	
	/**
	 * 
	 * @param array Ein array gefuellt mit Strings
	 * @return  Ein String der alle Elemente des Eingabearrays, getrennt mit dem im Bauplan
	 * 			festgelegten Trennzeichen, beeinhaltet
	 */
	
	public String getConcatElements(String[] array) {
		//H�ngt alle Strings des Arrays zusammen und gibt dazwischen einen Tabulator 
		String concatParts = "";
		if (array != null) {
			for (int i = 0; i < array.length-1;i++) {
				if (array[i] != null) {
					concatParts += array[i]+"\t";
				}
			}
			concatParts += array[array.length-1];
			return concatParts;
		} else {
			return null;
		}
	}
	

	/**Gibt einen String zurueck, der einen Part repraesentiert
	 * 
	 * @return 	einen String der am Anfang einen Namen eines Parts und darauffolgend 
	 * 			zufaellige Zahlen, getrennt mit dem im Bauplan festgelegten Trennzeichen, 
	 * 			enthaelt. Die Anzahl der Zahlen ist ebenfalls im Bauplan festgelegt
	 */
	
	public String getRandomLine() {
		//schreibt den Namen des Parts am Anfang
		String part = this.currentPart;
		//Erstellt so viele Zahlen wie es im Bauplan definiert ist
        for (int i = 0; i < this.sekretariat.getBauplan().getPartLength(); i++) {
        	//haengt das Trennzeichen und eine random Zahl, die das im Bauplan definierte Maximum nicht �berschreitet, an
            part += "" + this.sekretariat.getBauplan().getDelimiter() + new Random().nextInt(this.sekretariat.getBauplan().getMaxRandomNumber() + 1);  
        }
        return part;
	}
	
	/**
	 * Fuegt staendig zufaellige Parts dem lagermitarbeiter hinzu
	 */
	public void run() {
		//H�ngt alle Strings des Arrays zusammen und gibt dazwischen einen Tabulator
		while(!this.sekretariat.getEmployees().isShutdown()) {
			//Die Art des Parts wird geaendert
			changePart();
			//Die anzahl der zu produzierenden Parts wird festgelegt
			int anzahl = new Random().nextInt(20)+10;
			String[] parts = new String[anzahl];
			for (int i = 0;i<anzahl;i++) {
				parts[i] = this.getRandomLine();
			}
			//Die Parts werden dem Lagermitarbeiter uebergeben
			this.sekretariat.getLagermitarbeiter().addParts(this.currentPart, parts);
			logger.log(Level.INFO, "Lieferant ID" + this.getId() + ": Habe " + parts.length + " Teile dem Lagermitarbeiter uebergeben.");
		}
		
	}

}
