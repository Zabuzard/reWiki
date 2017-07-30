package de.zabuza.rewiki.tasks.map;

import de.zabuza.rewiki.WikiHub;
import de.zabuza.rewiki.exceptions.UnexpectedIOException;
import de.zabuza.rewiki.tasks.IWikiTask;
import de.zabuza.rewiki.tasks.WikiTaskUtil;
import net.sourceforge.jwbf.core.contentRep.Article;

public final class AreaListTask implements IWikiTask {
	private static final String ARTICLE = "Gebiete (Liste)";
	private static final String COMMAND = "javac SCRIPT_UNCOMPILED && java SCRIPT_COMPILED";
	private static final String SCRIPT_COMPILED = "arealist";
	private static final String SCRIPT_UNCOMPILED = "arealist.java";
	private static final String TARGET = "areaListDebugOutput.txt";

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.zabuza.rewiki.tasks.IWikiTask#executeCommand()
	 */
	@Override
	public void executeCommand() throws UnexpectedIOException {
		final String command = COMMAND.replaceAll("SCRIPT_UNCOMPILED", WikiTaskUtil.getPathToScript(SCRIPT_UNCOMPILED))
				.replaceAll("SCRIPT_COMPILED", WikiTaskUtil.getPathToScript(SCRIPT_COMPILED));
		WikiTaskUtil.executeCommand(command, TARGET);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.zabuza.rewiki.tasks.IWikiTask#pushToWiki(de.zabuza.rewiki.WikiHub)
	 */
	@Override
	public void pushToWiki(final WikiHub wiki) throws UnexpectedIOException {
		final String resultingContent = WikiTaskUtil.readContent(TARGET);

		// Save the resulting content to the article
		final Article article = wiki.getArticle(ARTICLE);
		article.setText(resultingContent);
		wiki.saveArticle(article);
	}

}
