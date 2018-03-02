package com.lenovo.lejingpin.hw.lcapackageinstaller.utils;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.spec.DSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import android.content.Context;
import android.util.Base64;

public class SignatureVerify {
	private final static String PUBLIC_KEY = "MIIBuDCCASwGByqGSM44BAEwggEfAoGBAP1/U4EddRIpUt9KnC7s5Of2EbdSPO9EAMMeP4C2USZpRV1AIlH7WT2NWPq/xfW6MPbLm1Vs14E7gB00b/JmYLdrmVClpJ+f6AR7ECLCT7up1/63xhv4O1fnxqimFQ8E+4P208UewwI1VBNaFpEy9nXzrith1yrv8iIDGZ3RSAHHAhUAl2BQjxUjC8yykrmCouuEC/BYHPUCgYEA9+GghdabPd7LvKtcNrhXuXmUr7v6OuqC+VdMCz0HgmdRWVeOutRZT+ZxBxCBgLRJFnEj6EwoFhO3zwkyjMim4TwWeotUfI0o4KOuHiuzpnWRbqN/C/ohNWLx+2J6ASQ7zKTxvqhRkImog9/hWuWfBpKLZl6Ae1UlZAFMO/7PSSoDgYUAAoGBAMo/anmTF0cAhbdgrBCDVPWP0mFNPTUWBzJRK6YgtwNh7CiExzXP+RsE3worFYMWcBrlLxanCPfE0SRdQq/qoKCaKzcZIWabvriI6YvJ+S6jlieFUIIo+ZbHuKKbBVFPEILsqLlMLkUbCjNt20BNV4nUIOQZcF/0XK+XcyEzCRPb";

	private final static String PUBLIC_TEST_P = "fd7f53811d75122952df4a9c2eece4e7f611b7523cef4400c31e3f80b6512669455d402251fb593d8d58fabfc5f5ba30f6cb9b556cd7813b801d346ff26660b76b9950a5a49f9fe8047b1022c24fbba9d7feb7c61bf83b57e7c6a8a6150f04fb83f6d3c51ec3023554135a169132f675f3ae2b61d72aeff22203199dd14801c7";
	private final static String PUBLIC_TEST_Q = "9760508f15230bccb292b982a2eb840bf0581cf5";
	private final static String PUBLIC_TEST_G = "f7e1a085d69b3ddecbbcab5c36b857b97994afbbfa3aea82f9574c0b3d0782675159578ebad4594fe67107108180b449167123e84c281613b7cf09328cc8a6e13c167a8b547c8d28e0a3ae1e2bb3a675916ea37f0bfa213562f1fb627a01243bcca4f1bea8519089a883dfe15ae59f06928b665e807b552564014c3bfecf492a";
	private final static String PUBLIC_TEST_Y = "d85ee6d58e39ebc3b31515ed9b41e32732f66661a4ea03e053c242b6af0a1e526b27c6cd56bf3d4f3677aaa4214807ccfcf09e07d91a87bd97eaab7c056881a6bfd444b4e6eae916f4cd7ac488c94169bce804fd971c8973cefd3ee2bc8bb89fce1c5c398e741d7dd9db79954ba059f256d7544b49fdc26601194bf6c65c6c27";
	
	private SignatureVerify(){
		
	}
	private static PublicKey getX509PublicKey(String keyBase64Encode) {
		PublicKey pubkey = null;
		try {
			KeyFactory keyFac = KeyFactory.getInstance("DSA");
			byte[] keyEncode = Base64.decode(keyBase64Encode.getBytes(), Base64.DEFAULT);
			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyEncode);
			pubkey = keyFac.generatePublic(keySpec);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pubkey;
	}

