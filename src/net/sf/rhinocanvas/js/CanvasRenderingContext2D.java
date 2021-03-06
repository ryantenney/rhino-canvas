package net.sf.rhinocanvas.js;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.ImageObserver;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import net.sf.css4j.Value;

public class CanvasRenderingContext2D {

	Graphics2D graphics;
	GeneralPath path = new GeneralPath();
	Stack<ContextState> stack = new Stack<ContextState>();
	Image image;
	Paint fillPaint = Color.BLACK;
	Paint strokePaint = Color.BLACK;
	Object fillStyle = "#000";
	Object strokeStyle = "#000";
	float globalAlpha = 1.0f;
	String globalCompositeOperation = "source-over";
	float lineWidth = 1.0f;
	String lineJoin = "miter";
	String lineCap = "butt";
	float miterLimit = 10.0f; // convert to rad?

	String fontStr = "10px sans-serif";
	Font font = parseFont(fontStr);
	String textAlign = "start";
	String textBaseline = "alphabetic";
	
	// 'left', 'start', 'center', 'right', 'end'
	// 'alphabetic', 'top', 'bottom', 'middle', 'ideographic', 'hanging'

	CanvasRenderingContext2D(Graphics2D graphics) {
		this.graphics = graphics;
	}

	CanvasRenderingContext2D(Image image) {
		this(image.getGraphics());
		this.image = image;
		this.graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		this.graphics.setPaint(Color.BLACK);
	}

	// back-reference to the canvas
	// readonly attribute HTMLCanvasElement canvas;

	// state
	// push state on state stack
	public void save() {
		stack.push(new ContextState(this));
	}

	public void restore() {
		ContextState st = (ContextState) stack.pop();
		st.apply(this);

	} // pop state stack and restore state

	// transformations (default transform is the identity matrix)
	public void scale(double x, double y) {
		graphics.scale(x, y);
	}

	public void rotate(double angle) {
		graphics.rotate(angle);

	}

	public void translate(double x, double y) {
		graphics.translate(x, y);
	}

	public void transform(float m11, float m12, float m21, float m22, float dx, float dy) {
		AffineTransform at = new AffineTransform(m11, m12, m21, m22, dx, dy);
		graphics.transform(at);
	}

	public void setTransform(float m11, float m12, float m21, float m22, float dx, float dy) {
		graphics.setTransform(new AffineTransform(m11, m12, m21, m22, dx, dy));
	}

	/*
	 * // compositing
	 */
	public float getGlobalAlpha() {
		return globalAlpha;

	} // (default 1.0)

	public void setGlobalAlpha(float globalAlpha) {
		this.globalAlpha = globalAlpha;
		setGlobalCompositeOperation(globalCompositeOperation);
	}

	public void setGlobalCompositeOperation(String op) {
		globalCompositeOperation = op;

		int c;
		if ("source-atop".equals(op)) {
			c = AlphaComposite.SRC_ATOP;
		} else if ("source-in".equals(op)) {
			c = AlphaComposite.SRC_IN;
		} else if ("source-out".equals(op)) {
			c = AlphaComposite.SRC_OUT;
		} else if ("destination-atop".equals(op)) {
			c = AlphaComposite.DST_ATOP;
		} else if ("destination-in".equals(op)) {
			c = AlphaComposite.DST_IN;
		} else if ("destination-out".equals(op)) {
			c = AlphaComposite.DST_OUT;
		} else if ("destination-over".equals(op)) {
			c = AlphaComposite.DST_OVER;
		} else if ("xor".equals(op)) {
			c = AlphaComposite.XOR;
		} else if ("over".equals(op)) {
			c = AlphaComposite.CLEAR;
		} else {
			c = AlphaComposite.SRC_OVER;
		}

		graphics.setComposite(AlphaComposite.getInstance(c, globalAlpha));

	}

	public String getFont() {
		return this.fontStr;
	}

	public void setFont(String font) {
		this.fontStr = font;
		this.font = parseFont(font);
	}

	public String getTextAlign() {
		return this.textAlign;
	}

	public void setTextAlign(String value) {
		this.textAlign = value;
	}

	public String getTextBaseline() {
		return this.textBaseline;
	}

	public void setTextBaseline(String value) {
		this.textBaseline = value;
	}

	public Object getFillStyle() {
		return fillStyle;
	}

	public void setFillStyle(Object fillStyle) {
		this.fillStyle = fillStyle;

		if (fillStyle instanceof String) {
			Value fs = new Value((String) fillStyle);
			fillPaint = new Color(fs.getColor(), true);
		} else if (fillStyle instanceof CanvasGradient) {
			fillPaint = ((CanvasGradient) fillStyle).getPaint();
		} else if (fillStyle instanceof CanvasPattern) {
			fillPaint = ((CanvasPattern) fillStyle).paint;
		}

		// System.out.println("color: "+Integer.toHexString(fs.getColor()));
	}

