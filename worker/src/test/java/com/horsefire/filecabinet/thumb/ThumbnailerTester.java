package com.horsefire.filecabinet.thumb;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import com.google.common.io.Files;
import com.google.inject.Guice;
import com.horsefire.filecabinet.MimeType;

public class ThumbnailerTester {

	public static void main(String[] args) throws IOException {
		ThumbnailerRegistry registry = Guice.createInjector().getInstance(
				ThumbnailerRegistry.class);

		File dir = new File("target/thumbnailTest");
		if (!dir.isDirectory()) {
			System.err.println(dir.getAbsolutePath() + " doesn't exist");
			return;
		}

		File[] files = dir.listFiles();
		if (files.length == 0) {
			System.err.println("No files found in " + dir.getAbsolutePath());
			return;
		}

		for (File file : files) {
			try {
				MimeType mimeType = MimeType.guessByFilename(file.getName());
				Collection<Thumbnailer> thumbnailers = registry
						.getThumbnailers(mimeType);
				if (thumbnailers == null || thumbnailers.isEmpty()) {
					System.err.println("No thumbnailers found for " + mimeType
							+ " for " + file.getName());
					continue;
				}

				byte[] pdfBytes = Files.toByteArray(file);
				for (Thumbnailer thumbnailer : thumbnailers) {
					byte[] thumb = thumbnailer.createThumbnail(pdfBytes);
					File thumbFile = new File(file.getParentFile(),
							file.getName()
									+ "."
									+ thumbnailer.suggestedName()
									+ "."
									+ thumbnailer.outgoingFormat()
											.getFileExtension());
					Files.write(thumb, thumbFile);
				}
			} catch (IOException e) {
				System.err.println(file + " failed thumbnailing: " + e);
			}
		}
	}
}
