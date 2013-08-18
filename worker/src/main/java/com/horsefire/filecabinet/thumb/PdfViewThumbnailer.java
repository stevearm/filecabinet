package com.horsefire.filecabinet.thumb;

import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import javax.imageio.ImageIO;

import com.google.common.io.Files;
import com.horsefire.filecabinet.MimeType;
import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;

public class PdfViewThumbnailer implements Thumbnailer {

	public byte[] createThumbnail(byte[] in) throws IOException {
		File inFile = File.createTempFile("pdfView", "pdf");
		RandomAccessFile raf = null;
		try {
			Files.write(in, inFile);
			raf = new RandomAccessFile(inFile, "r");
			FileChannel channel = raf.getChannel();
			ByteBuffer buf = channel.map(FileChannel.MapMode.READ_ONLY, 0,
					channel.size());
			PDFFile pdffile = new PDFFile(buf);

			// draw the first page to an image
			PDFPage page = pdffile.getPage(0);

			// get the width and height for the doc at the default zoom
			Rectangle rect = new Rectangle(0, 0, (int) page.getBBox()
					.getWidth(), (int) page.getBBox().getHeight());

			// generate the image
			Image img = page.getImage(rect.width, rect.height, // width & height
					rect, // clip rect
					null, // null for the ImageObserver
					true, // fill background with white
					true // block until drawing is done
					);
			if (img instanceof BufferedImage) {
				BufferedImage image = (BufferedImage) img;
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				ImageIO.write(image, "png", out);
				return out.toByteArray();
			} else {
				throw new IOException("Image created is not a BufferedImage");
			}
		} finally {
			if (raf != null) {
				raf.close();
			}
			inFile.delete();
		}
	}

	public void createThumbnail(File source, File dest) throws IOException {
		RandomAccessFile raf = new RandomAccessFile(source, "r");
		try {
			FileChannel channel = raf.getChannel();
			ByteBuffer buf = channel.map(FileChannel.MapMode.READ_ONLY, 0,
					channel.size());
			PDFFile pdffile = new PDFFile(buf);

			// draw the first page to an image
			PDFPage page = pdffile.getPage(0);

			// get the width and height for the doc at the default zoom
			Rectangle rect = new Rectangle(0, 0, (int) page.getBBox()
					.getWidth(), (int) page.getBBox().getHeight());

			// generate the image
			Image img = page.getImage(rect.width, rect.height, // width & height
					rect, // clip rect
					null, // null for the ImageObserver
					true, // fill background with white
					true // block until drawing is done
					);
			if (img instanceof BufferedImage) {
				BufferedImage image = (BufferedImage) img;
				ImageIO.write(image, "png", dest);
			} else {
				throw new IOException("Image created is not a BufferedImage");
			}
		} finally {
			raf.close();
		}
	}

	public MimeType incomingFormat() {
		return MimeType.PDF;
	}

	public MimeType outgoingFormat() {
		return MimeType.PNG;
	}

	public String suggestedName() {
		return "pdf_view";
	}
}
