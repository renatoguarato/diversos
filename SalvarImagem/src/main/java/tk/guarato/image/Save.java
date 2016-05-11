package tk.guarato.image;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;

public class Save {

	private static final String PATH_IMAGEM = "/Users/RenatoGuarato/Desktop/imagens/";
	private static final Integer timeout = 10000;

	public static void main(String[] args) {

		for (int ind = 0; ind < 1000; ind++) {
			
			System.out.println(String.format("Indice: %s", ind));

			Date date = new Date();

			String rand = String.valueOf(1 + (int) (Math.random() * 9999));

			String nameFile = String.valueOf(date.getTime()) + rand;

			File file = new File(PATH_IMAGEM + nameFile + ".jpg");

			URL url = null;
			URLConnection urlc = null;

			InputStream is = null;
			FileOutputStream fos = null;

			System.setProperty("jsse.enableSNIExtension", "false");

			try {

				file.createNewFile();

				url = new URL("https://dje.tjmg.jus.br/captcha.svl");
				urlc = url.openConnection();

				urlc.setConnectTimeout(timeout);
				urlc.setReadTimeout(timeout);

				is = urlc.getInputStream();
				fos = new FileOutputStream(file);

				int i;
				while ((i = is.read()) != -1) {
					fos.write(i);
				}

				if (is != null) {
					is.close();
				}
				if (fos != null) {
					fos.close();
				}

				String[] command = { "/opt/local/bin/convert", file.getAbsolutePath(), "-fuzz", "33%", "-fill", "black",
						"-opaque", "#a87830", "-threshold", "12%", "+compress", file.getAbsolutePath() + ".tif" };

				executeOSProcess(command);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static void executeOSProcess(String[] command) throws Exception {

		Process proc = Runtime.getRuntime().exec(command);
		if (proc != null) {
			proc.waitFor();

			BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
			StringBuilder errorMsg = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				errorMsg.append(line).append("\n");
			}

			if (errorMsg.length() > 0) {
				System.out.println(errorMsg.toString());
			}

			if (errorMsg.length() != 0 && !errorMsg.toString().trim().contains("Tesseract Open Source OCR Engine")) {
				System.out.println(errorMsg.toString());
			}

			proc.destroy();
		}
	}

}
