/**
 * Copyright © 2006 - 2020 SSHTOOLS Limited (support@sshtools.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.freedesktop.swing;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.Icon;

import com.kitfox.svg.SVGCache;
import com.kitfox.svg.SVGDiagram;
import com.kitfox.svg.SVGUniverse;

public class SVGIcon implements Icon {

	public static final long serialVersionUID = 1;

	public static final int INTERP_NEAREST_NEIGHBOR = 0;
	public static final int INTERP_BILINEAR = 1;
	public static final int INTERP_BICUBIC = 2;

	private SVGUniverse svgUniverse = SVGCache.getSVGUniverse();
	private boolean antiAlias = true;
	private int interpolation = INTERP_NEAREST_NEIGHBOR;
	private boolean clipToViewbox;
	private SVGDiagram diagram;
	private AffineTransform scaleXform = new AffineTransform();
	private Dimension preferredSize;

	public SVGIcon(String uri, InputStream in) throws IOException {
		this(uri, in, 0, 0);
	}

	public SVGIcon(String uri, InputStream in, int width, int height) throws IOException {
		super();
		if (width > 0 && height > 0) {
			setPreferredSize(new Dimension(width, height));
		}
		setSvgStream(uri, in);
	}

	public int getIconHeight() {
		if (preferredSize != null) {
			return preferredSize.height;
		}
		if (diagram == null)
			return 0;
		return (int) diagram.getHeight();
	}

	public int getIconWidth() {
		if (preferredSize != null) {
			return preferredSize.width;
		}
		if (diagram == null)
			return 0;
		return (int) diagram.getWidth();
	}

	public void paintIcon(Component comp, Graphics gg, int x, int y) {
		Graphics2D g = (Graphics2D) gg;

		Object oldAliasHint = g
				.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				antiAlias ? RenderingHints.VALUE_ANTIALIAS_ON
						: RenderingHints.VALUE_ANTIALIAS_OFF);

		Object oldInterpolationHint = g
				.getRenderingHint(RenderingHints.KEY_INTERPOLATION);
		switch (interpolation) {
		case INTERP_NEAREST_NEIGHBOR:
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
					RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
			break;
		case INTERP_BILINEAR:
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
					RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			break;
		case INTERP_BICUBIC:
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
					RenderingHints.VALUE_INTERPOLATION_BICUBIC);
			break;
		}

		if (diagram == null)
			return;

		g.translate(x, y);
		diagram.setIgnoringClipHeuristic(!clipToViewbox);
		if (clipToViewbox) {
			g.setClip(new Rectangle2D.Float(0, 0, diagram.getWidth(), diagram
					.getHeight()));
		}

		final int width = getIconWidth();
		final int height = getIconHeight();

		if (width == 0 || height == 0) {
			return;
		}

		final Rectangle2D.Double rect = new Rectangle2D.Double();
		diagram.getViewRect(rect);

		scaleXform.setToScale(width / rect.width, height / rect.height);

		AffineTransform oldXform = g.getTransform();
		g.transform(scaleXform);

		try {
			diagram.render(g);
		} catch (Exception e) {
			if("true".equals(System.getProperty("jfreedesktop.showRenderError"))) {
			e.printStackTrace();
			}
//			throw new RuntimeException(e);
		}

		g.setTransform(oldXform);

		g.translate(-x, -y);

		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, oldAliasHint);
		if (oldInterpolationHint != null)
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
					oldInterpolationHint);
	}

	public Dimension getPreferredSize() {
		if (preferredSize == null) {
			if (diagram != null) {
				setPreferredSize(new Dimension((int) diagram.getWidth(),
						(int) diagram.getHeight()));
			}
		}

		return new Dimension(preferredSize);
	}

	public void setPreferredSize(Dimension preferredSize) {
		this.preferredSize = preferredSize;
		if (diagram != null) {
			diagram.setDeviceViewport(new Rectangle(0, 0, preferredSize.width,
					preferredSize.height));
		}
	}

	public void setSvgStream(String uri, InputStream input) throws IOException {
		// SVGUniverse is sharing a validator instance which can cause problems
		synchronized (svgUniverse) {
			diagram = svgUniverse
					.getDiagram(svgUniverse.loadSVG(input, uri));	
		}
		if (diagram != null) {
			Dimension size = getPreferredSize();
			if (size == null) {
				size = new Dimension((int) diagram.getRoot().getDeviceWidth(),
						(int) diagram.getRoot().getDeviceHeight());
			}
			diagram.setDeviceViewport(new Rectangle(0, 0, size.width,
					size.height));
		}
	}
}