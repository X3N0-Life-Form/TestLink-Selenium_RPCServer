package check;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Cette classe permet d'enregistrer des points de contrôles d'un test JUnit
 * dans un log. 
 * @author Adrien Droguet - Félix Brandjonc
 * @version 1.0 5/06/2012<br>
 * 				Création de la classe.
 */
public class Check {

	public static final String LOG_URL = "D:\\myLog.txt";
	
	/**
	 * A appeler avant chaque test pour supprimer tout log pré-existant.
	 */
	public void deleteMyLog() {
		File file = new File(LOG_URL);
		file.delete();
	}

	/**
	 * Ajoute une ligne au log, contenant le descriptif du point de contrôle,
	 * et le résultat de l'exécution.
	 * @param descriptif
	 * @param resultat
	 * @return
	 */
	public boolean check(String descriptif, boolean resultat) {
		
		File myFile = new File(LOG_URL);
		if (myFile.exists()) {
			;
		} else {
			try {
				myFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		try {
			FileWriter fw = new FileWriter(LOG_URL, true);
			String r = "\nRésultat: "; 
			r += resultat;
			r += "\tDescriptif: ";
			r += descriptif;
			fw.write(r);
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return resultat;
	}
	
	
}
