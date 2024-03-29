/*
    <<plugin>>
    TablePlugin
    Dirk Frederickx 
    v.001 Feb 2005
    v.002 Mar 2005 : bugfix, nested plugins, sorting, cell-level styles
    v.003 Jun 2006 : remove escape stuff, jspwiki now supports nested templates
    v.004 Dec 2020 : ported into ElWiki (by Victor Fedorov)

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation; either version 2.1 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package org.elwiki.plugins;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.log4j.Logger;
import org.apache.wiki.api.core.Engine;
import org.apache.wiki.api.core.WikiContext;
import org.apache.wiki.api.engine.RenderApi;
import org.apache.wiki.api.exceptions.PluginException;
import org.apache.wiki.render0.RenderingManager;
import org.elwiki.api.plugin.InitializablePlugin;
import org.elwiki.api.plugin.WikiPlugin;
import org.elwiki_data.WikiPage;

/**
 * Extend JSPWiki syntax for tables
 *
 * <p>
 * Parameters
 * </p>
 * <ul>
 * <li>_body: actual table definition
 * <li>rowNumber: starting row number
 * <li>style: CSS style for the table
 * <li>headerStyle: CSS style for the header cells
 * <li>dataStyle: CSS style for the data cells
 * <li>evenRowStyle: CSS style for the even rows
 * <li>oddRowStyle: CSS style for the odd rows
 * </ul>
 *
 * @author Dirk Frederickx
 */
public class TablePlugin implements WikiPlugin, InitializablePlugin {

	private static Logger log = Logger.getLogger(TablePlugin.class);

	enum State {
		S_IDLE, S_IDLE_BLANK, S_ONE_CELL, S_STANDARD_ROW, S_MULTILINE_ROW, S_MULTILINE_ROW_DELIM,
	};

	/**
	 * local class definition -- storage container for a table cell
	 */
	private class TableCell {
		public int start;
		public int end;
		public boolean isHeader;
		public boolean colspan;
		public boolean rowspan;
		public boolean cssStyle;
		public int cssBracket;
		public int cssStart;
		public int cssEnd;

		public int registerStart(StringBuffer sb, int aCursor) {
			end = -1;
			cssStart = -1;
			cssEnd = -1;
			cssStyle = false;
			cssBracket = 0;
			colspan = false;
			rowspan = false;
			isHeader = false;

			if (aCursor + 1 < sb.length()) // parse also next char
			{
				char c = sb.charAt(aCursor + 1);

				if (c == '|') {
					isHeader = true;
					aCursor++;
				}
			}

			if (aCursor + 1 < sb.length()) // parse also next char
			{
				char c = sb.charAt(aCursor + 1);

				if (c == '<') {
					colspan = true;
					aCursor++;
				} else if (c == '^') {
					rowspan = true;
					aCursor++;
				} else if (c == '(') {
					cssStyle = true;
					cssBracket = 1;
					aCursor++;
				}
			}

			start = aCursor + 1;

			return aCursor;
		}

		/**
		 * Parse cell with css-style: |(css-style) ... Take care, the css style may contain nested ()
		 * 
		 * @param sb
		 * @param aCursor
		 */
		public void handleCss(StringBuffer sb, int aCursor) {
			char c = sb.charAt(aCursor);

			if (c == '(') {
				cssBracket++;
			} else if (c == ')') {
				cssBracket--;
			}
			;

			if (cssBracket == 0) {
				cssStyle = false;
				cssStart = start; // skip |(
				cssEnd = aCursor;
				start = cssEnd + 1;
			}
		}

		/**
		 * Register the end of a table cell
		 * 
		 * @param sb
		 * @param aCursor
		 * @return
		 */
		public TableCell registerEnd(StringBuffer sb, int aCursor) {
			end = aCursor;
			if (start > end) {
				end = start; // :FVK: workaround. (can happens for strings without a trailing space.)
			}
			log.debug("Cell $$" + sb.substring(start, end) + "$$");
			return this;
		}
	}
	
	public static final String PARAM_BODY = "_body";
	public static final String PARAM_STYLE = "style";
	public static final String PARAM_STYLE_HEADER = "headerStyle";
	public static final String PARAM_STYLE_DATA = "dataStyle";
	public static final String PARAM_STYLE_ROW_ODD = "evenRowStyle";
	public static final String PARAM_STYLE_ROW_EVEN = "oddRowStyle";
	public static final String PARAM_ROW_NUMBER = "rowNumber";

	private WikiContext m_context;

	private StringBuffer m_result = new StringBuffer();
	private String m_style;
	private String m_style_header;
	private String m_style_data;
	private String m_styleRowEven;
	private String m_styleRowOdd;
	private int m_startRow;

	RenderingManager renderingManager;

