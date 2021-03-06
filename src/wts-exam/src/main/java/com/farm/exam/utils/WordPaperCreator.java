package com.farm.exam.utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import com.farm.exam.domain.Material;
import com.farm.exam.domain.Paper;
import com.farm.exam.domain.ex.AnswerUnit;
import com.farm.exam.domain.ex.ChapterUnit;
import com.farm.exam.domain.ex.PaperUnit;
import com.farm.exam.domain.ex.SubjectUnit;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.model.XWPFHeaderFooterPolicy;
import org.apache.poi.xwpf.usermodel.Borders;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.TextAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFFooter;
import org.apache.poi.xwpf.usermodel.XWPFHeader;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFPicture;
import org.apache.poi.xwpf.usermodel.XWPFPictureData;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.xmlbeans.impl.xb.xmlschema.SpaceAttribute;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFldChar;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTabStop;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTText;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STFldCharType;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STHdrFtr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTabJc;

import com.farm.doc.server.FarmFileManagerInter;
import com.farm.doc.server.commons.FarmDocFiles;
import com.farm.parameter.FarmParameterService;
import com.farm.util.spring.BeanFactory;
import com.farm.util.web.FarmHtmlUtils;

public class WordPaperCreator {

	// ????????????
	private static int STANDARD_FONT_SIZE = 12;
	// ????????????
	private static int STANDARD_INDENTATION = 300;
	// ??????
	private static String STANDARD_FONT_FAMILY = "????????????";
	// ?????????????????????
	private static int SMALL_FONT_SIZE = 10;
	// ????????????
	private static int TITLE_FONT_SIZE = 18;
	// ?????????
	private static int H1_FONT_SIZE = 16;
	private static int H1_INDENTATION = 300;
	// ?????????
	private static int H2_FONT_SIZE = 14;
	private static int H2_INDENTATION = 600;
	// ?????????
	private static int H3_FONT_SIZE = 12;
	private static int H3_INDENTATION = 900;
	// ????????????
	private static List<String[]> answers = new ArrayList<>();

	/**
	 * ????????????word
	 * 
	 * @param document
	 */
	synchronized public static void initWordPaper(XWPFDocument document,
			PaperUnit paper) {
		answers = new ArrayList<>();
		// ????????????
		createHeader(
				document,
				FarmParameterService.getInstance().getParameter(
						"config.sys.title"));
		// ????????????
		createFooter(document, "wwww.wcpdoc.com", "wts");
		// ?????????????????????
		initPaperTitle(document, paper);
		// ??????????????????
		initpaperInfo(document, paper);
		// ??????????????????paper.info.papernote
		// ???????????????
		if (StringUtils.isNotBlank(paper.getInfo().getPapernote())) {
			initpaperNote(document, paper.getInfo().getPapernote());
		}
		// ????????????h1
		for (ChapterUnit chapter : paper.getChapters()) {
			initChapterMain(document, chapter, H1_FONT_SIZE, H1_INDENTATION);
			// ????????????h2
			for (ChapterUnit chapter2 : chapter.getChapters()) {
				initChapterMain(document, chapter2, H2_FONT_SIZE,
						H2_INDENTATION);
				// ????????????h3
				for (ChapterUnit chapter3 : chapter2.getChapters()) {
					initChapterMain(document, chapter3, H3_FONT_SIZE,
							H3_INDENTATION);
				}
			}
		}
		// ????????????
		initpaperAnswer(document, answers);
	}

