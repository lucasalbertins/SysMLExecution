package br.ufrpe.dc.sysml.control;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.omg.sysml.lang.sysml.Element;
import org.omg.sysml.lang.sysml.Namespace;
import org.omg.sysml.interactive.SysMLInteractive;
import org.omg.sysml.interactive.SysMLInteractiveResult;

public abstract class SysMLInteractiveTest {

	private final static String SYSML_LIBRARY_PATH_KEY = "libraryPath";
	
	public String getLibraryPath() {
		return System.getProperty(SYSML_LIBRARY_PATH_KEY);
	}
	
	public SysMLInteractive getSysMLInteractiveInstance() {
		SysMLInteractive instance = SysMLInteractive.getInstance();
		instance.setVerbose(false);
		instance.loadLibrary(getLibraryPath());
		return instance;
	}
	
	public List<Element> process(SysMLInteractive instance, String input) {
		if (instance == null) {
			instance = getSysMLInteractiveInstance();
		}
		SysMLInteractiveResult result = instance.process(input);
		assertTrue(result.getIssues().isEmpty(),"'" + input + "':\n" + result.formatIssues());
		Element root = result.getRootElement();
		assertTrue(root instanceof Namespace);
		return ((Namespace)root).getOwnedMember();
	}
	
	public List<Element> process(String input) {
		return process(null, input);
	}
}