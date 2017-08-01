package de.zabuza.rewiki.tasks.map;

import java.util.LinkedList;

import de.zabuza.rewiki.WikiHub;
import de.zabuza.rewiki.exceptions.UnexpectedIOException;
import de.zabuza.rewiki.tasks.IWikiTask;
import de.zabuza.rewiki.tasks.WikiTaskUtil;
import net.sourceforge.jwbf.core.contentRep.Article;

/**
 * Task that calls a script which creates the article
 * <tt>Vorlage:LocateRegion/Data</tt> from the content of
 * <tt>Koordinaten (Liste)</tt>.
 * 
 * @author Zabuza {@literal <zabuza.dev@gmail.com>}
 *
 */
public final class LocateRegionTask implements IWikiTask {
	/**
	 * The article to push the results to.
	 */
	private static final String ARTICLE = "Vorlage:LocateRegion/Data";
	/**
	 * The command to use.
	 */
	private static final String COMMAND = "php";
	/**
	 * The name of the script to execute.
	 */
	private static final String SCRIPT = "LocateRegion.php";
	/**
	 * The file to redirect the produced standard output of the script to.
	 */
	private static final String TARGET = "LocateRegion.txt";

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.zabuza.rewiki.tasks.IWikiTask#executeCommand()
	 */
	@Override
	public void executeCommand() throws UnexpectedIOException {
		final LinkedList<String> command = new LinkedList<>();
		command.add(COMMAND);
		command.add(WikiTaskUtil.getPathToScript(SCRIPT));

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