	/**
	 * ??????????????????
	 * 
	 * @param document
	 * @param paper
	 */
	public static void initpaperAnswer(XWPFDocument document,
			List<String[]> answers) {
		// answers[0] 1:????????????2????????????3??????
		// ????????????
		XWPFParagraph paragraphTitle = document.createParagraph();
		paragraphTitle.setAlignment(ParagraphAlignment.CENTER);
		{
			XWPFRun run0 = paragraphTitle.createRun();
			run0.addBreak();
			run0.addBreak();
			run0.addBreak();
			XWPFRun run = paragraphTitle.createRun();
			run.setText("--------------????????????--------------");
			run.setFontSize(TITLE_FONT_SIZE);
			run.setFontFamily(STANDARD_FONT_FAMILY);
			run.setBold(true);
			run.addBreak();
		}
		XWPFParagraph paragraph = document.createParagraph();
		paragraph.setAlignment(ParagraphAlignment.LEFT);
		for (String[] answer : answers) {
			// 1:????????????2????????????3??????
			if (answer[0].equals("1")) {
				XWPFRun run = paragraph.createRun();
				run.addBreak();
				run.setText(answer[1]);
				run.setFontSize(SMALL_FONT_SIZE);
				run.setFontFamily(STANDARD_FONT_FAMILY);
				run.addBreak();
			}
			if (answer[0].equals("2")) {
				XWPFRun run = paragraph.createRun();
				run.setText("  " + answer[1]);
				run.setFontSize(SMALL_FONT_SIZE);
				run.setFontFamily(STANDARD_FONT_FAMILY);
			}
			if (answer[0].equals("3")) {
				XWPFRun run = paragraph.createRun();
				run.setText(answer[1]);
				run.setFontSize(SMALL_FONT_SIZE);
				run.setFontFamily(STANDARD_FONT_FAMILY);
			}
		}
	}

	// ???????????????????????????
	private static void initChapterMain(XWPFDocument document,
			ChapterUnit chapter, int chapterTitleFontSize,
			int chapterIndentation) {
		// ????????????
		initChapterTitle(document, chapter.getChapter().getName(), " ???"
				+ chapter.getSubjectNum() + "????????????" + chapter.getAllpoint()
				+ "???", chapterTitleFontSize, chapterIndentation);
		// ????????????
		// ???????????????
		if (StringUtils.isNotBlank(chapter.getChapter().getTextnote())) {
			initChapterNote(document, chapter.getChapter().getTextnote());
		}
		// ?????????????????????
		initChapterMaterials(document, chapter.getMaterials());
		// ????????????
		int num = 0;
		for (SubjectUnit subject : chapter.getSubjects()) {
			// ????????????
			initSubject(document, subject, ++num);
		}
	}

