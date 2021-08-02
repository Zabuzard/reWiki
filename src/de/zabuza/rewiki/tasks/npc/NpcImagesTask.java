package de.zabuza.rewiki.tasks.npc;

import java.util.LinkedList;

import de.zabuza.rewiki.WikiHub;
import de.zabuza.rewiki.exceptions.UnexpectedIOException;
import de.zabuza.rewiki.tasks.IWikiTask;
import de.zabuza.rewiki.tasks.WikiTaskUtil;
import net.sourceforge.jwbf.core.contentRep.Article;

/**
 * Task that calls a script which creates the article <tt>NPC-Bilder</tt> from
 * the results of {@link NpcListDataTask}.
 * 
 * @author Zabuza {@literal <zabuza.dev@gmail.com>}
 *
 */
public final class NpcImagesTask implements IWikiTask {
	/**
	 * The article to push the results to.
	 */
	private static final String ARTICLE = "NPC-Bilder";
	/**
	 * The command to use.
	 */
	private static final String COMMAND = "perl";
	/**
	 * The file containing the intermediate results of {@link NpcListDataTask}.
	 */
	private static final String INPUT = "npclist.txt";
	/**
	 * Text that is prepended to the generated content.
	 */
	private static final String PRE_ARTICLE = "{{BotUpdate|Datum=DATE}}\nDiese Tabelle enthält alle [[NPC]]-Bilder, die dazugehörigen NPCs und die Autoren der Bilder.\n";
	/**
	 * The name of the script to execute.
	 */
	private static final String SCRIPT = "npclist2wikibilder.pl";
	/**
	 * The file to redirect the produced standard output of the script to.
	 */
	private static final String TARGET = "wikinpcbilder.txt";

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
		command.add(INPUT);

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
		final String data = WikiTaskUtil.readContent(TARGET);
		final String resultingContent = WikiTaskUtil.insertDateTemplateReady(PRE_ARTICLE) + data;

		// Save the resulting content to the article
		final Article article = wiki.getArticle(ARTICLE);
		article.setText(resultingContent);
		wiki.saveArticle(article);
	}

}
