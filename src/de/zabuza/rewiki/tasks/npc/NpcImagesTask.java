package de.zabuza.rewiki.tasks.npc;

import de.zabuza.rewiki.WikiHub;
import de.zabuza.rewiki.exceptions.UnexpectedIOException;
import de.zabuza.rewiki.tasks.IWikiTask;
import de.zabuza.rewiki.tasks.WikiTaskUtil;
import net.sourceforge.jwbf.core.contentRep.Article;

public final class NpcImagesTask implements IWikiTask {
	private static final String ARTICLE = "NPC-Bilder";
	private static final String COMMAND = "perl SCRIPT npclist.txt";
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
		final String data = WikiTaskUtil.readContent(TARGET);
		final String resultingContent = WikiTaskUtil.insertDateTemplateReady(PRE_ARTICLE) + data;

		// Save the resulting content to the article
		final Article article = wiki.getArticle(ARTICLE);
		article.setText(resultingContent);
		wiki.saveArticle(article);
	}

}
