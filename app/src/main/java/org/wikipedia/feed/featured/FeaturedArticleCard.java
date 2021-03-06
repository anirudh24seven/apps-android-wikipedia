package org.wikipedia.feed.featured;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.wikipedia.R;
import org.wikipedia.WikipediaApp;
import org.wikipedia.dataclient.WikiSite;
import org.wikipedia.feed.model.Card;
import org.wikipedia.feed.model.CardType;
import org.wikipedia.feed.model.UtcDate;
import org.wikipedia.history.HistoryEntry;
import org.wikipedia.page.PageTitle;
import org.wikipedia.server.restbase.RbPageSummary;
import org.wikipedia.util.DateUtil;
import org.wikipedia.util.StringUtil;

public class FeaturedArticleCard extends Card {
    @NonNull private UtcDate date;
    @NonNull private WikiSite wiki;
    @NonNull private RbPageSummary page;

    public FeaturedArticleCard(@NonNull RbPageSummary page, @NonNull UtcDate date, @NonNull WikiSite wiki) {
        this.page = page;
        this.wiki = wiki;
        this.date = date;
    }

    @Override
    @NonNull
    public String title() {
        return WikipediaApp.getInstance().getString(R.string.view_featured_article_card_title);
    }

    @Override
    @NonNull
    public String subtitle() {
        return DateUtil.getFeedCardDateString(date.baseCalendar());
    }

    @NonNull
    public WikiSite wikiSite() {
        return wiki;
    }

    @NonNull
    public String articleTitle() {
        return page.getTitle();
    }

    @Nullable
    public String articleSubtitle() {
        return page.getDescription() != null
                ? StringUtil.capitalizeFirstChar(page.getDescription()) : null;
    }

    @Override
    @Nullable
    public Uri image() {
        String thumbUrl = page.getThumbnailUrl();
        return thumbUrl != null ? Uri.parse(thumbUrl) : null;
    }

    @Nullable
    @Override
    public String extract() {
        return page.getExtract();
    }

    @NonNull @Override public CardType type() {
        return CardType.FEATURED_ARTICLE;
    }

    @NonNull
    public HistoryEntry historyEntry(int source) {
        PageTitle title = new PageTitle(articleTitle(), wikiSite());
        if (image() != null) {
            title.setThumbUrl(image().toString());
        }
        title.setDescription(articleSubtitle());
        return new HistoryEntry(title, source);
    }

    @Override
    protected int dismissHashCode() {
        return page.getTitle().hashCode();
    }
}
