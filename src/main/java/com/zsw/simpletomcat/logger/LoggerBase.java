package com.zsw.simpletomcat.logger;

import com.zsw.simpletomcat.Container;
import com.zsw.simpletomcat.LifecycleException;
import com.zsw.simpletomcat.Logger;

import javax.servlet.ServletException;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.CharArrayWriter;
import java.io.PrintWriter;

/**
 * @author zsw
 * @date 2020/07/24 17:04
 */
public abstract class LoggerBase implements Logger {


	protected Container container = null;


	protected int debug = 0;


	protected static final String info =
			"com.zsw.simpletomcat.logger.LoggerBase/1.0";

	protected PropertyChangeSupport support = new PropertyChangeSupport(this);


	protected int verbosity = ERROR;



	@Override
	public Container getContainer() {

		return (container);

	}



	@Override
	public void setContainer(Container container) {

		Container oldContainer = this.container;
		this.container = container;
		support.firePropertyChange("container", oldContainer, this.container);

	}


	public int getDebug() {

		return (this.debug);

	}


	public void setDebug(int debug) {

		this.debug = debug;

	}


	@Override
	public String getInfo() {

		return (info);

	}


	@Override
	public int getVerbosity() {

		return (this.verbosity);

	}


	@Override
	public void setVerbosity(int verbosity) {

		this.verbosity = verbosity;

	}


	public void setVerbosityLevel(String verbosity) {

		if ("FATAL".equalsIgnoreCase(verbosity))
			this.verbosity = FATAL;
		else if ("ERROR".equalsIgnoreCase(verbosity))
			this.verbosity = ERROR;
		else if ("WARNING".equalsIgnoreCase(verbosity))
			this.verbosity = WARNING;
		else if ("INFORMATION".equalsIgnoreCase(verbosity))
			this.verbosity = INFORMATION;
		else if ("DEBUG".equalsIgnoreCase(verbosity))
			this.verbosity = DEBUG;

	}


	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {

		support.addPropertyChangeListener(listener);

	}


	@Override
	public abstract void log(String msg);



	@Override
	public void log(Exception exception, String msg) {

		log(msg, exception);

	}



	@Override
	public void log(String msg, Throwable throwable) {

		CharArrayWriter buf = new CharArrayWriter();
		PrintWriter writer = new PrintWriter(buf);
		writer.println(msg);
		throwable.printStackTrace(writer);
		Throwable rootCause = null;
		if (throwable instanceof LifecycleException) {
			rootCause = ((LifecycleException) throwable).getThrowable();
		} else if (throwable instanceof ServletException) {
			rootCause = ((ServletException) throwable).getRootCause();
		}
		if (rootCause != null) {
			writer.println("----- Root Cause -----");
			rootCause.printStackTrace(writer);
		}
		log(buf.toString());

	}



	@Override
	public void log(String message, int verbosity) {

		if (this.verbosity >= verbosity)
			log(message);

	}


	@Override
	public void log(String message, Throwable throwable, int verbosity) {

		if (this.verbosity >= verbosity)
			log(message, throwable);

	}


	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {

		support.removePropertyChangeListener(listener);

	}

}