	@Override
	public void initialize(Engine engine) throws PluginException {
		this.renderingManager = engine.getManager(RenderingManager.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String execute(WikiContext context, Map<String, String> params) throws PluginException {
		m_context = context;

		WikiPage page = context.getPage();
		if (page == null)
			return "";

		// parse parameters
		String body = (String) params.get(PARAM_BODY);
		if (body == null)
			return "";

		m_style = (String) params.get(PARAM_STYLE);
		m_style_header = (String) params.get(PARAM_STYLE_HEADER);
		m_style_data = (String) params.get(PARAM_STYLE_DATA);
		m_styleRowEven = (String) params.get(PARAM_STYLE_ROW_EVEN);
		m_styleRowOdd = (String) params.get(PARAM_STYLE_ROW_ODD);

		m_startRow = NumberUtils.toInt((String) params.get(PARAM_ROW_NUMBER), 0);

		log.info("Running TABLE plugin\n");

		m_result = new StringBuffer("");

		processTable(body);

		return m_result.toString();
	}

	/**
	 * Actual processing of the table rows
	 * 
	 * @param aBody
	 */
	protected void processTable(String aBody) {
		StringBuffer sb = new StringBuffer(aBody);
		int i = 0; // running index in body string
		char bi; // character at index i in body string
		int bLen = sb.length(); // length of body string

		boolean opaqueCode = false;
		boolean opaquePre = false;
		boolean opaqueLink = false;
		boolean opaqueCSS = false;
		int opaquePlugin = 0;

		State state = State.S_IDLE;

		TableCell cell = null;
		List<TableCell> row = new ArrayList<>();
		List<List<TableCell>> table = new ArrayList<>();

		while (i < bLen) // while not beyond end of file
		{
			bi = sb.charAt(i);

			if (cell == null) {
				if (bi == '\n') {
					state = State.S_IDLE;
				} else if (Character.isWhitespace(bi)) {
					state = State.S_IDLE_BLANK;
				}

				else if ((bi == '|') && (state == State.S_IDLE)) // cell at start of line
				{
					cell = new TableCell();
					i = cell.registerStart(sb, i);
					row.add(cell);
					state = State.S_ONE_CELL;
				} else // char outside a table cell
				{
					log.info("ERR: CHAR outside table \"" + bi + "\"");
					break;
				}
			} else // cell != null : process data inside a cell
			{
				if (startsWith("[{", sb, i)) {
					opaquePlugin++;
					i++;
				} else if (startsWith("}]", sb, i)) {
					opaquePlugin--;
					i++;
				} else if (opaquePlugin > 0) {
				}

				else if (opaqueCode) // skip {{{ code-block }}}
				{
					if (startsWith("}}}", sb, i)) {
						opaqueCode = false;
						i += 2;
					}
				} else if (startsWith("{{{", sb, i)) {
					opaqueCode = true;
					i += 2;
				}

				else if (opaquePre) // skip {{ preformatted text }}
				{
					if (startsWith("}}", sb, i)) {
						opaquePre = false;
						i++;
					}
				} else if (startsWith("{{", sb, i)) {
					opaquePre = true;
					i++;
				}

				else if (opaqueCSS) // skip %% css styles %%
				{
					if (startsWith("%%", sb, i)) {
						opaqueCSS = false;
						i++;
					}
				} else if (startsWith("%%", sb, i)) {
					opaqueCSS = true;
					i++;
				}

				else if (opaqueLink) // skip [text | opaqueLinks]
				{
					if (bi == ']') {
						opaqueLink = false;
					}
				} else if (bi == '[') {
					opaqueLink = true;
				}

				else if (cell.cssStyle) {
					cell.handleCss(sb, i);
				} else if (startsWith("~#", sb, i)) // escape the #
				{
					i++;
				} else if (bi == '#') {
					sb.deleteCharAt(i);
					sb.insert(i, table.size() + m_startRow);
					bLen = sb.length();
				} else if (startsWith("~|", sb, i)) // escape the #
				{
					i++;
				}

				/*
				 * Pseudo
				 * 
				 * idle | => start new cell; mode=one_cell \n => mode=idle blank => mode=idle blank else <break>
				 * 
				 * idle_blank \n => mode=idle blank => mode=idle blank else <break>
				 * 
				 * one_cell \n| => end prev cell; start new cell ; mode=multiline row | => end prev cell; start new cell
				 * ; mode=standard row \n => mode=multiline row delim
				 * 
				 * standard-row | => end prev cell; start new cell ; mode=standard row \n => end prev cell; row++ ;
				 * mode=idle;
				 * 
				 * multiline_row \n| => end prev cell; start new cell; mode=multiline row \n => mode = multiline row
				 * delim
				 * 
				 * multiline_row_delim \n => end prev cell; row++ ; mode=idle; non-blank => mode=multiline row
				 * 
				 */

				else if ((startsWith("\n|", sb, i))
						&& ((state == State.S_ONE_CELL) || (state == State.S_MULTILINE_ROW))) {
					cell.registerEnd(sb, i); // :FVK: was: cell.registerEnd(sb, i - 1);

					cell = new TableCell();
					i = cell.registerStart(sb, i + 1);
					row.add(cell);
					state = State.S_MULTILINE_ROW;
				}

				else if ((bi == '|') && ((state == State.S_ONE_CELL) || (state == State.S_STANDARD_ROW))) {
					cell.registerEnd(sb, i);

					cell = new TableCell();
					i = cell.registerStart(sb, i);
					row.add(cell);
					state = State.S_STANDARD_ROW;
				}

				else if ((bi == '\n') && ((state == State.S_ONE_CELL) || (state == State.S_MULTILINE_ROW))) {
					state = State.S_MULTILINE_ROW_DELIM;
				}

				else if ((bi == '\n') && ((state == State.S_STANDARD_ROW) || (state == State.S_MULTILINE_ROW_DELIM))) {
					cell.registerEnd(sb, i);

					table.add(row);
					row = new ArrayList<>();
					cell = null;
					state = State.S_IDLE;
				}

				else if ((state == State.S_MULTILINE_ROW_DELIM) && (!Character.isWhitespace(bi))) {
					state = State.S_MULTILINE_ROW;
				}

			} // cell != null

			i++; // take next char
		}

		if (cell != null) {
			cell.registerEnd(sb, i);
			table.add(row);
		}

		processTableFlush(table, sb);
	}

	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

	/**
	 * Append HTML table to output
	 * 
	 * @param aTable
	 * @param aBody
	 */
	protected void processTableFlush(List<List<TableCell>> aTable, StringBuffer aBody) {
		if (aTable.isEmpty())
			return;

		m_result.append("<table border='1' class='wikitable'");

		if (m_style != null)
			m_result.append(" style=\"" + m_style + "\" ");

		m_result.append("> \n");

		int rowCounter = -1;
		for (List<TableCell> row : aTable) {
			rowCounter++;
			m_result.append("<tr>\n");

			int cellCounter = -1;
			for (TableCell cell : row) {
				cellCounter++;
				if (cell.colspan)
					continue; // this cell is collapsed with previous col
				if (cell.rowspan)
					continue; // this cell is collpased with previous row

				if (cell.isHeader) {
					m_result.append("<th");

					if (m_style_header != null)
						m_result.append(" style=\"" + m_style_header + "\"");
				} else {
					m_result.append("<td");

					String style = "";
					if (m_style_data != null)
						style += m_style_data + " ";
					if (cell.cssStart != -1)
						style += aBody.substring(cell.cssStart, cell.cssEnd);
					if ((m_styleRowEven != null) && (rowCounter % 2 == 0))
						style += m_styleRowEven + " ";
					if ((m_styleRowOdd != null) && (rowCounter % 2 != 0))
						style += m_styleRowOdd;
					if (style != "")
						m_result.append(" style=\"" + style + "\"");
				}

				int colspan = getColSpan(aTable, rowCounter, cellCounter);
				if (colspan > 1)
					m_result.append(" colspan='" + colspan + "'");

				int rowspan = getRowSpan(aTable, rowCounter, cellCounter);
				if (rowspan > 1)
					m_result.append(" rowspan='" + rowspan + "'");

				m_result.append(">");

				m_result.append(((RenderApi) this.renderingManager).textToHTML(m_context,
						aBody.substring(cell.start, cell.end)));

				if (cell.isHeader) {
					m_result.append("</th>\n");
				} else {
					m_result.append("</td>\n");
				}
			}

			m_result.append("</tr>\n");

			row.clear();

		}

		aTable.clear();
		m_result.append("</table>\n");

	}

	/**
	 * Calculate ColSpan by looking at next cells on the same row
	 * 
	 * @param aTable
	 * @param rowIndex
	 * @param columnIndex
	 * @return
	 */
	protected int getColSpan(List<List<TableCell>> aTable, int rowIndex, int columnIndex) {
		int colspan = 1;
		List<TableCell> row = aTable.get(rowIndex);

		while (++columnIndex < row.size()) {
			TableCell nextCell = (TableCell) row.get(columnIndex);
			if (!nextCell.colspan)
				break;
			colspan++;
		}

		return colspan;
	}

	/**
	 * Calculate RowSpan by looking at next rows
	 * 
	 * @param aTable
	 * @param rowIndex
	 * @param columnIndex
	 * @return
	 */
	protected int getRowSpan(List<List<TableCell>> aTable, int rowIndex, int columnIndex) {
		int rowspan = 1;

		while (++rowIndex < aTable.size()) {
			List<TableCell> nextRow = aTable.get(rowIndex);

			if (columnIndex < nextRow.size()) {
				TableCell nextRowCell = (TableCell) nextRow.get(columnIndex);
				if (!nextRowCell.rowspan)
					break;
				rowspan++;
			}

		}

		return rowspan;

	}

	/**
	 * Helper routing : startsWith
	 * 
	 * @param s
	 * @param sb
	 * @param startIndex
	 * @return
	 */
	protected boolean startsWith(String s, StringBuffer sb, int startIndex) {
		if (s.length() > sb.length() - startIndex)
			return false;

		for (int i = 0; i < s.length(); i++) {
			if (s.charAt(i) != sb.charAt(i + startIndex))
				return false;
		}
		
		return true;
	}

}
