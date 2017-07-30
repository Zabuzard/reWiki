package de.zabuza.rewiki.tasks.map;

import de.zabuza.rewiki.WikiHub;
import de.zabuza.rewiki.exceptions.UnexpectedIOException;
import de.zabuza.rewiki.tasks.IWikiTask;
import de.zabuza.rewiki.tasks.WikiTaskUtil;
import net.sourceforge.jwbf.core.contentRep.Article;

public final class CoordinateListTask implements IWikiTask {
	private static final String ARTICLE = "Koordinaten (Liste)";
	private static final String COMMAND = "php SCRIPT";
	private static final String SCRIPT = "maplist2wiki.php";
	private static final String TARGET = "wikimaplist.txt";

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.zabuza.rewiki.tasks.IWikiTask#executeCommand()
	 */
	@Override
	public void executeCommand() throws UnexpectedIOException {
		WikiTaskUtil.executeCommand(COMMAND, TARGET, WikiTaskUtil.getPathToScript(SCRIPT));
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