	public Object getStrokeStyle() {
		return strokeStyle;
	}

	public void setStrokeStyle(Object strokeStyle) {
		this.strokeStyle = strokeStyle;

		if (strokeStyle instanceof String) {
			Value fs = new Value((String) strokeStyle);
			strokePaint = new Color(fs.getColor(), true);
		} else if (strokeStyle instanceof CanvasGradient) {
			strokePaint = ((CanvasGradient) strokeStyle).getPaint();
		} else {
			strokePaint = ((CanvasPattern) strokeStyle).paint;
		}
	}

	public CanvasGradient createLinearGradient(float x0, float y0, float x1, float y1) {
		return new CanvasGradient(x0, y0, x1, y1);
	}

	public CanvasGradient createRadialGradient(float x0, float y0, float r0, float x1, float y1, float r1) {
		return new CanvasGradient(x0, y0, r0, x1, y1, r1);
	}

	public CanvasPattern createPattern(Image image, String repetition) {
		return new CanvasPattern(image, repetition);
	}

	// CanvasPattern createPattern(HTMLCanvasElement image, DOMString
	// repetition){}

	// line caps/joins
	// attribute float lineWidth; // (default 1)

	void updateStroke() {

		int cap;

		// butt, round and square. By default this property is set to butt.

		if (lineCap.equals("round")) {
			cap = BasicStroke.CAP_ROUND;
		} else if (lineCap.equals("square")) {
			cap = BasicStroke.CAP_SQUARE;
		} else {
			cap = BasicStroke.CAP_BUTT;
		}

		// round, bevel and miter. By default this property is set to miter.
		int join;
		if (lineJoin.equals("round")) {
			join = BasicStroke.JOIN_ROUND;
		} else if (lineJoin.equals("bevel")) {
			join = BasicStroke.JOIN_BEVEL;
		} else {
			join = BasicStroke.JOIN_MITER;
		}

		graphics.setStroke(new BasicStroke(lineWidth, cap, join, miterLimit));
	}

	public void setLineWidth(float lw) {
		if (lineWidth != lw) {
			lineWidth = lw;
			updateStroke();
		}
	}

	public float getLineWidth() {
		return lineWidth;
	}

	public void setLineCap(String cap) {
		this.lineCap = cap;
		updateStroke();
	}

	public String getLineCap() {
		return lineCap;
	}

	public void setLineJoin(String join) {
		this.lineJoin = join;
		updateStroke();
	}

	public void setMiterLimit(float miterLimit) {
		this.miterLimit = miterLimit;
		updateStroke();
	}

	public float getMiterLimit() {
		return miterLimit;
	}

	// attribute DOMString lineCap; // "butt", "round", "square" (default
	// "butt")
	// attribute DOMString lineJoin; // "round", "bevel", "miter" (default
	// "miter")
	// attribute float miterLimit; // (default 10)

	/*
	 * // shadows attribute float shadowOffsetX; // (default 0) attribute float
	 * shadowOffsetY; // (default 0) attribute float shadowBlur; // (default 0)
	 * attribute DOMString shadowColor; // (default black)
	 */

