package org.freedesktop.swing;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ConvertSVGtoPNG {
	static List<String> all = new ArrayList<>();
	static {
		try (BufferedReader r = new BufferedReader(
				new InputStreamReader(ConvertSVGtoPNG.class.getResourceAsStream("/default-icons.list")))) {
			String l;
			while ((l = r.readLine()) != null) {
				l = l.trim();
				all.add(l);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	static void doFile(File file, String path, int... sizes) throws Exception {
		if (file.isDirectory()) { 
			for (File f : file.listFiles()) {
				doFile(f, path + "/" + f.getName(), sizes);
			}
		} else {
			if (file.getName().toLowerCase().endsWith(".svg")) {
				System.out.println(String.format("Converting %s", file));
				for (int i = 0; i < sizes.length; i++) {
					String bn = file.getName();
					int idx = bn.lastIndexOf('.');
					String base = bn.substring(0, idx);
					String pngn = base + ".png";
					if (all.contains(base) && !file.getParentFile().getName().equals("symbolic")) {
						System.out.println(String.format("   %d", sizes[i]));
						File szDir = new File(file.getParentFile().getParentFile(), String.valueOf(sizes[i])).getCanonicalFile();
						if (!szDir.exists()) {
							System.out.println(String.format("   Creating dir %s", szDir));
							szDir.mkdirs();
						}
						File szFile = new File(szDir, pngn);
						if (!szFile.exists()) {
							System.out.println(String.format("     Saving to %s", szFile));
							ProcessBuilder pb = new ProcessBuilder("inkscape", "-z", "-e", szFile.getAbsolutePath(), "-w",
									String.valueOf(sizes[i]), String.valueOf(sizes[i]), file.getAbsolutePath());
							pb.redirectErrorStream(true);
							Process p = pb.start();
							try {
								byte[] b = new byte[1024];
								int r;
								InputStream in = p.getInputStream();
								while ((r = in.read(b)) != -1) {
									System.out.print(new String(b, 0, r));
								}
							} finally {
								if (p.waitFor() != 0) {
									// throw new Exception("Failed with " +
									// p.exitValue());
								}
							}
						}
					}
					else {
						System.out.println(String.format("     Deleting %s", file));
						file.delete();
					}
				}
			}
		}
	}

	public static void main(String[] args) throws Exception {
		doFile(new File(args[0]), "", 16, 22, 32, 48, 64);
	}
}
