/**
 * ================================================================
 * Copyright (c) 2017-2020 Maiereni Software and Consulting Inc
 * ================================================================
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.maiereni.quora.io;

import com.maiereni.quora.io.bo.*;
import com.maiereni.utils.archiving.zip.ZipArchivingUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A utility class that reads the content of an export file from quora
 * @author Petre Maierean
 */
public class FileReader {
    private static final Logger logger = LoggerFactory.getLogger(FileReader.class);
    private ZipArchivingUtil util = new ZipArchivingUtil();

    /**
     * Read all the postings from the zip file obtained from Quora
     * @param fName
     * @return
     * @throws Exception
     */
    public Postings getPostings(final String fName) throws Exception {
        Postings ret = null;
        if (StringUtils.isNotBlank(fName)) {
            ret = getPostings(new File(fName));
        }

        return ret;
    }

    /**
     * Read all the postiong in a Zip file
     * @param f
     * @return
     * @throws Exception
     */
    public Postings getPostings(final File f) throws Exception {
        if (f == null || !f.isFile()) {
            throw new Exception("Cannot find the file in the argument");
        }
        Status status = new Status();
        File toRemove = null;
        try {
            File fIn = f;
            if (util.isArchive(f)) {
                logger.debug("Unarchive the file");
                toRemove = util.decompress(f);
                fIn = new File(toRemove, "index.html");
            }
            if (fIn.isFile()) {
                String html = FileUtils.readFileToString(fIn, "UTF-8");
                Elements elms = getImportantElements(html);
                int max = elms.size();
                for (int i = 0; i < max; i++) {
                    Element e = elms.get(i);
                    status = parseNext(fIn, e, status);
                }
            }
        }
        finally {
            if (toRemove != null) {
                FileUtils.deleteDirectory(toRemove);
                logger.debug("The temporary folder at {} has been removed", toRemove.getPath());
            }
        }
        return status.postings;
    }

    private Elements getImportantElements(final String html) throws Exception {
        Document doc = Jsoup.parse(html);
        Element el = doc.body();
        Element dv = el.getElementsByTag("div").first();
        return dv.children();
    }

    protected Status parseNext(final File f, final Element e, final Status status) throws Exception {
        Status ret = new Status(status);
        String tagName = e.tagName();
        if (status.bStart) {
            if (tagName.equals("div")) {
                Posting p0 = updatePosting(e.children(), status.p, status.isQuestions);
                if (p0 != null) {
                    ret.p = p0;
                }
            } else {
                ret.bStart = false;
                if (status.p != null) {
                    if (StringUtils.isBlank(status.p.getTitle())) {
                        ret.p.setTitle(getTitle(status.p));
                    }
                    ret.postings.getPostings().add(status.p);
                    ret.p = null;
                }
            }
        }
        else {
            if (tagName.equals("h1") && e.ownText().equals("Questions")) {
                ret.postings.setType(PostingsType.QUESTIONS);
                ret.isQuestions = true;
            }
            else if (tagName.equals("h1") && e.ownText().equals("Answers")) {
                ret.postings.setType(PostingsType.ANSWERS);
            }
            else if (e.tagName().equals("h2") && e.ownText().equals("Question")) {
                ret.bStart = true;
            }
            else if (e.tagName().equals("h2") && e.ownText().equals("Answer")) {
                ret.bStart = true;
            }
        }
        status.postings = null;
        return ret;
    }

    private static final List<String> DELIMS = Arrays.asList(new String[] {
            "Question:", "Text:", "Content:", "Creation time:", "Content language:"
    });
    private Posting updatePosting(final Elements el, final Posting p, boolean isQuestions) {
        Posting ret = p;
        int selector = -1;
        for(int i=0; i<el.size(); i++) {
            Element e = el.get(i);
            String tagName = e.tagName();
            if (tagName.equals("strong")) {
                String text = e.ownText();
                selector = DELIMS.indexOf(text);
            }
            else if (tagName.equals("span")) {
                switch(selector) {
                    case 0:
                        ret = updateQuestion(e, ret);
                        break;
                    case 1:
                    case 2:
                        ret = updateContent(e, ret, isQuestions);
                        break;
                    case 3:
                        ret = updateCreationTime(e, ret);
                        break;
                    case 4:
                        ret = updateLanguage(e, ret);
                        break;
                }
            }
        }
        return ret;
    }

