package model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Helper static class for copying files.
 */
public class FileCopyHelper {

	/**
	 * Copies the source file to the destination file.  
	 * NOTE: If the source and destination have the same path, the files will not be copied.
	 * NOTE: If there is already a file at the destination of the given name, a unique _# will be added to the end of the name.
	 * @param source filename of the source file
	 * @param destination filename of the destination file
	 * @return returns the new name of the file
	 * @throws IOException throws any input/output exception that prevented the copy from succeeding
	 */
	public static String copyFile(String source, String destination) throws IOException {
		InputStream in = null;
		OutputStream out = null;
		try {
			File sourceFile = new File(source);
			File destinationFile = new File(destination);
			if (sourceFile.getAbsolutePath().equals(destinationFile.getAbsolutePath()))
				return destinationFile.getName();
			
			int count = 1;
			while (destinationFile.exists()) {
				int extensionIndex = destination.lastIndexOf('.');
				if (extensionIndex == -1)
					destinationFile = new File(destination + "_" + count++);
				else {
					String preExtension = destination.substring(0, extensionIndex);
					String extension = destination.substring(extensionIndex);
					destinationFile = new File(preExtension + "_" + count + extension);
				}
				count++;
			}
			
			in = new FileInputStream(sourceFile);
			out = new FileOutputStream(destinationFile);
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0)
				out.write(buf, 0, len);
			return destinationFile.getName();
		}
		finally {
			if (in != null) try { in.close(); } catch (IOException ex) {}
			if (out != null) try { out.close(); } catch (IOException ex) {}
		}
	}
}
