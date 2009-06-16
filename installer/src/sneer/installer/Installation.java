package sneer.installer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;

class Installation {

	private File _sneerHome;
	private File _sneerInstallDir;

	Installation(File sneerHome) throws IOException {
		_sneerHome = sneerHome;
		_sneerInstallDir = new File(_sneerHome.getParentFile(), "sneer_installer");
		
		createDirectory();
		addBinaries();
		renameDirectory();
	}

	private void createDirectory() throws IOException {
		IOUtils.deleteDirectory(_sneerInstallDir);
		
		if(_sneerHome.exists()){
			_sneerInstallDir=_sneerHome;
			return;
		}
		
		_sneerInstallDir.mkdirs();
	}

	private void renameDirectory() throws IOException {
		if(_sneerInstallDir == _sneerHome) return;
		if(!_sneerInstallDir.renameTo(_sneerHome))
			throw new IOException(_sneerInstallDir.getAbsolutePath() + " can't renamed to " + _sneerHome.getAbsolutePath());	
	}
	
	private void addBinaries() throws IOException {
		URL jarFileName = this.getClass().getResource("/sneer.jar");
		IOUtils.write(new  File(_sneerInstallDir, "log.txt"), "jar file url: " + jarFileName.toString());
		File file = extractJar(jarFileName);
		extractFiles(file);
	}

	private File extractJar(URL url) throws IOException {
		File file =  File.createTempFile("sneer", ".jar");
		file.deleteOnExit();

		InputStream input = url.openStream();
		IOUtils.copyToFile(input, file);
		input.close();
		return file;
	}
	
	private void extractFiles(File src) throws IOException {
		IOUtils.write(new  File(_sneerInstallDir, "log.txt"), "expand files from: " + src.getAbsolutePath());
		if(!(src.exists()))
			throw new IOException("File '" + src.getAbsolutePath() + "' not found!");	

		FileInputStream inputStream = new FileInputStream(src);
		extractFiles(src, inputStream);
		inputStream.close();
	}

	private void extractFiles(File src, FileInputStream inputStream) throws IOException {
		JarInputStream jis = new JarInputStream(inputStream);
		JarFile jar = new JarFile(src);
		JarEntry entry = null;
		
        while ((entry = jis.getNextJarEntry()) != null) {
        	File file = new File(new File(_sneerInstallDir, "code"), entry.getName());

        	if(entry.isDirectory()) {
				file.mkdirs();
				continue;
        	}
        	
        	IOUtils.write(file, IOUtils.readEntryBytes(jar, entry));
        }
	}
}