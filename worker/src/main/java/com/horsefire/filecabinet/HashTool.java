package com.horsefire.filecabinet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.google.common.hash.HashCodes;

public class HashTool {

	private static MessageDigest getSha1Digest() {
		try {
			return MessageDigest.getInstance("SHA-1");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("Somehow missing SHA-1", e);
		}
	}

	public static String sha1(byte[] buffer) throws IOException {
		MessageDigest digest = getSha1Digest();
		digest.update(buffer);
		return HashCodes.fromBytes(digest.digest()).toString();
	}

	public static String sha1(File file) throws IOException {
		InputStream fis = new FileInputStream(file);
		try {
			return sha1(fis);
		} finally {
			try {
				fis.close();
			} catch (IOException e) {
				// Ignore
			}
		}
	}

	public static String sha1(InputStream in) throws IOException {
		MessageDigest digest = getSha1Digest();
		int n = 0;
		byte[] buffer = new byte[8192];
		while (n != -1) {
			n = in.read(buffer);
			if (n > 0) {
				digest.update(buffer, 0, n);
			}
		}
		return HashCodes.fromBytes(digest.digest()).toString();
	}
}