	/**
	 * ????????????
	 * 
	 * @param document
	 * @param subject
	 */
	private static void initSubject(XWPFDocument document, SubjectUnit subject,
			int num) {
		answers.add(new String[] { "2", num + ". " });
		// ????????????
		XWPFParagraph paragraph0 = document.createParagraph();
		XWPFRun run0 = paragraph0.createRun();
		run0.setText("");
		XWPFParagraph paragraph = document.createParagraph();
		paragraph.setIndentationFirstLine(STANDARD_INDENTATION);
		// ??????
		XWPFRun run11 = paragraph.createRun();
		run11.setText(num + ". ");
		run11.setFontSize(STANDARD_FONT_SIZE);
		run11.setFontFamily(STANDARD_FONT_FAMILY);
		// ??????
		XWPFRun run12 = paragraph.createRun();
		run12.setText(formatSubjectTip(subject.getVersion().getTipstr()));
		run12.setFontSize(STANDARD_FONT_SIZE);
		run12.setFontFamily(STANDARD_FONT_FAMILY);
		// ?????????
		XWPFRun run13 = paragraph.createRun();
		run13.setText(subject.getPoint() + "???");
		run13.setFontSize(SMALL_FONT_SIZE);
		run13.setColor("666666");
		run13.setFontFamily(STANDARD_FONT_FAMILY);
		// ???????????????
		if (StringUtils.isNotBlank(subject.getVersion().getTipnote())) {
			XWPFParagraph paragraph2 = document.createParagraph();
			initHtmlContent(paragraph2, subject.getVersion().getTipnote(),
					STANDARD_FONT_SIZE);
		}
		int answernum = 0;
		// ????????????
		for (AnswerUnit answer : subject.getAnswers()) {
			int showAnswerNum = (++answernum);
			if (subject.getVersion().getTiptype().equals("2")
					|| subject.getVersion().getTiptype().equals("3")) {
				XWPFParagraph paragraphAnswer = document.createParagraph();
				paragraphAnswer.setIndentationFirstLine(STANDARD_INDENTATION);
				XWPFRun runAnswer1 = paragraphAnswer.createRun();
				runAnswer1.setText(getWordCode(showAnswerNum) + ". ");
				runAnswer1.setFontSize(STANDARD_FONT_SIZE);
				runAnswer1.setFontFamily(STANDARD_FONT_FAMILY);
				XWPFRun runAnswer2 = paragraphAnswer.createRun();
				runAnswer2.setText(answer.getAnswer().getAnswer());
				runAnswer2.setFontSize(STANDARD_FONT_SIZE);
				runAnswer2.setFontFamily(STANDARD_FONT_FAMILY);
				// ???????????????
				if (StringUtils.isNotBlank(answer.getAnswer().getAnswernote())) {
					XWPFParagraph paragraph2 = document.createParagraph();
					paragraph2.setIndentationFirstLine(STANDARD_INDENTATION);
					initHtmlContent(paragraph2, answer.getAnswer()
							.getAnswernote(), STANDARD_FONT_SIZE);
				}
			}
			{
				// ????????????
				if (answer.getAnswer().getRightanswer().equals("1")
						&& (subject.getVersion().getTiptype().equals("4")
								|| subject.getVersion().getTiptype()
										.equals("2") || subject.getVersion()
								.getTiptype().equals("3"))) {
					// ??????2.?????????3.?????????4??????
					if (subject.getVersion().getTiptype().equals("4")) {
						answers.add(new String[] { "3",
								(showAnswerNum == 1) ? "???" : "???" });
					}
					if (subject.getVersion().getTiptype().equals("2")
							|| subject.getVersion().getTiptype().equals("3")) {
						answers.add(new String[] { "3",
								getWordCode(showAnswerNum) });
					}
				}
				if (subject.getVersion().getTiptype().equals("1")) {
					// 1.?????????
					answers.add(new String[] { "3",
							answer.getAnswer().getAnswer() });
				}
				if (subject.getVersion().getTiptype().equals("5")
						|| subject.getVersion().getTiptype().equals("6")) {
					// 5??????,6??????
					answers.add(new String[] { "3",
							answer.getAnswer().getAnswer() });
				}
			}
		}
	}

