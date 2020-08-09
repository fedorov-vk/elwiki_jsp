package org.elwiki.pageprovider.cdo;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.eclipse.core.runtime.Assert;
import org.elwiki_data.WikiPage;

public class TextChanger {

	private Set<String> setRefPagesId;
	private List<WikiPage> wikiPages;

	/**
	 * Default Constructor.
	 */
	public TextChanger() {
		super();
	}

	/**
	 * Изменение текста страницы.</br>
	 * Замена формата ссылок JSPwiki на ElWiki.
	 * 
	 * @param pageText
	 * @param setRefPagesId1
	 * @param wikiPages
	 * @throws Exception
	 */
	public String changeText(String pageText, Set<String> setRefPagesId1, List<WikiPage> wikiPages1) throws Exception {
		this.setRefPagesId = setRefPagesId1;
		this.wikiPages = wikiPages1;

		String regexp = "\\[[^\\]]+\\]"; // регулярное выражение.
		Pattern pattern;
		try { // Create a Pattern object
			pattern = Pattern.compile(regexp);
		} catch (PatternSyntaxException e) {
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}

		// Now create matcher object.
		Matcher matcher = pattern.matcher(pageText);
		StringBuffer sb = new StringBuffer();
		boolean isFound;
		do {
			isFound = matcher.find();
			if (isFound) {
				String str = matcher.group();
				str = str.replaceAll("\\n", "");
				//
				if (!str.startsWith("[[") && !str.startsWith("[{")) { // не [[ ... ] или [{ ... ]
					str = str.substring(1, str.length() - 1).trim(); // Удалить: скобки, ведущие/ведомые пробелы.
					String link;
					try {
						link = makeNewLink(str);
					} catch (Exception e) {
						link = str; // страница не найдена - оставить ссылку нетронутой.
					}
					if (link.lastIndexOf("$") >= 0) {
						link = link.replaceAll("\\$", "\\\\\\$");
					}
					try {
						matcher.appendReplacement(sb, "[" + link + "]");
					} catch (Exception e) {
						System.out.printf("ERROR:: link: %s", link);
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		} while (isFound);
		matcher.appendTail(sb);

		return sb.toString();
	}

	private String makeNewLink(String jspWikiLink) throws Exception {
		String[] strings = jspWikiLink.split("\\|", 2);

		String pageName = strings[0].trim(); // это - имя (возможно это ссылка) реальной страницы (инфо из ссылок старого формата )
		String linkName = pageName; // это - "имя"/text ссылки (новый формат).
		String pageId = ""; // идентификатор страницы. 
		String attributes = "";

		if (strings.length == 1) { // простая ссылка, т.е. без разименования.
			pageId = getPageId(pageName);
		} else { // strings.length > 1
			pageName = strings[1].trim();
			boolean isAttributes = strings[1].indexOf("|") >= 0;
			if (isAttributes) { // выделить атрибуты.
				strings = strings[1].split("\\|", 2);
				pageName = strings[0].trim(); // страница-ссылка (или просто http://...).
				attributes = strings[1].trim(); // атрибуты ссылки.
			}
			// Обработка якоря ( ...#ancor ).
			String anchor = "";
			boolean isExtLink = pageName.indexOf("#") >= 0;
			if (isExtLink) { // Выделить якорь.
				anchor = pageName.substring(pageName.indexOf("#") + 1);
				pageName = pageName.substring(0, pageName.indexOf("#"));
			}
			pageId = getPageId(pageName);
			if (anchor.length() > 0) { // восстановить 'якорь'.
				pageId += "#" + anchor;
			}
		}

		if (pageId.length() > 0) { // для сохранения в список ссылок страницы.
			this.setRefPagesId.add(pageId);
		}

		String link = linkName + " | @" + pageId; // :FVK:
		if (attributes.length() > 0) {
			link += " | " + attributes;
		}
		return link;
	}

	/**
	 * Получить индентификатор страницы, по имени страницы, т.е. {pageName}.
	 * 
	 * @param pageName1
	 * @return
	 * @throws Exception
	 */
	private String getPageId(String pageName1) throws Exception {
		for (WikiPage wikiPage : this.wikiPages) {
			String pageName = wikiPage.getName();
			if (pageName1.equals(pageName)) {
				return wikiPage.getId();
			}
		}
		throw new Exception("Wiki page '" + pageName1 + "' not found.");
	}

	/**
	 * @param pageText
	 *            текст страницы.
	 * @return список идентификаторов ссылок вида "@13245".
	 */
	public Set<String> getReferences(String pageText) {
		Set<String> references = new HashSet<>();
		if (pageText == null) {
			return references;
		}
		if (pageText.trim().length() == 0) {
			return references;
		}

		String regexp = "@(\\d+)";
		Pattern pattern = null;
		try { // Create a Pattern object
			pattern = Pattern.compile(regexp);
		} catch (PatternSyntaxException e) {
			e.printStackTrace();
		}

		Assert.isNotNull(pattern, "Internal error!"); //:FVK:

		/* Изменить содержимое PageContent - изменить ссылки. */
		Matcher matcher = pattern.matcher(pageText); // create matcher object.
		boolean isFound = false;
		do {
			isFound = matcher.find();
			if (isFound) {
				String cdoId = matcher.group(1);
				references.add(cdoId);
			}
		} while (isFound);

		return references;
	}

	/**
	 * Преобразование ссылок @id текста страницы согласно заданной карте.
	 * 
	 * @param oldContent
	 *            Старое содержимое.
	 * @param oldId2NewId
	 * @return Новое содержимое.
	 */
	public static String fixTextForReferences(String oldContent, Map<String, String> oldId2NewId) {
		if (oldContent == null) {
			return ""; // WORKAROUND.
		}

		String regexp = "@(\\d+)";
		Pattern pattern = null;
		try { // Create a Pattern object
			pattern = Pattern.compile(regexp);
		} catch (PatternSyntaxException e) {
			e.printStackTrace();
		}

		Assert.isNotNull(pattern, "Internal error!"); //:FVK:

		/* Изменить содержимое PageContent - изменить ссылки. */
		Matcher matcher = pattern.matcher(oldContent); // create matcher object.
		StringBuffer sb = new StringBuffer();
		boolean isFound = false;
		do {
			isFound = matcher.find();
			if (isFound) {
				String oldId = matcher.group(1);
				String newId = oldId2NewId.get(oldId);
				if (newId != null) {
					matcher.appendReplacement(sb, "@" + newId);
				}
			}
		} while (isFound);
		matcher.appendTail(sb);

		return sb.toString();
	}
}