	public void clearRect(float x, float y, float w, float h) {
		graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, globalAlpha));
		graphics.setPaint(Color.WHITE);
		graphics.fill(new Rectangle2D.Double(x, y, w, h));
		setGlobalAlpha(globalAlpha);
		dirty();
	}

	public void fillRect(double x, double y, double w, double h) {
		graphics.setPaint(fillPaint);
		graphics.fill(new Rectangle2D.Double(x, y, w, h));
		dirty();
	}

	public void strokeRect(float x, float y, float w, float h) {
		graphics.setPaint(strokePaint);
		graphics.draw(new Rectangle2D.Float(x, y, w, h));
		dirty();
	}

	// path API
	public void beginPath() {
		path = new GeneralPath();
		// moveTo(0,0);
	}

	public void closePath() {
		path.closePath();
	}

	public void moveTo(float x, float y) {
		Point2D p = new Point2D.Float(x, y);
		graphics.getTransform().transform(p, p);
		path.moveTo((float) p.getX(), (float) p.getY());
	}

	public void lineTo(float x, float y) {
		Point2D p = new Point2D.Float(x, y);
		graphics.getTransform().transform(p, p);
		path.lineTo((float) p.getX(), (float) p.getY());
	}

	public void quadraticCurveTo(float cpx, float cpy, float x, float y) {

		float[] xy = { cpx, cpy, x, y };
		graphics.getTransform().transform(xy, 0, xy, 0, 2);

		path.quadTo(xy[0], xy[1], xy[2], xy[3]);
	}

	public void bezierCurveTo(float cp1x, float cp1y, float cp2x, float cp2y, float x, float y) {
		float[] xy = { cp1x, cp1y, cp2x, cp2y, x, y };
		graphics.getTransform().transform(xy, 0, xy, 0, 3);
		path.curveTo(xy[0], xy[1], xy[2], xy[3], xy[4], xy[5]);
	}

	public void arcTo(float x1, float y1, float x2, float y2, float radius) {
		// System.out.println("arcTo Not Yet implemented...");
		throw new RuntimeException("arcTo Not Yet implemented...");
	}

	public void rect(float x, float y, float w, float h) {
		path.append(new Rectangle2D.Double(x, y, w, h), true);
	}

	public void arc(double x, double y, double radius, double startAngle, double endAngle, boolean counterclockwise) {

		boolean clockwise = !counterclockwise;
		double twopi = 2 * Math.PI;

		while (startAngle < 0) {
			startAngle += twopi;
		}
		while (startAngle > twopi) {
			startAngle -= twopi;
		}

		while (endAngle < 0) {
			endAngle += twopi;
		}
		while (endAngle > twopi) {
			endAngle -= twopi;
		}

		// System.out.println("drawArc from "+
		// Math.toDegrees(startAngle)+" to "+Math.toDegrees(endAngle)+
		// (counterclockwise ? " counterclockwise":" clockwise"));

		if (clockwise) {
			if (startAngle > endAngle) {
				endAngle += twopi;
			}
			// ang must be negative!

		} else {
			if (startAngle < endAngle) {
				endAngle -= twopi;
			}

			// ang must be positve!
		}
		double ang = startAngle - endAngle;

		// System.out.println("Angle: "+Math.toDegrees(ang)+
		// " should be "+(counterclockwise ? " positive ": " negative "));

		// TODO: is this correct? cf:
		// http://developer.mozilla.org/en/docs/Canvas_tutorial:Drawing_shapes
		if (ang == 0.0) {
			ang = Math.PI * 2;
		}

		startAngle = -startAngle;

		path.append(
				graphics.getTransform().createTransformedShape(
						new Arc2D.Double(x - radius, y - radius, 2 * radius,
								2 * radius, Math.toDegrees(startAngle), Math
										.toDegrees(ang), Arc2D.OPEN)), true);
	}

	public void fill() {
		AffineTransform t = graphics.getTransform();
		graphics.setTransform(new AffineTransform());
		graphics.setPaint(fillPaint);
		graphics.fill(path);
		graphics.setTransform(t);
		dirty();
	}

	public void stroke() {

		graphics.setPaint(strokePaint);

		try {
			graphics.draw(graphics.getTransform().createInverse()
					.createTransformedShape(path));
		} catch (NoninvertibleTransformException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		dirty();
	}

	public void clip() {
		AffineTransform t = graphics.getTransform();
		graphics.setTransform(new AffineTransform());
		graphics.setClip(path);
		graphics.setTransform(t);
	}

	public boolean isPointInPath(float x, float y) {
		Point2D p = new Point2D.Float(x, y);
		graphics.getTransform().transform(p, p);
		return path.contains(p);
	}

	public void drawImage(Image image, float dx, float dy) {
		AffineTransform at = new AffineTransform();
		at.setToTranslation(dx, dy);
		graphics.drawImage(image.image, at, (ImageObserver) null);
	}

	public void drawImage(Image image, float dx, float dy, float dw, float dh) {
		AffineTransform at = new AffineTransform(dw / image.getWidth(), 0, 0,
				dh / image.getHeight(), dx, dy);
		graphics.drawImage(image.image, at, (ImageObserver) null);
	}

	public void drawImage(Image image, float sx, float sy, float sw, float sh, float dx, float dy, float dw, float dh) {
		Graphics2D g = (Graphics2D) graphics.create();
		g.clip(new Rectangle2D.Float(dx, dy, dw, dh));

		float scaleX = dw / sw;
		float scaleY = dh / sh;

		float x0 = dx - sx * scaleX;
		float y0 = dy - sy * scaleY;

		AffineTransform at = new AffineTransform(scaleX, 0, 0, scaleY, x0, y0);
		g.drawImage(image.image, at, (ImageObserver) null);
	}

	public void fillText(String text, float x, float y) {
		graphics.setFont(font);
		graphics.setPaint(fillPaint);
		graphics.fill(textShape(text, x, y));

		dirty();
	}

	public void fillText(String text, float x, float y, float maxWidth) {
		throw new UnsupportedOperationException();
	}

	public void strokeText(String text, float x, float y) {
		graphics.setFont(font);
		graphics.setPaint(strokePaint);
		graphics.draw(textShape(text, x, y));

		dirty();
	}
	
	public void strokeText(String text, float x, float y, float maxWidth) {
		throw new UnsupportedOperationException();
	}

	public CanvasTextMetrics measureText(String textToMeasure) {
		FontMetrics metrics = graphics.getFontMetrics(font);
		Rectangle2D rect = metrics.getStringBounds(textToMeasure, graphics);
		return new CanvasTextMetrics((float) rect.getWidth());
	}

	Shape textShape(String text, float x, float y) {
		TextLayout layout = new TextLayout(text, font, graphics.getFontRenderContext());
		Point2D.Float pos = calcTextPos(text, x, y);
		AffineTransform transform = AffineTransform.getTranslateInstance(pos.x, pos.y);
		return layout.getOutline(transform);
	}

	Point2D.Float calcTextPos(String text, float x, float y) {
		FontMetrics metrics = graphics.getFontMetrics(font);

		if ("center".equals(textAlign)) {
			x = x - metrics.stringWidth(text) / 2;
		} else if ("right".equals(textAlign)) {
			x = x - metrics.stringWidth(text);
		}

		y = y + metrics.getLeading() + metrics.getAscent();

		if ("alphabetic".equals(textBaseline)) {
			y = y - metrics.getLeading() + metrics.getAscent();
		} else if ("top".equals(textBaseline)) {
			y = y - metrics.getLeading();
		} else if ("middle".equals(textBaseline)) {
			y = y - metrics.getLeading() - metrics.getAscent() / 2;
		} else if ("bottom".equals(textBaseline)) {
			y = y - metrics.getHeight();
		}

		return new Point2D.Float(x, y);
	}

	private static final String
			weights = "normal|bold|bolder|lighter|[1-9]00",
			styles = "normal|italic|oblique",
			units = "px|pt|pc|in|cm|mm|%",
			string = "'([^']+)'|\"([^\"]+)\"|[\\w-]+";

	private static final String fontre =
			"^ *" +
			"(?:(" + weights + ") *)?" +
			"(?:(" + styles + ") *)?" +
			"([\\d\\.]+)(" + units + ") *" +
			"((?:" + string + ")( *, *(?:" + string + "))*)";

	private static final Pattern fontPattern;

	static {
		try {
			fontPattern = Pattern.compile(fontre);
		} catch (PatternSyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Parse font `str`.
	 *
	 * @param String str
	 */
	private Font parseFont(String str) {
		Matcher m = fontPattern.matcher(str);

		if (!m.find()) {
			return null;
		}

		String weight = m.group(1);
		String style = m.group(2);
		float size = Float.parseFloat(ifNull(m.group(3), "10"));
		String unit = ifNull(m.group(4), "px");
		String name = ifNull(m.group(5), "sans-serif");

		if ("sans-serif".equals(name)) {
			name = "SansSerif";
		}

		int fontSize = (int) convertUnitToPts(size, unit);
		int fontStyle = 0;

		if ("bold".equals(weight)) {
			fontStyle |= Font.BOLD;
		}

		if ("oblique".equals(style) || "italic".equals(style)) {
			fontStyle |= Font.ITALIC;
		}

		return new Font(name, fontStyle, fontSize);
	}

	private static final <T> T ifNull(T arg1, T arg2) {
		return arg1 != null ? arg1 : arg2;
	}

	/**
	 * Convert to mm then to pt
	 * @param val
	 * @param srcUnit
	 * @return
	 */
	private float convertUnitToPts(float val, String srcUnit) {
		if ("px".equals(srcUnit)) {
			val *= 0.26f;
		} else if ("in".equals(srcUnit)) {
			val *= 25.4f;
		} else if ("cm".equals(srcUnit)) {
			val *= 10.0f;
		} else if ("pt".equals(srcUnit)) {
			val *= 25.4f / 72.0f;
		} else if ("pc".equals(srcUnit)) {
			val *= 25.4f / 6.0f;
		} else if ("mm".equals(srcUnit)) {
			// noop
		} else {
			val *= 4.0f * 25.4f / 72.0f;
		}
		return 72.0f * val / 25.4f;
	}

	void dirty() {
		if (image != null) {
			image.dirty();
		}
	}

	// public void drawImage(HTMLImageElement image, float dx, float dy, float
	// dw, float dh){}
	// public void drawImage(HTMLImageElement image, float sx, float sy, float
	// sw, float sh, float dx, float dy, float dw, float dh){}
	// public void drawImage(HTMLCanvasElement image, float dx, float dy){}
	// public void drawImage(HTMLCanvasElement image, float dx, float dy, float
	// dw, float dh){}
	// public void drawImage(HTMLCanvasElement image, float sx, float sy, float
	// sw, float sh, float dx, float dy, float dw, float dh){}

	// pixel manipulation
	// ImageData jsFunction_getImageData(float sx, float sy, float sw, float
	// sh){}
	// public void putImageData(ImageData image, float dx, float dy){}

	// drawing text is not supported this version of the API
	// (there is no way to predict what metrics the fonts will have,
	// which makes fonts very hard to use for painting)

}