	/**
	 * ?????????????????????
	 * 
	 * @param paragraph
	 * @param html
	 * @param fontSize
	 */
	private static void initHtmlContent(XWPFParagraph paragraph, String html,
			int fontSize) {
		paragraph.setAlignment(ParagraphAlignment.LEFT);
		Document document = Jsoup.parse(html);
		Elements imgs = document.getElementsByTag("img");
		imgs.after("666WORDIMG666");
		imgs.before("666WORDIMG666");
		html = document.html();
		String[] htmlparts = html.split("666WORDIMG666");
		for (String part : htmlparts) {
			if (StringUtils.isNotBlank(part)) {
				XWPFRun run2 = paragraph.createRun();
				run2.setFontSize(fontSize);
				run2.setFontFamily(STANDARD_FONT_FAMILY);
				if (part.trim().startsWith("<img")) {
					// ??????
					// ???????????????????????????
					// ??????????????????word???
					FileInputStream is = null;
					try {
						File img = getImgFile(part);
						if (img != null) {
							is = new FileInputStream(img);
							run2.addPicture(is, getImgFileType(img),
									img.getName(),
									Units.toEMU(getImgWidth(part, img)),
									Units.toEMU(getImgHeight(part, img))); // 200x200
						}
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						try {
							if (is != null) {
								is.close();
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				} else {
					// ??????
					// ???????????????????????????????????????
					if (isHeadBreak(part)) {
						run2.addBreak();
						run2.addBreak();
					}
					run2.setText(StringEscapeUtils.unescapeHtml(formatSubjectTip(FarmHtmlUtils
							.HtmlRemoveTag(part.replace("&nbsp;", " ")))));
					if (isFootBreak(part)) {
						run2.addBreak();
						run2.addBreak();
					}
				}
			}
		}
	}

	/**
	 * ???????????????????????????
	 * 
	 * @param htmlImg
	 * @return
	 */
	private static File getImgFile(String htmlImg) {
		Document document = Jsoup.parse(htmlImg);
		Elements imgs = document.getElementsByTag("img");
		String url = imgs.get(0).attr("src");
		String fileid = FarmDocFiles.getFileIdFromImgUrl(url);
		if (StringUtils.isNotBlank(fileid)) {
			FarmFileManagerInter fileServer = (FarmFileManagerInter) BeanFactory
					.getBean("farmFileManagerImpl");
			File file = fileServer.getFile(fileServer.getFile(fileid));
			return file;
		} else {
			return null;
		}
	}

	// ??????????????????
	private static int getImgFileType(File img) {
		if (img.getName().toUpperCase().indexOf("JPG") > 0
				|| img.getName().toUpperCase().indexOf("JPEG") > 0) {
			return XWPFDocument.PICTURE_TYPE_JPEG;
		}
		if (img.getName().toUpperCase().indexOf("GIF") > 0) {
			return XWPFDocument.PICTURE_TYPE_GIF;
		}
		if (img.getName().toUpperCase().indexOf("BMP") > 0) {
			return XWPFDocument.PICTURE_TYPE_BMP;
		}
		return XWPFDocument.PICTURE_TYPE_JPEG;
	}

	/**
	 * ??????????????????
	 * 
	 * @param htmlImg
	 * @param img
	 * @return
	 */
	private static int getImgWidth(String htmlImg, File img) {
		int widthInt = 0;
		try {
			BufferedImage sourceImg = ImageIO.read(new FileInputStream(img));
			Document document = Jsoup.parse(htmlImg);
			Elements imgs = document.getElementsByTag("img");
			String width = imgs.attr("width");
			String height = imgs.attr("height");
			int fileWidth = sourceImg.getWidth();
			int heightWidth = sourceImg.getHeight();
			widthInt = fileWidth;
			if (StringUtils.isNotBlank(width)) {
				// ?????????
				widthInt = Integer.valueOf(width);
			}
			if (StringUtils.isBlank(width) && StringUtils.isNotBlank(height)) {
				// ???????????????????????????
				widthInt = Integer.valueOf(height) * (fileWidth / heightWidth);
			}
			if (widthInt > 430) {
				widthInt = 430;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return widthInt;
	}

	/**
	 * ??????????????????
	 * 
	 * @param htmlImg
	 * @param img
	 * @return
	 */
	private static int getImgHeight(String htmlImg, File img) {
		int heightInt = 0;
		try {
			BufferedImage sourceImg = ImageIO.read(new FileInputStream(img));
			Document document = Jsoup.parse(htmlImg);
			Elements imgs = document.getElementsByTag("img");
			String width = imgs.attr("width");
			String height = imgs.attr("height");
			int fileWidth = sourceImg.getWidth();
			int heightWidth = sourceImg.getHeight();
			heightInt = heightWidth;
			if (StringUtils.isNotBlank(height)) {
				// ?????????
				heightInt = Integer.valueOf(height);
			}
			if (StringUtils.isBlank(height) && StringUtils.isNotBlank(width)) {
				// ???????????????????????????
				heightInt = Integer.valueOf(width) * (heightWidth / fileWidth);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return heightInt;
	}

	/**
	 * ????????????????????????
	 * 
	 * @param html
	 * @return
	 */
	private static boolean isHeadBreak(String html) {
		if (html.trim().startsWith("<div") || html.trim().startsWith("<p")) {
			return true;
		}
		return false;
	}

	/**
	 * ????????????????????????
	 * 
	 * @param html
	 * @return
	 */
	private static boolean isFootBreak(String html) {
		if (html.trim().endsWith("</div>") || html.trim().endsWith("</p>")
				|| html.trim().endsWith("<p>") || html.trim().endsWith("<br/>")
				|| html.trim().endsWith("<p align=\"center\">")
				|| html.trim().endsWith("<p align=\"right\">")
				|| html.trim().endsWith("<p align=\"left\">")
				|| html.trim().endsWith("<br>")
				|| html.trim().endsWith("<dir>")) {
			return true;
		}
		return false;
	}

	/**
	 * ?????????????????????
	 * 
	 * @param text
	 * @return
	 */
	private static String formatSubjectTip(String text) {
		text = text.replace("???", "(");
		text = text.replace("???", " )");
		text = text.replaceAll("\\([_, ]{0,5}?\\)", "(_____)");
		text = text.replaceAll("[_]{1,5}", "_____");
		return text;
	}

	/**
	 * ??????????????????
	 * 
	 * @param code
	 * @return
	 */
	private static String getWordCode(int code) {
		Map<Integer, String> codeMap = new HashMap<>();
		codeMap.put(1, "A");
		codeMap.put(2, "B");
		codeMap.put(3, "C");
		codeMap.put(4, "D");
		codeMap.put(5, "E");
		codeMap.put(6, "F");
		codeMap.put(7, "G");
		codeMap.put(8, "H");
		codeMap.put(9, "I");
		codeMap.put(10, "J");
		codeMap.put(11, "K");
		codeMap.put(12, "L");
		codeMap.put(13, "M");
		codeMap.put(14, "N");
		codeMap.put(15, "O");
		return codeMap.get(code);
	}

	/**
	 * ????????????
	 * 
	 * @param doc
	 * @param orgFullName
	 */
	public static void createHeader(XWPFDocument doc, String orgFullName) {
		try {
			/*
			 * ????????????????????????????????????logo???????????????????????????????????????????????????
			 */
			CTSectPr sectPr = doc.getDocument().getBody().addNewSectPr();
			XWPFHeaderFooterPolicy headerFooterPolicy = new XWPFHeaderFooterPolicy(
					doc, sectPr);
			XWPFHeader header;

			header = headerFooterPolicy
					.createHeader(XWPFHeaderFooterPolicy.DEFAULT);

			XWPFParagraph paragraph = header.getParagraphArray(0);
			paragraph.setAlignment(ParagraphAlignment.LEFT);
			paragraph.setBorderBottom(Borders.THICK);

			CTTabStop tabStop = paragraph.getCTP().getPPr().addNewTabs()
					.addNewTab();
			tabStop.setVal(STTabJc.RIGHT);
			int twipsPerInch = 1440;
			tabStop.setPos(BigInteger.valueOf(6 * twipsPerInch));
			XWPFRun run = paragraph.createRun();
			run.setFontFamily(STANDARD_FONT_FAMILY);
			/*
			 * ????????????logo???ftp????????????????????????????????????????????? ????????????logo????????????logo?????????
			 */
			{
				String webPath = FarmParameterService.getInstance()
						.getParameter("farm.constant.webroot.path");
				String filePath = "/view/web-simple/atext/png/icon".replaceAll(
						"/", File.separator.equals("/") ? "/" : "\\\\");
				File file = new File(webPath + filePath);
				InputStream is = null;
				try {
					is = new FileInputStream(file);
					XWPFPicture picture = run.addPicture(is,
							XWPFDocument.PICTURE_TYPE_JPEG, webPath + filePath,
							Units.toEMU(24), Units.toEMU(24));
					String blipID = "";
					for (XWPFPictureData picturedata : header
							.getAllPackagePictures()) { // ?????????????????????????????????logo???????????????
						blipID = header.getRelationId(picturedata);
					}
					picture.getCTPicture().getBlipFill().getBlip()
							.setEmbed(blipID);
					run.addTab();
				} finally {
					is.close();
				}
			}
			/*
			 * ????????????????????????????????? ?????????????????????
			 */
			if (StringUtils.isNotBlank(orgFullName)) {
				run = paragraph.createRun();
				run.setText(orgFullName);
				run.setFontFamily(STANDARD_FONT_FAMILY);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			e.printStackTrace();
		}
	}

	/**
	 * ????????????
	 * 
	 * @param document
	 * @param telephone
	 * @param orgAddress
	 */
	public static void createFooter(XWPFDocument document, String telephone,
			String orgAddress) {
		try {
			/*
			 * ?????????????????? ???????????????????????????????????? ?????????????????????????????????????????????????????????????????????
			 */
			CTSectPr sectPr = document.getDocument().getBody().addNewSectPr();
			XWPFHeaderFooterPolicy headerFooterPolicy = new XWPFHeaderFooterPolicy(
					document, sectPr);
			XWPFFooter footer = headerFooterPolicy
					.createFooter(STHdrFtr.DEFAULT);
			XWPFParagraph paragraph = footer.getParagraphArray(0);
			paragraph.setAlignment(ParagraphAlignment.LEFT);
			paragraph.setVerticalAlignment(TextAlignment.CENTER);
			paragraph.setBorderTop(Borders.THICK);
			CTTabStop tabStop = paragraph.getCTP().getPPr().addNewTabs()
					.addNewTab();
			tabStop.setVal(STTabJc.RIGHT);
			int twipsPerInch = 1440;
			tabStop.setPos(BigInteger.valueOf(6 * twipsPerInch));

			/*
			 * ????????????????????? ?????????????????????????????????+????????????
			 */
			XWPFRun run = paragraph.createRun();
			run.setText((StringUtils.isNotBlank(orgAddress) ? orgAddress : "")
					+ (StringUtils.isNotBlank(telephone) ? "  " + telephone
							: ""));
			run.setFontFamily(STANDARD_FONT_FAMILY);
			run.addTab();

			/*
			 * ???????????? ???????????????
			 */
			run = paragraph.createRun();
			run.setText("???");
			run.setFontFamily(STANDARD_FONT_FAMILY);

			run = paragraph.createRun();
			CTFldChar fldChar = run.getCTR().addNewFldChar();
			fldChar.setFldCharType(STFldCharType.Enum.forString("begin"));

			run = paragraph.createRun();
			CTText ctText = run.getCTR().addNewInstrText();
			ctText.setStringValue("PAGE  \\* MERGEFORMAT");
			ctText.setSpace(SpaceAttribute.Space.Enum.forString("preserve"));
			run.setFontFamily(STANDARD_FONT_FAMILY);

			fldChar = run.getCTR().addNewFldChar();
			fldChar.setFldCharType(STFldCharType.Enum.forString("end"));

			run = paragraph.createRun();
			run.setText("??? ??????");
			run.setFontFamily(STANDARD_FONT_FAMILY);

			run = paragraph.createRun();
			fldChar = run.getCTR().addNewFldChar();
			fldChar.setFldCharType(STFldCharType.Enum.forString("begin"));

			run = paragraph.createRun();
			ctText = run.getCTR().addNewInstrText();
			ctText.setStringValue("NUMPAGES  \\* MERGEFORMAT ");
			ctText.setSpace(SpaceAttribute.Space.Enum.forString("preserve"));
			run.setFontFamily(STANDARD_FONT_FAMILY);

			fldChar = run.getCTR().addNewFldChar();
			fldChar.setFldCharType(STFldCharType.Enum.forString("end"));

			run = paragraph.createRun();
			run.setText("???");
			run.setFontFamily(STANDARD_FONT_FAMILY);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ??????????????????
	 * 
	 * @param document
	 * @param chapter
	 */
	private static void initChapterMaterials(XWPFDocument document,
			List<Material> materials) {
		for (Material material : materials) {
			XWPFParagraph paragraph = document.createParagraph();
			paragraph.setAlignment(ParagraphAlignment.CENTER);
			XWPFRun run = paragraph.createRun();
			run.setText("???" + material.getTitle() + "???");
			run.setFontSize(STANDARD_FONT_SIZE);
			run.setFontFamily(STANDARD_FONT_FAMILY);
			run.setBold(true);
			// ???????????????
			{
				XWPFParagraph paragraph2 = document.createParagraph();
				paragraph2.setIndentationFirstLine(STANDARD_INDENTATION);
				initHtmlContent(paragraph2, material.getText(), SMALL_FONT_SIZE);
			}
		}
	}

	/**
	 * ????????????
	 * 
	 * @param document
	 * @param chapter
	 */
	private static void initChapterNote(XWPFDocument document, String html) {
		XWPFParagraph paragraph = document.createParagraph();
		initHtmlContent(paragraph, html, SMALL_FONT_SIZE);
	}

	/**
	 * ????????????
	 * 
	 * @param document
	 * @param chapter
	 */
	private static void initChapterTitle(XWPFDocument document, String title,
			String info, int chapterTitleFontSize, int chapterIndentation) {
		XWPFParagraph paragraph0 = document.createParagraph();
		XWPFRun run0 = paragraph0.createRun();
		run0.addBreak();
		// ????????????
		XWPFParagraph paragraph = document.createParagraph();
		paragraph.setIndentationFirstLine(chapterIndentation);
		XWPFRun run1 = paragraph.createRun();
		answers.add(new String[] { "1", title });
		run1.setText(title);
		run1.setFontSize(chapterTitleFontSize);
		run1.setFontFamily(STANDARD_FONT_FAMILY);
		run1.setBold(true);
		XWPFRun run = paragraph.createRun();
		run.setText(info);
		run.setFontSize(SMALL_FONT_SIZE);
		run.setColor("666666");
		run.setFontFamily(STANDARD_FONT_FAMILY);
	}

	/**
	 * ????????????
	 * 
	 * @param document
	 * @param paper
	 */
	public static void initPaperTitle(XWPFDocument document, PaperUnit paper) {
		// ????????????
		XWPFParagraph paragraph = document.createParagraph();
		paragraph.setAlignment(ParagraphAlignment.CENTER);
		XWPFRun run = paragraph.createRun();
		run.setText(paper.getInfo().getName());
		run.setFontSize(TITLE_FONT_SIZE);
		run.setFontFamily(STANDARD_FONT_FAMILY);
		run.setBold(true);
		run.addBreak();
	}

	/**
	 * ????????????????????????????????????
	 * 
	 * @param document
	 * @param paper
	 */
	public static void initpaperInfo(XWPFDocument document, PaperUnit paper) {
		// ???${paper.rootChapterNum}????????????${paper.subjectNum}??????????????????${paper.allPoint}????????????????????????${paper.info.advicetime}??????
		XWPFParagraph paragraph = document.createParagraph();
		paragraph.setAlignment(ParagraphAlignment.CENTER);
		XWPFRun run = paragraph.createRun();
		run.setText("???" + paper.getRootChapterNum() + "????????????"
				+ paper.getSubjectNum() + "??????????????????" + paper.getAllPoint()
				+ "????????????????????????" + paper.getInfo().getAdvicetime() + "??????");
		run.setFontSize(SMALL_FONT_SIZE);
		run.setColor("666666");
		run.setFontFamily(STANDARD_FONT_FAMILY);
		run.addBreak();
	}

	/**
	 * ????????????
	 * 
	 * @param document
	 * @param paper
	 */
	public static void initpaperNote(XWPFDocument document, String html) {
		// paper.info.papernote
		XWPFParagraph paragraph = document.createParagraph();
		paragraph.setAlignment(ParagraphAlignment.LEFT);
		initHtmlContent(paragraph, html, SMALL_FONT_SIZE);
		XWPFRun run = paragraph.createRun();
		run.addBreak();
	}

	public static void main(String[] args) {
		// ??????????????????docx??????
		File papaerFile = new File("D:\\test\\??????demo.docx");
		papaerFile.delete();
		FileOutputStream out = null;
		XWPFDocument document = null;
		PaperUnit paper = new PaperUnit();
		paper.setInfo(new Paper());
		paper.setRootChapterNum(5);
		paper.setSubjectNum(32);
		paper.setAllPoint(100);
		paper.getInfo().setAdvicetime(60);
		paper.getInfo().setName("??????????????????????????????");
		// paper.info.papernote
		paper.getInfo()
				.setPapernote(
						"<div>???????????????????????????????????? 2 ???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????</div>");
		try {
			// new ???????????????
			document = new XWPFDocument();
			out = new FileOutputStream(papaerFile);
			WordPaperCreator.initWordPaper(document, paper);
			document.write(out);
			// ??????
			// ????????????2????????????5??????????????????5????????????????????????100?????????
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				document.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
