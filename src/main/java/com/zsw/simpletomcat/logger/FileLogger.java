package com.zsw.simpletomcat.logger;

import com.zsw.simpletomcat.Lifecycle;
import com.zsw.simpletomcat.LifecycleException;
import com.zsw.simpletomcat.LifecycleListener;
import com.zsw.simpletomcat.util.LifecycleSupport;
import com.zsw.simpletomcat.util.StringManager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;

/**
 * @author zsw
 * @date 2020/07/24 17:11
 */
public class FileLogger extends LoggerBase implements Lifecycle {
	private String date = "";


	private String directory = "logs";


	protected static final String info =
			"com.zsw.simpletomcat.logger.FileLogger/1.0";


	protected LifecycleSupport lifecycle = new LifecycleSupport(this);


	private String prefix = "simpletomcat.";


	private StringManager sm =
			StringManager.getManager("com.zsw.simpletomcat.logger");


	private boolean started = false;


	private String suffix = ".log";


	private boolean timestamp = false;


	private PrintWriter writer = null;


	public String getDirectory() {

		return (directory);

	}

	public void setDirectory(String directory) {

		String oldDirectory = this.directory;
		this.directory = directory;
		support.firePropertyChange("directory", oldDirectory, this.directory);

	}


	/**
	 * Return the log file prefix.
	 */
	public String getPrefix() {

		return (prefix);

	}


	public void setPrefix(String prefix) {

		String oldPrefix = this.prefix;
		this.prefix = prefix;
		support.firePropertyChange("prefix", oldPrefix, this.prefix);

	}


	public String getSuffix() {

		return (suffix);

	}


	public void setSuffix(String suffix) {

		String oldSuffix = this.suffix;
		this.suffix = suffix;
		support.firePropertyChange("suffix", oldSuffix, this.suffix);

	}


	public boolean getTimestamp() {

		return (timestamp);

	}



	public void setTimestamp(boolean timestamp) {

		boolean oldTimestamp = this.timestamp;
		this.timestamp = timestamp;
		support.firePropertyChange("timestamp", new Boolean(oldTimestamp),
				new Boolean(this.timestamp));

	}


	@Override
	public void log(String msg) {

		Timestamp ts = new Timestamp(System.currentTimeMillis());
		String tsString = ts.toString().substring(0, 19);
		String tsDate = tsString.substring(0, 10);

		if (!date.equals(tsDate)) {
			synchronized (this) {
				if (!date.equals(tsDate)) {
					close();
					date = tsDate;
					open();
				}
			}
		}

		if (writer != null) {
			if (timestamp) {
				writer.println(tsString + " " + msg);
			} else {
				writer.println(msg);
			}
		}

	}



	private void close() {

		if (writer == null) {
			return;
		}
		writer.flush();
		writer.close();
		writer = null;
		date = "";

	}


	private void open() {

		// Create the directory if necessary
		File dir = new File(directory);
		if (!dir.isAbsolute())
			dir = new File(System.getProperty("catalina.base"), directory);
		dir.mkdirs();

		// Open the current log file
		try {
			String pathname = dir.getAbsolutePath() + File.separator +
					prefix + date + suffix;
			writer = new PrintWriter(new FileWriter(pathname, true), true);
		} catch (IOException e) {
			writer = null;
		}


	}


	// ------------------------------------------------------ Lifecycle Methods


	/**
	 * Add a lifecycle event listener to this component.
	 *
	 * @param listener The listener to add
	 */
	@Override
	public void addLifecycleListener(LifecycleListener listener) {

		lifecycle.addLifecycleListener(listener);

	}


	/**
	 * Get the lifecycle listeners associated with this lifecycle. If this
	 * Lifecycle has no listeners registered, a zero-length array is returned.
	 */
	@Override
	public LifecycleListener[] findLifecycleListeners() {

		return lifecycle.findLifecycleListeners();

	}


	/**
	 * Remove a lifecycle event listener from this component.
	 *
	 * @param listener The listener to add
	 */
	public void removeLifecycleListener(LifecycleListener listener) {

		lifecycle.removeLifecycleListener(listener);

	}


	/**
	 * Prepare for the beginning of active use of the public methods of this
	 * component.  This method should be called after <code>configure()</code>,
	 * and before any of the public methods of the component are utilized.
	 *
	 * @exception LifecycleException if this component detects a fatal error
	 *  that prevents this component from being used
	 */
	public void start() throws LifecycleException {

		// Validate and update our current component state
		if (started)
			throw new LifecycleException
					(sm.getString("fileLogger.alreadyStarted"));
		lifecycle.fireLifecycleEvent(START_EVENT, null);
		started = true;

	}


	/**
	 * Gracefully terminate the active use of the public methods of this
	 * component.  This method should be the last one called on a given
	 * instance of this component.
	 *
	 * @exception LifecycleException if this component detects a fatal error
	 *  that needs to be reported
	 */
	public void stop() throws LifecycleException {

		// Validate and update our current component state
		if (!started)
			throw new LifecycleException
					(sm.getString("fileLogger.notStarted"));
		lifecycle.fireLifecycleEvent(STOP_EVENT, null);
		started = false;

		close();

	}
}
