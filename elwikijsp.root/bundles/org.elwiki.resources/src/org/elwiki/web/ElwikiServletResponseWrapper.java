package org.elwiki.web;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Locale;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * A simple response wrapper that simply allows us to use the generated response
 * before it is transmitted to the client.<br/>
 * To access the generated data, a parallel output stream is connected, the data
 * of which is used for subsequent processing.
 * <p>
 * NOTE: The JSPWiki base code is extended by an idea taken from the Eclipse
 * Equinox Code (HttpServletResponseWrapper Impl): methods getOutputStream,
 * getWriter - return streams for writing, which are created based on the
 * superclass. <br/>
 * This revision allows to use the JSP mechanism of the error diagnostics page
 * without problems.
 * 
 * @author vfedorov
 */
public class ElwikiServletResponseWrapper extends HttpServletResponseWrapper {

	/**
	 * How large the initial buffer should be. This should be tuned to achieve a
	 * balance in speed and memory consumption.
	 */
	private static final int INIT_BUFFER_SIZE = 0x7000;

	private int status = -1;
	private String message;
	private boolean completed;
	private InternalOutputStream outputStream;
	private PrintWriter writer;

	private ByteArrayOutputStream outputBuffer;

	private boolean isUseEncoding;

	private String wikiEncoding;

	public ElwikiServletResponseWrapper(HttpServletResponse response, String wikiEncoding, boolean isUseEncoding) {
		super(response);
		this.wikiEncoding = wikiEncoding;
		this.isUseEncoding = isUseEncoding;
	}

	@Override
	public void sendError(int theStatus) {
		this.status = theStatus;
	}

	@Override
	public void sendError(int theStatus, String theMessage) {
		this.status = theStatus;
		this.message = theMessage;
	}

	public String getMessage() {
		return this.message;
	}

	@Override
	public int getStatus() {
		if (this.status == -1) {
			return super.getStatus();
		}
		return this.status;
	}

	public int getInternalStatus() {
		return this.status;
	}

	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		if (this.outputStream == null) {
			this.outputStream = new InternalOutputStream(super.getOutputStream(), getBuffer());
		}
		return this.outputStream;
	}

	@Override
	public PrintWriter getWriter() throws IOException {
		if (writer == null) {
			ByteArrayOutputStream outputBuffer1 = getBuffer();
			PrintWriter internalPrintWriter = new PrintWriter( //
					new OutputStreamWriter(outputBuffer1, this.wikiEncoding), true);
			this.writer = new InternalWriter(super.getWriter(), internalPrintWriter);
		}
		return this.writer;
	}

	protected ByteArrayOutputStream getBuffer() {
		if (this.outputBuffer == null) {
			this.outputBuffer = new ByteArrayOutputStream(INIT_BUFFER_SIZE);
		}

		return this.outputBuffer;
	}

	@Override
	public void flushBuffer() throws IOException {
		if (status != -1) {
			HttpServletResponse wrappedResponse = (HttpServletResponse) this.getResponse();
			wrappedResponse.sendError(status, getMessage());
		}
		super.flushBuffer();
		if (writer != null)
			writer.flush(); // workaround.
	}

	public boolean isCompleted() {
		return completed;
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
	}

	/**
	 * Returns whatever was written so far into the Writer.
	 * 
	 * @return
	 * @throws IOException
	 */
	public String getHtmlContent() throws IOException {
		String result = "";

		flushBuffer();

		if (this.isUseEncoding) {
			String characterEncoding = getResponse().getCharacterEncoding();
			result = outputBuffer.toString(characterEncoding);
		} else {
			result = outputBuffer.toString();
		}

		return result;
	}

	/**
	 * @author vfedorov
	 */
	private class InternalOutputStream extends ServletOutputStream {

		private final ServletOutputStream originalOutputStream;
		private ByteArrayOutputStream bufferOutputStream;

		public InternalOutputStream(ServletOutputStream originalOutputStream,
				ByteArrayOutputStream byteArrayOutputStream) {
			this.originalOutputStream = originalOutputStream;
			this.bufferOutputStream = byteArrayOutputStream;
		}

		@Override
		public void close() throws IOException {
			originalOutputStream.close();
			this.bufferOutputStream.close();
		}

		@Override
		public void flush() throws IOException {
			if (getInternalStatus() != -1) {
				HttpServletResponse wrappedResponse = (HttpServletResponse) ElwikiServletResponseWrapper.this
						.getResponse();
				wrappedResponse.sendError(getInternalStatus(), getMessage());
			}
			originalOutputStream.flush();
			this.bufferOutputStream.flush();
		}

		@Override
		public boolean isReady() {
			return originalOutputStream.isReady();
		}

		@Override
		public void setWriteListener(WriteListener writeListener) {
			originalOutputStream.setWriteListener(writeListener);
		}

		@Override
		public void write(int b) throws IOException {
			if (isCompleted()) {
				return;
			}
			originalOutputStream.write(b);
			this.bufferOutputStream.write(b);
		}

		@Override
		public void write(byte[] b) throws IOException {
			if (isCompleted()) {
				return;
			}
			originalOutputStream.write(b);
			this.bufferOutputStream.write(b);
		}

		@Override
		public void write(byte[] b, int off, int len) throws IOException {
			if (isCompleted()) {
				return;
			}
			originalOutputStream.write(b, off, len);
			this.bufferOutputStream.write(b, off, len);
		}
	}

	/**
	 * @author vfedorov
	 */
	private class InternalWriter extends PrintWriter {

		private PrintWriter internalPrintWriter;

		public InternalWriter(PrintWriter originalWriter, PrintWriter internalPrintWriter) {
			super(originalWriter);
			this.internalPrintWriter = internalPrintWriter;
		}

		@Override
		public PrintWriter format(Locale l, String format, Object... args) {
			if (!isCompleted()) {
				super.format(l, format, args);
				this.internalPrintWriter.format(l, format, args);
			}
			return this;
		}

		@Override
		public PrintWriter format(String format, Object... args) {
			if (!isCompleted()) {
				super.format(format, args);
				this.internalPrintWriter.format(format, args);
			}
			return this;
		}

		@Override
		public void println() {
			if (isCompleted()) {
				return;
			}
			super.println();
			this.internalPrintWriter.println();
		}

		@Override
		public void write(int c) {
			if (isCompleted()) {
				return;
			}
			super.write(c);
			this.internalPrintWriter.write(c);
		}

		@Override
		public void write(char[] buf, int off, int len) {
			if (isCompleted()) {
				return;
			}
			super.write(buf, off, len);
			this.internalPrintWriter.write(buf, off, len);
		}

		@Override
		public void write(char[] buf) {
			if (isCompleted()) {
				return;
			}
			super.write(buf);
			this.internalPrintWriter.write(buf);
		}

		@Override
		public void write(String s, int off, int len) {
			if (isCompleted()) {
				return;
			}
			super.write(s, off, len);
			this.internalPrintWriter.write(s, off, len);
		}

		@Override
		public void write(String s) {
			if (isCompleted()) {
				return;
			}
			super.write(s);
			this.internalPrintWriter.write(s);
		}
	}

}
