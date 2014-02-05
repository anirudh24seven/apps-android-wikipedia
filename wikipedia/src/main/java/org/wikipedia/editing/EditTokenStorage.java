package org.wikipedia.editing;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import org.wikipedia.Site;
import org.wikipedia.Utils;
import org.wikipedia.WikipediaApp;
import org.wikipedia.editing.FetchEditTokenTask;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URI;
import java.util.*;

public class EditTokenStorage {
    private final HashMap<String, String> tokenJar = new HashMap<String, String>();
    private final SharedPreferences prefs;
    private final Context context;

    public interface TokenRetreivedCallback {
        void onTokenRetreived(String token);
    }

    public EditTokenStorage(Context context) {
        this.context = context;
        this.prefs = PreferenceManager.getDefaultSharedPreferences(context);
        List<String> wikis = makeList(prefs.getString(WikipediaApp.PREFERENCE_EDITTOKEN_WIKIS, ""));
        for (String wiki: wikis) {
            String key = String.format(WikipediaApp.PREFERENCE_EDITTOKEN_FOR_WIKI, wiki);
            tokenJar.put(wiki, prefs.getString(key, null));
        }
    }

    public void get(final Site site, final TokenRetreivedCallback callback) {
        // This might run an AsyncTask, and hence must be called from main thread
        Utils.ensureMainThread();

        String curToken = tokenJar.get(site.getDomain());
        if (curToken != null) {
            callback.onTokenRetreived(curToken);
            return;
        }

        new FetchEditTokenTask(context, site) {
            @Override
            public void onFinish(String result) {
                updatePrefs(site.getDomain(), result);
                callback.onTokenRetreived(result);
            }
        }.execute();
    }

    private void updatePrefs(String wiki, String token) {
        tokenJar.put(wiki, token);
        String wikisList = makeString(tokenJar.keySet());
        String wikiKey = String.format(WikipediaApp.PREFERENCE_EDITTOKEN_FOR_WIKI, wiki);
        prefs.edit()
                .putString(WikipediaApp.PREFERENCE_EDITTOKEN_WIKIS, wikisList)
                .putString(wikiKey, token)
                .commit();
    }

    private String makeString(Iterable<String> list) {
        return TextUtils.join(";", list);
    }

    private List<String> makeList(String str) {
        return Arrays.asList(TextUtils.split(str, ";"));
    }
}
