package com.horsefire.filecabinet.file;

import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import javax.imageio.ImageIO;

import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;

public class PdfDocument extends Document {

	public PdfDocument(File dir, String id) throws IOException {
		super(dir, id);
	}

	@Override
	public void createThumbnail() throws IOException {
		RandomAccessFile raf = new RandomAccessFile(getRawFile(), "r");
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
				ImageIO.write(image, EXT_THUMBNAIL, getThumbnailFile());
			} else {
				throw new IOException("Image created is not a BufferedImage");
			}
		} finally {
			raf.close();
		}
	}

}
