package com.horsefire.filecabinet.thumb;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import javax.imageio.ImageIO;

import org.imgscalr.Scalr;
import org.imgscalr.Scalr.Method;
import org.imgscalr.Scalr.Mode;

import com.horsefire.filecabinet.MimeType;

public class ImageThumbnailer implements Thumbnailer {

	private static final int WIDTH = 1000;

	public byte[] createThumbnail(byte[] in) throws IOException {
		BufferedImage image = ImageIO.read(new ByteArrayInputStream(in));
		BufferedImage thumb = Scalr.resize(image, Method.QUALITY,
				Mode.FIT_TO_WIDTH, WIDTH, 0);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ImageIO.write(thumb, "jpg", out);
		byte[] result = out.toByteArray();

		image.flush();
		thumb.flush();

		return result;
	}

	public Collection<MimeType> incomingFormats() {
		return Arrays.asList(MimeType.JPG, MimeType.PNG);
	}

	public MimeType outgoingFormat() {
		return MimeType.JPG;
	}

	public String suggestedName() {
		return "scalr";
	}
}
