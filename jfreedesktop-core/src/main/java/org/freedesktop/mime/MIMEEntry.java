/**
 * SSHTOOLS Limited licenses this file to you under the Apache
 * License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.freedesktop.mime;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.vfs2.FileObject;
import org.freedesktop.FreedesktopResource;
import org.freedesktop.util.Log;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Represents a single <i>alias</i> as described in the <a
 * http://standards.freedesktop
 * .org/shared-mime-info-spec/shared-mime-info-spec-latest.html">Shared
 * MIME-info Database Specification</a>
 */
public class MIMEEntry extends DefaultHandler implements FreedesktopResource {

	private final static List<String> streamable = Arrays.asList(new String[] { "application", "audio", "image", "message",
		"model", "multipart", "video", "x-content", "x-epoc" });

	private String name;
	private java.util.List<AliasEntry> aliases;
	private Properties comments;
	private List<String> subclasses;
	private List<String> acronyms;
	private List<String> expandedAcronyms;
	private String icon;
	private String genericIcon;
	private String family;
	private String type;

	public MIMEEntry(String family, String type, FileObject file) throws IOException {
		this.family = family;
		this.type = type;
		this.name = family + "/" + type;
		this.icon = icon == null ? this.name.replace('/', '-') : icon;

		acronyms = new ArrayList<String>();
		expandedAcronyms = new ArrayList<String>();
		aliases = new ArrayList<AliasEntry>();
		subclasses = new ArrayList<String>();
		comments = new Properties();
		genericIcon = family + "-x-generic";

		load(file);

		// Some types are implicity subclasses
		if (family.equals("text")) {
			subclasses.add("text/plain");
		} else if (streamable.contains(family)) {
			subclasses.add("application/octet-stream");
		}
	}

	/**
	 * Add a type to the list that are considered <i>streamable</i>, i.e. those
	 * that are implicitly subclasses of application/octet-stream. Streamable
	 * types must be configured before the directory is scanned.
	 * 
	 * @param mimeType type to add
	 */
	public static void addStreamableType(String mimeType) {
		streamable.add(mimeType);
	}

	/**
	 * Remove a type from the list that are considered <i>streamable</i>, i.e.
	 * those that are implicitly subclasses of application/octet-stream.
	 * Streamable types must be configured before the directory is scanned.
	 * 
	 * @param mimeType type to remove
	 */
	public static void removeStreamableType(String mimeType) {
		streamable.remove(mimeType);
	}

	public String getFamilyName() {
		return family;
	}

	public String getTypeName() {
		return type;
	}

	public Collection<String> getAcronyms() {
		// TODO support localisation
		return acronyms;
	}

	public Collection<String> getSubclasses() {
		return subclasses;
	}

	public Collection<AliasEntry> getAliases() {
		return aliases;
	}

	public String getInternalName() {
		return name;
	}

	public String getComment() {
		return getComment((String) null);
	}

	public Collection<String> getExpandedAcronyms() {
		// TODO support localisation
		return expandedAcronyms;
	}

	public String getComment(String language) {
		return comments == null ? null : comments.getProperty(language == null ? "" : language);
	}

	public String getComment(Locale locale) {
		return getComment(locale == null ? (String) null : locale.getLanguage());
	}

	public String getName() {
		return getInternalName();
	}

	public String getName(Locale locale) {
		return getInternalName();
	}

	public String getName(String language) {
		return getInternalName();
	}

	public String getIcon() {
		return icon;
	}

	public String getGenericIcon() {
		return genericIcon;
	}

	void load(FileObject file) throws IOException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(false);
		factory.setNamespaceAware(true);
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			InputStream in = file.getContent().getInputStream();
			try {
				Document document = builder.parse(in);
				build(document);
			} finally {
				in.close();
			}
		} catch (IOException ioe) {
			throw ioe;
		} catch (Exception e) {
			IOException ioe = new IOException("Failed to parse XML.");
			ioe.initCause(e);
			throw ioe;
		}

	}

	void build(Document document) throws IOException {
		/*
		 * Make sure the type attribute of the rool element is the same as the
		 * mime type determined by the filename
		 */
		Element root = document.getDocumentElement();
		String typeValue = root.getAttribute("type");
		if (!name.equals(typeValue)) {
			throw new IOException("Root elements type attribute ('" + typeValue
				+ "') oes not match MIME type determined by filename ('" + name + "').");
		}

		NodeList children = document.getElementsByTagName("icon");
		buildIcon(children);
		children = document.getElementsByTagName("generic-icon");
		buildGenericIcon(children);
		children = document.getElementsByTagName("comment");
		buildComments(children);
		children = document.getElementsByTagName("alias");
		buildAlias(children);
		children = document.getElementsByTagName("sub-class-of");
		buildSubclass(children);
		children = document.getElementsByTagName("acronym");
		buildAcronym(children);
		children = document.getElementsByTagName("expanded-acronym");
		buildExpandedAcronym(children);

	}

	void buildAcronym(NodeList children) {
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			Node langAttr = child.getAttributes().getNamedItem("xml:lang");
			String key = langAttr != null ? langAttr.getTextContent() : "";
			if (!key.equals("")) {
				Log.warn("Localised acronyms are not yet supported.");
			} else {
				acronyms.add(child.getTextContent());
			}
		}
	}

	void buildExpandedAcronym(NodeList children) {
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			Node langAttr = child.getAttributes().getNamedItem("xml:lang");
			String key = langAttr != null ? langAttr.getTextContent() : "";
			if (!key.equals("")) {
				Log.warn("Localised expanded acronyms are not yet supported.");
			} else {
				expandedAcronyms.add(child.getTextContent());
			}
		}
	}

	void buildSubclass(NodeList children) {
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			Node typeAttr = child.getAttributes().getNamedItem("type");
			String type = typeAttr.getTextContent();
			if (!subclasses.contains(type)) {
				subclasses.add(type);
			}
		}
	}

	void buildAlias(NodeList children) {
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			Node typeAttr = child.getAttributes().getNamedItem("type");
			String type = typeAttr.getTextContent();
			aliases.add(new AliasEntry(type, getInternalName()));
		}
	}

	void buildComments(NodeList children) {
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			Node langAttr = child.getAttributes().getNamedItem("xml:lang");
			String key = langAttr != null ? langAttr.getTextContent() : "";
			Log.debug("Putting comment '" + child.getTextContent() + "' for locale " + key);
			comments.put(key, child.getTextContent());
		}
	}

	void buildIcon(NodeList children) {
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			Node langAttr = child.getAttributes().getNamedItem("lang");
			if (langAttr != null) {
				String lang = langAttr.getTextContent();
				Log.warn("Localised icons not yet supported [" + lang + "].");
			} else {
				icon = child.getTextContent();
			}
		}
	}

	void buildGenericIcon(NodeList children) {
		if (children.getLength() > 0) {
			Node child = children.item(0);
			genericIcon = child.getTextContent();
		}
	}

	@Override
	public String toString() {
		return "MIMEEntry [name=" + name + ", aliases=" + aliases
				+ ", comments=" + comments + ", subclasses=" + subclasses
				+ ", acronyms=" + acronyms + ", expandedAcronyms="
				+ expandedAcronyms + ", icon=" + icon + ", genericIcon="
				+ genericIcon + ", family=" + family + ", type=" + type + "]";
	}

}
