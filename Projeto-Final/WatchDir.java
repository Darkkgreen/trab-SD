import java.nio.file.*;
import static java.nio.file.StandardWatchEventKinds.*;
import static java.nio.file.LinkOption.*;
import java.nio.file.attribute.*;
import java.io.*;
import java.util.*;

public class WatchDir{

	private final WatchService watcher;
	private final Map<WatchKey, Path> keys;
	private final boolean recursive;
	private boolean trace = false;

	WatchDir(){
		this.watcher = FileSystems.getDefault().newWatchService();	

	}
}