    private Posting updateQuestion(final Element el, final Posting p) {
        Posting ret = p;
        if (ret == null) {
            ret = new Posting();
        }
        String question = el.ownText();
        ret.setQuestion (question);
        return ret;
    }

    private Posting updateContent(final Element el, final Posting p, boolean isQuestions) {
        Posting ret = null;
        if (isQuestions) {
            ret = updateQuestion(el, p);
        } else {
            ret = p;
            if (ret == null) {
                ret = new Posting();
            }
            String html = el.html();
            if (StringUtils.isNotBlank(html)) {
                StringBuffer sb = new StringBuffer();
                if (StringUtils.isNotBlank(ret.getHtml())) {
                    sb.append(ret.getHtml()).append("\r\n");
                }
                sb.append(html);
                ret.setHtml(sb.toString());
            }
            Elements els = el.children();
            if (els.size() > 0) {
                Section section = addSection(ret);
                for (int i = 0; i < els.size(); i++) {
                    Element sub = els.get(i);
                    String tagName = sub.tagName();
                    if (tagName.equals("p")) {
                        addText(section, sub);
                    }
                    else if (tagName.equals("div")) {
                        Element sc = sub.child(0).child(0);
                        if (sc.tagName().equals("img")) {
                            Span sp = new Span();
                            Modifier m = new Modifier();
                            m.setType(ModifierType.IMAGE);
                            m.setUrl(sc.attr("src"));
                            sp.setModifier(m);
                            section.getSpans().add(sp);
                        }
                    }
                }
            }
            else {
                Section section = addSection(ret);
                String text = el.ownText();
                Span sp = new Span();
                sp.setText(text);
                section.getSpans().add(sp);
            }
        }
        return ret;
    }

    private void addText(final Section section, final Element element)  {
        int size = element.childNodeSize();
        if (size == 1) {
            String text = element.ownText();
            if (text.startsWith("Chance opportunity came with the establishment")) {
                System.out.println("here");
            }
            Span sp = new Span();
            sp.setText(text);
            section.getSpans().add(sp);
        }
        else {
            Span crtSpan = null;
            for(int j=0; j<size; j++) {
                Node node = element.childNode(j);
                String tName = node.nodeName();
                if (node instanceof TextNode) {
                    String text = ((TextNode)node).text();
                    crtSpan = new Span();
                    crtSpan.setText(text);
                    section.getSpans().add(crtSpan);
                }
                else {
                    if (node.nodeName().equals("span")) {
                        List<Node> nl = node.childNodes();
                        for(Node n: nl) {
                            if (n.nodeName().equals("a")) {
                                Modifier m = new Modifier();
                                m.setType(ModifierType.LINK);
                                m.setUrl(n.attr("href"));
                                if (crtSpan == null) {
                                    crtSpan = new Span();
                                    section.getSpans().add(crtSpan);
                                }
                                crtSpan.setModifier(m);
                            }
                        }
                    }
                }
            }
        }
    }

    private Section addSection(final Posting posting) {
        if (posting.getSections() == null) {
            posting.setSections(new ArrayList<>());
        }
        Section ret = new Section();
        ret.setSpans(new ArrayList<>());
        posting.getSections().add(ret);
        return ret;
    }

    private Posting updateCreationTime(final Element el, final Posting p) {
        Posting ret = p;
        if (ret == null) {
            ret = new Posting();
        }
        String text = el.ownText();
        p.setPublishDate(text);
        return ret;
    }

    private Posting updateLanguage(final Element el, final Posting p) {
        Posting ret = p;
        if (ret == null) {
            ret = new Posting();
        }
        String text = el.ownText();
        p.setLanguage(text);
        return ret;
    }

    private String getTitle(final Posting posting) {
        String ret = null;
        if (posting != null && StringUtils.isNotBlank(posting.getQuestion())) {
            ret = posting.getQuestion().replaceAll("\\p{Punct}", "");
            ret = ret.replaceAll("\\x20", "_");
        }
        return ret;
    }

    protected class Status {
        boolean bStart, isQuestions;
        Posting p;
        Postings postings;
        public Status() {
            this.p = null;
            this.bStart = false;
            this.isQuestions = false;
            this.postings = new Postings();
            this.postings.setPostings(new ArrayList<>());
        }
        public Status(final Status status) {
            this.p = status.p;
            this.postings = status.postings;
            this.bStart = status.bStart;
            this.isQuestions = status.isQuestions;
        }
    }
}