	private static PublicKey getDSAPublicKey(String p, String q, String g, String y) {
		PublicKey pubkey = null;
		try {
			KeyFactory keyFac = KeyFactory.getInstance("DSA");

			BigInteger bp = new BigInteger(p, 16);
			BigInteger bq = new BigInteger(q, 16);
			BigInteger bg = new BigInteger(g, 16);
			BigInteger by = new BigInteger(y, 16);
			DSAPublicKeySpec keySpec = new DSAPublicKeySpec(by, bp, bq, bg);
			
			pubkey = keyFac.generatePublic(keySpec);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pubkey;
	}
	
	private static Certificate[] loadCertificates(JarFile jarFile, JarEntry je,
			byte[] readBuffer) {
		try {
			// We must read the stream for the JarEntry to retrieve
			// its certificates.
			InputStream is = jarFile.getInputStream(je);
//			while (is.read(readBuffer, 0, readBuffer.length) != -1) {
//				// not using
//			}
			is.close();
			return je != null ? je.getCertificates() : null;
		} catch (IOException e) {
			// Log.w(TAG, "Exception reading " + je.getName() + " in "
			// + jarFile.getName(), e);
		}
		return null;
	}
	
	public static boolean verifySignature(Context context,String filePath){
		boolean verifyed = true;
		 
		try {
			JarFile jarFile = new JarFile(filePath);
			JarEntry je = null;

			PublicKey publicKey = getX509PublicKey(PUBLIC_KEY);

			Enumeration<JarEntry> entries = jarFile.entries();
			byte[] readBuffer = new byte[8192];
			while (entries.hasMoreElements()) {
				je = (JarEntry) entries.nextElement();
				if (je.isDirectory())
					continue;
				if (je.getName().startsWith("META-INF/"))
					continue;
				Certificate[] certs = loadCertificates(jarFile, je, readBuffer);
				if (certs != null && certs.length > 0) {
					final int N = certs.length;
					try {
						for (int i = 0; i < N; i++) {
							certs[i].verify(publicKey);
						}
					} catch (NoSuchAlgorithmException e) {
						verifyed = false;
					} catch (InvalidKeyException e) {
						verifyed = false;
					} catch (NoSuchProviderException e) {
						verifyed = false;
					} catch (SignatureException e) {
						verifyed = false;
					} catch (CertificateException e) {
						verifyed = false;
					}
				}else{
					verifyed = false;
				}
				if(!verifyed){
					break;
				}
			}

			jarFile.close();
		} catch (IOException e) {
			verifyed = false;
		} catch (Exception e){
			verifyed = false;
		}
		return verifyed;
	}
	
	public static boolean verifyTestSignature(Context context,String filePath){
		boolean verifyed = true;
		
		try {
			JarFile jarFile = new JarFile(filePath);
			JarEntry je = null;

			PublicKey publicKey = getDSAPublicKey(PUBLIC_TEST_P, PUBLIC_TEST_Q, PUBLIC_TEST_G, PUBLIC_TEST_Y);
			Enumeration<JarEntry> entries = jarFile.entries();
			byte[] readBuffer = new byte[8192];
			while (entries.hasMoreElements()) {
				je = (JarEntry) entries.nextElement();
				if (je.isDirectory())
					continue;
				if (je.getName().startsWith("META-INF/"))
					continue;
				Certificate[] certs = loadCertificates(jarFile, je, readBuffer);
				if (certs != null && certs.length > 0) {
					final int N = certs.length;
					try {
						for (int i = 0; i < N; i++) {
							certs[i].verify(publicKey);
						}
					} catch (NoSuchAlgorithmException e) {
						verifyed = false;
					} catch (InvalidKeyException e) {
						verifyed = false;
					} catch (NoSuchProviderException e) {
						verifyed = false;
					} catch (SignatureException e) {
						verifyed = false;
					} catch (CertificateException e) {
						verifyed = false;
					}
				}else{
					verifyed = false;
				}
				if(!verifyed){
					break;
				}
			}
			jarFile.close();
		} catch (IOException e) {
			verifyed = false;
		} catch (Exception e){
			verifyed = false;
		}
		
		return verifyed;
	}
}
