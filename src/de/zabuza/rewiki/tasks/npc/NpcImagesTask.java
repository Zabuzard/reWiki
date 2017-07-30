package de.zabuza.rewiki.tasks.npc;

import java.util.LinkedList;

import de.zabuza.rewiki.WikiHub;
import de.zabuza.rewiki.exceptions.UnexpectedIOException;
import de.zabuza.rewiki.tasks.IWikiTask;
import de.zabuza.rewiki.tasks.WikiTaskUtil;
import net.sourceforge.jwbf.core.contentRep.Article;

public final class NpcImagesTask implements IWikiTask {
	private static final String ARTICLE = "NPC-Bilder";
	private static final String COMMAND = "perl";
	private static final String INPUT = "npclist.txt";
	private static final String PRE_ARTICLE = "{{BotUpdate|Datum=DATE}}\nDiese Tabelle enthält alle [[NPC]]-Bilder, die dazugehörigen NPCs und die Autoren der Bilder.\n";
	private static final String SCRIPT = "npclist2wikibilder.pl";
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
