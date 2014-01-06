package dataSet;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;

import utils.MyFileUtils;
import check.Check;

/**
 * Manages a data set file and loops through a test case using the data
 * contained in said file.
 * @author Adrien Droguet
 *
 */
public class DataSet {
	private String url_jeu_de_donnees;
	
	private Check check;
	private TestInterface ti;
	
	public DataSet(TestInterface ti) {
		this.ti = ti;
		url_jeu_de_donnees = ti.getUrl();
		check = new Check();
		check.deleteMyLog();
	}
	
	/**
	 * Exécute la méthode test de ti en boucle en fonction du contenu du fichier
	 * de jeu de données.
	 * @throws Exception
	 */
	public void boucle() throws Exception {
		File jeuDeDonnees = new File(url_jeu_de_donnees);
		BufferedReader br;
		if (jeuDeDonnees.exists()) {
			try {
				br = MyFileUtils.getBufferedReader(jeuDeDonnees);
				String line = "";
				while ((line = br.readLine()) != null) {
					System.out.println("Jeu de données : " + line);
					ti.test(line, check);
				}
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			ti.test("", check);
		}
	}
	
	/**
	 * 
	 * @return Check
	 */
	public Check getCheck() {
		return check;
	}
	
	/**
	 * Returns true if the specified data set file exists.
	 * @return
	 */
	public boolean exists() {
		File jeuDeDonnees = new File(url_jeu_de_donnees);
		return jeuDeDonnees.exists();
	}
}
